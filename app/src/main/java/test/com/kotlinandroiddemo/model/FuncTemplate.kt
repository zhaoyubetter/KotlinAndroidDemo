package test.com.kotlinandroiddemo.model

import android.app.Activity
import test.com.kotlinandroiddemo.ui.PrivacyLockViewUse1Activity

/**
 * Created by zhaoyu1 on 2017/7/4.
 */
class FuncTemplate {
    companion object {
        val items = mutableListOf<SampleItem<Activity>>()
        val groupsItems = mutableMapOf<Int, List<SampleItem<Activity>>>()

        fun item(closure: SampleItem<Activity>.() -> Unit) {
            items.add(SampleItem<Activity>().apply(closure))
        }

        // 分组模板
        fun group(closure: () -> Unit) {
            closure.invoke()
            groupsItems += items.groupBy { it.pid }     // 根据pid进行分组
        }

        operator fun get(id: Int?) = groupsItems[id]

        operator fun contains(id: Int?) = groupsItems.any { it.key == id }

        init {
            group {
                item {
                    id = 1
                    title = "验证码控件示例"
                    desc = "仿微信密码、验证码输入控件"

                    item {
                        pid = 1
                        title = "基本使用示例"
                        desc = "仿微信密码、验证码输入控件"
                        clazz = PrivacyLockViewUse1Activity::class.java
                    }
                }
            }
        }
    }
}