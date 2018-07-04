package com.hanke.navi.framwork.arith;


import android.content.Context;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.ImpactWarningBean;
import com.hanke.navi.skyair.pop.bean.KongYuBean;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.List;

public class Warnning {

    private Context context;
    private Nav NavTool;

    public Warnning() {
        NavTool = new Nav(context);
    }

    public Warnning(Context context) {
        this.context = context;
        NavTool = new Nav(context);
    }

    //防相撞判断
    public int judgeWarning(double homeLat, double homeLon, double lat1, double lon1) {
        int result = 0;
        //得到当前本机和旁边飞机的中心经纬度距离.
        //判断距离和飞机的警告圈和报警圈的关系.
        double distance = DistanceUtil.getInstance().getDistance(new LatLng(homeLat, homeLon), new LatLng(lat1, lon1));


        return result;
    }

    //新的防相撞算法
    public ImpactWarningBean safe_level(double lat0, double lon0, double h0, double ve0, double vn0, double vu0, double lat1, double lon1, double h1, double ve1, double vn1, double vu1, double dis_caz, double h_caz, double time_caz, double dis_paz, double h_paz, double time_paz, double distance) {
        ImpactWarningBean bean = new ImpactWarningBean();
        bean.level = "5";
        bean.result = 0x10;
        if (distance < dis_caz && Math.abs(h0 - h1) < h_caz) {
            bean.result = 0x01;
            bean.level = "1";
            return bean;
        }     //避碰区域


        if (distance < dis_paz && Math.abs(h0 - h1) < h_paz)      //保护区域
        {
            if (line_in_circle(lat0, lon0, h0, ve0, vn0, vu0, lat1, lon1, h1, ve1, vn1, vu1, dis_caz, h_caz, time_caz, distance) == 0) {
                bean.result = 0x04;
                bean.level = "3";
                return bean;    //黄,改成绿闪吧
            } else {
                bean.result = 0x02;
                bean.level = "2";
                return bean;    //黄闪
            }
        }

        if (line_in_circle(lat0, lon0, h0, ve0, vn0, vu0, lat1, lon1, h1, ve1, vn1, vu1, dis_paz, h_paz, time_paz, distance) == 0) {
            bean.result = 0x10;
            bean.level = "5";
            return bean;    //safe
        } else {
            bean.result = 0x08;
            bean.level = "4";
            return bean;       //绿闪
        }

    }
//    public ImpactWarningBean safe_level(double lat0, double lon0, double h0, double ve0, double vn0, double vu0, double lat1, double lon1, double h1, double ve1, double vn1, double vu1, double dis_caz, double h_caz, double time_caz, double dis_paz, double h_paz, double time_paz, double distance) {
//        ImpactWarningBean bean = new ImpactWarningBean();
//
//        //避碰区域
//        if (distance < dis_caz && Math.abs(h0 - h1) < h_caz) {
//            bean.result = 0x01;
//            bean.level = "1";
//            Log.i("hahaha","hahahalevel1");
//            return bean;
//        }
//
//        if (distance < dis_paz && Math.abs(h0 - h1) < h_paz)      //保护区域
//        {
//            if (line_in_circle(lat0, lon0, h0, ve0, vn0, vu0, lat1, lon1, h1, ve1, vn1, vu1, dis_caz, h_caz, time_caz, distance) == 0) {
//                bean.result = 0x04;
//                bean.level = "3";
//                Log.i("hahaha","hahahalevel3");
//                return bean;    //黄闪
//            } else {
//                bean.result = 0x02;
//                bean.level = "2";
//                Log.i("hahaha","hahahalevel2");
//                return bean;    //黄快闪
//            }
//
//        }
//        if (line_in_circle(lat0, lon0, h0, ve0, vn0, vu0, lat1, lon1, h1, ve1, vn1, vu1, dis_paz, h_paz, time_paz, distance) == 0) {
//            bean.result = 0x10;
//            bean.level = "5";
//            Log.i("hahaha","hahahalevel5");
//            return bean;    //safe
//        } else {
//            bean.result = 0x08;
//            bean.level = "4";
//            Log.i("hahaha","hahahalevel4");
//            return bean;       //绿闪
//        }
//
//    }


