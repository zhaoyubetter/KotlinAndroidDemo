package better.common.communicate.home

import android.content.Context

/**
 * 主页模块 通信接口定义
 * Created by zhaoyu1 on 2017/7/11.
 */
interface IHomeCommunication {
    fun getMainFragmentName(ctx: Context): String
}
