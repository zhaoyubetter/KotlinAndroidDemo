package test.com.settings

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import android.widget.TextView
import better.common.sharedprefrences.Preference
import better.common.sharedprefrences.SharedPrefs
import better.common.utils.postEvent
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
        // 当前设置的locale
        var currentLocale: Locale = resources.configuration.locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = resources.configuration.locales[0]
        }

        // 用户选择的
        val local = Locale(item.codeLanguage, item.codeCountry)
        if (currentLocale != local) {
            SharedPrefs.codeLanguage = item.codeLanguage ?: ""
            SharedPrefs.codeCountry = item.codeCountry ?: ""
            postEvent("localeChangeEvent", Bundle().apply { putSerializable("locale", local) })
        }
    }


    inner class Adapter(val datas: List<LanguageModel>, val closure: ((LanguageModel) -> Unit)?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = parent?.context?.layoutInflater?.inflate(android.R.layout.simple_list_item_1, parent, false)
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
