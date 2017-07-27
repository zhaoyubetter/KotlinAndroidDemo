package better.common.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by liyu20 on 2017/7/27.
 */

public final class DateUtils {

    /**
     * 日期格式
     */
    public class DATE_TYPE {
        /* 英文格式 2017/2/22 */
        public static final int EN = 0;
        /* 中文格式 2017-2-22 */
        public static final int ZN = 1;
        /* 默认格式 ZN 2017-2-22 */
        public static final int DEFAULT = ZN;
    }

    public static String changeTimeZone(String dateStr, String desTimeZoneId) {
        return changeTimeZone(dateStr, desTimeZoneId, getDateType(dateStr));
    }

    public static String changeTimeZone(long timeMills, String desTimeZoneId, String formatStr) {
        if (isNull(formatStr, desTimeZoneId)) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        format.setTimeZone(TimeZone.getTimeZone(desTimeZoneId));
        return format.format(timeMills);
    }

    /*
    * 获取切换时区后的日期
    * @param dateStr 日期字符串，格式可以为中英文两种标准格式 yyyy/-MM/-dd HH:mm
    * @param desTimeZoneId 时区ID
    * @param formatType 转换格式，有EN、ZN两种
    * @return 目标时区的所对应的日期
    * */
    public static String changeTimeZone(String dateStr, String desTimeZoneId, int formatType) {
        if (isNull(dateStr, desTimeZoneId)) {
            return null;
        }
        Date date = new Date(parseDate(dateStr));
        SimpleDateFormat format = new SimpleDateFormat(getFormatStr(formatType));
        format.setTimeZone(TimeZone.getTimeZone(desTimeZoneId));
        return format.format(date);
    }

    /*
    * 解析日期为时间戳
    * @param dateStr 日期字符串，支持ZN、EN两种格式
    * @return 日期所对应的 ms 数
    * */
    public static Long parseDate(String dateStr) {
        if (isNull(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(getFormatStr(getDateType(dateStr)));
        try {
            return format.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatTimeSpan(String timeSpan) {
        return formatTimeSpan(timeSpan, DATE_TYPE.DEFAULT);
    }

    /*
    * 格式化时间戳
    * @param formatType 转换格式，有EN、ZN两种
    * @param timeSpan 时间戳
    * @return 按指定转换格式得到的日期
    * */
    public static String formatTimeSpan(String timeSpan, int formatType) {
        if (isNull(timeSpan)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(getFormatStr(formatType));
        try {
            return format.format(new Date(Long.valueOf(timeSpan)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isNull(String... strings) {
        for (String str : strings) {
            if (TextUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /*
    * 获取日期格式化字符串
    * */
    private static String getFormatStr(int formatType) {
        switch (formatType) {
            case DATE_TYPE.EN:
                return "dd/MM/yyyy HH:mm";
            case DATE_TYPE.ZN:
                return "yyyy-MM-dd HH:mm";
            default:
                return null;
        }
    }

    /*
    * 依据传入的日期字符串获取其所对应的类型值
    * */
    private static int getDateType(String dateStr) {
        return dateStr.contains("-") ? DATE_TYPE.ZN : DATE_TYPE.EN;
    }
}
