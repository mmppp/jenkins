package com.hanke.navi.skyair.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.pop.bean.GaojingSetBean;

/**
 * Created by mahao on 2017/9/16.
 */

public class GaojingPreference {
    private static final String PREFERENCES_NAME = "Gaojing";
    private static final String PREFERENCES_CAZ_DISTANCE = "preferences_caz_distance";
    private static final String PREFERENCES_PAZ_DISTANCE = "preferences_paz_distance";
    private static final String PREFERENCES_CAZ_HEIGHT = "preferences_caz_height";
    private static final String PREFERENCES_PAZ_HEIGHT = "preferences_paz_height";
    private static final String PREFERENCES_CAZ_TIME = "preferences_caz_time";
    private static final String PREFERENCES_PAZ_TIME = "preferences_paz_time";
    private static final String PREFERENCES_AIRSPACE_WARNING_TIME = "preferences_airspace_warning_time";
    private static final String PREFERENCES_IMPACT_LAND_WARNING_TIME = "preferences_impact_land_warning_time";

    //机场设置
    private static final String PREFERENCES_AIRPORT_NAME = "preferences_airport_name";
    private static final String PREFERENCES_RUN_ANGLE = "preferences_run_angle";
    private static final String PREFERENCES_PLANE_DOWN_ANGLE = "preferences_plane_down_angle";
    private static final String PREFERENCES_FAF_DISTANCE = "preferences_faf_distance";
    private static final String PREFERENCES_AIRPORT_LAT = "preferences_airport_lat";
    private static final String PREFERENCES_AIRPORT_LON = "preferences_airport_lon";
    private static final String PREFERENCES_AIRPORT_HEIGHT = "PREFERENCES_AIRPORT_HEIGHT";
    private static final String PREFERENCES_JOIN_IN_LEFT_RIGHT = "preferences_join_in_left_right";

    //nexttime
    private static final String PREFERENCES_NEXT_TIME_HOUR = "preferences_next_time_hour";
    private static final String PREFERENCES_NEXT_TIME_MINUTE = "preferences_next_time_minute";
    private static final String PREFERENCES_NEXT_TIME_SECAND = "preferences_next_time_secand";
    private static final String PREFERENCES_CURRENT_TIME = "preferences_current_time";

    //eta,xtk
    private static final String PREFERENCES_ETA_TIME = "preferences_eta_time";
    private static final String PREFERENCES_XTK_DISTANCE = "preferences_xtk_distance";

