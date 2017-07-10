package test.com.kotlinandroiddemo.ui

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import lib.basenet.okhttp.OkHttpRequest
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import test.com.kotlinandroiddemo.R

class RecyclerTestActivity : AppCompatActivity() {

    private val items = listOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    private var progressDiaolg: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_test)

        val toolbar = find<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("title")
        supportActionBar?.subtitle = intent.getStringExtra("desc")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        progressDiaolg = ProgressDialog(this)
        progressDiaolg?.setMessage("加载中...")

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter(items, object : OnItemClickListener {
            override fun invoke(item: String) {
                progressDiaolg?.show()

                doAsync {
                    SystemClock.sleep(2000)
                    val response = OkHttpRequest.Builder().url("http://www.baidu.com").build().requestSync()
                    uiThread {
                        progressDiaolg?.hide()
                        if (response != null) {
                            toast("" + response?.responseBody)
                        } else {
                            toast("请求失败")
                        }
                    }
                }
            }
        })
    }

    class Adapter(val datas: List<out String>, val itemClick: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int = datas.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // with 函数，给 receiver 添加扩展函数，并执行扩展函数
            with(datas[position]) {
                val text = holder.itemView as TextView
                text.text = "aa" + this
                holder.itemView.setOnClickListener { itemClick(this) }
            }
        }

        class ViewHolder(text: View) : RecyclerView.ViewHolder(text)
    }

    interface OnItemClickListener {
        operator fun invoke(item: String)
    }
}
