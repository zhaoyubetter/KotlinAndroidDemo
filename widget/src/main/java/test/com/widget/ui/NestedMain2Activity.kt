package test.com.widget.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AbsListView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.widget__activity_nested_test_2.*
import test.com.widget.R

class NestedMain2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_nested_test_2)


        val list = mutableListOf<String>()
        (0..100).forEach { list.add("item $it") }

        id_stickynavlayout_scrollview.adapter = ArrayAdapter(this@NestedMain2Activity, android.R.layout.simple_list_item_1,
                list.toTypedArray())


        id_stickynavlayout_scrollview.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val firstView = id_stickynavlayout_scrollview.getChildAt(0)
                firstView?.let {
                    Log.e("firstTop", "top: ---> ${firstView.top}")
                }
            }
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }
        })
    }
}