    //  速度单位化为 m/s ，距离 m ，时间s，以本机追击，他机不动进行解算，distance>dis时进入此种判断,0 不相交，1 相交
    public int line_in_circle(double lat0, double lon0, double h0, double ve0, double vn0, double vu0, double lat1, double lon1, double h1, double ve1, double vn1, double vu1, double dis, double h, double time, double distance) {

        double[] a = new double[2];  //    0 纬度   1  经度

        double vg = Math.sqrt(Math.pow((ve0 - ve1), 2) + Math.pow((vn0 - vn1), 2));
        double dis0 = vg * time;                                              ////实际距离

        double angle0 = NavTool.angle_trans(ve0 - ve1, vn0 - vn1);   /////////////////////////XG
        double angle1 = NavTool.CAz(lat0, lon0, h0, lat1, lon1, h1);
        double angle_jj = Math.abs(angle1 - angle0);

        double angle_jjarc = angle_jj * Math.PI / 180;

        double angle_in = Math.asin(dis / distance);
        double angle2 = angle_in * 180 / Math.PI;

        if ((h0 > h1 + h) && (h0 + time * vu0 < h1 + h) && (distance > dis))    //上外 向下
        {

            if (angle_jj > angle2)                      //不相交    //////XG
                return 0;

            double disx = -vg * (h0 - h1 - h) / vu0;       //平面相交
            a = NavTool.PosVd11(lat1, lon1, angle0, disx);

            if (NavTool.CRange(a[0], a[1], h1, lat1, lon1, h1) <= dis)    //相交
                return 1;

            double dis1 = distance * Math.cos(angle_jjarc) - Math.sqrt(Math.pow(dis, 2) - Math.pow(distance * Math.sin(angle_jjarc), 2)); ///切点距离       XG

            if (dis0 < dis1)                    //不相交
                return 0;
            else {
                if (Math.abs(h0 + dis1 * vu0 / vg - h1) <= h)
                    return 1;
                else
                    return 0;
            }

        } else if ((h0 < h1 - h) && (h0 + time * vu0 > h1 - h) && (distance > dis))    //下外 向上
        {

            if (angle_jj > angle2)                      //不相交     //////XG
                return 0;

            double disx = vg * (h1 - h - h0) / vu0;       //平面相交
            a = NavTool.PosVd11(lat1, lon1, angle0, disx);

            if (NavTool.CRange(a[0], a[1], h1 - h, lat1, lon1, h1 - h) <= dis)    //相交
                return 1;

            double dis1 = distance * Math.cos(angle_jjarc) - Math.sqrt(Math.pow(dis, 2) - Math.pow(distance * Math.sin(angle_jjarc), 2)); ///切点距离      XG

            if (dis0 < dis1)                    //不相交
                return 0;
            else {
                if (Math.abs(h0 + dis1 * vu0 / vg - h1) <= h)
                    return 1;
                else
                    return 0;
            }
        } else if ((h0 > h1 + h) && (h0 + time * vu0 < h1 + h) && (distance < dis))    //上内 向下
        {
            double disx = -vg * (h0 - h1 - h) / vu0;       //平面相交
            a = NavTool.PosVd11(lat1, lon1, angle0, disx);

            if (NavTool.CRange(a[0], a[1], h1 + h, lat1, lon1, h1 + h) <= dis)    //相交
                return 1;
            else
                return 0;
        } else if ((h0 < h1 - h) && (h0 + time * vu0 > h1 - h) && (distance < dis))    //下内  向上
        {
            double disx = vg * (h1 - h - h0) / vu0;       //平面相交
            a = NavTool.PosVd11(lat1, lon1, angle0, disx);

            if (NavTool.CRange(a[0], a[1], h1 - h, lat1, lon1, h1 - h) <= dis)    //相交
                return 1;
            else
                return 0;
        } else      //中外
        {

            if (angle_jj > angle2)                      //不相交      //////XG
                return 0;

            double dis1 = distance * Math.cos(angle_jjarc) - Math.sqrt(Math.pow(dis, 2) - Math.pow(distance * Math.sin(angle_jjarc), 2)); ///切点距离    XG

            if (dis0 < dis1)                    //不相交
                return 0;
            else {
                if (Math.abs(h0 + dis1 * vu0 / vg - h1) <= h)
                    //走的这里
                    return 1;
                else
                    return 0;
            }
        }
    }

//    public int line_in_circle(double lat0, double lon0, double h0, double ve0, double vn0, double vu0, double lat1, double lon1, double h1, double ve1, double vn1, double vu1, double dis, double h, double time, double distance) {
//
//        double[] a = new double[2];  //    0 纬度   1  经度
//
//        double vg = Math.sqrt(Math.pow((ve0 - ve1), 2) + Math.pow((vn0 - vn1), 2));
//        double dis0 = vg * time;                                              ////实际距离
//
//
//        double angle0 = NavTool.angle_trans(ve0 - ve1, vn0 - vn1);
//        double angle1 = NavTool.CAz(lat0, lon0, h0, lat1, lon1, h1);
//        double angle_jj = Math.abs(angle1 - angle0);
//
//        double angle_in = Math.asin(dis / distance);
//        double angle2 = angle_in * 180 / Math.PI;
//
//        if (angle_jj > angle2)                      //不相交
//            return 0;
//
//        if ((h0 > h1 + h) && (h0 + time * vu0 < h1 + h) && (distance > dis))    //上外 向下
//        {
//
//            double disx = -vg * (h0 - h1 - h) / vu0;       //平面相交
//            a = NavTool.PosVd11(lat1, lon1, angle0, disx);
//
//            if (NavTool.CRange(a[0], a[1], h1, lat1, lon1, h1) <= dis)    //相交
//                return 1;
//
//            double dis1 = distance * Math.cos(angle_jj) - Math.sqrt(Math.pow(dis, 2) - Math.pow(distance * Math.sin(angle_jj), 2)); ///切点距离
//
//            if (dis0 < dis1)                    //不相交
//                return 0;
//            else {
//                if (Math.abs(h0 + dis1 * vu0 / vg - h1) <= h)
//                    return 1;
//                else
//                    return 0;
//            }
//
//        } else if ((h0 < h1 - h) && (h0 + time * vu0 > h1 - h) && (distance > dis))    //下外 向上
//        {
//            double disx = vg * (h1 - h - h0) / vu0;       //平面相交
//            a = NavTool.PosVd11(lat1, lon1, angle0, disx);
//
//            if (NavTool.CRange(a[0], a[1], h1 - h, lat1, lon1, h1 - h) <= dis)    //相交
//                return 1;
//
//            double dis1 = distance * Math.cos(angle_jj) - Math.sqrt(Math.pow(dis, 2) - Math.pow(distance * Math.sin(angle_jj), 2)); ///切点距离
//
//            if (dis0 < dis1)                    //不相交
//                return 0;
//            else {
//                if (Math.abs(h0 + dis1 * vu0 / vg - h1) <= h)
//                    return 1;
//                else
//                    return 0;
//            }
//        } else if ((h0 > h1 + h) && (h0 + time * vu0 < h1 + h) && (distance < dis))    //上内 向下
//        {
//            double disx = -vg * (h0 - h1 - h) / vu0;       //平面相交
//            a = NavTool.PosVd11(lat1, lon1, angle0, disx);
//
//            if (NavTool.CRange(a[0], a[1], h1 + h, lat1, lon1, h1 + h) <= dis)    //相交
//                return 1;
//            else
//                return 0;
//        } else if ((h0 < h1 - h) && (h0 + time * vu0 > h1 - h) && (distance < dis))    //下内  向上
//        {
//            double disx = vg * (h1 - h - h0) / vu0;       //平面相交
//            a = NavTool.PosVd11(lat1, lon1, angle0, disx);
//
//            if (NavTool.CRange(a[0], a[1], h1 - h, lat1, lon1, h1 - h) <= dis)    //相交
//                return 1;
//            else
//                return 0;
//        } else {
//
//            double dis1 = distance * Math.cos(angle_jj) - Math.sqrt(Math.pow(dis, 2) - Math.pow(distance * Math.sin(angle_jj), 2)); ///切点距离
//
//            if (dis0 < dis1)                    //不相交
//                return 0;
//            else {
//                if (Math.abs(h0 + dis1 * vu0 / vg - h1) <= h)
//                    return 1;
//                else
//                    return 0;
//            }
//        }
//    }


