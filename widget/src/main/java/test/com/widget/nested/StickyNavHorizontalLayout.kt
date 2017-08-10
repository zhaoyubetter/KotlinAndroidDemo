package test.com.widget.nested

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
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
    private var topViewHeight: Int = 0
    private var lastX: Float = 0f
    private var isDrag = false      // 是否拖拽

    // --- 配置相关
    private var touchSlop: Int = 0

    init {
        orientation = HORIZONTAL
        val config = ViewConfiguration.get(context)
        touchSlop = config.scaledTouchSlop
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
            scrollView?.layoutParams?.height = this@StickyNavHorizontalLayout.measuredHeight.minus(navView?.measuredHeight ?: 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topViewHeight = topView?.measuredHeight ?: 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val x = it.x
            when (it.action) {
                MotionEvent.ACTION_DOWN -> lastX = x
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
                }
                MotionEvent.ACTION_CANCEL -> {
                    isDrag = false
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

}