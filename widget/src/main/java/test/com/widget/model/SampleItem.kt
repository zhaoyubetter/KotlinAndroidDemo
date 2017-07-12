package test.com.widget.model

/**
 * Created by zhaoyu1 on 2017/7/4.
 */
data class SampleItem<T>(var id: Int?, var pid: Int = 0, var clazz: Class<out T>?, var title: String?, var desc: String?) {
    constructor() : this(null, 0, null, null, null)
}