    //已知载机和空域里飞机 纬、经、高、东速度、北速度、天向速度(升降速度)，判断是否有相撞可能
    //预测1分钟
    double h_safe = 300;  //6000避碰  12000保护

    public int is_safe(double lat1, double lon1, double h1, double ve1, double vn1, double vu1, double lat2, double lon2, double h2, double ve2, double vn2, double vu2, double distance) {
        double delt_h = Math.abs(h2 - h1);   //m
        double delt_vu = Math.abs(vu2 - vu1);  //m/s
        double lat3 = 0, lon3 = 0;

        //已经处在了报警区域
        if (distance < 6000 && delt_h < 300)     //避碰区域
            return 0x01;

        //已经处在了警告区域
        if (distance < 12000 && delt_h < 300)      //保护区域
            return 0x02;

        double delt_distance;
        double delt_distance1;

        double h0;
        double l1;

        if ((delt_h - h_safe) < 60 * delt_vu && (h2 - h1) * (vu2 - vu1) <= 0)   //相向而行
        {
            double[] a = new double[2];

            double radius = Math.sqrt(Math.pow((ve1 - ve2) / 60, 2) + Math.pow((vn1 - vn2) / 60, 2));

            double angle = NavTool.angle_trans(ve1 - ve2, vn1 - vn2);//计算角度
            a = NavTool.PosVd11(lat1, lon1, angle, radius * 1000);//计算目标点经纬度

            lat3 = a[0];
            lon3 = a[1];

            delt_distance = NavTool.CRange(lat3, lon3, h1 + (vu1 - vu2) * 60, lat2, lon2, h2);//计算两点间距离

            if (delt_distance < 6000)
                return 0x04;
            if (delt_distance < 12000)
                return 0x08;

            h0 = area_cal(lat1, lon1, lat2, lon2, lat3, lon3);//已知三点,求面积,有符号
            h0 = Math.abs(h1 / 2.0);

            l1 = Math.pow(distance * distance - h0 * h0, 0.5);

            //60秒后处于报警区域
            if (h0 < 6000) {
                delt_distance1 = NavTool.CRange(lat3, lon3, h1 + (vu1 - vu2) * 60, lat1, lon1, h1);//计算两点间距离
                if (delt_distance1 > l1)
                    return 0x04;
            }

            //60秒后处于警告区域
            if (h0 < 12000) {
                delt_distance1 = NavTool.CRange(lat3, lon3, h1 + (vu1 - vu2) * 60, lat1, lon1, h1);//计算两点间距离
                if (delt_distance1 > l1)
                    return 0x08;
            }

        }
        //没有碰撞危险
        return 0x0F;

    }

