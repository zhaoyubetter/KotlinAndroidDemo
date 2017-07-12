package test.com.widget.communication

import better.common.CommunicationTag
import better.common.communicate.BaseModule
import better.common.communicate.router.IRouterCommunication

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class WidgetModule private constructor(val r: IRouterCommunication) : BaseModule(r) {

    companion object {
        private var instance: WidgetModule? = null

        fun getInstance(r: IRouterCommunication): WidgetModule {
            if (instance == null)
                instance = WidgetModule(r)
            return instance!!
        }
    }

    override fun getTag(): String = CommunicationTag.WIDGET_SERVICE

    override fun getCommunication(): Any? = WidgetCommunication()

    override fun onInit() = Unit

}