package test.com.widget.ui.linear

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import kotlinx.android.synthetic.main.widget__activity_linearlayout.*
import org.jetbrains.anko.find
import test.com.widget.R
import test.com.widget.widget.MyLinearLayout

class MyLinearLayoutDemoActivity : AppCompatActivity() {

    private lateinit var linearyLayout: MyLinearLayout
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_my_linear_layout_demo)
        linearyLayout = find<MyLinearLayout>(R.id.linear)

        // 添加
        add.setOnClickListener {
            Button(MyLinearLayoutDemoActivity@this).let {
                it.text = "" + ++index
                linearyLayout.addView(it)
            }
        }

        // 方向设置
        orientation.setOnCheckedListener { _, position, isChecked ->
            if (isChecked) {
                linearyLayout.setOrientation(if (position == 0) MyLinearLayout.HORIZONTAL else MyLinearLayout.VERTICAL)
            }
        }

        // 方位设置
        horizontal.setOnCheckedListener { _, position, isChecked ->
            if(isChecked) {
                var gravity = 0
                when (position) {
                    0 -> gravity = gravity or MyLinearLayout.LEFT
                    1 -> gravity = gravity or MyLinearLayout.RIGHT
                    2 -> gravity = gravity or MyLinearLayout.CENTER_HORIZONTAL
                }
                linearyLayout.setGravity(gravity or (vertical.tag?.toString()?.toInt() ?: 0))
                horizontal.tag = gravity
            }
        }

        // 方位设置
        vertical.setOnCheckedListener { _, position, isChecked ->
            if(isChecked) {
                var gravity = 0
                when (position) {
                    0 -> gravity = gravity or MyLinearLayout.TOP
                    1 -> gravity = gravity or MyLinearLayout.BOTTOM
                    2 -> gravity = gravity or MyLinearLayout.CENTER_VERTICAL
                }
                linearyLayout.setGravity(gravity or (horizontal.tag?.toString()?.toInt() ?: 0))
                vertical.tag = gravity
            }
        }
    }
}
