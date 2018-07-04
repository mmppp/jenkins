package com.hanke.navi.framwork.arith;

import android.content.Context;

/**
 * 导航算法类，包括了项目所需各种导航方法
 */
public class Nav {
    private Context context;

    public Nav() {

    }

    public Nav(Context context) {
        this.context = context;
    }

    //时间转换
    public double TimeTrans(double hour, double minut, double secd)//时 分 秒 ------> 秒
    {//时 分 秒 ------> 秒
        double rst = 0;
        rst = hour * 3600 + minut * 60 + secd;
        return rst;
    }

    //时间转换
    public double[] TimeTrans(double t)//秒-------------->时 分 秒
    {
        double[] rst = new double[3];
        rst[0] = Math.floor(t / 3600.0);//时
        rst[1] = Math.floor((t / 3600.0 - rst[0]) * 60);//分
        rst[2] = ((t / 3600.0 - rst[0]) * 60 - rst[2]) % 1;//秒
        return rst;
    }
//    public double[] TimeTrans(double t)//秒-------------->时 分 秒
//    {
//        double[] rst = new double[3];
//        rst[0] = Math.floor(t / 3600.0);//时
//        rst[1] = Math.floor((t / 3600.0 - rst[0]) * 60);//分
//        rst[2] = ((t / 3600.0 - rst[0]) * 60 - rst[2]) % 1;//秒
//        return rst;
//    }

    //计算角度
    public double angle_trans(double x, double y)//水平速度差、南北速度差

    {
        if (x == 0 && y > 0)
            return 0;
        if (x == 0 && y < 0)
            return 180;
        if (y == 0 && x < 0)
            return 270.0;
        if (y == 0 && x > 0)
            return 90.0;
        if (x > 0 && y > 0)
            return Math.atan(x / y ) / Math.PI * 180;
        if (x < 0 && y < 0)
            return (180 + Math.atan(x / y)  / Math.PI * 180);
        if (x > 0 && y < 0)
            return (180 + Math.atan(y / x)  / Math.PI * 180);
        if (x < 0 && y > 0)
            return (360 + Math.atan(x / y ) / Math.PI * 180);
        return 0;
    }
    //计算角度,有问题.会出现角度跳转,但是其实不是算法的问题,是因为经纬度,有时候会出现错误的传递
    public double getAngle(double lat_a, double lng_a, double lat_b, double lng_b) {
        double y = Math.sin(lng_b - lng_a) * Math.cos(lat_b);
        double x = Math.cos(lat_a) * Math.sin(lat_b) - Math.sin(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        if (brng <= 0) {
            brng = brng + 360;
        }
        if (brng == 0)
            brng = 0;

        return brng;
    }

    // 计算方位角pab。
    public double pab(double lat_a, double lng_a, double lat_b, double lng_b) {
        double d = 0;
        lat_a = lat_a * Math.PI / 180;
        lng_a = lng_a * Math.PI / 180;
        lat_b = lat_b * Math.PI / 180;
        lng_b = lng_b * Math.PI / 180;

        d = Math.sin(lat_a) * Math.sin(lat_b) + Math.cos(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
        d = Math.sqrt(1 - d * d);
        d = Math.cos(lat_b) * Math.sin(lng_b - lng_a) / d;
        d = Math.asin(d) * 180 / Math.PI;
        if (d < 0) {
            d = d + 360;
        }
        return d;
    }

    //计算目标点经纬度(已知一个点,航向,距离,求目标点的经纬度)
    //返回值 double[0] =维度  double[1] = 经度
    public double[] PosVd11(double lat0, double lon0, double azt, double range) {
        double A, B, C, D, q, x, y, w, r, a;
        double[] rst = new double[2];
        double ej2 = 0.006740229771718;
        double pi = Math.PI;
//        double pi =3.1415926;

        double b1 = lat0 * pi / 180;
        double a12 = azt * pi / 180;

        double l1 = lon0 * pi / 180;

        a = 6378137.0;

        A = Math.sqrt(1.0 + ej2 * Math.pow(Math.cos(b1), 4.0));
        B = Math.sqrt(1.0 + ej2 * Math.pow(Math.cos(b1), 2.0));
        C = Math.sqrt(1.0 + ej2);

        q = range * B * B / (a * C);
        x = A * Math.tan(q) * Math.sin(a12);
        y = B * Math.cos(b1) - Math.tan(q) * Math.sin(b1) * Math.cos(a12);

        if (x == 0 && y != 0)
            r = 0;

        if (y == 0 && x != 0)
            r = pi / 2;
        else
            r = Math.atan2(x, y);

        rst[1] = l1 + r / A;

        w = A * (rst[1] - l1) / 2;
        D = Math.asin(Math.sin(q) * (Math.cos(a12) - Math.sin(b1) * Math.sin(a12) * Math.tan(w) / A)) / 2.0;
        rst[0] = b1 + 2 * D * (B - 1.5 * ej2 * D * Math.sin(2.0 * b1 + 4.0 * B * D / 3.0));

        rst[1] *= 180.0 / pi;
        rst[0] *= 180.0 / pi;

        return rst;

    }

    //计算两点间距离
    public double CRange(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1)//已知两点经纬高，计算两点斜距
    {//已知两点经纬高，计算两点斜距
        long A = 6378137;//地球半径
        double f = 0.016695103237552;//偏心率
//        double f = 0.006695103237552;//偏心率
        double Pi = Math.PI;
//        double Pi = 3.14159265358979;
        double N0, N1, x0, y0, z0, x1, y1, z1, range;

        //转换为弧度
        lat0 = lat0 * Pi / 180;
        lon0 = lon0 * Pi / 180;
        lat1 = lat1 * Pi / 180;
        lon1 = lon1 * Pi / 180;

        N0 = A / (Math.sqrt(1 - f * Math.sin(lat0) * Math.sin(lat0)));
        N1 = A / (Math.sqrt(1 - f * Math.sin(lat1) * Math.sin(lat1)));

        //转换为空间直角坐标
        x0 = (N0 + alti0) * Math.cos(lat0) * Math.cos(lon0);
        y0 = (N0 + alti0) * Math.cos(lat0) * Math.sin(lon0);
        z0 = (N0 * (1 - f) + alti0) * Math.sin(lat0);

        x1 = (N1 + alti1) * Math.cos(lat1) * Math.cos(lon1);
        y1 = (N1 + alti1) * Math.cos(lat1) * Math.sin(lon1);
        z1 = (N1 * (1 - f) + alti1) * Math.sin(lat1);

        //计算斜距
        range = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) + (z1 - z0) * (z1 - z0));
        return range;

    }

