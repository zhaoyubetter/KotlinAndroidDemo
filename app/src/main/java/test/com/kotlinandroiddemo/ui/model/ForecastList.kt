package test.com.kotlinandroiddemo.ui.model

/**
 * Created by zhaoyu1 on 2017/7/6.
 */
data class ForecastList(val city: String, val country: String, val forecast: List<Forecast>) {
    // 操作符重载
    operator fun get(position: Int): Forecast = forecast[position]
}