package better.common.communicate.widget

import android.content.Context

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
interface IWidgetCommunication {
    fun getMainFragmentName(ctx: Context): String
}