    //计算两点相对方位,是第一个点相对于第二个点(当前的和机场的)
    public double CAz(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1)//已知两点经纬高，计算两点相对方位
    {
        //已知两点经纬高，计算两点相对方位
        long A = 6378137;
        double f = 0.006695103237552;
        double Pi = 3.14159265358979;
        double N0, N1, x0, y0, z0, x1, y1, z1, xg, yg, zg, az;


        //转换为弧度
        lat0 = lat0 * Pi / 180;
        lon0 = lon0 * Pi / 180;
        lat1 = lat1 * Pi / 180;
        lon1 = lon1 * Pi / 180;

        N0 = A / (Math.sqrt(1 - f * Math.sin(lat0) * Math.sin(lat0)));
        N1 = A / (Math.sqrt(1 - f * Math.sin(lat1) * Math.sin(lat1)));

        //转换为空间直角坐标
        x0 = (N0 + alti0) * Math.cos(lat0) * Math.cos(lon0);
        y0 = (N0 + alti0) * Math.cos(lat0) * Math.sin(lon0);
        z0 = (N0 * (1 - f) + alti0) * Math.sin(lat0);

        x1 = (N1 + alti1) * Math.cos(lat1) * Math.cos(lon1);
        y1 = (N1 + alti1) * Math.cos(lat1) * Math.sin(lon1);
        z1 = (N1 * (1 - f) + alti1) * Math.sin(lat1);

        //转换地平坐标
        xg = Math.sin(lon0) * (x1 - x0) - Math.cos(lon0) * (y1 - y0);     //////XG
        yg = Math.sin(lat0) * Math.cos(lon0) * (x1 - x0) + Math.sin(lat0) * Math.sin(lon0) * (y1 - y0) - Math.cos(lat0) * (z1 - z0);  //////XG
        zg = -Math.cos(lat0) * Math.cos(lon0) * (x1 - x0) - Math.cos(lat0) * Math.sin(lon0) * (y1 - y0) - Math.sin(lat0) * (z1 - z0);   //////XG

        //计算方位角
        if (yg == 0)
        {
            if (xg > 0)
                az = 270;
            else if (xg < 0)
                az = 90;
            else
                az = 0;
        }
        else
        {
            az = Math.atan(xg / yg);
            az = az * 180 / Pi;
        }
        if (yg > 0 && xg > 0)
            az = az - 180.0;
        else if (yg > 0 && xg < 0)
            az = az + 180.0;

        if (az < 0)    //////XG
            az = az + 360;    //////XG

        return az;
    }


//    public double CAz(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1)//已知两点经纬高，计算两点相对方位
//    {
//        //已知两点经纬高，计算两点相对方位
//        long A = 6378137;
//        double f = 0.006695103237552;
//        double Pi = 3.14159265358979;
//        double N0, N1, x0, y0, z0, x1, y1, z1, xg, yg, zg, az;
//
//
//        //转换为弧度
//        lat0 = lat0 * Pi / 180;
//        lon0 = lon0 * Pi / 180;
//        lat1 = lat1 * Pi / 180;
//        lon1 = lon1 * Pi / 180;
//
//        N0 = A / (Math.sqrt(1 - f * Math.sin(lat0) * Math.sin(lat0)));
//        N1 = A / (Math.sqrt(1 - f * Math.sin(lat1) * Math.sin(lat1)));
//
//        //转换为空间直角坐标
//        x0 = (N0 + alti0) * Math.cos(lat0) * Math.cos(lon0);
//        y0 = (N0 + alti0) * Math.cos(lat0) * Math.sin(lon0);
//        z0 = (N0 * (1 - f) + alti0) * Math.sin(lat0);
//
//        x1 = (N1 + alti1) * Math.cos(lat1) * Math.cos(lon1);
//        y1 = (N1 + alti1) * Math.cos(lat1) * Math.sin(lon1);
//        z1 = (N1 * (1 - f) + alti1) * Math.sin(lat1);
//
//        //转换地平坐标
//        xg = -Math.sin(lon0) * (x1 - x0) + Math.cos(lon0) * (y1 - y0);
//        yg = -Math.sin(lat0) * Math.cos(lon0) * (x1 - x0) - Math.sin(lat0) * Math.sin(lon0) * (y1 - y0) + Math.cos(lat0) * (z1 - z0);
//        zg = Math.cos(lat0) * Math.cos(lon0) * (x1 - x0) + Math.cos(lat0) * Math.sin(lon0) * (y1 - y0) + Math.sin(lat0) * (z1 - z0);
//
//        //计算方位角
//        if (yg == 0) {
//            if (xg > 0)
//                az = 270;
//            else if (xg < 0)
//                az = 90;
//            else
//                az = 0;
//        } else {
//            az = Math.atan(xg / yg);
//            az = az * 180 / Pi;
//        }
//        if (yg > 0 && xg > 0)
//            az = az - 180.0;
//        else if (yg > 0 && xg < 0)
//            az = az + 180.0;
//        if (az < 0) {
//            az = az + 360;
//        }
//        return az;
//    }

