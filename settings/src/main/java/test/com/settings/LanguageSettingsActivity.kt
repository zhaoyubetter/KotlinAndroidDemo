package test.com.settings

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.TextView
//import kotlinx.android.synthetic.main.common__toolbar.*
import kotlinx.android.synthetic.main.settings__activity_language_setttings.*
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.sdk25.coroutines.onClick
import test.com.settings.model.LanguageModel
import test.com.settings.model.LanguageTemplate
import java.util.*

class LanguageSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings__activity_language_setttings)

//        toolbar?.setTitle(R.string.settings__language)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.settings__language)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = Adapter(LanguageTemplate.items) {
            changeLocale(it)
        }
    }

    private fun changeLocale(item: LanguageModel) {
        var config = resources.configuration
        var sysLocal: Locale? = resources.configuration.locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocal = resources.configuration.locales[0]
        }

        val local = Locale(item.codeCountry, item.codeLanguage)
        if(sysLocal != local) {
            config.locale = Locale(item.codeCountry, item.codeLanguage)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    inner class Adapter(val datas: List<LanguageModel>, val closure: ((LanguageModel) -> Unit)?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = parent?.context?.layoutInflater?.let {
                it.inflate(android.R.layout.simple_list_item_1, parent, false)
            }
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            holder?.itemView?.find<TextView>(android.R.id.text1)?.text = datas[position].langName
            holder?.itemView?.onClick {
                closure?.invoke(datas[position])
            }
        }
    }
}
