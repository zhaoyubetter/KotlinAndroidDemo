package test.com.me.communication

import better.common.CommunicationTag
import better.common.communicate.BaseModule
import better.common.communicate.router.IRouterCommunication

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class MEModule private constructor(val r: IRouterCommunication) : BaseModule(r) {

    companion object {

        private var instance: MEModule? = null

        fun getInstance(r: IRouterCommunication): MEModule {
            if (instance == null) {
                instance = MEModule(r)
            }
            return instance!!
        }
    }

    override fun getCommunication(): Any? = MECommunication()

    override fun onInit() = Unit

    override fun getTag(): String = CommunicationTag.ME_SERVICE

}