    //计算俯仰角
    public double CEle(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1)//已知两点经纬度，计算两点俯仰角
    {
        long A = 6378137;
        double f = 0.006695103237552;
        double Pi = 3.14159265358979;
        double N0, N1, x0, y0, z0, x1, y1, z1, xg, yg, zg, ele, range;


        //转换为弧度
        lat0 = lat0 * Pi / 180;
        lon0 = lon0 * Pi / 180;
        lat1 = lat1 * Pi / 180;
        lon1 = lon1 * Pi / 180;

        N0 = A / (Math.sqrt(1 - f * Math.sin(lat0) * Math.sin(lat0)));
        N1 = A / (Math.sqrt(1 - f * Math.sin(lat1) * Math.sin(lat1)));

        //转换为空间直角坐标
        x0 = (N0 + alti0) * Math.cos(lat0) * Math.cos(lon0);
        y0 = (N0 + alti0) * Math.cos(lat0) * Math.sin(lon0);
        z0 = (N0 * (1 - f) + alti0) * Math.sin(lat0);

        x1 = (N1 + alti1) * Math.cos(lat1) * Math.cos(lon1);
        y1 = (N1 + alti1) * Math.cos(lat1) * Math.sin(lon1);
        z1 = (N1 * (1 - f) + alti1) * Math.sin(lat1);

        //计算斜距
        range = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) + (z1 - z0));

        //转换地平坐标
        xg = -Math.sin(lon0) * (x1 - x0) + Math.cos(lon0) * (y1 - y0);
        yg = -Math.sin(lat0) * Math.cos(lon0) * (x1 - x0) - Math.sin(lat0) * Math.sin(lon0) * (y1 - y0) + Math.cos(lat0) * (z1 - z0);
        zg = Math.cos(lat0) * Math.cos(lon0) * (x1 - x0) + Math.cos(lat0) * Math.sin(lon0) * (y1 - y0) + Math.sin(lat0) * (z1 - z0);

        //计算俯仰角
        ele = Math.asin(zg / range) * 180 / Pi;

        return ele;


    }

    //计算大圆航线（点数可控） OK
