package test.com.widget.model

import android.app.Activity
import android.content.Context
import test.com.widget.R
import test.com.widget.ui.*

/**
 * Created by zhaoyu1 on 2017/7/4.
 */
class FuncTemplate private constructor(ctx: Context) {


    companion object {
        private var instance: FuncTemplate? = null
        private val items = mutableListOf<SampleItem<Activity>>()
        private val groupsItems = mutableMapOf<Int, List<SampleItem<Activity>>>()

        fun getInstance(context: Context): FuncTemplate {
            if (instance == null)
                instance = FuncTemplate(context)
            return instance!!
        }


        private fun item(closure: SampleItem<Activity>.() -> Unit) {
            items.add(SampleItem<Activity>().apply(closure))
        }

        // 分组模板
        private fun group(closure: () -> Unit) {
            closure.invoke()
            groupsItems += items.groupBy { it.pid }     // 根据pid进行分组
        }
    }

    operator fun get(id: Int?) = groupsItems[id]
    operator fun contains(id: Int?) = groupsItems.any { it.key == id }

    init {
        group {
            item {
                id = 1
                title = ctx.resources.getString(R.string.widget_privacy_title)
                desc = ctx.resources.getString(R.string.widget_privacy_desc)

                item {
                    pid = 1
                    title = ctx.resources.getString(R.string.widget_privacy_title_base_use)
                    desc = ctx.resources.getString(R.string.widget_privacy_title_base_use_desc)
                    clazz = PrivacyLockViewNormalUseActivity::class.java
                }

                item {
                    pid = 1
                    title = ctx.resources.getString(R.string.widget_privacy_title_dialog_use)
                    desc = ctx.resources.getString(R.string.widget_privacy_title_dialog_use_desc)
                    clazz = PrivacyLockViewDialogUseActivity::class.java
                }
            }

            item {
                id = 2
                title = ctx.resources.getString(R.string.widget_palette_view_title)
                desc = ctx.resources.getString(R.string.widget_palette_view_desc)
                clazz = PaletteImageViewActivity::class.java
            }

            // 嵌套滚动
            item {
                id = 2
                title = ctx.resources.getString(R.string.widget_nestted_title)
                desc = ctx.resources.getString(R.string.widget_nestted_title)

                item {
                    pid = 2
                    title = ctx.resources.getString(R.string.widget_nestted_vertical)
                    desc = ctx.resources.getString(R.string.widget_nestted_vertical)
                    clazz = NestedMain2Activity::class.java
                }

                item {
                    pid = 2
                    title = ctx.resources.getString(R.string.widget_nestted_horiz)
                    desc = ctx.resources.getString(R.string.widget_nestted_horiz)
                    clazz = NestedHorizonalActivity::class.java
                }
            }
        }
    }
}