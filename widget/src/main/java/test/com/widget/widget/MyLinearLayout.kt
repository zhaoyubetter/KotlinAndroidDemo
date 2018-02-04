package test.com.widget.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import test.com.widget.R


/**
 * 自定义LinearLayout
 * Created by zhaoyu on 2018/1/20.
 * 1. 实现 vertical layout
 * 2. 实现 horizontail layout 2018/2/3
 * 已知问题：
 * 1. 垂直布局：gravity:center 失效问题，totalLength
 *
 * 有困惑的地方：
 * 1. measureChildWithMargins 与 measureChild 什么时候用；
 *
 */
class MyLinearLayout(context: Context, attrs: AttributeSet?, defAttrStyle: Int, defStyleRes: Int) : ViewGroup(context, attrs, defAttrStyle, defStyleRes) {

    // ===== companion object
    companion object {
        // orientation
        val HORIZONTAL = 0
        val VERTICAL = 1

        // gravity
        val LEFT = 3
        val TOP = 48
        val RIGHT = 5
        val BOTTOM = 80

        val CENTER_VERTICAL = 16
        val FILL_VERTICAL = 112          // 0111 0000  (112)
        val CENTER_HORIZONTAL = 1        // 0000 0001  (1)
        val FILL_HORIZONTAL = 7          // 0000 0111  (7)
        val CENTER = 17                  // 0001 0001  (17)
    }

    // ===== constructor
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : this(context, attrs, defAttrStyle, 0)

    // ===== member
    /** vertical or horizontal sum of children size */
    private var totalLength: Int = 0
    private var orientation: Int = HORIZONTAL
    private var gravity: Int = Gravity.LEFT + Gravity.TOP


    // ===== init
    init {
        context.obtainStyledAttributes(attrs, R.styleable.MyLinearLayout, defAttrStyle, defStyleRes).apply {
            setOrientation(getInt(R.styleable.MyLinearLayout_orientation, HORIZONTAL))
            setGravity(getInt(R.styleable.MyLinearLayout_gravity, Gravity.LEFT or Gravity.TOP))
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (orientation == HORIZONTAL) {
            horizontalMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            verticalMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private inline fun horizontalMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        totalLength = 0
        var maxHeight = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // === measureChildWithMargins
        (0 until childCount).map { getChildAt(it) }.filter { it != null && it.visibility != View.GONE }.forEach { it ->
            // [better]  困惑1
            measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = it.layoutParams as LayoutParams
            totalLength += it.measuredWidth + lp.leftMargin + lp.rightMargin
            maxHeight = Math.max(maxHeight, it.measuredHeight + lp.topMargin + lp.bottomMargin)
        }

        var resultWidth = widthSize
        when (widthMode) {
            MeasureSpec.EXACTLY -> resultWidth = widthSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                totalLength += paddingLeft + paddingRight
                totalLength = Math.max(totalLength, suggestedMinimumWidth)
                resultWidth = totalLength
            }
        }

        when (heightMode) {
            MeasureSpec.EXACTLY -> maxHeight = heightSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                maxHeight += paddingTop + paddingBottom
                maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
            }
        }

        setMeasuredDimension(resolveSize(resultWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec))
    }

    private inline fun verticalMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        totalLength = 0
        var maxWidth = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        (0 until childCount).map { getChildAt(it) }.filter { it != null && it.visibility != View.GONE }.forEach { it ->
            measureChildWithMargins(it, widthMeasureSpec, totalLength, heightMeasureSpec, 0)
            val lp = it.layoutParams as LayoutParams
            totalLength += it.measuredHeight + lp.topMargin + lp.bottomMargin
            maxWidth = Math.max(maxWidth, it.measuredWidth + lp.leftMargin + lp.rightMargin)
        }

