package test.com.widget.nested

import android.content.Context
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
                    lastX = x
                }
                MotionEvent.ACTION_UP -> {
                    isDrag = false
                    velocityTracker?.let {
                        it.computeCurrentVelocity(1000, maxVelocity.toFloat())
                        Log.e("better", "min: ${minVelocity}, current:${it.xVelocity}")
                        if (Math.abs(it.xVelocity) > minVelocity) {  // 加速度
                            flingX(-it.xVelocity)
                        }
                    }
                    releaseVelocity()
                }
                MotionEvent.ACTION_CANCEL -> {
                    isDrag = false
                    if(!scroller.isFinished)  scroller.abortAnimation()
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

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, 0)
            invalidate()
        }
    }

    private inline fun flingX(velocityX: Float) {
        // int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY
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