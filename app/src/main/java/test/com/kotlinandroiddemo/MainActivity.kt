package test.com.kotlinandroiddemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import better.common.ModuleTag
import better.common.communicate.home.IHomeCommunication
import better.common.utils.getService


class MainActivity : AppCompatActivity() {

    companion object {
        val FRAGMENT_HOME = "home"
        val FRAGMENT_WIDGET = "widget"
        val FRAGMENT_ME = "me"
        val FRAGMENT_SETTING = "setting"

        val KEY_SAVED_FRAGMENT_TAG = "key_fragment_tag"

        // tag
        val fragmentTags = arrayOf(FRAGMENT_HOME, FRAGMENT_WIDGET, FRAGMENT_ME, FRAGMENT_SETTING)

        // 功能模块
        val funTabs = mutableMapOf<String, String>()
    }

    private var currentFragmentTag = FRAGMENT_HOME
    private var isSavedInstanceCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = "KotlinAndroidDemo"

        if (savedInstanceState != null) {
            restoreFragments()
            currentFragmentTag = savedInstanceState.getString(KEY_SAVED_FRAGMENT_TAG)
            isSavedInstanceCalled = true
        }

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

            false
        }

        // 设置功能
        setupTabFun()
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
        val homeComm = getService<IHomeCommunication>(ModuleTag.HOME_SERVICE)       // 首页
        funTabs.put(FRAGMENT_HOME, homeComm.getMainFragmentName(this))
        funTabs.put(FRAGMENT_WIDGET, homeComm.getMainFragmentName(this))
        funTabs.put(FRAGMENT_ME, homeComm.getMainFragmentName(this))
        funTabs.put(FRAGMENT_SETTING, homeComm.getMainFragmentName(this))

        navigation.selectedItemId = R.id.home
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(KEY_SAVED_FRAGMENT_TAG, currentFragmentTag)
    }
}