//    public double[,] CDYpath(double lat0, double lon0, double lat1, double lon1, int N)//已知起始点坐标生成大圆航线
//    {
//        double t1, t2, a, b;
//        double Pi = 3.14159265358979;
//        int i;
//        double[,] rst = new double[2, N];///// lat lon
//        double[] x = new double[N];
//        double[] y = new double[N];
//        double[] T1 = new double[4];
//        double[] T2 = new double[4];
//
//
//        if ((lat0 != lat1) || (lon0 != lon1))
//        {
//            T1 = WToS(lat0, lon0, 0.0);
//            T2 = WToS(lat1, lon1, 0.0);
//
//            t1 = (lat1 - lat0) / (double)N;
//            t2 = (lon1 - lon0) / (double)N;
//
//            for (i = 0; i < N; i++)
//            {
//                x[i] = i * t1 + lat0;
//                y[i] = i * t2 + lon0;
//            }
//
//            lat0 = lat0 * Pi / 180.0;
//            lon0 = lon0 * Pi / 180.0;
//            lat1 = lat1 * Pi / 180.0;
//            lon1 = lon1 * Pi / 180.0;
//
//            a = (Math.tan(lat0) * Math.sin(lon1) - Math.tan(lat1) * Math.sin(lon0)) / (Math.cos(lon0) * Math.sin(lon1) - Math.sin(lon0) * Math.cos(lon1));
//            b = (Math.tan(lat1) * Math.cos(lon0) - Math.tan(lat0) * Math.cos(lon1)) / (Math.cos(lon0) * Math.sin(lon1) - Math.sin(lon0) * Math.cos(lon1));
//
//            for (i = 0; i < N; i++)
//            {
//                rst[0, i] = y[i];
//                rst[1, i] = Math.atan(a * Math.cos(rst[0, i] * Pi / 180.0) + b * Math.sin(rst[0, i] * Pi / 180.0)) * 180.0 / Pi;
//
//            }
//        }
//        else
//        {
//            t1 = (lat1 - lat0) / (double)N;
//            t2 = (lon1 - lon0) / (double)N;
//
//            for (i = 0; i < N; i++)
//            {
//                x[i] = i * t1 + lat0;
//                y[i] = i * t2 + lon0;
//            }
//
//            lat0 = lat0 * Pi / 180.0;
//            lon0 = lon0 * Pi / 180.0;
//            lat1 = lat1 * Pi / 180.0;
//            lon1 = lon1 * Pi / 180.0;
//
//            for (i = 0; i < N; i++)
//            {
//                rst[0, i] = x[i];
//                rst[1, i] = y[i];
//
//            }
//
//        }
//
//
//        return rst;
//
//    }

    //计算偏航距
    public double CXTK(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1, double lat, double lon, double alti) {// 0 起始点     1终止点    lat lon alti 飞机即时位置
        double xtk, a, b, w, p;
        double Pi = 3.14159265358979;
        long R = 6371400;
        double[] T1 = new double[3];
        double[] T2 = new double[3];
        double[] T3 = new double[3];


        if (lon0 != lon1)//经度不相等的情况
        {
            T1 = WToS(lat0, lon0, alti0);
            T2 = WToS(lat1, lon1, alti1);
            T3 = WToS(lat, lon, alti);

            b = (T2[2] * T1[0] - T1[2] * T2[0]) / (T1[0] * T2[1] - T1[1] * T2[0]);
            a = (T1[2] - b * T1[1]) / T1[0];

            w = (a * T3[0] + b * T3[1] - T3[2]) / (Math.sqrt(T3[0] * T3[0] + T3[1] * T3[1] + T3[2] * T3[2]) * (Math.sqrt(a * a + b * b + 1)));
            p = Pi / 2 - Math.acos(w);

            xtk = R * p;//单位m

        } else//经度相等的情况
        {
            xtk = (lon1 - lon0) * Math.cos(lat0) * 60;
        }
        return xtk;
    }

    //计算着陆偏差(长度单位)
    public double[] CLs(double lat, double lon, double alti, double latstd, double lonstd, double altistd, double azstd, double elestd) {
        double[] rst = new double[3];// az ele range偏差（长度单位）
        // double[] temp = new double[3];// az ele range偏差（角度单位）
        double az, ele, range;
        //
        az = CAz(latstd, lonstd, altistd, lat, lon, alti);
        ele = CEle(latstd, lonstd, altistd, lat, lon, alti);
        range = CRange(latstd, lonstd, altistd, lat, lon, alti);
        //得到角度偏差
        az = az - azstd;
        ele = ele - elestd;
        rst[0] = az;
        rst[1] = ele;
        rst[2] = range;

        return rst;
    }

    //0应该是当前的位置,1是目标的航路点
    public double[] CETA(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1, double Vg, double Vy,double track)
    {//速度单位为km/h
        double t, range, ele, V;
        double[] rst = new double[3];
        range = CRange(lat0, lon0, alti0, lat1, lon1, alti1);
        ele = CEle(lat0, lon0, alti0, lat1, lon1, alti1);
        double ang =( CAz(lat0, lon0, alti0, lat1, lon1, alti1)-track)*Math.PI/180;
        Vg = Vg * 1000.0 / 3600.0;

        V = Vg * Math.cos(ang) + Vy * Math.sin(ele);//合成速度
        t = (int)(range /Math.abs(V));
        rst = TimeTrans(t);

        return rst;
    }

    //计算ETA  时 分 秒