        var resultHeight = heightSize
        when (heightMode) {
            MeasureSpec.EXACTLY -> resultHeight = heightSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                totalLength += paddingBottom + paddingTop
                totalLength = Math.max(totalLength, suggestedMinimumHeight)
                resultHeight = totalLength
            }
        }

        when (widthMode) {
            MeasureSpec.EXACTLY -> maxWidth = widthSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                maxWidth += paddingLeft + paddingRight
                maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
            }
        }

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(resultHeight, heightMeasureSpec))
    }


    /**
     * set orientation
     */
    fun setOrientation(orientation: Int) {
        if (this.orientation != orientation) {
            this.orientation = orientation
            requestLayout()
        }
    }

    fun setGravity(gravity: Int) {
        if (gravity != this.gravity) {
            this.gravity = gravity
            requestLayout()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (orientation == VERTICAL) {
            layoutVertical(l, t, r, b)
        } else {
            layoutHorizontal(l, t, r, b)
        }
    }

    private inline fun layoutHorizontal(l: Int, t: Int, r: Int, b: Int) {
        var childLeft: Int
        var childTop: Int
        val width = r - l
        var rect: Rect

        /* -----------------------------------------------------------------------------------
           总体逻辑：与 vertical 布局正好相反
           1. 先确定自己在父容器的位置,从 (左.右.水平居中）3者中，确定一种，childLeft
           2. 再确定其孩子的位置，从（上.下.垂直居中）3者中，确定一种，对应childTop
         ------------------------------------------------------------------------------------
        */
        val majorGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
        var minorGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        // ==== 1. 父位置 左，右，水平居中
        when (majorGravity) {
            RIGHT -> childLeft = width - totalLength + paddingLeft
            CENTER_HORIZONTAL -> childLeft = (width - totalLength) / 2 + paddingLeft
            else -> childLeft = paddingLeft
        }

        (0 until childCount).map { getChildAt(it) }.filter { it != null && it.visibility != View.GONE }.forEach { it ->
            var lp: LayoutParams = it.layoutParams as LayoutParams
            rect = Rect()

            // 设置默认子组件的位置属性
            var childGravity = if (lp.gravity < 0) minorGravity else lp.gravity
            // ==== 2. 确定子view的位置（上.下.垂直居中）
            when (childGravity and Gravity.VERTICAL_GRAVITY_MASK) {
                BOTTOM -> childTop = b - t + paddingBottom + lp.bottomMargin
                CENTER_VERTICAL -> childTop = paddingTop +
                        (b - t - paddingLeft - paddingBottom - it.measuredHeight) / 2 + lp.topMargin - lp.bottomMargin
                else -> childTop = paddingTop + lp.topMargin
            }

            rect.left = childLeft + lp.leftMargin
            rect.top = childTop + lp.topMargin
            rect.right = rect.left + it.measuredWidth
            rect.bottom = rect.top + it.measuredHeight

            it.layout(rect.left, rect.top, rect.right, rect.bottom)
            childLeft += lp.leftMargin + it.measuredWidth + lp.rightMargin
        }
    }

    /**
     *  vertical layout
     *  -----------------------------------------------------------------------------------
    总体逻辑：
    1. 先确定自己在父容器的位置,从（上.下.垂直居中）3者中，确定一种，对应childTop
    2. 再确定其孩子的位置，从(左.右.水平居中）3者中，确定一种，对应childLeft
    ------------------------------------------------------------------------------------
     */
    private inline fun layoutVertical(l: Int, t: Int, r: Int, b: Int) {
        var childTop: Int
        var childLeft: Int
        val width = r - l

        val majorGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
        var minorGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK

        // ==== 1. 确定自己的位置 (上.下.垂直居中)
        when (majorGravity) {
            BOTTOM -> childTop = b - t + paddingTop - totalLength
            CENTER_VERTICAL -> childTop = (b - t - totalLength) / 2 + paddingTop
            else -> childTop = paddingTop
        }

        (0 until childCount).map { getChildAt(it) }.filter { it != null && it.visibility != View.GONE }.forEach { it ->
            val lp = it.layoutParams as LayoutParams
            var rect = Rect()

            // 设置默认子组件的位置属性
            var childGravity = if (lp.gravity < 0) minorGravity else lp.gravity

            // ==== 2. 确定子view的位置（左.右.水平居中）
            when (childGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                RIGHT ->  childLeft = width - paddingRight - it.measuredWidth - lp.rightMargin
                CENTER_HORIZONTAL -> {childLeft = paddingLeft + (width - paddingLeft - paddingRight - it.measuredWidth) / 2
                                                + lp.leftMargin - lp.rightMargin}
                else -> childLeft = paddingLeft + lp.leftMargin
            }

            rect.left = childLeft
            rect.top = childTop + lp.topMargin
            rect.right = rect.left + it.measuredWidth
            rect.bottom = rect.top + it.measuredHeight
            it.layout(rect.left, rect.top, rect.right, rect.bottom)
            childTop += lp.topMargin + it.measuredHeight + lp.bottomMargin
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    // ====== LayoutParams
    class LayoutParams(context: Context, attrs: AttributeSet) : ViewGroup.MarginLayoutParams(context, attrs) {

        // layout_gravity
        var gravity: Int = -1

        init {
            context.obtainStyledAttributes(attrs, R.styleable.MyLinearLayout).apply {
                gravity = (getInt(R.styleable.MyLinearLayout_layout_gravity, -1))
                recycle()
            }
        }
    }

}