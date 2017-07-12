package test.com.kotlinandroiddemo.router

import android.app.Application
import android.content.Context
import better.common.communicate.BaseModule
import better.common.communicate.CommonModule
import better.common.communicate.router.IRouterCommunication
import com.better.module.home.communication.HomeModule
import test.com.kotlinandroiddemo.BuildConfig
import test.com.widget.communication.WidgetModule

/**
 * 组件通信
 * Created by zhaoyu1 on 2017/7/10.
 */
class RouterCommunication private constructor() : IRouterCommunication {

    companion object {
        /**
         * 接口服务
         * (communicationTag与其对应的服务)
         */
        private val communications = hashMapOf<String, Any?>()
        /**
         * module map (module名称，对应的module)
         */
        private val modules = hashMapOf<String, BaseModule>()
        private lateinit var ctx: Application

        /**
         * 注册自己，并初始化各个模块
         */
        @JvmStatic fun registerSelf() {
            if (!communications.containsKey("ROUTER")) {
                communications.put("ROUTER", RouterCommunication())
            }
            // 初始化模块
            initModules(getRouter())
        }

        /**
         * 初始化modules
         * 主module中完成各个模块的注册
         */
        @JvmStatic fun initModules(thiz: IRouterCommunication) {
            modules.put(CommonModule.getIns(thiz).getTag(), CommonModule.getIns(thiz))      // 公共的module
            modules.put(HomeModule.getInstance(thiz).getTag(), HomeModule.getInstance(thiz))
            modules.put(WidgetModule.getInstance(thiz).getTag(), WidgetModule.getInstance(thiz))
        }

        @JvmStatic fun getRouter(): IRouterCommunication = communications["ROUTER"] as IRouterCommunication
    }

    override fun getContext(): Context = ctx

    override fun restoreContext(application: Application) {
        ctx = application
    }

    override fun registerService(tag: String, o: Any?) {
        if (!communications.containsKey(tag))
            communications.put(tag, o)
    }

    override fun getService(tag: String): Any? {
        if (!communications.containsKey(tag)) {
            val module = modules[tag]
            module?.init()      // module 的初始化操作,直接调用 registerService()
            module?.onInit()
        }
        return communications[tag]
    }

    override fun isDebug(): Boolean = BuildConfig.DEBUG
}