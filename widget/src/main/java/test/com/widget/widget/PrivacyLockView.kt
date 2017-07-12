package test.com.widget.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.view.inputmethod.InputMethodManager
import test.com.widget.R


/**
 * 自定义验证码/密码输入框
 *
 * Created by zhaoyu1 on 2017/7/5.
 */
class PrivacyLockView(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : EditText(context, attrs, defAttrStyle) {

    constructor(context: Context) : this(context, null, android.R.attr.editTextStyle)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.editTextStyle)

    private val MAX_COUNT = 10 //最大条目个数
    private var listener: ((CharSequence) -> Unit)? = null
    private var borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var privacyDrawable: Drawable? = null//加密时，绘制的Drawable
    private var drawableWidth = 0f
    private var drawableHeight = 0f
    private var itemPadding = 0f
    private var itemCount = 0
    private var encrypt = false
    private var rect = Rect()

    init {
        isCursorVisible = false
        borderPaint.style = Paint.Style.STROKE
        super.setBackgroundColor(Color.TRANSPARENT)
        // 加载样式
        context.obtainStyledAttributes(attrs, R.styleable.PrivacyLockView, R.attr.privacyLockView, R.style.PrivacyLockView).apply {
            setItemCount(getInt(R.styleable.PrivacyLockView_pv_itemCount, 0))
            setItemPadding(getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0F))
            setPrivacyDrawable(getDrawable(R.styleable.PrivacyLockView_pv_privacyDrawable))
            setPrivacyDrawableWidth(getDimension(R.styleable.PrivacyLockView_pv_privacyDrawableWidth, 0f))
            setPrivacyDrawableHeight(getDimension(R.styleable.PrivacyLockView_pv_privacyDrawableHeight, 0f))
            setBorderColor(getColor(R.styleable.PrivacyLockView_pv_borderColor, Color.TRANSPARENT))
            setBorderStrokeWidth(getDimension(R.styleable.PrivacyLockView_pv_borderStrokeWidth, 0f))
            setItemPadding(getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0F))
            setEncrypt(getBoolean(R.styleable.PrivacyLockView_pv_isPrivacy, false))
            recycle()
        }
    }

    fun setEncrypt(boolean: Boolean) {
        this.encrypt = boolean
        invalidate()
    }

    fun setBorderColor(color: Int) {
        this.borderPaint.color = color
        invalidate()
    }

    fun setBorderStrokeWidth(strokeWidth: Float) {
        this.borderPaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun setPrivacyDrawableWidth(width: Float) {
        this.drawableWidth = width
        invalidate()
    }

    fun setPrivacyDrawableHeight(height: Float) {
        this.drawableHeight = height
        invalidate()
    }

    fun setPrivacyDrawable(drawable: Drawable?) {
        this.privacyDrawable = drawable
        invalidate()
    }

    fun setItemPadding(padding: Float) {
        this.itemPadding = padding
        requestLayout()
    }

    /**
     * item 个数
     */
    fun setItemCount(itemCount: Int) {
        if (0 >= itemCount || itemCount > MAX_COUNT) {
            throw IllegalArgumentException("item count error!")
        }
        this.itemCount = itemCount
        invalidate()
    }


    /**
     * 禁用设置背景,避免外围修改背景色
     */
    override fun setBackgroundDrawable(background: Drawable?) {
        super.setBackgroundDrawable(background)
//        throw UnsupportedOperationException("Can't set background!")
    }

    /**
     * 禁用外围设置文本
     */
    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
//        throw UnsupportedOperationException("Can't set text!")
    }


    /**
     * 清空方法
     */
    fun clearText() {
        super.setText(null)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        var measureHeight = paddingTop + paddingBottom
        if (View.MeasureSpec.EXACTLY == heightMode) {
            measureHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        } else {
            measureHeight += (drawableHeight.toInt() + itemPadding * 2).toInt()
        }
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text?.length ?: 0 < itemCount) {
            invalidate()
        }
        if (text?.length == itemCount) {
            hideSortInput()
            listener?.invoke(text)
        }
    }

    override fun onDetachedFromWindow() {
        hideSortInput()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)//禁用EditText本来绘制
        //绘制外围边界线
        drawRectItem(canvas)
        //绘制中心显示加密信息
        drawPrivacy(canvas)
    }

    /**
     * 画方形格子 item
     */
    private fun drawRectItem(canvas: Canvas) {
        val strokeWidth = borderPaint.strokeWidth
        val itemWidth = width / itemCount
        canvas.drawRect(paddingLeft.toFloat() + strokeWidth / 2, paddingTop.toFloat(), width - paddingRight - strokeWidth / 2, (height - paddingBottom).toFloat(), borderPaint)
        for (index in 1..itemCount - 1) {
            canvas.drawLine((index * itemWidth).toFloat(), paddingTop.toFloat(), (index * itemWidth).toFloat(), (height - paddingBottom).toFloat(), borderPaint)
        }
    }

    /**
     * 绘制加密信息
     */
    private fun drawPrivacy(canvas: Canvas) {
        val text = text ?: return
        // 加入居中处理，指定位置居中处理
        val itemWidth = width / itemCount
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        val drawable = privacyDrawable
        var offsetLeft: Int = paddingLeft + itemWidth / 2
        for (i in 0..text.length - 1) {
            if (encrypt) {
                // 显示密文
                if (null != drawable) {
                    drawable.setBounds((offsetLeft - drawableWidth / 2).toInt(),
                            (height / 2 - drawableHeight / 2).toInt(),
                            (offsetLeft - drawableWidth / 2 + drawableWidth).toInt(),
                            (height / 2 + drawableHeight / 2).toInt())
                    offsetLeft += itemWidth
                    drawable.draw(canvas)
                }
            } else {
                paint.getTextBounds(text.toString(), 0, text.length, rect)
                canvas.drawText(text[i].toString(), offsetLeft.toFloat(), rect.height().toFloat() + (height - rect.height()) / 2, paint)      // // 文字居中处理
                offsetLeft += itemWidth
            }
        }
    }

    /**
     * 隐藏输入法
     */
    fun hideSortInput() = (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).let { it.showSoftInput(this, 0) }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            postDelayed({
                if (null != windowToken) {
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).let { it.showSoftInput(this, 0) }
                }
            }, 100)
        } else {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply { hideSoftInputFromWindow(windowToken, 0) }
        }
    }


    fun setOnTextSubmitListener(listener: (CharSequence) -> Unit) {
        this.listener = listener
    }

    override fun onSaveInstanceState(): Parcelable {
        val save = super.onSaveInstanceState()
        return Bundle().apply {
            putParcelable("save", save)
            putInt("itemCount", itemCount)
            putFloat("itemPadding", itemPadding)
            putInt("borderColor", borderPaint.color)
            putFloat("borderStrokeWidth", borderPaint.strokeWidth)
            putFloat("drawableWidth", drawableWidth)
            putFloat("drawableHeight", drawableHeight)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        setItemCount(bundle.getInt("itemCount"))
        setItemPadding(bundle.getFloat("itemPadding"))
        setBorderColor(bundle.getInt("borderColor"))
        setBorderStrokeWidth(bundle.getFloat("borderStrokeWidth"))
        setPrivacyDrawableWidth(bundle.getFloat("drawableWidth"))
        setPrivacyDrawableHeight(bundle.getFloat("drawableHeight"))
        super.onRestoreInstanceState(bundle.getParcelable("save"))
    }
}