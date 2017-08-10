package test.com.kotlinandroiddemo

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import better.common.CommonKey
import better.common.base.BaseActivity
import better.common.communicate.CommunicationTag
import better.common.communicate.home.IHomeCommunication
import better.common.communicate.me.IMeCommunication
import better.common.communicate.settings.ISettingsCommunication
import better.common.communicate.widget.IWidgetCommunication
import better.common.utils.getService
import better.common.utils.registerEvent
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity() {

    companion object {
        val FRAGMENT_HOME = "home"
        val FRAGMENT_WIDGET = "widget"
        val FRAGMENT_ME = "me"
        val FRAGMENT_SETTING = "setting"
        val KEY_SAVED_FRAGMENT_TAG = "key_fragment_tag"

        // fragment 对应的 tag 名称
        val fragmentTags = arrayOf(FRAGMENT_HOME, FRAGMENT_WIDGET, FRAGMENT_ME, FRAGMENT_SETTING)

        // 功能模块
        val funTabs = mutableMapOf<String, String>()
    }

    private var currentFragmentTag = FRAGMENT_HOME
    private var isSavedInstanceCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "KotlinAndroidModuleDemo"

        // 回收后唤醒处理
        if (savedInstanceState != null) {
            restoreFragments()
            currentFragmentTag = savedInstanceState.getString(KEY_SAVED_FRAGMENT_TAG)
            isSavedInstanceCalled = true
        }

        // 底部导航条事件
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> currentFragmentTag = FRAGMENT_HOME
                R.id.widget -> currentFragmentTag = FRAGMENT_WIDGET
                R.id.me -> currentFragmentTag = FRAGMENT_ME
                R.id.settings -> currentFragmentTag = FRAGMENT_SETTING
            }

            val trans = supportFragmentManager.beginTransaction()
            hideFragments(trans)

            supportFragmentManager.findFragmentByTag(currentFragmentTag).let {
                var currentF = it
                if (currentF == null) {
                    currentF = Class.forName(funTabs[currentFragmentTag])?.newInstance() as Fragment?
                    if (currentF != null) {
                        trans.add(R.id.content, currentF, currentFragmentTag)
                    }
                }
                if (currentF != null) {
                    trans.show(currentF).commitAllowingStateLoss()
                }
            }

            true
        }

        // 设置功能
        setupTabFun()

        // 监听广播
        registerEvent { intent, key, bundle ->
            if (key == CommonKey.EVENT_CHANGE_LOCALE) {
                changeLocale(bundle?.getSerializable("locale") as Locale)
            }
        }
    }


    private fun restoreFragments() {
        supportFragmentManager.beginTransaction().let {
            hideFragments(it)
            it.commitAllowingStateLoss()
        }
    }

    /**
     * 隐藏fragment
     */
    private fun hideFragments(trans: FragmentTransaction) {
        for (tag in fragmentTags) {
            supportFragmentManager.findFragmentByTag(tag)?.let { trans.hide(it) }
        }
    }

    /**
     * 设置功能
     */
    private fun setupTabFun() {
        funTabs.clear()

        // 组装内容
        val homeComm = getService<IHomeCommunication>(CommunicationTag.HOME_SERVICE)       // 首页
        val widgetComm = getService<IWidgetCommunication>(CommunicationTag.WIDGET_SERVICE)
        val meComm = getService<IMeCommunication>(CommunicationTag.ME_SERVICE)
        val settingsComm = getService<ISettingsCommunication>(CommunicationTag.SETTINGS_SERVICE)

        funTabs.put(FRAGMENT_HOME, homeComm.getMainFragmentName(this))
        funTabs.put(FRAGMENT_WIDGET, widgetComm.getMainFragmentName(this))
        funTabs.put(FRAGMENT_ME, meComm.getMainFragmentName(this))
        funTabs.put(FRAGMENT_SETTING, settingsComm.getMainFragmentName(this))

        navigation.selectedItemId = R.id.home
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_SAVED_FRAGMENT_TAG, currentFragmentTag)
    }


    private inline fun changeLocale(locale: Locale) {
        val config = resources.configuration
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        // 真正意義上的重啟要 釋放掉所有資源，如果只是 clear_top 单利资源是不会回收的
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        finish()
        Runtime.getRuntime().exit(0)
    }

}
