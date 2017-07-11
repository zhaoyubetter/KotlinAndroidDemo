package better.common.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.util.Log
import better.common.communicate.CommonModule
import java.lang.Exception

/**
 * 扩展方法
 * Created by zhaoyu1 on 2017/7/11.
 */

/**
 * 获取通信服务
 */
inline fun <reified T> Fragment.getService(tag: String): T = CommonModule.getIns().getService(tag) as T

inline fun <reified T> Activity.getService(tag: String): T = CommonModule.getIns().getService(tag) as T

//log
inline fun <reified T> T.v(log: String) = Log.v(T::class.java.name, log)

inline fun <reified T> T.i(log: String) = Log.i(T::class.java.name, log)
inline fun <reified T> T.d(log: String) = Log.d(T::class.java.name, log)
inline fun <reified T> T.w(log: String) = Log.w(T::class.java.name, log)
inline fun <reified T> T.e(log: String) = Log.e(T::class.java.name, log)
inline fun <reified T> T.e(e: Exception) = Log.e(T::class.java.name, null, e)
