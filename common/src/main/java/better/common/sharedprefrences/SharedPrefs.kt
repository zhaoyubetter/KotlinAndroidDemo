package better.common.sharedprefrences

import android.app.Application
import android.content.Context

/**
 * Created by cz on 2017/6/26.
 */
object SharedPrefs {
    var appContext: Context? = null
    fun getApplicationContext(): Context {
        if (appContext == null) {
            try {
                val activityThreadClass = Context::class.java.classLoader.loadClass("android.app.ActivityThread")
                val currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread")
                val activityThread = currentActivityThread.invoke(null)
                val getApplication = activityThreadClass.getDeclaredMethod("getApplication")
                val application = getApplication.invoke(activityThread) as Application
                appContext = application.applicationContext
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return appContext!!
    }

    var firstLoad: Boolean by Preference(getApplicationContext(), "firstLoad", false)
    var isAuthorized: Boolean by Preference(getApplicationContext(), "authorized", false)
    var versionCode: Int by Preference(getApplicationContext(), "app_version", -1)

    var codeCountry:String by Preference(getApplicationContext(), "code_country", "")
    var codeLanguage:String by Preference(getApplicationContext(), "code_language", "")
}