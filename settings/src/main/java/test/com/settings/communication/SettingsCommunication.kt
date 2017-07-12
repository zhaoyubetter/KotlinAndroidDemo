package test.com.settings.communication

import android.content.Context
import better.common.communicate.settings.ISettingsCommunication
import test.com.settings.SettingsFragment

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class SettingsCommunication : ISettingsCommunication {
    override fun getMainFragmentName(ctx: Context): String = SettingsFragment::class.java.name
}