package test.com.widget.nested

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
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

    private var topHide = false
    private var isInControl = false


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
                    if(!scroller.isFinished) {
                        scroller.abortAnimation()
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = y - lastY
                    if (!isDrag && Math.abs(dy) > touchSlop) {
                        isDrag = true
                    }
                    if (isDrag) {
                        scrollBy(0, -dy.toInt())    // 反向取反
                    }
                    // 如果滑到顶了，将事件转换成点击事情，发送
                    if (scrollY == topHeight) {
                        Log.e("better", "topHide: ${topHide}")
                        event.action = MotionEvent.ACTION_DOWN
                        dispatchTouchEvent(event)
                        isInControl = false
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
                    if(!scroller.isFinished) {
                        scroller.abortAnimation()
                    }
                    releaseVelocity()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> lastY = ev.y.toInt()
            MotionEvent.ACTION_MOVE -> {    // 进行判断，是否重发事件
                val dy = ev.y - lastY
                Log.e("TAG", "canScrollVertically: ${!ViewCompat.canScrollVertically(scrollView, -1)}")
                // 头不可见，scrollView 到上边界 && 继续下拉，重发事件
                if (topHide && !ViewCompat.canScrollVertically(scrollView, -1) && dy > 0 && !isInControl) {
                    isInControl = true
                    ev.action = MotionEvent.ACTION_CANCEL
                    val ev2 = MotionEvent.obtain(ev)
                    dispatchTouchEvent(ev)
                    ev2.action = MotionEvent.ACTION_DOWN
                    return dispatchTouchEvent(ev2)
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     * 拦截判断
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> lastY = y.toInt()
            MotionEvent.ACTION_MOVE -> {        // 重点
                // 惯性未结束，拦截事件
                if(!scroller.isFinished) {
                    return true
                }

                val dy = y - lastY
                if (Math.abs(dy) > touchSlop) {
                    // topView 可见 || (topView不可见 && scrollView不能再下拉 && 继续下拉)
                    if (!topHide || (topHide && !ViewCompat.canScrollVertically(scrollView, -1) && dy > 0)) {
                        lastY = y.toInt()
                        isDrag = true
                        initVelocityTracker()
                        velocityTracker?.let {
                            it.addMovement(ev)
                        }
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDrag = false
                releaseVelocity()
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    /**
     * 边界处理
     */
    override fun scrollTo(x: Int, y: Int) {
        var tmpY = y
        if (y < 0) tmpY = 0
        if (y > topHeight) tmpY = topHeight
        super.scrollTo(x, tmpY)
        topHide = scrollY == topHeight
    }

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