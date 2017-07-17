package better.common.sharedprefrences

/**
 * Created by zhaoyu1 on 2017/7/17.
 */
import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by cz on 2017/6/26.
 */
class Preference<T>(val context: Context, val name: String, val default: T) : ReadWriteProperty<Any?, T> {

    val sharedPreferences by lazy { context.getSharedPreferences("app_config", Context.MODE_PRIVATE) }


    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    /**
     * 查找数据 返回给调用方法一个具体的对象
     * 如果查找不到类型就采用反序列化方法来返回类型
     * default是默认对象 以防止会返回空对象的异常
     * 即如果name没有查找到value 就返回默认的序列化对象，然后经过反序列化返回
     */
    private fun <A> findPreference(name: String, default: A): A = with(sharedPreferences) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> getString(name, default as String)
        }
        res as A
    }

    private fun <A> putPreference(name: String, value: A) = with(sharedPreferences.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, value.toString())
        }.apply()
    }

    /**
     * 删除全部数据
     */
    fun clearPreference() {
        sharedPreferences.edit().clear().commit()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key: String) {
        sharedPreferences.edit().remove(key).commit()
    }
}