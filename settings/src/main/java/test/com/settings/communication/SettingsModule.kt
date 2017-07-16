package test.com.settings.communication

import better.common.communicate.BaseModule
import better.common.communicate.CommunicationTag
import better.common.communicate.router.IRouterCommunication

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class SettingsModule private constructor(r: IRouterCommunication) : BaseModule(r) {

    companion object {
        private var instance: SettingsModule? = null
        fun getInstance(r: IRouterCommunication): SettingsModule {
            if (instance == null) {
                instance = SettingsModule(r)
            }
            return instance!!
        }
    }

    override fun getTag(): String = CommunicationTag.SETTINGS_SERVICE

    override fun getCommunication(): Any? = SettingsCommunication()

    override fun onInit() = Unit

}