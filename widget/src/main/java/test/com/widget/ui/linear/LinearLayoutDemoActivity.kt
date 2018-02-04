package test.com.widget.ui.linear

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import org.jetbrains.anko.find
import test.com.widget.R

/**
 * Created by zhaoyu on 2018/1/20.
 */
class LinearLayoutDemoActivity : AppCompatActivity() {

    private lateinit var linearyLayout: LinearLayout
    private val list = mutableListOf<String>()

    init {
//        (0..2).forEach { it ->
//            list.add("View$it")
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_linearlayout)
        linearyLayout = find<LinearLayout>(R.id.linear)

//        list.forEach {
//            val btn = Button(this)
//            btn.text = it
//            btn.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            linearyLayout.addView(btn)
//        }

        // event set
//        find<View>(R.id.left).onClick {
//            linearyLayout.gravity = Gravity.LEFT
//        }
//        find<View>(R.id.center).onClick { linearyLayout.gravity = Gravity.CENTER }
//        find<View>(R.id.right).onClick { linearyLayout.gravity = Gravity.RIGHT }
    }


}