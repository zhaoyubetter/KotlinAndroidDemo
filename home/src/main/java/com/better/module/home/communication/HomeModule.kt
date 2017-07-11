package com.better.module.home.communication

import better.common.ModuleTag
import better.common.communicate.BaseModule
import better.common.communicate.router.IRouterCommunication

/**
 * 主页模块
 * Created by zhaoyu1 on 2017/7/11.
 */
class HomeModule private constructor(val r: IRouterCommunication) : BaseModule(r) {

    companion object {
        private  var instance: HomeModule? = null

        fun getInstance(r: IRouterCommunication): HomeModule {
            if (instance == null)
                instance = HomeModule(r)
            return instance!!
        }
    }

    override fun getTag(): String = ModuleTag.HOME_SERVICE

    override fun getCommunication(): Any? = HomeCommunication()

    override fun onInit() = Unit
}