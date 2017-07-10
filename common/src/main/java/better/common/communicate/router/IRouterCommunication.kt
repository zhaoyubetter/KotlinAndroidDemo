package better.common.communicate.router

import android.app.Application
import android.content.Context

/**
 * 路由通信接口
 * 供主工程来实现
 * Created by zhaoyu1 on 2017/7/10.
 */
interface IRouterCommunication {
    fun getContext(): Context

    fun restoreContext(application: Application)

    /**
     *注册服务
     */
    fun registerService(tag: String, o: Any?)

    fun getService(tag: String): Any?

    fun isDebug(): Boolean
}

