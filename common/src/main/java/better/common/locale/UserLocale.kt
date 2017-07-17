package better.common.locale

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import android.view.TextureView
import better.common.sharedprefrences.SharedPrefs
import java.util.*

/**
 * 应用内的语言设置，即：用户的设置，采用 SP 文件来保存
 *
 * // 1.获取应用内的语言设置，默认跟随系统
// 2.获取系统的语言设置，
// 3.更改应用的语言设置，存 SP文件中
// 4.改换成功后需要重启app
 * user local settings
 * Created by zhaoyu1 on 2017/7/14.
 */
class UserLocale {
    companion object {
        /**
         * 获取系统的Locale
         */
        fun getSystemLocale(ctx: Context): Locale {
//            // 返回当前app Resource 对应的 Locale设置
//            var syslocale: Locale? = ctx.resources.configuration.locale
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                syslocale = ctx.resources.configuration.locales[0]
//            }
            // 获取系统的
            return Resources.getSystem().configuration.locale
        }

        /**
         * 获取用户设置的
         */
        fun getUserLocale(ctx: Context): Locale? {
            val lang: String = SharedPrefs.codeLanguage
            val country: String = SharedPrefs.codeCountry
            if (!TextUtils.isEmpty(lang) || !TextUtils.isEmpty(country)) {
                return Locale(lang, country)
            }
            return null
        }
    }
}