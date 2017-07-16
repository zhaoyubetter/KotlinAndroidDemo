package test.com.widget

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.widget_activity_kotlin_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import test.com.widget.model.FuncTemplate
import test.com.widget.model.SampleItem

/**
 * 以apk形式编译时，启动起activity
 */
class MainKotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_kotlin_main)

        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title")

        setSupportActionBar(toolbar)
        toolbar.title = title ?: "KotlinSample"
        toolbar.subtitle = intent.getStringExtra("desc") ?: ""
        // 是否显示toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled((title?.length ?: 0) > 0)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        val items = FuncTemplate.getInstance(this)[id]
        items?.let { recyclerView.adapter = Adapter(it) }
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
                if (item.id in FuncTemplate.getInstance(context)) {
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
