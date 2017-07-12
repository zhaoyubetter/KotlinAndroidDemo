package test.com.widget

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.widget_fragment_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import test.com.widget.model.FuncTemplate
import test.com.widget.model.SampleItem
import android.support.v7.app.AppCompatActivity


/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class WidgetFragment : Fragment() {

    private var toolbar: ActionBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.widget_fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.widget_title)
        toolbar?.setSubtitle(R.string.widget_subtitle)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        val items = FuncTemplate[0] // 获取根
        items?.let { recyclerView.adapter = Adapter(it) }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            toolbar?.setTitle(R.string.widget_title)
            toolbar?.setSubtitle(R.string.widget_subtitle)
        }
    }


    inner class Adapter(val items: List<SampleItem<Activity>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = with(parent.context) {
                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    orientation = LinearLayout.VERTICAL
                    lparams(width = matchParent, height = wrapContent)
                    leftPadding = dip(16)
                    rightPadding = dip(16)

                    textView {
                        id = android.R.id.text1
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                        topPadding = dip(4)
                        bottomPadding = dip(4)
                    }

                    textView {
                        id = android.R.id.text2
                        textSize = 14f
                        topPadding = dip(2)
                        bottomPadding = dip(4)
                        maxLines = 2
                    }
                }
            }
            // 对象表达式, kotlin 的匿名内部类形式
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]
            holder.itemView.find<TextView>(android.R.id.text1).text = item.title
            holder.itemView.find<TextView>(android.R.id.text2).text = item.desc
            holder.itemView.onClick {
                val context = it?.context ?: return@onClick
                if (item.id in FuncTemplate) {
                    it.context.startActivity(Intent(context, MainKotlinActivity::class.java).apply {
                        putExtra("id", item.id)
                        putExtra("title", item.title)
                        putExtra("desc", item.desc)
                    })
                } else {
                    it.context.startActivity(Intent(context, item.clazz).apply {
                        putExtra("title", item.title)
                    })
                }
            }
        }
    }

}