package test.com.widget.nested

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.Scroller
import test.com.widget.R

/**
 * 水平嵌套滑動
 * Created by zhaoyu1 on 2017/8/10.
 */
class StickyNavHorizontalLayout(context: Context, attrs: AttributeSet?, defAttrStyle: Int) : LinearLayout(context, attrs, defAttrStyle) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    // --- view 相关
    private var topView: View? = null
    private var navView: View? = null
    private var scrollView: View? = null

    // --- 操作相关
    private var topViewWidth: Int = 0
    private var lastX: Float = 0f
    private var isDrag = false      // 是否拖拽
    private var velocityTracker: VelocityTracker? = null
    private var scroller: Scroller
    private var topHide = false
    private var isInControl = false

    // --- 配置相关
    private var touchSlop: Int = 0
    private var maxVelocity: Int = 0
    private var minVelocity: Int = 0

    init {
        orientation = HORIZONTAL
        val config = ViewConfiguration.get(context)
        touchSlop = config.scaledTouchSlop
        maxVelocity = config.scaledMaximumFlingVelocity
        minVelocity = config.scaledMinimumFlingVelocity
        scroller = Scroller(context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        topView = findViewById(R.id.id_stickynavlayout_topview)
        navView = findViewById(R.id.id_stickynavlayout_indicator)
        scrollView = findViewById(R.id.id_stickynavlayout_scrollview)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scrollView?.let {
            scrollView?.layoutParams?.width = this@StickyNavHorizontalLayout.measuredWidth.minus(navView?.width ?: 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topViewWidth = topView?.measuredWidth ?: 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            initVelocityTracker()
            velocityTracker?.addMovement(event)

            val x = it.x
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = x
                    if (!scroller.isFinished) {
                        scroller.abortAnimation()
                        return true
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = x - lastX
                    if (!isDrag && Math.abs(dx) > touchSlop) isDrag = true
                    if (isDrag) {
                        scrollBy(-dx.toInt(), 0)
                    }

                    // 如果滑到顶了，将事件转换成点击事情，发送
                    if (scrollX == topViewWidth) {        // dx<0可以不要
                        event.action = MotionEvent.ACTION_DOWN
                        dispatchTouchEvent(event)
                        isInControl = false
                    }
                    lastX = x
                }
                MotionEvent.ACTION_UP -> {
                    isDrag = false
                    velocityTracker?.let {
                        it.computeCurrentVelocity(1000, maxVelocity.toFloat())
                        if (Math.abs(it.xVelocity) > minVelocity) {  // 加速度
                            flingX(-it.xVelocity)
                        }
                    }
                    releaseVelocity()
                }
                MotionEvent.ACTION_CANCEL -> {
                    isDrag = false
                    if (!scroller.isFinished) scroller.abortAnimation()
                    releaseVelocity()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> lastX = it.x
                MotionEvent.ACTION_MOVE -> {
                    val dx = it.x - lastX
                    // 头不可见，继续右拉，重发事件 (实现嵌套滑动)
                    if (topHide && !ViewCompat.canScrollHorizontally(scrollView, -1) && dx > 0 && !isInControl) {
                        isInControl = true
                        ev.action = MotionEvent.ACTION_CANCEL
                        val ev2 = MotionEvent.obtain(ev)
                        ev2.action = MotionEvent.ACTION_DOWN
                        dispatchTouchEvent(ev)
                        return dispatchTouchEvent(ev2)
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    /**
     * 拦截
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            var x = ev.x
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = x
                }
                MotionEvent.ACTION_MOVE -> {
                    scroller.let { if (!it.isFinished) return true }  // 惯性还未结束，拦截事件

                    val dx = x - lastX
                    if (Math.abs(dx) > touchSlop) {
                        isDrag = true
                        // topView可见 || (topView不可见 && 右拉到头 && 右拉)
                        if (!topHide || (topHide && !ViewCompat.canScrollHorizontally(scrollView, -1) && dx > 0)) {
                            initVelocityTracker()
                            velocityTracker?.addMovement(ev)
                            lastX = x       // 拦截后赋值
                            return true
                        }
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDrag = false
                    releaseVelocity()
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, 0)
            invalidate()
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        var temp = x
        if (x < 0) temp = 0
        if (x > topViewWidth) temp = topViewWidth
        super.scrollTo(temp, y)
        topHide = topViewWidth == scrollX
    }

    private inline fun flingX(velocityX: Float) {
        scroller.fling(scrollX, 0, velocityX.toInt(), 0, 0, topViewWidth, 0, 0)
        invalidate()
    }

    private inline fun initVelocityTracker() {
        if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
    }

    private inline fun releaseVelocity() {
        velocityTracker?.let {
            it.recycle()
            velocityTracker = null
        }
    }
}