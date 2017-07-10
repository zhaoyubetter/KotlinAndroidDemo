package better.common.communicate

import better.common.communicate.router.IRouterCommunication

/**
 * 模块抽象基类
 * Created by zhaoyu1 on 2017/7/10.
 */
abstract class BaseModule(val router: IRouterCommunication) {

    /**
     * 模块初始化方法
     */
    fun init() {
        register()
    }

    /**
     * 模块对应的tag
     */
    abstract fun getTag(): String

    /**
     * 模块对应的Communication
     */
    abstract fun getCommunication(): Any?

    /**
     * 子类调用
     */
    abstract fun onInit()

    /**
     * 获取模块对应的服务
     */
    fun getService(tag: String): Any? = router?.getService(tag)

    /**
     * 注册服务
     */
    private fun register() {
        router.registerService(getTag(), getCommunication())
    }

}