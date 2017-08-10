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

/**
 * 嵌套滑动，参考zhy
 * Created by zhaoyu on 2017/8/9.
 */
class StickyNavVerticalLayout(context: Context, attrs: AttributeSet? , defAttrStyle: Int) : LinearLayout(context, attrs, defAttrStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    // --- 内部view
    private var top: View? = null
    private var nav: View? = null
    private var scrollView: View? = null
    private var topHeight: Int? = 0
    private var topHide = false

    // --- view事件操作相关
    private var touchSlop = 0       // 是否移动
    private var isDrag = false      // 是否拖拽
    private var velocityTracker: VelocityTracker? = null
    private var scroller: Scroller? = null
    private var maxFlingVelocity: Int? = 0
    private var minFlingVelocity: Int? = 0
    private var isInControl = false

    private var lastY = 0f

    init {
        val config = ViewConfiguration.get(context)
        touchSlop = config.scaledTouchSlop
        scroller = Scroller(context)
        maxFlingVelocity = config.scaledMaximumFlingVelocity
        minFlingVelocity = config.scaledMinimumFlingVelocity
        orientation = VERTICAL
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        top = findViewById(R.id.id_stickynavlayout_topview)
        nav = findViewById(R.id.id_stickynavlayout_indicator)
        scrollView = findViewById(R.id.id_stickynavlayout_scrollview)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 设置 scrollView的高
        scrollView?.let {
            val lp = it.layoutParams
            lp.height = this@StickyNavVerticalLayout.measuredHeight.minus(nav?.height ?: 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topHeight = top?.measuredHeight
    }

    override fun scrollTo(x: Int, y: Int) {
        var tmp = y
        if (tmp < 0) {
            tmp = 0
        }
        if (tmp > topHeight ?: 0) {
            tmp = topHeight ?: 0
        }
        super.scrollTo(x, tmp)
        topHide = scrollY == topHeight
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            initVelocityTrackerIfNotExists()
            velocityTracker?.addMovement(event)

            val y = it.y

            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastY = y
                    scroller?.let {
                        if (!it.isFinished) {
                            it.abortAnimation()
                            return true
                        }
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val dy = y - lastY
                    Log.e("TAG", "dy = $dy , y = $y , mLastY = $lastY")

                    if (!isDrag && Math.abs(dy) > touchSlop) {
                        isDrag = true
                    }
                    if (isDrag) {
                        scrollBy(0, -dy.toInt())
                    }
                    // 如果滑到顶了，将事件转换成点击事情，发送
                    if (scrollY == topHeight && dy < 0) {
                        event.action = MotionEvent.ACTION_DOWN
                        dispatchTouchEvent(event)
                    }
                    lastY = y
                }
                MotionEvent.ACTION_CANCEL -> {
                    isDrag = false
                    releaseVelocity()
                    scroller?.let { if (!it.isFinished) it.abortAnimation() }
                }
                MotionEvent.ACTION_UP -> {
                    isDrag = false
                    velocityTracker?.let {
                        it.computeCurrentVelocity(1000, maxFlingVelocity?.toFloat() ?: 0f)
                        if (Math.abs(it.yVelocity) > minFlingVelocity?.toFloat() ?: 0f) {
                            fling(-it.yVelocity.toInt())
                        }
                    }
                    releaseVelocity()
                }
                else -> {
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> lastY = y
                MotionEvent.ACTION_MOVE -> {
                    val dy = it.y - lastY
                    // 头不可见，继续下拉，重发事件
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
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 拦
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastY = ev.y
                }

                MotionEvent.ACTION_MOVE -> {
                    scroller?.let {
                        // 惯性还未结束，拦截事件
                        if (!it.isFinished) return true
                    }

                    val dy = ev.y - lastY
                    if (Math.abs(dy) > touchSlop) {
                        isDrag = true
                        scrollView?.let {
                                // top不隐藏 或者 (top 隐藏 && 下拉) firstView.getTop == 0
                                if (!topHide || (topHide && !ViewCompat.canScrollVertically(scrollView, -1) && dy > 0)) {
                                    initVelocityTrackerIfNotExists()
                                    velocityTracker?.addMovement(ev)
                                    lastY = ev.y
                                    return true
                                }
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    releaseVelocity()
                    isDrag = false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }


    override fun computeScroll() {
        scroller?.let {
            if (it.computeScrollOffset()) {
                scrollTo(0, it.currY)
                invalidate()
            }
        }
    }

    private inline fun fling(velocityY: Int) {
        scroller?.let {
            it.fling(0, scrollY, 0, velocityY, 0, 0, 0, topHeight ?: 0)
            invalidate()
        }
    }

    private inline fun initVelocityTrackerIfNotExists() {
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