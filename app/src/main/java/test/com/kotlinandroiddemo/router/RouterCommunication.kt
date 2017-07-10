package test.com.kotlinandroiddemo.router

import android.app.Application
import android.content.Context
import better.common.communicate.BaseModule
import better.common.communicate.router.IRouterCommunication
import test.com.kotlinandroiddemo.BuildConfig

/**
 * 组件通信
 * Created by zhaoyu1 on 2017/7/10.
 */
class RouterCommunication private constructor() : IRouterCommunication {

    companion object {
        /**
         * module 与 其访问接口服务
         */
        private val communications: Map<String, Any> = hashMapOf()
        private val modules: Map<String, BaseModule> = hashMapOf()
        private lateinit var ctx: Application

        /**
         * 注册自己，并初始化各个模块
         */
        @JvmStatic fun registerSelf() {
            if (!communications.containsKey("ROUTER")) {
                communications.plus(Pair("ROUTER", RouterCommunication()))
            }

            // 初始化模块
            initModules(getRouter())
        }

        /**
         * 初始化modules
         */
        @JvmStatic fun initModules(thiz: IRouterCommunication) {

        }

        @JvmStatic fun getRouter(): IRouterCommunication = communications["ROUTER"] as IRouterCommunication
    }

    override fun getContext(): Context = ctx

    override fun restoreContext(application: Application) {
        ctx = application
    }

    override fun registerService(tag: String, o: Any?) {
        if (!communications.containsKey(tag))
            communications.plus(Pair(tag, o))
    }

    override fun getService(tag: String): Any? {
        if (!communications.containsKey(tag)) {
            val module = modules[tag]
            module?.init()
            module?.onInit()
        }
        return communications[tag]
    }

    override fun isDebug(): Boolean = BuildConfig.DEBUG
}