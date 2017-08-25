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


/**
 * http://blog.csdn.net/dingding_android/article/details/52948379
 * Created by zhaoyu on 2017/8/17.
 */
class NestedChildView(context: Context, attrs: AttributeSet?, defAttrStyle: Int)
    : View(context, attrs, defAttrStyle), NestedScrollingChild {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val TAG = "NestedChildView"
    private val childHelper = NestedScrollingChildHelper(this)

    private var downY: Float = 0F
    private var consumed: IntArray = kotlin.IntArray(2)
    private var offsetInWindow = kotlin.IntArray(2)

    init {
        isNestedScrollingEnabled = true
    }

    /**
     * 这个方法通过pointerId获取pointerIndex,然后获取Y
     */
    private fun getPointerY(event: MotionEvent, pointerId: Int): Float {
        val pointerIndex = MotionEventCompat.findPointerIndex(event, pointerId)
        if (pointerIndex < 0) {
            return -1f
        }
        return MotionEventCompat.getY(event, pointerIndex)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val masked = MotionEventCompat.getActionMasked(event)
        val pointerId = MotionEventCompat.getPointerId(event, 0)    // 第一个手指id
        when (masked) {
            MotionEvent.ACTION_DOWN -> {
                downY = getPointerY(event, pointerId)
                if (downY == -1f) return false

                // 通知父view，开始滑动
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerY = getPointerY(event, pointerId)
                if (downY == -1f) return false
                var dy = pointerY - downY
                Log.e(TAG, "downY=${downY}, pY=${pointerY}")
                Log.e(TAG, "before dispatchNestedPreScroll, dy = $dy")

                // 通知父View, 子View想滑动 dy 个偏移量，父View要不要先滑一下，然后把父View滑了多少，告诉子View一下
                // 下面这个方法的前两个参数为在x，y方向上想要滑动的偏移量
                // 第三个参数为一个长度为2的整型数组，父View将消费掉的距离放置在这个数组里面
                // 第四个参数为一个长度为2的整型数组，父View在屏幕里面的偏移量放置在这个数组里面
                // 返回值为 true，代表父View有消费任何的滑动.
                if (dispatchNestedPreScroll(0, (dy).toInt(), consumed, offsetInWindow)) {     // 询问parent是否需要滑动
                    dy -= consumed[1] // 偏移量需要减掉被父View消费掉的
                    Log.e(TAG, "after dispatchNestedPreScroll, dy = $dy")
                }

//                Log.e(TAG, "floor: ${dy.toDouble()} --> ${Math.floor(Math.abs(dy.toDouble()))}")
                if (Math.floor(Math.abs(dy.toDouble())).toInt() == 0) {
                    dy = 0f
                }
                // 这里移动子View，下面的min,max是为了控制边界，避免子View越界 (不能越过父top，不能超父bottom)
                y = Math.min(Math.max(y + dy, 0f), ((parent as View).height - height).toFloat())

                // ====》setY后 ，pointerY 会跟着改变，所以不要更新downY了
            }

        }

        return true
    }


    override fun setNestedScrollingEnabled(enabled: Boolean) {
        Log.e(TAG, String.format("setNestedScrollingEnabled , enabled = %b", enabled))
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        Log.e(TAG, "isNestedScrollingEnabled : ${childHelper.isNestedScrollingEnabled}")
        return childHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        Log.e(TAG, String.format("startNestedScroll , axes = %d", axes))
        return childHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        Log.e(TAG, "stopNestedScroll")
        childHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        Log.e(TAG, "hasNestedScrollingParent")
        return childHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        val b = childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
        Log.e(TAG, String.format("dispatchNestedScroll , dxConsumed = %d, dyConsumed = %d, dxUnconsumed = %d, dyUnconsumed = %d, offsetInWindow = %s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, Arrays.toString(offsetInWindow)))
        return b
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        val b = childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
        Log.d(TAG, String.format("dispatchNestedPreScroll , dx = %d, dy = %d, consumed = %s, offsetInWindow = %s", dx, dy, Arrays.toString(consumed), Arrays.toString(offsetInWindow)))
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