//    public double[] CETA(double lat0, double lon0, double alti0, double lat1, double lon1, double alti1, double Vg, double Vy) {//速度单位为km/h
//        double t, range, ele, V;
//        double[] rst = new double[3];
//        range = CRange(lat0, lon0, alti0, lat1, lon1, alti1);
//        ele = CEle(lat0, lon0, alti0, lat1, lon1, alti1);
//        Vg = Vg * 1000.0 / 3600.0;
//        Vy = Vy * 1000.0 / 3600.0;
//        V = Vg / Math.cos(ele) + Vy / Math.sin(ele);//合成速度
//        t = range / Vg;
//        rst = TimeTrans(t);
//
//        return rst;
//    }

//    //已知当前点位置、方位距离，计算目标点经纬度
//    public double[] PosVd(double lat0, double lon0, double azt, double range)
//    {// range(km) crange(nm)
//        double elev, lat_err, lon_err, alti_err, lat1, lon1, t0, t1, t2, range1, angle, rzd, az, az1, az11;
//        //int k;
//        double[] rst = new double[2];
//
//        double Pi = 3.14159265358979;
//
//
//        t0 = 0.2;
//        t2 = 0.2;
//        elev = 0.0;
//        az = azt - 180.0;
//        lat1 = 0.0;
//        lon1 = 0.0;
//
//        if (az > 180.0)
//        {
//            az = az - 360.0;
//        }
//        else if (az < -180.0)
//        {
//            az = az + 360.0;
//        }
//
//        lat_err = Math.abs(range * Math.cos(elev * Pi / 180.0) * Math.cos(az * Pi / 180.0) * 1000.0 / 60.0 / 1852.0);
//        lon_err = Math.abs(range * Math.cos(elev * Pi / 180.0) * Math.sin(az * Pi / 180.0) * 1000.0 / 60.0 / 1852.0 / Math.cos(lat0 * Pi / 180.0));
//        alti_err = range * Math.sin(elev * Pi / 180.0);
//
//        if (az <= -90 && az >= -180)
//        {
//            lat1 = lat0 + lat_err;
//            lon1 = lon0 + lon_err;
//        }
//        else if (az <= 0 && az > -90)
//        {
//            lat1 = lat0 - lat_err;
//            lon1 = lon0 + lon_err;
//        }
//        else if (az > 0 && az <= 90)
//        {
//            lat1 = lat0 - lat_err;
//            lon1 = lon0 - lon_err;
//        }
//        else if (az <= 180 && az > 90)
//        {
//            lat1 = lat0 + lat_err;
//            lon1 = lon0 - lon_err;
//        }
//        while (t0 > 0.0001 || t2 > 0.0001)
//        {
//            t0 = CRange(lat1, lon1, 0.0, lat0, lon0, 0.0) / 1000.0;
//            t1 = CEle(lat1, lon1, 0.0, lat0, lon0, 0.0);
//            t2 = CAz(lat1, lon1, 0.0, lat0, lon0, 0.0);
//            elev = 0;
//
//            range1 = Math.sqrt(range * range + t0 * t0 - 2 * range * t0 * Math.cos((t2 - azt) * Pi / 180.0));
//            angle = Math.acos((Math.pow(t0, 2) + Math.pow(range1, 4) - Math.pow(range, 2)) / (2 * range1 * t0)) * 180.0 / Pi;
//            rzd = range * Math.sin((180.0 - azt) * Pi / 180.0) * Math.sin((2 * azt - t2) * Pi / 180.0);
//
//            if (t2 < azt)
//            {
//                az1 = -(180.0 - t2 + angle);
//            }
//            else if (t0 > rzd)
//            {
//                az1 = angle - (180.0 - t2);
//            }
//            else
//            {
//                az1 = 180 - t2 - angle;
//            }
//
//
//            if (az1 > 180.0)
//            {
//                az1 = az1 - 360.0;
//
//            }
//            else if (az1 < -180.0)
//            {
//                az1 = az1 + 360.0;
//            }
//
//            if (az1 < 0.0)
//            {
//                az11 = az1 + 180.0;
//            }
//            else
//            {
//                az11 = az1 - 180.0;
//            }
//
//
//            if (az11 > 180.0)
//            {
//                az11 = az11 - 360;
//            }
//            else if (az11 < -180.0)
//            {
//                az11 = az11 - 360;
//            }
//
//            lat_err = Math.abs(range1 * Math.cos(elev * Pi / 180.0) * Math.cos(az11 * Pi / 180.0) * 1000.0 / 60.0 / 1852);
//            lon_err = Math.abs(range1 * Math.cos(elev * Pi / 180.0) * Math.sin(az11 * Pi / 180.0) * 1000.0 / 60.0 / 1852.0 / Math.cos(lat1 * Pi / 180.0));
//            alti_err = range * Math.sin(elev * Pi / 180.0);
//
//            if (az11 <= -90.0 && az11 >= -180.0)
//            {
//                lat1 = lat1 + lat_err;
//                lon1 = lon1 + lon_err;
//            }
//            else if (az11 <= 0.0 && az11 > -90.0)
//            {
//                lat1 = lat1 - lat_err;
//                lon1 = lon1 + lon_err;
//            }
//            else if (az11 > 0.0 && az11 <= 90.0)
//            {
//                lat1 = lat1 - lat_err;
//                lon1 = lon1 - lon_err;
//            }
//            else if (az11 <= 180.0 && az11 > 90.0)
//            {
//                lat1 = lat1 + lat_err;
//                lon1 = lon1 - lon_err;
//            }
//
//            t0 = Math.abs(CRange(lat1, lon1, 0, lat0, lon0, 0) * 1.852 / 1852 - range);
//            t1 = CEle(lat1, lon1, 0, lat0, lon0, 0);
//            t2 = Math.abs(CAz(lat1, lon1, 0, lat0, lon0, 0) - azt);
//        }
//        rst[0] = lat1;
//        rst[1] = lon1;
//
//        return rst;
//
//    }

    //偏置航线
