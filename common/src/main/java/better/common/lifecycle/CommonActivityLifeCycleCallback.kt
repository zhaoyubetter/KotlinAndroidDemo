package better.common.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import better.common.utils.unRegisterEvent

/**
 * Created by zhaoyu on 2017/7/15.
 */
class CommonActivityLifeCycleCallback : Application.ActivityLifecycleCallbacks {

    private val fragmentLifeCycle = CommonFragmentLifeCycleCallBack()


    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (null != activity) {
            (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(fragmentLifeCycle, true)
            ActivityManager.add(activity)
        }
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
            activity.unRegisterEvent()        // 取消所有
            ActivityManager.remove(activity)

            // 不在使用了
            //unRegisterBroadCast(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }
}