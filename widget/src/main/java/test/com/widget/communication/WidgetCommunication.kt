package test.com.widget.communication

import android.content.Context
import better.common.communicate.widget.IWidgetCommunication
import test.com.widget.WidgetFragment

/**
 * Created by zhaoyu1 on 2017/7/12.
 */
class WidgetCommunication : IWidgetCommunication {
    override fun getMainFragmentName(ctx: Context): String = WidgetFragment::class.java.name
}
