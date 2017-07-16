package better.common.lifecycle

import android.app.Activity

/**
 * Created by cz
 */
object ActivityManager {

    private val linkActivities = mutableListOf<Activity>()
    val size: Int
        get() = linkActivities.size

    /**
     * 结束所有activity
     */
    fun finishActivities() {
        val iterator = linkActivities.iterator()
        while (iterator.hasNext()) {
            val activity = iterator.next()
            if (null != activity) {
                iterator.remove()
                activity.finish()
            }
        }
    }

    /**
     * 添加activity
     * @param activity
     */
    fun add(activity: Activity) {
        linkActivities.add(activity)
    }

    fun hasActivity(clazz: Class<*>): Boolean = linkActivities.any { it::class.java.name == clazz.name }

    fun removeTo(clazz: Class<out Activity>) {
        //以倒序删除
        linkActivities.takeLast(linkActivities.reversed().indexOfFirst { clazz == it.javaClass }).reversed().forEach {
            linkActivities.remove(it)
            it.finish()
        }
    }

    /**
     * 移除指定activity
     * @param activity
     */
    fun remove(activity: Activity) {
        linkActivities.remove(activity)
    }

    /**
     * 获得栈顶的activity对象
     * @return
     */
    val topActivity: Activity
        get() = linkActivities.elementAt(size - 1)


    fun forEach(action: (Activity) -> Unit): Unit {
        for (element in linkActivities) action(element)
    }
}