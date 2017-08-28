package test.com.widget.nested.view

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.util.Log
import android.view.*

/**
 * 嵌套滑动，类似jd的商品详情页
 * Created by zhaoyu on 2017/8/27.
 */
class SlidingUpLayout(context: Context, attrs: AttributeSet?, defAttrStyle: Int) :
        ViewGroup(context, attrs, defAttrStyle), NestedScrollingParent {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    companion object {
        val TWO_CHILD_COUNT = 2
        val THREE_CHILD_COUNT = 3
        val TAG = "SlidingUpLayout"
        val DEBUG = true
    }


    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    private var velocityTracker: VelocityTracker? = null
    private val scroller = ScrollerCompat.create(context)
    private val maxFlingVelocity = viewConfig.scaledMaximumFlingVelocity
    private val minFlingVelocity = viewConfig.scaledMinimumFlingVelocity

    private var scrollDuration = 800
    private val resistance = 1.8f       // 阻力系数
    private var minScrollDistance = 100 // 最小滑动距离

    private var isNestedPreScroll = false //此标记标记nested内为拖动事件


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount != TWO_CHILD_COUNT && THREE_CHILD_COUNT != childCount) {
            throw IllegalArgumentException("Error child count! must two or three child count")
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var layoutTop = paddingTop
        (0..childCount - 1).map { getChildAt(it) }.forEach {
            it.layout(l, layoutTop, r, layoutTop + it.measuredHeight)
            layoutTop += it.measuredHeight
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

    /**
     * 打开或关闭当前布局体
     * 展开上边
     * @see Gravity.TOP
     * @see Gravity.START
     * 展开底部
     * @see Gravity.BOTTOM
     * @see Gravity.END
     */
    fun setLayoutShadow(gravity: Int) {
        if (gravity == Gravity.BOTTOM || gravity == Gravity.END) {
            val bottomLayout = getChildAt(childCount - 1)
            scroller.startScroll(scrollX, scrollY, 0, bottomLayout.top - scrollY, scrollDuration)
        } else if (gravity == Gravity.TOP || gravity == Gravity.START) {
            scroller.startScroll(scrollX, scrollY, 0, -scrollY, scrollDuration)
        }
        invalidate()
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            invalidate()
        }
    }

    // -------------------- 嵌套滑动 -----------------------

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return 0 != (ViewCompat.SCROLL_AXIS_VERTICAL and nestedScrollAxes)   // 开启
    }

    /**
     * target 会发生变化，要么是topLayout，要么是bottomLayout
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val topLayout = getChildAt(0)
        val bottomLayout = getChildAt(childCount - 1)

        if (null != topLayout.findViewById(target.id)) {
            if (dy > 0 && !ViewCompat.canScrollVertically(target, dy)) {    // （上拉）
                consumed[1] = (dy / resistance).toInt()
                scrollBy(dx, consumed[1])
            } else if (dy < 0 && scrollY > 0) {         // 下拉 && SlidingUpLayout整体 往上偏移时） 消费dy
                consumed[1] = dy
                scrollBy(dx, (consumed[1] / resistance).toInt())
            }
        } else if (null != bottomLayout.findViewById(target.id)) {
            // （下拉） or （bottomLayout没有完整显示时） 拦截
            if ((dy < 0 && !ViewCompat.canScrollVertically(target, dy)) ||
                    bottomLayout.top > scrollY) {
                consumed[1] = dy
                var dy = (dy / resistance).toInt()
                if (scrollY + dy > bottomLayout.top) {  // 不能越界
                    dy = bottomLayout.top - scrollY
                }
                scrollBy(dx, dy)
            }
        }
        isNestedPreScroll = true
    }

    override fun onStopNestedScroll(target: View) {
        if (DEBUG) Log.e(TAG, "onStopNestedScroll")
        if (isNestedPreScroll) {
            val topLayout = getChildAt(0)
            val bottomLayout = getChildAt(childCount - 1)
            if (null != topLayout.findViewById(target.id)) {
                if (scrollY > minScrollDistance) {
                    setLayoutShadow(Gravity.END)        // 显示bottom
                } else {
                    setLayoutShadow(Gravity.START)
                }
            } else if (null != bottomLayout.findViewById(target.id)) {
                if (scrollY + minScrollDistance < bottomLayout.top) {
                    setLayoutShadow(Gravity.START)
                } else {
                    setLayoutShadow(Gravity.END)       // 显示bottom
                }
            }
        }
        isNestedPreScroll = false
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {}
    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {}
    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun getNestedScrollAxes(): Int = ViewCompat.SCROLL_AXIS_VERTICAL


}