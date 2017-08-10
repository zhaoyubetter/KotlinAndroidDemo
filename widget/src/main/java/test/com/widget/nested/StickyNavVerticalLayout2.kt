package test.com.widget.nested

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.Scroller
import test.com.widget.R

class StickyNavVerticalLayout2(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : LinearLayout(context, attrs, defAttrStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    // --- 内部 View 相关成员变量
    private var top: View? = null
    private var nav: View? = null
    private var scrollView: View? = null
    private var topHeight: Int = 0     // top的高度

    // --- 事件操作相关的成员变量
    private var lastY: Int = 0
    private var isDrag = false      // 拖拽

    private var scroller: Scroller = Scroller(getContext())
    private var velocityTracker: VelocityTracker? = null
    private var touchSlop: Int = 0
    private var maxFlingVelocity: Int = 0
    private var minFlingVelocity: Int = 0


    init {
        val config = ViewConfiguration.get(context)
        touchSlop = config.scaledTouchSlop
        maxFlingVelocity = config.scaledMaximumFlingVelocity
        minFlingVelocity = config.scaledMinimumFlingVelocity
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        top = findViewById(R.id.id_stickynavlayout_topview)
        nav = findViewById(R.id.id_stickynavlayout_indicator)
        scrollView = findViewById(R.id.id_stickynavlayout_scrollview)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scrollView?.layoutParams?.height = measuredHeight.minus(nav?.measuredHeight ?: 0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topHeight = top?.measuredHeight ?: 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val y = it.y
            initVelocityTracker()
            velocityTracker?.let { it.addMovement(event) }     // 添加运动轨迹

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastY = y.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = y - lastY
                    if (!isDrag && Math.abs(dy) > touchSlop) {
                        isDrag = true
                    }
                    if (isDrag) {
                        scrollBy(0, -dy.toInt())    // 反向取反
                    }
                    lastY = y.toInt()
                }

                MotionEvent.ACTION_UP -> {          // 抬起，运动轨迹判断，是否fling
                    isDrag = false
                    velocityTracker?.let {
                        it.computeCurrentVelocity(1000, maxFlingVelocity?.toFloat())
                        if (Math.abs(it.yVelocity) > minFlingVelocity) {
                            fling(-it.yVelocity.toInt())
                        }
                    }
                    releaseVelocity()
                }

                MotionEvent.ACTION_CANCEL -> {
                    isDrag = false
                    releaseVelocity()
                }
            }
        }
        return true
        //return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
        //return super.onInterceptTouchEvent(ev)
    }

    /**
     * 边界处理
     */
//    override fun scrollTo(x: Int, y: Int) {
//        var tmpY = y
//        if (y < 0) tmpY = 0
//        if (y > topHeight) tmpY = topHeight
//        super.scrollTo(x, tmpY)
//    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.currY)
            invalidate()
        }
    }

    private inline fun fling(velocityY: Int) {
        scroller.let {
            it.fling(0, scrollY, 0, velocityY, 0, 0, 0, topHeight)     // 滑翔
            invalidate()
        }
    }

    private inline fun initVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    private inline fun releaseVelocity() {
        velocityTracker?.let {
            it.recycle()
            velocityTracker = null
        }
    }
}