package test.com.me.communication

import android.content.Context
import better.common.communicate.me.IMeCommunication
import test.com.me.MEFragment

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class MECommunication : IMeCommunication {
    override fun getMainFragmentName(ctx: Context): String = MEFragment::class.java.name
}