    //已知三点,求面积,有符号
    public double area_cal(double ax, double ay, double bx, double by, double cx, double cy) {
        double c = (ax - cx) * (by - cy) - (ay - cy) * (bx - cx);
        return c;
    }

    //  空域超限判断
    //(x0,y0)(x1,y1)为一线段   true  相交  false 不相交
    public boolean CheckCross(double x0, double y0, double x1, double y1, double xx0, double yy0, double xx1, double yy1) {
        double[] v1 = new double[2];//x,y
        double[] v2 = new double[2];//x,y
        double[] v3 = new double[2];//x,y

        double area1 = area_cal(x0, y0, x1, y1, xx0, yy0);
        double area2 = area_cal(x0, y0, x1, y1, xx1, yy1);

        if (area1 * area2 > 0)
            return false;

        double area3 = area_cal(xx0, yy0, xx1, yy1, x0, y0);
        double area4 = area_cal(xx0, yy0, xx1, yy1, x1, y1);

        if (area3 * area4 > 0)
            return false;

        return true;
    }

    //纬度、经度、高度、地速、升降速度、航向、空域文件
    public byte ky_warnning(double lat, double lon, double h, double vg, double v_up, double hj_angle, KongYuBean kyb) {
        int time = 60 * 1000;

        double angle = hj_angle * 2 * 3.1415926 / 360.0;

        double[] aa = new double[2];

        aa = NavTool.PosVd11(lat, lon, hj_angle, vg / 3.6 * 60);//计算目标点经纬度

        double x0 = aa[0];  //预测到点
        double y0 = aa[1];

        double h1 = 0;
        if (v_up >= 0)
            h1 = h + v_up * time;
        else if (v_up < 0)
            h1 = h - Math.abs(v_up) * time;

        int i, t;

        double a, b, c, d;

        int k = 0;

        while (kyb != null) {
//        for (int k=0;k<listkyb.size();k++){
//            if (listkyb.get(k) != null){
            if (PointInPolygon(lon, lat, kyb) == true) {//面内
                if (h >= kyb.getKymingd() && h <= kyb.getKymaxgd())//即时飞行在高度值允许范围内
                    return 0x02;

                for (i = 0; i < kyb.getKyds(); i++) {
                    a = Double.parseDouble(kyb.getKywds().get(i));
                    b = Double.parseDouble(kyb.getKyjds().get(i));

                    t = i + 1;

                    if (t == kyb.getKyds())
                        t = 0;

                    c = Double.parseDouble(kyb.getKywds().get(t));
                    d = Double.parseDouble(kyb.getKyjds().get(t));

                    if (CheckCross(lat, lon, x0, y0, a, b, c, d) == true)//预测前方60s的一个点????????
                        return 4;
                }

                if (h1 > kyb.getKymingd() && h1 < kyb.getKymaxgd())//预测值超过高度值
                    return 8;

                return 0xf;//在空域内
            } else //面外
                k++;
        }


//        }

        return 1;  //不在所有空域内

    }

