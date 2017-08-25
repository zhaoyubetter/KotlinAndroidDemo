package test.com.widget.nested.view

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*
import android.support.v4.view.ViewCompat.isNestedScrollingEnabled
import android.support.v4.view.ViewCompat.startNestedScroll
import android.support.v4.view.ViewCompat.stopNestedScroll
import android.support.v4.view.ViewCompat.hasNestedScrollingParent
import android.support.v4.view.ViewCompat.dispatchNestedScroll
import android.support.v4.view.ViewCompat.dispatchNestedPreFling
import android.support.v4.view.ViewCompat.dispatchNestedFling














/**
 * http://blog.csdn.net/dingding_android/article/details/52948379
 * Created by zhaoyu on 2017/8/17.
 */
class NestedChildView(context: Context, attrs: AttributeSet?, defAttrStyle: Int)
    : View(context, attrs, defAttrStyle), NestedScrollingChild {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val TAG = "NestedChildView"

    private var downY = 0f
    private val childHelper = NestedScrollingChildHelper(this)
    private val consumed = IntArray(2)
    private val offsetInWindow = IntArray(2)

    init {
        isNestedScrollingEnabled = true     // 允许嵌套滑动
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {        // 提醒父，开始了
                downY = event.y
                // 通知父View，开始滑动
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }

            MotionEvent.ACTION_MOVE -> {
                var dy = (event.y - downY).toInt()       // 偏移量
                Log.e(TAG, "before dispatchNestedPreScroll, dy=$dy")

                // 通知父View, 子View想滑动 dy 个偏移量，父View要不要先滑一下，然后把父View滑了多少，告诉子View一下
                // 下面这个方法的前两个参数为在x，y方向上想要滑动的偏移量
                // 第三个参数为一个长度为2的整型数组，父View将消费掉的距离放置在这个数组里面
                // 第四个参数为一个长度为2的整型数组，父View在屏幕里面的偏移量放置在这个数组里面
                // 返回值为 true，代表父View有消费任何的滑动.
                if (dispatchNestedPreScroll(0, dy, consumed, offsetInWindow)) {
                    dy -= consumed[1]   // 减去被父view消费的部分
                    Log.e(TAG, String.format("after dispatchNestedPreScroll , dy = %s", dy))
                }

                // 移动子View，控制边界 (不能越上，不能超低)
                y = Math.min(Math.max(y.toInt() + dy, 0), (parent as View).height - height).toFloat()
            }
        }
        return true
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        Log.d(TAG, String.format("dispatchNestedPreScroll , dx = %d, dy = %d, consumed = %s, offsetInWindow = %s", dx, dy, Arrays.toString(consumed), Arrays.toString(offsetInWindow)));
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        Log.e(TAG, "setNestedScrollingEnabled : $enabled")
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        Log.e(TAG, "startNestedScroll, axes=$axes")
        return childHelper.startNestedScroll(axes)
    }

    override fun isNestedScrollingEnabled(): Boolean {
        Log.d(TAG, "isNestedScrollingEnabled")
        return childHelper.isNestedScrollingEnabled
    }

    override fun stopNestedScroll() {
        Log.d(TAG, "stopNestedScroll")
        childHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        Log.d(TAG, "hasNestedScrollingParent")
        return childHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        val b = childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
        Log.d(TAG, String.format("dispatchNestedScroll , dxConsumed = %d, dyConsumed = %d, dxUnconsumed = %d, dyUnconsumed = %d, offsetInWindow = %s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, Arrays.toString(offsetInWindow)))
        return b
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d(TAG, String.format("dispatchNestedFling , velocityX = %f, velocityY = %f, consumed = %b", velocityX, velocityY, consumed))
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, String.format("dispatchNestedPreFling , velocityX = %f, velocityY = %f", velocityX, velocityY))
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

}