package com.hanke.navi.skyair.pop.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by mahao on 2017/11/13.
 * 用来表示三种预警的级别和要显示的预警文字
 */

public class WarningResultBean implements Comparable<WarningResultBean>,Serializable{
    //标题,用来区别三种预警 碰撞 撞地的空域
    public String warningTitle;
    //预警提示文字
    public String warningText;
    //预警级别
    public int warningLevel;
    //碰撞预警需要用到的adsb的航班号
    public String adsbPlaneNum;
    //预警文字的胭脂色
    public int textColor;

    @Override
    public int compareTo(@NonNull WarningResultBean another) {
        return warningLevel - another.warningLevel;
    }
}
