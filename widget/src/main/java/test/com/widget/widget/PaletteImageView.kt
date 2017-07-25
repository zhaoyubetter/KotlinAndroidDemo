package test.com.widget.widget

import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.support.v7.graphics.Palette
import android.util.AttributeSet
import android.view.View
import test.com.widget.R
import java.lang.ref.WeakReference


/**
 * 参考：https://github.com/DingMouRen
 * 可为View添加阴影
 * Created by zhaoyu1 on 2017/7/25.
 */
class PaletteImageView(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : View(context, attrs, defAttrStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    // ---- 自定义的属性
    private var radius = 0f
    private var srcId: Int = 0
    private var padding = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var shadowRadius = 0f

    // ---- 画笔
    private val paintShadow: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // ---- 获取颜色
    private var palette: Palette? = null
    private var mainColor: Int? = null

    // ----
    private var bitmap: Bitmap? = null
    private var realBitmap: Bitmap? = null
    private var roundBitmap: Bitmap? = null
    private var heightMode: Int = 0
    private val xfermode: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private var rectFShadow: RectF? = null      // 阴影rect
    private var roundRectF: RectF? = null       // 圆角rect

    // ---- handler
    private val handler: CurrentHandler by lazy {
        CurrentHandler(this)
    }
    private val MSG: Int = 0x1088

    // ---- AsyncTask
    private var asyncTask: AsyncTask<Bitmap, Void, Palette>? = null

    // ---- 对外接口
    var listener: OnParseColorListener? = null
        set(value) {
            field = listener
        }


    init {
        context.obtainStyledAttributes(attrs, R.styleable.PaletteImageView, R.attr.paletteImageView, R.style.PaletteImageView).apply {
            setPaletteRadius(getDimension(R.styleable.PaletteImageView_paletteRadius, 0F))
            srcId = getResourceId(R.styleable.PaletteImageView_paletteSrc, 0)
            setPalettePadding(getDimension(R.styleable.PaletteImageView_palettePadding, 0F))
            setPaletteOffsetX(getDimension(R.styleable.PaletteImageView_paletteOffsetX, 0F))
            setPaletteOffsetY(getDimension(R.styleable.PaletteImageView_paletteOffsetY, 0F))
            setPaletteShadowRadius(getDimension(R.styleable.PaletteImageView_paletteShadowRadius, 0F))
            recycle()
        }

        val tPadding = padding.toInt()
        setPadding(tPadding, tPadding, tPadding, tPadding)

        paintShadow.isDither = true
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(resources.getColor(android.R.color.transparent))
        paint.isDither = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (bitmap != null) {
                height = ((width - 2 * padding) * (bitmap!!.height * 1.0f / bitmap!!.width) + 2 * padding).toInt()
            }
            if (srcId != 0 && realBitmap != null) {
                height = (realBitmap!!.height + 2 * padding).toInt()
            }
        }

        if (bitmap != null) {
            height = ((width - 2 * padding) * (bitmap!!.height * 1.0f / bitmap!!.width) + 2 * padding).toInt()
        }

        setMeasuredDimension(width,height)
//        setMeasuredDimension(resolveSize(width, widthMode), resolveSize(height, heightMode))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        zipBitmap(srcId, bitmap, heightMode)        // 缩放
        rectFShadow = RectF(padding, padding, width - padding, height - padding)
        roundRectF = RectF(0f, 0f, width - padding * 2, height - padding * 2)
        roundBitmap = createRoundCornerImage(realBitmap, radius)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (realBitmap != null) {
            canvas?.drawRoundRect(rectFShadow, radius, radius, paintShadow)
            canvas?.drawBitmap(roundBitmap, padding, padding, null)
            mainColor?.let {
                asyncTask?.cancel(true)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
    }


    // ------------------ 对外提供的方法 ------------

    fun setShadowColor(color: Int) {
        this.mainColor = color
        handler.sendEmptyMessage(MSG)
    }

    fun setPaletteRadius(r: Float) {
        this.radius = r
        roundBitmap = createRoundCornerImage(realBitmap, radius)
        invalidate()
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()
    }

    fun setPalettePadding(r: Float) {
        this.padding = r
        invalidate()
    }

    fun setPaletteOffsetX(r: Float) {
        if (r >= padding) offsetX = r else offsetX = padding
        handler.sendEmptyMessage(MSG)
    }

    fun setPaletteOffsetY(r: Float) {
        if (r >= padding) offsetY = r else offsetY = padding
        handler.sendEmptyMessage(MSG)
    }

    fun setPaletteShadowRadius(r: Float) {
        this.shadowRadius = r
        handler.sendEmptyMessage(MSG)
    }

    /**
     * 有活力的颜色
     */
    fun getVibrantColor(): IntArray? {
        if (palette == null || palette?.vibrantSwatch == null) return null
        val array = IntArray(3)
        array[0] = palette?.vibrantSwatch?.titleTextColor ?: 0
        array[1] = palette?.vibrantSwatch?.bodyTextColor ?: 0
        array[2] = palette?.vibrantSwatch?.rgb ?: 0
        return array
    }

    /**
     *  暗色系
     */
    fun getDarkVibrantColor(): IntArray? {
        if (palette == null || palette?.darkVibrantSwatch == null) return null
        val arry = IntArray(3)
        arry[0] = palette?.darkVibrantSwatch?.titleTextColor ?: 0
        arry[1] = palette?.darkVibrantSwatch?.bodyTextColor ?: 0
        arry[2] = palette?.darkVibrantSwatch?.rgb ?: 0
        return arry
    }

    /**
     * 亮色系
     */
    fun getLightVibrantColor(): IntArray? {
        if (palette == null || palette?.lightVibrantSwatch == null) return null
        val arry = IntArray(3)
        arry[0] = palette?.lightVibrantSwatch?.titleTextColor ?: 0
        arry[1] = palette?.lightVibrantSwatch?.bodyTextColor ?: 0
        arry[2] = palette?.lightVibrantSwatch?.rgb ?: 0
        return arry
    }

    /**
     * 柔和的
    fun getMutedColor(): IntArray? {
    if (mPalette == null || mPalette.getMutedSwatch() == null) return null
    val arry = IntArray(3)
    arry[0] = mPalette.getMutedSwatch().getTitleTextColor()
    arry[1] = mPalette.getMutedSwatch().getBodyTextColor()
    arry[2] = mPalette.getMutedSwatch().getRgb()
    return arry
    }*/

    /**
     * 柔和的，暗

    fun getDarkMutedColor(): IntArray? {
    if (mPalette == null || mPalette.getDarkMutedSwatch() == null) return null
    val arry = IntArray(3)
    arry[0] = mPalette.getDarkMutedSwatch().getTitleTextColor()
    arry[1] = mPalette.getDarkMutedSwatch().getBodyTextColor()
    arry[2] = mPalette.getDarkMutedSwatch().getRgb()
    return arry
    }

    fun getLightMutedColor(): IntArray? {
    if (mPalette == null || mPalette.getLightMutedSwatch() == null) return null
    val arry = IntArray(3)
    arry[0] = mPalette.getLightMutedSwatch().getTitleTextColor()
    arry[1] = mPalette.getLightMutedSwatch().getBodyTextColor()
    arry[2] = mPalette.getLightMutedSwatch().getRgb()
    return arry
    }*/

    interface OnParseColorListener {
        fun onComplete(paletteImageView: PaletteImageView)
        fun onFail()
    }

    /**
     * 圆角图片
     */
    private fun createRoundCornerImage(realBitmap: Bitmap?, radius: Float): Bitmap? {
        realBitmap?.let {
            val target = Bitmap.createBitmap((width - 2 * padding).toInt(), (height - padding * 2).toInt(), Bitmap.Config.ARGB_4444)
            val canvas = Canvas(target)
            canvas.drawRoundRect(roundRectF, radius, radius, paint)
            paint.xfermode = xfermode               // 切割，实现圆角
            canvas.drawBitmap(it, 0f, 0f, paint)
            paint.xfermode = null
            return target
        }
        return null
    }

    /**
     * 压缩
     */
    private fun zipBitmap(imgId: Int, pBitmap: Bitmap?, heightMode: Int) {
        val weakMatrix = WeakReference<Matrix>(Matrix())
        weakMatrix.get() ?: return

        bitmap = pBitmap
        val matrix = weakMatrix.get()
        val reqWidth = width - 2 * padding
        val reqHeight = height - 2 * padding

        if (reqWidth <= 0 || reqHeight <= 0) {
            return
        }

        var rawWidth: Int = 0
        var rawHeight: Int = 0

        if (imgId != 0 && bitmap == null) {         // resource 转成bitmap
            val weakOptions = WeakReference<BitmapFactory.Options>(BitmapFactory.Options())
            val options = weakOptions.get() ?: return
            BitmapFactory.decodeResource(resources, imgId, options)
            options.inJustDecodeBounds = true
            rawWidth = options.outWidth
            rawHeight = options.outHeight
            options.inSampleSize = calculateInSample(rawWidth, rawHeight, (width - 2 * padding).toInt(), (height - 2 * padding).toInt())
            options.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeResource(resources, imgId, options)
        } else if (imgId == 0 && bitmap != null) {  // 直接设置的bitmap
            rawWidth = bitmap?.width ?: 0
            rawHeight = bitmap?.height ?: 0
            val scale = rawHeight * 1.0f / rawWidth     // 等比缩放
            realBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth.toInt(), (reqWidth * scale).toInt(), true)
            initShadow(realBitmap)
            return
        }

        // 判断高度模式
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            val scale = rawHeight * 1.0f / rawWidth
            realBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth.toInt(), (reqWidth * scale).toInt(), true)
        } else {    // 整体缩放
            var dx = 0
            var dy = 0
            val small = Math.min(rawHeight, rawWidth)
            val big = Math.max(reqHeight, reqWidth)
            val scale = big * 1.0f / small
            matrix?.setScale(scale, scale)
            if (rawHeight > rawWidth) {
                dy = (rawHeight - rawWidth) / 2
            } else if (rawHeight < rawWidth) {
                dx = (rawWidth - rawHeight) / 2
            }
            realBitmap = Bitmap.createBitmap(bitmap, dx, dy, small, small, matrix, true)
        }
        initShadow(realBitmap)
    }

    /**
     * 初始化阴影
     */
    private fun initShadow(bitmap: Bitmap?) {
        bitmap?.let {
            asyncTask = Palette.from(it).generate {
                it.let {
                    if (it != null) {
                        palette = it
                        mainColor = it.dominantSwatch?.rgb      // 柔和的颜色，暗的 ，这里为阴影的主色
                        handler.sendEmptyMessage(MSG)
                        listener?.onComplete(this)
                    } else {
                        listener?.onFail()
                    }
                }
            }
        }
    }

    private fun calculateInSample(rawWidth: Int, rawHeight: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (rawWidth > reqWidth || rawHeight > reqHeight) {
            val widthRatio = Math.round(rawWidth * 1.0f / reqWidth)
            val heightRatio = Math.round(rawHeight * 1.0f / reqHeight)
            inSampleSize = Math.min(widthRatio, heightRatio)
        }
        return inSampleSize
    }

    private class CurrentHandler(paletteView: PaletteImageView) : Handler() {

        val paletteViewWeak by lazy {
            WeakReference<PaletteImageView>(paletteView)
        }

        override fun handleMessage(msg: Message?) {     // 这里画阴影
            paletteViewWeak.get()?.let {
                it.paintShadow.setShadowLayer(it.shadowRadius, it.offsetX, it.offsetY, it.mainColor ?: android.R.color.transparent)
                it.invalidate()
            }
        }
    }

}
