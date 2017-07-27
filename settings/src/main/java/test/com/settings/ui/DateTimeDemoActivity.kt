package test.com.settings.ui

import android.os.Bundle
import better.common.base.ToolbarActivity
import better.common.utils.DateUtils
import kotlinx.android.synthetic.main.settings__activity_date_time_demo.*

import test.com.settings.R
import java.util.*

class DateTimeDemoActivity : ToolbarActivity() {

    private var timeMills: Long? = null
    private val date_format = "yyyy-MM-dd HH:mm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings__activity_date_time_demo)
        title = getString(R.string.settings__date_time_demo_title)

        timeMills = Calendar.getInstance().timeInMillis

        timeMills?.let {
            text_sys_time.text = DateUtils.changeTimeZone(it, TimeZone.getDefault().id, date_format)
            text_standard_time.text = DateUtils.changeTimeZone(it, "GMT", date_format)
            text_beijing_time.text = DateUtils.changeTimeZone(it, "Asia/Shanghai", date_format)
            text_dongjing_time.text = DateUtils.changeTimeZone(it, "Asia/Tokyo", date_format)
            text_tanxiangshan_time.text = DateUtils.changeTimeZone(it, "Pacific/Honolulu", date_format)
        }
    }

}
