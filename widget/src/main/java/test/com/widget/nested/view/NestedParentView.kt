package test.com.widget.nested.view

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.NestedScrollingParentHelper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import java.util.*
import android.support.v4.view.ViewParentCompat.onStopNestedScroll
import android.support.v4.view.ViewGroupCompat.getNestedScrollAxes


/**
 * http://blog.csdn.net/dingding_android/article/details/52948379
 * Created by zhaoyu on 2017/8/18.
 */
class NestedParentView(context: Context, attrs: AttributeSet?, defAttrStyle: Int)
    : FrameLayout(context, attrs, defAttrStyle), NestedScrollingParent {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    val TAG = "NestedParentView"
    private val parentHelper = NestedScrollingParentHelper(this)

    init {
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        Log.d(TAG, String.format("onNestedPreScroll, dx = %d, dy = %d, consumed = %s", dx, dy, Arrays.toString(consumed)));

        val shouldMoveY = y + dy
        val parent = parent as View     // 父 view
        var consumeY = dy

        // 滑到顶了，只消费子View到父View上边的距离
        if (shouldMoveY <= 0) {
            consumeY = -y.toInt()
        } else if (shouldMoveY >= parent.height - height) { // 滑到底了
            consumeY = (parent.height - height - y).toInt()
        } else {    // 其他情况全部消费
            consumeY = dy
        }

        y += consumeY               // 自己移动
        consumed[1] = consumeY      // 消费掉的放入数组
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        Log.d(TAG, String.format("onStartNestedScroll, child = %s, target = %s, nestedScrollAxes = %d",
                child, target, nestedScrollAxes))
        return true
    }

    override fun onNestedScrollAccepted(child: View?, target: View?, axes: Int) {
        Log.d(TAG, String.format("onNestedScrollAccepted, child = %s, target = %s, nestedScrollAxes = %d",
                child, target, nestedScrollAxes))
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes)
    }

    override fun onStopNestedScroll(target: View) {
        Log.d(TAG, "onStopNestedScroll")
        parentHelper.onStopNestedScroll(target)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        Log.d(TAG, String.format("onNestedScroll, dxConsumed = %d, dyConsumed = %d, dxUnconsumed = %d, dyUnconsumed = %d", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed))
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d(TAG, String.format("onNestedFling, velocityX = %f, velocityY = %f, consumed = %b", velocityX, velocityY, consumed))
        return true
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, String.format("onNestedPreFling, velocityX = %f, velocityY = %f", velocityX, velocityY))
        return true
    }

    override fun getNestedScrollAxes(): Int {
        Log.d(TAG, "getNestedScrollAxes")
        return parentHelper.nestedScrollAxes
    }
}