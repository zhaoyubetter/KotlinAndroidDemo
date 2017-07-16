package better.common

import android.app.Application
import better.common.lifecycle.CommonActivityLifeCycleCallback
import java.util.*

/**
 *
 * Created by zhaoyu on 2017/7/15.
 */
open class CommonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(CommonActivityLifeCycleCallback())
        initLocale()
    }

    /**
     * app 初始化时，指定Locale
     */
    private fun initLocale() {
        val item = Locale("en", "US")
        val config = resources.configuration
        config.locale = item
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}