//    public double[,] ShiftLine(double lat0, double lon0, double lat1, double lon1, double range, int N, char c)
//    {
//        double[,] rst = new double[2, N];// lat lon
//        double[] s = new double[2];//lat lon
//        double[] d = new double[2];//lat lon
//
//        //初始化
//        for (int i = 0; i < 2; i++)
//            for (int j = 0; j < N; j++)
//            {
//                rst[i, j] = 0;
//            }
//
//        double az;
//        az = CAz(lat0, lon0, 0, lat1, lon1, 0);
//        //判断方向
//        if (c == 'l')
//        {
//            az = az - 90;
//        }
//        else if (c == 'r')
//        {
//            az = az + 90;
//        }
//        else
//        {
//            return rst;//异常
//        }
//
//        s = PosVd(lat0, lon0, az, range);
//        d = PosVd(lat1, lon1, az, range);
//
//        rst = CDYpath(s[0], s[1], d[0], d[1], N);
//        return rst;
//    }

    //WGS-84坐标转换为空间直角坐标
    public double[] WToS(double lat, double lon, double alti)//WGS-84地心坐标转换为空间直角坐标系
    {
        double[] rst = new double[4];
        long A = 6378137;
        double f = 0.006695103237552;
        double Pi = 3.14159265358979;
        double N = 0.0, x = 0.0, y = 0.0, z = 0.0;

        //转换为弧度
        lat = lat * Pi / 180.0;
        lon = lon * Pi / 180.0;


        N = A / (Math.sqrt(1 - f * Math.sin(lat) * Math.sin(lat)));

        //转换为空间直角坐标
        x = (N + alti) * Math.cos(lat) * Math.cos(lon);
        y = (N + alti) * Math.cos(lat) * Math.sin(lon);
        z = (N * (1 - f) + alti) * Math.sin(lat);

        rst[0] = x;
        rst[1] = y;
        rst[2] = z;
        rst[3] = N;

        return rst;
    }


    public class point_3d {
        public double x;
        public double y;
        public double z;

    }

    public class multi_pt_3d {
        public int num;
        public point_3d[] pt = new point_3d[100];
    }


    public class Gps_Data {
        public long time;  //北京时间
        public double lon;
        public double lat;
        public double height;
        public double vi;
        public double ve;
        public double vn;
        public double vup;
        public double[] ps_lat = new double[60];
        public double[] ps_lon = new double[60];
        public double[] ps_height = new double[60];
        public int num;  //航迹数
        public double track;
        public String data_valid;
        public String lat_valid;
        public String lon_valid;

        public int inout_flag = 0xf;  //空域超限告警标志
        public int crack_flag = 0xf;   //防相撞告警标志

        public int flash_flag;   //闪烁标志
    }

    public class Ads_B_Data {
        public long time;
        public double lon;
        public double lat;
        public double height;
        public double ve;
        public double vn;
        public double vup;
        public double pre_height;
        public String flightID;
        public String HexAddress;
        public double track;
        public double Category;
        public String DataSource;
        public double rssi;
        public double vi;
        public double distance;

        public double[] ps_lat = new double[30];
        public double[] ps_lon = new double[30];
        public int num = 0;  //航迹数
        public int crack_flag = 0xf;   //防相撞告警标志

        public int flash_flag;   //闪烁标志

    }

}
