package com.hanke.navi.framwork.utils;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.skyair.db.HLModel;
import com.hanke.navi.skyair.db.HXModel;

public class Constants {
    //数据库
    public static final String DB_NAME = "nav_db";//数据库名称
    public static final int DB_VERSION = 1;//数据库版本
    public static String[] TABLES = new String[]{HXModel.class.getName(),HLModel.class.getName()};//所有表

    public static final int ZHIXING=0;//按执行
    public static final int KEYBACK = 1;//按系统返回键
    public static final int SHUAXIN = 3;//刷新UI发送消息
    public static final int SHUJU = 4;

    public static final LatLng YANLIANG = new LatLng(34.657679, 109.239685);// 阎良经纬度
    public static final LatLng XIANYANG = new LatLng(34.4467, 108.7517);// 咸阳机场经纬度
    public static final LatLng XIAN = new LatLng(34.278149, 108.957019);// 西安经纬度
    public static final LatLng XIANT = new LatLng(34.31, 109.0);// 西安经纬度
}
