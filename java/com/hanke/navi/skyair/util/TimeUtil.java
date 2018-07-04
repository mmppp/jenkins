package com.hanke.navi.skyair.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by mahao on 2017/9/21.
 */

public class TimeUtil {
    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        //这里使用/的话好像是会被转义,这个肯定是可以解决的,多使用一个就可以避免这种情况.
        return year + ":" + month + ":" + day + "-" + hour + ":" + minute + ":" + second;
    }
}
