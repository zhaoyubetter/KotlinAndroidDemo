package better.common.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import better.common.communicate.CommonModule
import java.lang.Exception

/**
 * 扩展方法
 * Created by zhaoyu1 on 2017/7/11.
 */

// ============ 模块之间通信控制
/**
 * 获取通信服务
 */
inline fun <reified T> Fragment.getService(tag: String): T = CommonModule.getIns().getService(tag) as T

inline fun <reified T> Activity.getService(tag: String): T = CommonModule.getIns().getService(tag) as T


// ============= 全局 log 控制 参考 cz
inline fun <reified T> T.v(log: String) = Log.v(T::class.java.name, log)

inline fun <reified T> T.i(log: String) = Log.i(T::class.java.name, log)
inline fun <reified T> T.d(log: String) = Log.d(T::class.java.name, log)
inline fun <reified T> T.w(log: String) = Log.w(T::class.java.name, log)
inline fun <reified T> T.e(log: String) = Log.e(T::class.java.name, log)
inline fun <reified T> T.e(e: Exception) = Log.e(T::class.java.name, null, e)


/* ================================================
    全局事件控制
  ================================================*/

// 添加扩展属性
val Activity.COMMON_EVENT_ACTION: String get() = "COMMON_EVENT_ACTION"
val Activity.COMMON_EVENT_KEY: String get() = "COMMON_EVENT_KEY"
val Activity.COMMON_EVENT_DATA: String get() = "COMMON_EVENT_DATA"

/**
 * 发送事件
 * @param eventKey 事件key
 * @param eventData 传送的数据对象
 */
inline fun Activity.postEvent(eventKey: String, eventData: Bundle?) {
    val intent = Intent(this.COMMON_EVENT_ACTION)
    intent.putExtra(this.COMMON_EVENT_KEY, eventKey)        // key
    intent.putExtra(this.COMMON_EVENT_DATA, eventData)      // value
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

/**
 * 发送事件
 * @param eventKey 事件key
 * @param eventData 传送的数据对象
 */
inline fun Fragment.postEvent(eventKey: String, eventData: Bundle?) {
    if (this != null && this.activity != null) {
        val intent = Intent(this.activity.COMMON_EVENT_ACTION)
        intent.putExtra(this.activity.COMMON_EVENT_KEY, eventKey)        // key
        intent.putExtra(this.activity.COMMON_EVENT_DATA, eventData)
        LocalBroadcastManager.getInstance(this.activity).sendBroadcast(intent)
    }
}


// -------------------------------------------------------------------
// [07-22]使用cz推荐的新方案, 想要的时候，直接注册，避免每个activity新建的时候注册
// -------------------------------------------------------------------


val receiverMap = mutableMapOf<String, BroadcastReceiver>()

/**
 * 注册事件，只注册一次
 * @param closure:
 */
fun Activity.registerEvent(closure: (Intent?, String?, Bundle?) -> Unit) {
    receiverMap.get(this.toString()) ?: let {
        val filter = IntentFilter(COMMON_EVENT_ACTION)
        val localReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val eventKey = intent?.getStringExtra(COMMON_EVENT_KEY)
                val eventValue = intent?.getBundleExtra(COMMON_EVENT_DATA)
                closure.invoke(intent, eventKey, eventValue)
            }
        }

        // 存放在map中
        receiverMap.put(this.toString(), localReceiver)
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter)
    }
}


/**
 * 解除所有广播接收者
 */
fun Activity.unRegisterEvent() {
    receiverMap[this.toString()]?.let {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverMap.remove(this.toString()))
    }
}