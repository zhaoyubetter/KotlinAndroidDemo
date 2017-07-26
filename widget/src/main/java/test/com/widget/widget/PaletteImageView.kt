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
import android.graphics.BitmapFactory


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
    private var listener: OnParseColorListener? = null

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
        setMeasuredDimension(width, height)
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
            canvas?.drawRoundRect(rectFShadow, radius, radius, paintShadow)     // 画阴影
            canvas?.drawBitmap(roundBitmap, padding, padding, null)             // 画图片
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
        this.mainColor = null
        requestLayout()
    }

    fun setPalettePadding(r: Float) {
        this.padding = r
        invalidate()
    }

    fun setPaletteOffsetX(r: Float) {
        if (r >= padding) offsetX = padding else offsetX = r
        handler.sendEmptyMessage(MSG)
    }

    fun setPaletteOffsetY(r: Float) {
        if (r >= padding) offsetY = padding else offsetY = r
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
     */
    fun getMutedColor(): IntArray? {
        if (palette == null || palette?.mutedSwatch == null) return null
        val arry = IntArray(3)
        arry[0] = palette?.mutedSwatch?.titleTextColor ?: 0
        arry[1] = palette?.mutedSwatch?.bodyTextColor ?: 0
        arry[2] = palette?.mutedSwatch?.rgb ?: 0
        return arry
    }

    /**
     * 柔和的，暗
     */
    fun getDarkMutedColor(): IntArray? {
        if (palette == null || palette?.getDarkMutedSwatch() == null) return null
        val arry = IntArray(3)
        arry[0] = palette?.getDarkMutedSwatch()?.getTitleTextColor() ?: 0
        arry[1] = palette?.getDarkMutedSwatch()?.getBodyTextColor() ?: 0
        arry[2] = palette?.getDarkMutedSwatch()?.getRgb() ?: 0
        return arry
    }

    fun getLightMutedColor(): IntArray? {
        if (palette == null || palette?.getDarkMutedSwatch() == null) return null
        val arry = IntArray(3)
        arry[0] = palette?.getLightMutedSwatch()?.getTitleTextColor() ?: 0
        arry[1] = palette?.getLightMutedSwatch()?.getBodyTextColor() ?: 0
        arry[2] = palette?.getLightMutedSwatch()?.getRgb() ?: 0
        return arry
    }

    interface OnParseColorListener {
        fun onComplete(paletteImageView: PaletteImageView)
        fun onFail()
    }

    fun setListener(listener: OnParseColorListener) {
        this.listener = listener
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

        //---- 图片原始高度
        var rawWidth: Int = 0
        var rawHeight: Int = 0

        if (imgId != 0 && bitmap == null) {         // resource 转成bitmap
            val weakOptions = WeakReference<BitmapFactory.Options>(BitmapFactory.Options())
            val options = weakOptions.get() ?: return
            BitmapFactory.decodeResource(resources, imgId, options)
            options.inJustDecodeBounds = true
            rawWidth = options.outWidth
            rawHeight = options.outHeight
            options.inSampleSize = calculateInSampleSize(options, (width - 2 * padding).toInt(), (height - 2 * padding).toInt())
            options.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeResource(resources, imgId, options)
        } else if (imgId == 0 && bitmap != null) {  // 有了bitmap，缩放一下 bitmap,形成 realbitmap
            rawWidth = bitmap?.width ?: 0
            rawHeight = bitmap?.height ?: 0
            val scale = rawHeight * 1.0f / rawWidth     // 等比缩放系数
            realBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth.toInt(), (reqWidth * scale).toInt(), true)   // 宽 ，宽 * 系数
            initShadow(realBitmap)
            return
        }

        // 修正问题，bitmap已经缩放过了，直接赋值
        realBitmap = bitmap

        /*
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
            //realBitmap = Bitmap.createBitmap(bitmap, dx, dy, small, small, matrix, true)

            val scale2 = rawHeight * 1.0f / rawWidth
            realBitmap = bitmap
        }*/

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

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }

            var totalPixels = (width * height / inSampleSize).toLong()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toLong()
            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2
                totalPixels /= 2
            }
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
