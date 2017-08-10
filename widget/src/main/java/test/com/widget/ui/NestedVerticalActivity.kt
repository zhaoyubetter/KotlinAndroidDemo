package test.com.widget.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import test.com.widget.R

class NestedVerticalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_nested_vertical)

        val list = mutableListOf<String>()
        (0..100).forEach { list.add("item $it") }
//        id_stickynavlayout_scrollview.adapter = ArrayAdapter(this@NestedVerticalActivity, android.R.layout.simple_list_item_1,
//                list.toTypedArray())
    }
}
