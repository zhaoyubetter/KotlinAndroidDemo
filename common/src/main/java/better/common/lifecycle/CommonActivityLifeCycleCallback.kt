package better.common.lifecycle

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.LocalBroadcastManager
import better.common.base.BaseActivity
import better.common.utils.COMMON_EVENT_ACTION
import better.common.utils.COMMON_EVENT_DATA
import better.common.utils.COMMON_EVENT_KEY

/**
 * Created by zhaoyu on 2017/7/15.
 */
class CommonActivityLifeCycleCallback : Application.ActivityLifecycleCallbacks {

    private val fragmentLifeCycle = CommonFragmentLifeCycleCallBack()
    /**
     * 本地广播监听
     */
    private lateinit var localReceiver: BroadcastReceiver

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (null != activity) {
            (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(fragmentLifeCycle, true)

            ActivityManager.add(activity)
            registerLocalBroadCast(activity)
        }
    }

    /**
     * 注册全局事件服务
     */
    private inline fun registerLocalBroadCast(activity: Activity) {
        val filter = IntentFilter(activity.COMMON_EVENT_ACTION)
        localReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val eventKey = intent?.getStringExtra(activity.COMMON_EVENT_KEY)
                val eventValue = intent?.getBundleExtra(activity.COMMON_EVENT_DATA)
                (activity as? BaseActivity)?.onReceiveEvent(intent, eventKey, eventValue)
            }
        }

        LocalBroadcastManager.getInstance(activity).registerReceiver(localReceiver, filter)
    }

    override fun onActivityStarted(activity: Activity?) {
    }


    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityStopped(activity: Activity?) {
    }


    override fun onActivityDestroyed(activity: Activity?) {
        if (null != activity) {
            ActivityManager.remove(activity)
            localReceiver.let {
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(it)
            }

        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }
}