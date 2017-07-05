package test.com.kotlinandroiddemo

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import org.jetbrains.anko.dip
import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import android.text.TextUtils


/**
 * 自定义验证码/密码输入框
 *
 * Created by zhaoyu1 on 2017/7/5.
 */
class PrivacyLockView(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : EditText(context, attrs, defAttrStyle) {

    // 次要构造函数
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var submitListener: OnTextSubmitListener? = null
        set(value) {
            field = value
        }

    /**
     * 个数
     */
    var mItemCount: Int = 0
        set(value) {
            if (value > 0) {
                field = value
                requestLayout()
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] = InputFilter.LengthFilter(value)
                this.filters = FilterArray
            }
        }
    var mPrivacyDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }
    var mPrivacyDrawableSize: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 边框颜色
     */
    var mBorderColor: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var mItemSize: Int = 0
        set(value) {
            if (value > 0) {
                field = value
                invalidate()
            }
        }
    var mItemPadding: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 是否加密
     */
    private var mEncrypt: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var mPaint: Paint

    init {
        // 加载样式
        context.obtainStyledAttributes(attrs, R.styleable.PrivacyLockView).apply {

            /*
            setItemCount(getInt(R.styleable.PrivacyLockView_pv_itemCount, 6))
            setItemPadding(getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0F) as Int)
            setItemCount(getInteger(R.styleable.PrivacyLockView_pv_itemCount, 6))
            setPrivacyDrawable(getDrawable(R.styleable.PrivacyLockView_pv_privacyDrawable))
            setPrivacyDrawableSize(getDimension(R.styleable.PrivacyLockView_pv_privacyDrawableSize, dip(8).toFloat()) as Int)
            setBorderColor(getColor(R.styleable.PrivacyLockView_pv_border_color, resources.getColor(android.R.color.darker_gray)))
            setItemSize(getDimension(R.styleable.PrivacyLockView_pv_item_size, dip(28f).toFloat()) as Int)
            setItemPadding(getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0F) as Int)
            setEncrypt(getBoolean(R.styleable.PrivacyLockView_pv_is_privacy, false))
            */

            mItemCount = getInt(R.styleable.PrivacyLockView_pv_itemCount, 6)
            mItemCount = getInteger(R.styleable.PrivacyLockView_pv_itemCount, 6)
            mPrivacyDrawable = getDrawable(R.styleable.PrivacyLockView_pv_privacyDrawable)
            mPrivacyDrawableSize = getDimension(R.styleable.PrivacyLockView_pv_privacyDrawableSize, 8f).toInt()
            mBorderColor = getColor(R.styleable.PrivacyLockView_pv_border_color, resources.getColor(android.R.color.darker_gray))
            mItemSize = getDimension(R.styleable.PrivacyLockView_pv_item_size, 28f).toInt()
            mItemPadding = getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0F).toInt()
            mEncrypt = getBoolean(R.styleable.PrivacyLockView_pv_is_privacy, false)
        }

        isCursorVisible = false
        setBackgroundColor(Color.TRANSPARENT)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    fun clearText() {
        setText("")
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
            measureHeight += mItemSize
        }
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text?.length ?: 0 < mItemCount) {
            invalidate()
        }
        if (text?.length == mItemCount) {
            hideSortInput()
            submitListener?.onSubmit(text)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawRectItem(canvas)
        drawUserInput(canvas)
    }

    /**
     * 方形item
     */
    private fun drawRectItem(canvas: Canvas?) {
        val width = width
        val height = height
        val offsetLeft = (width - mItemPadding * (mItemCount - 1) - mItemSize * mItemCount) / 2


        // 外边框
        mPaint?.color = mBorderColor
        mPaint?.style = Paint.Style.STROKE
        mPaint?.strokeWidth = 0f
        val rect = RectF(offsetLeft.toFloat(), paddingTop.toFloat(), (getWidth() - offsetLeft).toFloat(), (height - paddingBottom).toFloat())
        canvas?.drawRoundRect(rect, 2f, 2f, mPaint)

        (0..mItemCount - 1 - 1)
                .map { offsetLeft + (it + 1) * mItemSize + it * mItemPadding.toFloat() }
                .forEach { canvas?.drawLine(it, rect.top, it, rect.bottom, mPaint) }

        /*
        for (i in 0..mItemCount - 1 - 1) {
            val startX = offsetLeft + (i + 1) * mItemSize + i * mItemPadding as Float
            canvas?.drawLine(startX, rect.top, startX, rect.bottom, mPaint)
        }*/
    }

    var mRect: Rect? = null

    private fun drawUserInput(canvas: Canvas?) {

        val text = text.toString()
        if (TextUtils.isEmpty(text)) {
            return
        }

        mPaint.color = textColors.defaultColor
        mPaint.textSize = textSize
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER        // 加入居中处理，指定位置居中处理
        var offsetLeft: Int = (width - mItemPadding * (mItemCount - 1) - mItemSize * mItemCount) / 2 + mItemSize / 2


        if (mRect == null) {
            mRect = Rect()
            mPaint.getTextBounds(text, 0, text.length, mRect)
        }

        for (i in 0..text.length - 1) {
            val c = text[i].toString()
            if (mEncrypt && mPrivacyDrawable != null) {     // 显示密文
                mPrivacyDrawable!!.setBounds(offsetLeft - mPrivacyDrawableSize / 2,
                        paddingTop + mItemSize / 2 - mPrivacyDrawableSize / 2,
                        offsetLeft - mPrivacyDrawableSize / 2 + mPrivacyDrawableSize,
                        paddingTop + mItemSize / 2 + mPrivacyDrawableSize / 2)
                offsetLeft += mItemSize + mItemPadding
                mPrivacyDrawable!!.draw(canvas)
            } else {
                canvas!!.drawText(c, offsetLeft.toFloat(), mRect!!.height().toFloat() + (height - mRect!!.height()) / 2, mPaint)      // // 文字居中处理
                offsetLeft += mItemSize + mItemPadding
            }
        }
    }

    override fun onDetachedFromWindow() {
        hideSortInput()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed({
            if (this@PrivacyLockView != null) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this@PrivacyLockView, 0)
            }
        }, 100)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (hasWindowFocus) {
            postDelayed({
                if (this@PrivacyLockView != null) {
                    imm.showSoftInput(this@PrivacyLockView, 0)
                }
            }, 100)
        } else {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    private fun hideSortInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    interface OnTextSubmitListener {
        fun onSubmit(editable: CharSequence)
    }

    /*
    fun setEncrypt(boolean: Boolean) {
        this.mEncrypt = boolean
        invalidate()
    }

    fun setItemSize(i: Int) {
        this.mItemSize = i
    }

    fun setBorderColor(colorValue: Int) {
        this.mBorderColor = colorValue
        invalidate()
    }

     fun setPrivacyDrawableSize(i: Int) {}

    fun setPrivacyDrawable(drawable: Drawable?) {
        this.mPrivacyDrawable = drawable;
        invalidate()
    }

    fun setItemPadding(i: Int) {
        this.mItemPadding = i
        invalidate()
    }

    /**
     * item 个数
     */
    fun setItemCount(count: Int) {
        this.mItemCount = count
        invalidate()
    }*/
}