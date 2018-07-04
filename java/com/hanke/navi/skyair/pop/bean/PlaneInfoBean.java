package com.hanke.navi.skyair.pop.bean;

import com.amap.api.maps.model.LatLng;

/**
 * Created by Che on 2017/8/23.
 */
public class PlaneInfoBean implements Comparable<PlaneInfoBean> {

    public LatLng latLng;
    public String planeNum;
    public double flyHeight;
    public double flySpeed;
    public double flyAngle;
    public double upOrDownSpeed;
    public double distanceWithHomePlane;
    public long currentTimeMillis;
    //这里添加预警信息和预警级别
    public int warningInfo;
    public String warningLevel;


    @Override
    public int compareTo(PlaneInfoBean another) {
        if (distanceWithHomePlane - another.distanceWithHomePlane < 0) {
            return -1;
        } else if (distanceWithHomePlane - another.distanceWithHomePlane > 0) {
            return 1;
        } else {
            return 0;
        }

    }
}
