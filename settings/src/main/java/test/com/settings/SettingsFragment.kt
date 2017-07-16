package test.com.settings

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.settings__fragment_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class SettingsFragment : Fragment() {
    private var toolbar: ActionBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.settings__fragment_main, container, false)
    }



    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.settings_title)
        toolbar?.setSubtitle(R.string.settings_subtitle)
        container_language.onClick {
            startActivity(Intent(activity, LanguageSettingsActivity::class.java))
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            toolbar?.setTitle(R.string.settings_title)
            toolbar?.setSubtitle(R.string.settings_subtitle)
        }
    }
}