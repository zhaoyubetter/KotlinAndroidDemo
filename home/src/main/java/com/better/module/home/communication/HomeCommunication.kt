package com.better.module.home.communication

import android.content.Context
import better.common.communicate.home.IHomeCommunication
import com.better.module.home.HomeFragment

/**
 * Created by zhaoyu1 on 2017/7/11.
 */
class HomeCommunication : IHomeCommunication {
    override fun getMainFragmentName(ctx: Context): String = HomeFragment::class.java.name
}