    //纬度、经度、高度、地速、升降速度、航向、空域文件
    public byte ky_warnning(double lat, double lon, double h, double vg, double v_up, double hj_angle, List<KongYuBean> listkyb) {//现在用的
        int time = 60;
        GaojingPreference preference = new GaojingPreference(MyApplication.getMyApplication());
        String airspace_warning_time = preference.getGaoJingSetInfo().airspace_warning_time;
        if (!TextUtils.isEmpty(airspace_warning_time)) {
            time = Integer.parseInt(airspace_warning_time);
        }
        double angle = hj_angle * 2 * 3.1415926 / 360.0;

        double[] aa = new double[2];

        aa = NavTool.PosVd11(lat, lon, hj_angle, vg / 3.6 * time);//计算目标点经纬度

        double x0 = aa[0];  //预测到点
        double y0 = aa[1];

        double h1 = 0;
        if (v_up >= 0)
            h1 = h + v_up * time;
        else if (v_up < 0)
            h1 = h - Math.abs(v_up) * time;

        int i, t;

        double a, b, c, d;

        int k = 0;

        while (k < listkyb.size() && listkyb.get(k) != null) {
//        for (int k=0;k<listkyb.size();k++){
//            if (listkyb.get(k) != null){
            if (PointInPolygon(lon, lat, listkyb.get(k)) == true) {//面内
                if (h < listkyb.get(k).getKymingd() || h > listkyb.get(k).getKymaxgd())//即时飞行高度超限
                    return 0x02;

                for (i = 0; i < listkyb.get(k).getKyds(); i++) {
                    a = Double.parseDouble(listkyb.get(k).getKywds().get(i));
                    b = Double.parseDouble(listkyb.get(k).getKyjds().get(i));

                    t = i + 1;

                    if (t == listkyb.get(k).getKyds())
                        t = 0;

                    c = Double.parseDouble(listkyb.get(k).getKywds().get(t));
                    d = Double.parseDouble(listkyb.get(k).getKyjds().get(t));

                    if (CheckCross(lat, lon, x0, y0, a, b, c, d) == true)//预测前方60s的一个点???????? //60s后即将飞出空域外（水平面）
                        return 4;
                }

                if (h1 <= listkyb.get(k).getKymingd() || h1 >= listkyb.get(k).getKymaxgd())//60S后垂直高度即将超过允许范围
                    return 8;

                return 0xf;//在空域内（在水平或者垂直方向均在可飞空域，并且在60s预测后也在正常范围内）
            } else //面外
                k++;
        }
//        }
        return 1;  //不在所有空域内

    }

