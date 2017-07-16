package better.common.communicate

/**
 * Communication Tag，单例形式
 * Created by zhaoyu1 on 2017/7/10.
 */
class CommunicationTag {
    companion object {
        val COMMON_SERVICE = "common_service"
        val ALBUM_SERVICE = "album_service"
        val HOME_SERVICE = "home_service"
        val WIDGET_SERVICE = "widget_service"
        val ME_SERVICE = "me_service"
        val SETTINGS_SERVICE = "settings_service"
    }
}
