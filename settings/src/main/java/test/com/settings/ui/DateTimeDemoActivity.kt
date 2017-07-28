package test.com.settings.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.text.format.DateFormat
import better.common.base.ToolbarActivity
import better.common.utils.DateUtils
import better.common.utils.e
import kotlinx.android.synthetic.main.settings__activity_date_time_demo.*
import org.jetbrains.anko.sdk25.coroutines.onClick

import test.com.settings.R
import java.text.SimpleDateFormat
import java.util.*

class DateTimeDemoActivity : ToolbarActivity() {

    private var timeMills: Long? = null
    private val date_format = "yyyy-MM-dd HH:mm"
    private val calender = Calendar.getInstance()

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

        changeServiceTime(calender)

        // 日期
        btn_date.onClick {
            DatePickerDialog(this@DateTimeDemoActivity, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calender.set(Calendar.YEAR, year)
                calender.set(Calendar.MONTH, month)
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                changeServiceTime(calender)
            }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 时间
        btn_date_time.onClick {
            TimePickerDialog(this@DateTimeDemoActivity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calender.set(Calendar.MINUTE, minute)
                changeServiceTime(calender)
            }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show()
        }
    }

    // 模拟服务器时间
    inline fun changeServiceTime(cal: Calendar) {
        txt_server_time_value.text = DateUtils.formatDate(cal.timeInMillis, resources.getText(R.string.common__datetime_fmt_12), resources.configuration.locale)
    }
}