    //纬度  经度
    public boolean PointInPolygon(double X, double Y, KongYuBean kongYuBean) {

        double p1x = 0.0, p1y = 0.0;

        double p2x = 0.0, p2y = 0.0;

        int i = 0;
        int nCross = 0;  //交点个数

        for (i = 0; i < kongYuBean.getKyds(); i++) {
            p1x = Double.parseDouble(kongYuBean.getKyjds().get(i));
            p1y = Double.parseDouble(kongYuBean.getKywds().get(i));

            int t = i + 1;
            if (t == kongYuBean.getKyds())
                t = 0;

            p2x = Double.parseDouble(kongYuBean.getKyjds().get(t));
            p2y = Double.parseDouble(kongYuBean.getKywds().get(t));

            if (p1y == p2y)
                continue;
            if (Y < Math.min(p1y, p2y))
                continue;
            if (Y > Math.max(p1y, p2y))
                continue;

            double x = (double) (Y - p1y) * (double) (p2x - p1x) / (double) (p2y - p1y) + p1x;

            if (x > X)
                nCross++;
        }

        if (nCross % 2 == 1)
            return true;   //面内
        else
            return false;   //面外

    }

    //纬度、经度、高度、地速、升降速度、航向、空域文件
    public byte ky_warnning(double lat, double lon, double h, double vg, double v_up, double hj_angle, ky_sx[] kyinfo) {
        int time = 60;

        double angle = hj_angle * 2 * 3.1415926 / 360.0;

        double[] aa = new double[2];

        aa = NavTool.PosVd11(lat, lon, hj_angle, vg / 3.6 * 60);//计算目标点经纬度

        double x0 = aa[0];  //预测到点
        double y0 = aa[1];

        double h1 = h + v_up * time;

        int i, t;

        double a, b, c, d;

        int k = 0;

        while (kyinfo[k] != null) {
            if (PointInPolygon(lon, lat, kyinfo[k]) == true) {
                if (h > kyinfo[k].height)
                    return 0x02;

                for (i = 0; i < kyinfo[k].num; i++) {
                    a = kyinfo[k].lat[i];
                    b = kyinfo[k].lon[i];

                    t = i + 1;

                    if (t == kyinfo[k].num)
                        t = 0;

                    c = kyinfo[k].lat[t];
                    d = kyinfo[k].lon[t];

                    if (CheckCross(lat, lon, x0, y0, a, b, c, d) == true)
                        return 4;
                }

                if (h1 > kyinfo[k].height)
                    return 8;

                return 0xf;
            } else
                k++;

        }

        return 1;  //不在所有空域内

    }

    public boolean PointInPolygon(double X, double Y, ky_sx kyinfo)//纬度  经度
    {

        double p1x = 0.0, p1y = 0.0;

        double p2x = 0.0, p2y = 0.0;

        int i = 0;
        int nCross = 0;  //交点个数

        for (i = 0; i < kyinfo.num; i++) {
            p1x = kyinfo.lon[i];
            p1y = kyinfo.lat[i];

            int t = i + 1;
            if (t == kyinfo.num)
                t = 0;

            p2x = kyinfo.lon[t];
            p2y = kyinfo.lat[t];

            if (p1y == p2y)
                continue;
            if (Y < Math.min(p1y, p2y))
                continue;
            if (Y > Math.max(p1y, p2y))
                continue;

            double x = (double) (Y - p1y) * (double) (p2x - p1x) / (double) (p2y - p1y) + p1x;

            if (x > X)
                nCross++;
        }

        if (nCross % 2 == 1)
            return true;   //面内
        else
            return false;   //面外

    }

    public class ky_sx {
        public boolean is_kf;   //是否禁飞区
        public int num;  //空域边界点点数目
        public int height;  //空域允许高度
        public String name;
        public double[] lat = new double[10];
        public double[] lon = new double[10];

    }


}
