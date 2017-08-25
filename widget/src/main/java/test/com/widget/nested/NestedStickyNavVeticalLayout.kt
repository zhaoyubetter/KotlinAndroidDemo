package test.com.widget.nested

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.Scroller
import test.com.widget.R

/**
 * 使用嵌套滑动 api
 * Created by zhaoyu on 2017/8/15.
 */
class NestedStickyNavVeticalLayout(context: Context, attrs: AttributeSet?, defAttrStyle: Int)
    : LinearLayout(context, attrs, defAttrStyle), NestedScrollingParent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var topView: View? = null
    private var navView: View? = null
    private var scrollView: View? = null
    private var topHeight: Int = 0
    private var scroller: Scroller = Scroller(getContext(), AccelerateInterpolator())

    init {
        orientation = VERTICAL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        topView = findViewById(R.id.id_stickynavlayout_topview)
        navView = findViewById(R.id.id_stickynavlayout_indicator)
        scrollView = findViewById(R.id.id_stickynavlayout_scrollview)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        scrollView?.layoutParams?.height = measuredHeight.minus(navView?.measuredHeight ?: 0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topHeight = topView?.measuredHeight ?: 0
    }

    override fun computeScroll() {
        scroller?.let {
            if (it.computeScrollOffset()) {
                scrollTo(0, it.currY)
                invalidate()
            }
        }
    }
    // ------------------------ 嵌套滑动实现接口 ------------------------
    // ------------------------ 嵌套滑动实现接口 ------------------------

    /**
     * 边界处理
     */
    override fun scrollTo(x: Int, y: Int) {
        var tmpY = y
        if (y < 0) tmpY = 0
        if (y > topHeight) tmpY = topHeight
        super.scrollTo(x, tmpY)
    }

    val TAG = "better"
    /**
     * 是否开启嵌套滚动流程
     */
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        Log.e(TAG, "onStartNestedScroll");
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL !== 0
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        Log.e(TAG, "onNestedPreScroll, scrollY:$scrollY view: ${target.id}, dx: $dx , dy: $dy , consume:${consumed}")

        val toHiddenTop = dy > 0 && scrollY < topHeight     // to隐藏top
        // to显示top
        val toShowTop = dy < 0 && scrollY > 0 && !ViewCompat.canScrollVertically(target, -1)

        Log.e(TAG, "toShowtop:$toShowTop")

        if (toHiddenTop || toShowTop) {     // 直接消耗
            scrollBy(0, dy)
            consumed[1] = dy
        } // 其他情况不消耗
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.e(TAG, "onNestedPreFling, velocityY: ${velocityY}")
        if (scrollY >= topHeight) return false
        fling(velocityY.toInt())
        return true
    }

    private inline fun fling(velocityY: Int) {
        scroller.let {
            it.fling(0, scrollY, 0, velocityY, 0, 0, 0, topHeight)
            invalidate()
        }
    }
}