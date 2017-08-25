package test.com.widget.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.widget__activity_nested_scroll_vertical.*
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import test.com.widget.R

class NestedScrollVerticalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget__activity_nested_scroll_vertical)

        val list = mutableListOf<String>()
        (0..15).forEach { list.add("item $it") }
        id_stickynavlayout_scrollview.layoutManager = LinearLayoutManager(this)

        id_stickynavlayout_scrollview.adapter = Adapter(list)
    }

    inner class Adapter(val items: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = with(parent.context) {
                layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            }
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            holder.itemView.find<TextView>(android.R.id.text1).text = item
        }
    }
}
