package test.com.widget.ui.linear

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget__activity_linearlayout.*
import org.jetbrains.anko.find
import test.com.widget.R

/**
 * Created by zhaoyu on 2018/1/20.
 */
class LinearLayoutDemoActivity : AppCompatActivity() {

    private lateinit var linearyLayout: LinearLayout
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_linearlayout)
        linearyLayout = find<LinearLayout>(R.id.linear)

        // 添加
        add.setOnClickListener {
            Button(baseContext).let {
                it.text = "" + ++index
                linearyLayout.addView(it)
            }
        }

        // 方向设置
        orientation.setOnCheckedListener { _, position, isChecked ->
            if (isChecked) {
                linearyLayout.orientation = if (position == 0) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            }
        }

        // 方位设置
        horizontal.setOnCheckedListener { _, position, isChecked ->
            if(isChecked) {
                var gravity = 0
                when (position) {
                    0 -> gravity = gravity or Gravity.LEFT
                    1 -> gravity = gravity or Gravity.RIGHT
                    2 -> gravity = gravity or Gravity.CENTER_HORIZONTAL
                }
                linearyLayout.gravity = gravity or (vertical.tag?.toString()?.toInt() ?: 0)
                horizontal.tag = gravity
            }
        }

        // 方位设置
        vertical.setOnCheckedListener { _, position, isChecked ->
            if(isChecked) {
                var gravity = 0
                when (position) {
                    0 -> gravity = gravity or Gravity.TOP
                    1 -> gravity = gravity or Gravity.BOTTOM
                    2 -> gravity = gravity or Gravity.CENTER_VERTICAL
                }
                linearyLayout.gravity = gravity or (horizontal.tag?.toString()?.toInt() ?: 0)
                vertical.tag = gravity
            }
        }
    }


}