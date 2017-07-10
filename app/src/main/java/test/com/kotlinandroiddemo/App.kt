package test.com.kotlinandroiddemo

import android.app.Application
import test.com.kotlinandroiddemo.router.RouterCommunication

/**
 * Created by zhaoyu1 on 2017/7/7.
 */
class App : Application() {
    companion object {
        private var instance: Application? = null
        fun instance() = instance!!     // 返回非空的 Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        RouterCommunication.registerSelf()
        RouterCommunication.getRouter().restoreContext(this)
    }
}