    //回放文件
    private static final String PREFERENCES_LOOKBACK_FILE = "preferences_lookback_file";
    //经度
    private static final String PREFERENCES_START_LON = "preferences_lookback_file_lon";
    //纬度
    private static final String PREFERENCES_START_LAT = "preferences_lookback_file_lat";
    //角度
    private static final String PREFERENCES_START_ANGLE = "preferences_lookback_file_angle";
    //高度
    private static final String PREFERENCES_START_HEIGHT = "preferences_lookback_file_height";
    //速度
    private static final String PREFERENCES_START_SPEED = "preferences_lookback_file_speed";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public GaojingPreference(Context context) {
        super();
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
            if (sharedPreferences != null) {
                editor = sharedPreferences.edit();
            }
        } else {
            Log.i("hahaha", "context位空了");
        }
    }

    public void saveStartLatAndLon(String lat, String lon) {
        editor.putString(PREFERENCES_START_LON, lon);
        editor.putString(PREFERENCES_START_LAT, lat);

        editor.commit();
    }

    public void saveStartHeight(String height) {
        editor.putString(PREFERENCES_START_HEIGHT, height);

        editor.commit();
    }

    public String getStartHeight() {
        String result = sharedPreferences.getString(PREFERENCES_START_HEIGHT, "");

        return result;
    }

    public void saveStartSpeed(String speed) {
        editor.putString(PREFERENCES_START_SPEED, speed);

        editor.commit();
    }

    public String getStartSpeed() {
        String result = sharedPreferences.getString(PREFERENCES_START_SPEED, "");

        return result;
    }


    public String[] getLatAndLon() {
        String[] result = new String[2];

        result[0] = sharedPreferences.getString(PREFERENCES_START_LAT, "");
        result[1] = sharedPreferences.getString(PREFERENCES_START_LON, "");

        return result;
    }

    public void saveAngle(String angle) {
        editor.putString(PREFERENCES_START_ANGLE, angle);
        editor.commit();
    }

    public String getAngle() {
        String result = sharedPreferences.getString(PREFERENCES_START_ANGLE, "");

        return result;
    }


    public void setLookBackFile(String fileName) {
        editor.putString(PREFERENCES_LOOKBACK_FILE, fileName);

        editor.commit();
    }

    public String getLookBackFile() {
        return sharedPreferences.getString(PREFERENCES_LOOKBACK_FILE, "");
    }

    public void setEtaTime(boolean isShowEtaTime) {
        editor.putBoolean(PREFERENCES_ETA_TIME, isShowEtaTime);

        editor.commit();
    }

    public boolean isShowEtaTime() {
        boolean result = sharedPreferences.getBoolean(PREFERENCES_ETA_TIME, true);
        return result;
    }

    public void setXtkDistance(boolean isShowRtkDistance) {
        editor.putBoolean(PREFERENCES_XTK_DISTANCE, isShowRtkDistance);

        editor.commit();
    }

    public boolean isShowXtkDistance() {
        boolean result = sharedPreferences.getBoolean(PREFERENCES_XTK_DISTANCE, true);
        return result;
    }


    public void setNextTime(double[] nextTime, String currentTime) {
        editor.putFloat(PREFERENCES_NEXT_TIME_HOUR, (float) nextTime[0]);
        editor.putFloat(PREFERENCES_NEXT_TIME_MINUTE, (float) nextTime[1]);
        editor.putFloat(PREFERENCES_NEXT_TIME_SECAND, (float) nextTime[2]);
        editor.putString(PREFERENCES_CURRENT_TIME, currentTime);

        editor.commit();
    }

    public String[] getNextTime() {
        float string1 = sharedPreferences.getFloat(PREFERENCES_NEXT_TIME_HOUR, 0);
        float string2 = sharedPreferences.getFloat(PREFERENCES_NEXT_TIME_MINUTE, 0);
        float string3 = sharedPreferences.getFloat(PREFERENCES_NEXT_TIME_SECAND, 0);
        String string4 = sharedPreferences.getString(PREFERENCES_CURRENT_TIME, "");
        String[] strings = new String[4];
        strings[0] = string1 + "";
        strings[1] = string2 + "";
        strings[2] = string3 + "";
        strings[3] = string4 + "";
        return strings;
    }

    public void setGaojingInfo(String cazDistance, String pazDistance, String cazHeight, String pazHeight, String cazTime, String pazTime, String airspace_time, String impace_land_time) {
        editor.putString(PREFERENCES_CAZ_DISTANCE, cazDistance);
        editor.putString(PREFERENCES_PAZ_DISTANCE, pazDistance);
        editor.putString(PREFERENCES_CAZ_HEIGHT, cazHeight);
        editor.putString(PREFERENCES_PAZ_HEIGHT, pazHeight);
        editor.putString(PREFERENCES_CAZ_TIME, cazTime);
        editor.putString(PREFERENCES_PAZ_TIME, pazTime);
        editor.putString(PREFERENCES_AIRSPACE_WARNING_TIME, airspace_time);
        editor.putString(PREFERENCES_IMPACT_LAND_WARNING_TIME, impace_land_time);

        editor.commit();
    }

    public GaojingSetBean getGaoJingSetInfo() {
        GaojingSetBean bean = new GaojingSetBean();
        bean.caz_distance = sharedPreferences.getString(PREFERENCES_CAZ_DISTANCE, "6000");
        bean.paz_distance = sharedPreferences.getString(PREFERENCES_PAZ_DISTANCE, "12000");
        bean.caz_height = sharedPreferences.getString(PREFERENCES_CAZ_HEIGHT, "300");
        bean.paz_height = sharedPreferences.getString(PREFERENCES_PAZ_HEIGHT, "600");
        bean.caz_time = sharedPreferences.getString(PREFERENCES_CAZ_TIME, "60");
        bean.paz_time = sharedPreferences.getString(PREFERENCES_PAZ_TIME, "300");
        bean.airspace_warning_time = sharedPreferences.getString(PREFERENCES_AIRSPACE_WARNING_TIME, "60");
        bean.impact_land_warning_time = sharedPreferences.getString(PREFERENCES_IMPACT_LAND_WARNING_TIME, "");

        return bean;
    }

    public void setAirport(AirportSetBean bean) {
        editor.putString(PREFERENCES_AIRPORT_NAME, bean.airportName);
        editor.putString(PREFERENCES_RUN_ANGLE, bean.runAngle);
        editor.putString(PREFERENCES_PLANE_DOWN_ANGLE, bean.planeDownAngle);
        editor.putString(PREFERENCES_FAF_DISTANCE, bean.fafDistance);
        editor.putString(PREFERENCES_AIRPORT_LAT, bean.airportLat);
        editor.putString(PREFERENCES_AIRPORT_LON, bean.airportLon);
        editor.putString(PREFERENCES_AIRPORT_HEIGHT, bean.airportHeight);
        editor.putString(PREFERENCES_JOIN_IN_LEFT_RIGHT, bean.joininLeftOrRight);

        editor.commit();
    }

    public AirportSetBean getAirportSet() {
        AirportSetBean result = new AirportSetBean();

        result.airportName = sharedPreferences.getString(PREFERENCES_AIRPORT_NAME, "");
        result.runAngle = sharedPreferences.getString(PREFERENCES_RUN_ANGLE, "");
        result.planeDownAngle = sharedPreferences.getString(PREFERENCES_PLANE_DOWN_ANGLE, "");
        result.fafDistance = sharedPreferences.getString(PREFERENCES_FAF_DISTANCE, "");
        result.airportLat = sharedPreferences.getString(PREFERENCES_AIRPORT_LAT, "");
        result.airportLon = sharedPreferences.getString(PREFERENCES_AIRPORT_LON, "");
        result.airportHeight = sharedPreferences.getString(PREFERENCES_AIRPORT_HEIGHT, "");
        result.joininLeftOrRight = sharedPreferences.getString(PREFERENCES_JOIN_IN_LEFT_RIGHT, "");

        return result;
    }

}
