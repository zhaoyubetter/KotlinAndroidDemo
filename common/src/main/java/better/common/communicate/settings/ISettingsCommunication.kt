package better.common.communicate.settings

import android.content.Context
import java.util.*

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
interface ISettingsCommunication {
    fun getMainFragmentName(ctx: Context): String
}