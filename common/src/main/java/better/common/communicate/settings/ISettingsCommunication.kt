package better.common.communicate.settings

import android.content.Context

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
interface ISettingsCommunication {
    fun getMainFragmentName(ctx: Context): String
}