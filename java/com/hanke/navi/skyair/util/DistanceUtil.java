package com.hanke.navi.skyair.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.List;

/**
 * Created by Che on 2017/8/24.
 */
public class DistanceUtil {


    private final double EARTH_RADIUS = 6378137.0;
    public static DistanceUtil instance;

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static DistanceUtil getInstance() {
        return new DistanceUtil();
    }

    // 返回单位是米
    public double getDistance(LatLng latLng1, LatLng latLng2) {
        double Lat1 = rad(latLng1.latitude);
        double Lat2 = rad(latLng2.latitude);
        double a = Lat1 - Lat2;
        double b = rad(latLng1.longitude) - rad(latLng2.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    //计算到达时间和剩余时间
    public int caculateArriveTime(LatLng currentLatlng, LatLng destination, double flySpeed) {
        //需要飞过的距离.
        double distance = getDistance(destination, currentLatlng);
        //arrivedTime
        double temp= flySpeed / 3.6;
        int arrivedTime = (int) (distance / temp);
        //例如求得的时间是3.2小时,就是3.2* 60  = 192分钟,也就是3小时12分钟.所以就在现在的时间里面加上这么多时间.
        return arrivedTime;
    }

    //计算所有航路点的总距离.
    public double getAllHLPointTotalDistance(List<Marker> hangLuBeenList) {
        double totalDistance = 0;
        for (int x = 1; x < hangLuBeenList.size(); x++) {
            LatLng latLng1 = hangLuBeenList.get(x).getPosition();
            LatLng latLng2 = hangLuBeenList.get(x - 1).getPosition();
            double distance = getDistance(latLng1, latLng2);
            totalDistance = totalDistance + distance;
        }

        return totalDistance;
    }


    //计算偏行距
    public double deviateDistance() {
        double deviateDistance = 0;


        return deviateDistance;
    }


}
