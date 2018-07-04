package com.hanke.navi.skyair.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.pop.bean.AirwayDeviationBean;
import com.hanke.navi.skyair.pop.bean.HangLuBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.jcpop.JinjinPop;
import com.hanke.navi.skyair.pop.jcpop.ZhuoLuPop;
import com.hanke.navi.skyair.pop.navpop.hl.HLPop;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;
import com.hanke.navi.skyair.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Che on 2017/8/28.
 * 这里需要重新理一下思路,这里是用来计算导航里面的相关事情的
 * 例如飞过的航线的变色以及etc和eta时间
 * eta etc 时间,需要的是gps的时间,然后和飞行的时间做运算,而不是用
 * 当前的时间.
 * gps时间的话,就应该使用从北斗里面解析出来的时间,不管是在正常的飞行
 * 还是在回放的时候,只要使用从北斗里面解析出来的时间,就没有问题
 */
public class LandNavService extends IntentService {

    private Timer timer;
    public int currentIndex = 0;
    public boolean hasAleradyChange = false;
    //    private String timeFromGps;
    public double lastFlySpeed;
    private int nextPointTime;
    private GaojingPreference preference;
    private static LandNavService instance;
    private Nav nav;

    public LandNavService() {
        super("LandNavService");
    }

    public static synchronized LandNavService getService() {
        return instance;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        instance = this;
        preference = new GaojingPreference(getApplicationContext());
        nav = new Nav(getApplicationContext());
        currentIndex = 1;
        //这里使用当前的时间.
        nextPointTime = 0;
//        timeFromGps = TimeUtil.getCurrentTime();
        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    private Handler landHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                PlaneInfoBean bean = (PlaneInfoBean) msg.obj;
                ZhuoLuPop zhuoLuPop = new ZhuoLuPop(MyApplication.getAppContext(), bean.flyHeight);
                zhuoLuPop.showPopWindow(MainActivity.instence.mapView);

                JinjinPop jinjinPop = new JinjinPop(MyApplication.getAppContext(), bean.distanceWithHomePlane);
                jinjinPop.showPopWindow(MainActivity.instence.mapView);

                //弹出了之后停止service
                stopSelf();
                MyApplication.getMyApplication().flyLineList.clear();
                timer.cancel();
            } else if (msg.what == 1) {
                int[] twoTime = (int[]) msg.obj;
                MyApplication.getMyApplication().nextPointTime = twoTime[0];
                MyApplication.getMyApplication().remainderTime = twoTime[1];
                //这个service不应该能够控制MyAplication里面的额Gps时间,那个只能是从clienttask里面解析出来,然后再赋值的
//                MyApplication.getMyApplication().timeFromGps = timeFromGps;

            } else if (msg.what == 2) {
                int deviateDistance = (int) msg.obj;
                MyApplication.getMyApplication().deviateDistance = deviateDistance;
            } else if (msg.what == 1001) {
                //代表执行完成了这次航线
                MainActivity.instence.flag_zx = true;
                Toast.makeText(getApplicationContext(), "导航执行完毕", Toast.LENGTH_LONG).show();
                //删掉导航的点和线
                for (int x = 0; x < MyApplication.getMyApplication().hlMarkerList.size(); x++) {
                    MyApplication.getMyApplication().hlMarkerList.get(x).remove();
                }
                for (int x = 0; x < MainActivity.instence.listPolyline.size(); x++) {
                    MainActivity.instence.listPolyline.get(x).remove();
                }
                MyApplication.getMyApplication().hlMarkerList.clear();
                MainActivity.instence.listPolyline.clear();

                //停止掉service
                Intent intent = new Intent(getApplicationContext(), LandNavService.class);
                stopService(intent);
            }
        }
    };

    boolean isNeedAdd = false;

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (MainActivity.instence != null) {
                if (MainActivity.instence.flag_zx == false) {
                    //正在执行航路计划,正在判断是否到达.
                    Log.i("hhh", "正在执行航路计划,正在判断是否到达.");
                    //不停的拿到本机的经纬度
                    PlaneInfoBean bean = MyApplication.getMyApplication().homePlane;
                    LatLng homeLatlng = bean.latLng;
                    //这个时候需要拿到正在执行的导航的目的地的经纬度信息.
                    AirportSetBean airportSet = preference.getAirportSet();
                    LatLng destination = null;
                    if (!TextUtils.isEmpty(airportSet.airportLat) && !TextUtils.isEmpty(airportSet.airportLon)) {
                        destination = new LatLng(Double.parseDouble(airportSet.airportLat), Double.parseDouble(airportSet.airportLon));
                    }
                    if (homeLatlng != null) {
                        ArrayList<Marker> hlMarkerList = MyApplication.getMyApplication().hlMarkerList;
                        ArrayList<HangLuBean> data_hl = HLPop.instence.data_hl;
                        if (hlMarkerList.size() != 0) {
                            Marker marker = hlMarkerList.get(currentIndex);
                            Log.i("hahaha", "看看currentIndex" + currentIndex);
                            double temp = DistanceUtil.getInstance().getDistance(homeLatlng, marker.getPosition());
                            if (DistanceUtil.getInstance().getDistance(homeLatlng, marker.getPosition()) < 500 && !hasAleradyChange) {
                                hasAleradyChange = true;
                                //如果飞过的,就把标标成蓝色
                                marker.remove();
                                marker.getOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.biaolan)));
                                Marker marker2 = MainActivity.instance.aMap.addMarker(marker.getOptions());
                                MyApplication.getMyApplication().hlMarkerList.remove(currentIndex);
                                MyApplication.getMyApplication().hlMarkerList.add(currentIndex, marker2);
                                Log.i("hahaha", "hllist的长度142:" + MyApplication.getMyApplication().hlMarkerList.size());
                                isNeedAdd = true;
                                //然后把这个点放到集合了里面去
                                //这条是之前飞过的,变成蓝色,表示成已经飞了的颜色
                                Log.i("hahaha", "改红色listpolinesize" + MainActivity.instence.listPolyline.size() + "......x" + currentIndex);
                                Polyline polyline = MainActivity.instence.listPolyline.get(currentIndex - 1);
//                            polyline.remove();
                                polyline.getOptions().color(Color.BLUE);
                                Polyline polylineNew = MainActivity.instance.aMap.addPolyline(polyline.getOptions());
                                MainActivity.instence.listPolyline.add(polylineNew);
                                if (currentIndex < hlMarkerList.size() - 1) {
                                    Log.i("hahaha", "改红色x" + currentIndex);
                                    //改点的颜色.
                                    Marker nextMarker = hlMarkerList.get(currentIndex + 1);
                                    nextMarker.remove();
                                    nextMarker.getOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.biaofen)));
                                    Marker marker1 = MainActivity.instance.aMap.addMarker(nextMarker.getOptions());
                                    MyApplication.getMyApplication().hlMarkerList.remove(currentIndex + 1);
                                    MyApplication.getMyApplication().hlMarkerList.add(currentIndex + 1, marker1);
                                    //改线的颜色
                                    Polyline polylineFlying = MainActivity.instence.listPolyline.get(currentIndex);
                                    polylineFlying.getOptions().color(Color.RED);
                                    Polyline polyline1 = MainActivity.instance.aMap.addPolyline(polylineFlying.getOptions());
//                                //改了颜色的.
                                    MainActivity.instence.listPolyline.add(polyline1);
                                    currentIndex++;
                                    Marker newmarker = hlMarkerList.get(currentIndex);
                                    HangLuBean hangLuBean = data_hl.get(currentIndex);
//                                    nextPointTime = (DistanceUtil.getInstance().caculateArriveTime(homeLatlng, newmarker.getPosition(), bean.flySpeed));
                                    double[] ceta = nav.CETA(bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight,
                                            newmarker.getPosition().latitude, newmarker.getPosition().longitude, hangLuBean.getGaodu(), bean.flySpeed, bean.upOrDownSpeed,360 - bean.flyAngle);
                                    Log.i("ceta11111", "hour" + ceta[0] + "min" + ceta[1] + "sec" + ceta[2]);
                                    if (MyApplication.getMyApplication().isBack) {
                                        preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps_lookback);
                                    } else {
                                        preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps);
                                    }
                                    Log.i("hahaha", "到了一个点来计算......" + TimeUtil.getCurrentTime());
                                } else {
                                    landHandler.sendEmptyMessage(1001);
                                }

                            } else {
                                hasAleradyChange = false;
                            }

                            if (preference.isShowEtaTime()) {
                                //计算当前位置和即将到达的点的距离,然后计算时间,这里需要注意的是,如果是在回放的时候,那么这个时候就不能使用currenttime了,要使用回放读取的gps时间
//                                if (lastFlySpeed == 0) {
                                //代表第一次进来的时候,计算一下
//                                    nextPointTime = (DistanceUtil.getInstance().caculateArriveTime(homeLatlng, marker.getPosition(), bean.flySpeed));
                                HangLuBean hangLuBean = data_hl.get(currentIndex);
                                double[] ceta = nav.CETA(bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight,
                                        hangLuBean.getWeidu(), hangLuBean.getJingdu(), hangLuBean.getGaodu(), bean.flySpeed, bean.upOrDownSpeed, 360 - bean.flyAngle);
                                Log.i("ceta2222", "hour" + ceta[0] + "min" + ceta[1] + "sec" + ceta[2]);
                                lastFlySpeed = bean.flySpeed;
                                //这里应该传入的是下一个的时间,和计算的时候gps的时间,这个时间可以从MyApplication里面来获取
                                if (MyApplication.getMyApplication().isBack) {
                                    //如果在回放的话,就使用从回放报文里面解析出来的时间
                                    preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps_lookback);
                                } else {
                                    //否则,就使用从当前发送过来的北斗报文里面得到的时间
                                    preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps);
                                }
//                                } else {
//                                    if (bean.flySpeed != lastFlySpeed) {
////                                        nextPointTime = (DistanceUtil.getInstance().caculateArriveTime(homeLatlng, marker.getPosition(), bean.flySpeed));
//                                        HangLuBean hangLuBean = data_hl.get(currentIndex);
//                                        double[] ceta = nav.CETA(bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight,
//                                                hangLuBean.getWeidu(), hangLuBean.getJingdu(), hangLuBean.getGaodu(), bean.flySpeed, bean.upOrDownSpeed, bean.flyAngle);
//                                        lastFlySpeed = bean.flySpeed;
//                                        if (MyApplication.getMyApplication().isBack) {
//                                            //如果在回放的话,就使用从回放报文里面解析出来的时间
//                                            preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps_lookback);
//                                        } else {
//                                            //否则,就使用从当前发送过来的北斗报文里面得到的时间
//                                            preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps);
//                                        }
//                                    }
//                                }

                                int totalTime = 0;
                                if (hlMarkerList.size() != 0) {
                                    List<Marker> markers = hlMarkerList.subList(currentIndex, hlMarkerList.size());
//                        if (hlMarkerList.size() > 2) {
                                    //计算还剩下的点之间的距离.
                                    double allHLPointTotalDistance_partone = DistanceUtil.getInstance().getAllHLPointTotalDistance(markers);
                                    double allHLPointTotalDistance_partwo = DistanceUtil.getInstance().getDistance(homeLatlng, hlMarkerList.get(currentIndex).getPosition());
                                    double totalDistance = allHLPointTotalDistance_partone + allHLPointTotalDistance_partwo;
                                    //剩余时间
                                    totalTime = (int) (totalDistance / (bean.flySpeed / 3.6));
//                        }
//                        else {
//                            totalTime = nextPointTime;
//                        }
                                    int[] twoTime = new int[2];
                                    twoTime[0] = nextPointTime;
                                    twoTime[1] = totalTime;
                                    Message msg1 = new Message();
                                    msg1.what = 1;
                                    msg1.obj = twoTime;
                                    landHandler.sendMessage(msg1);
                                }

                            }
                            if (preference.isShowXtkDistance()) {
                                //计算偏行距
                                Log.i("hahaha", "hllist的长度214:" + MyApplication.getMyApplication().hlMarkerList.size());
                                Marker startBean = hlMarkerList.get(currentIndex - 1);
                                Marker endBean = hlMarkerList.get(currentIndex);
                                HangLuBean startHanglu = data_hl.get(currentIndex - 1);
                                HangLuBean endHanglu = data_hl.get(currentIndex);
                                //预计到达下一个航路点的时间
                                AirwayDeviationBean airwayDeviationBean = new AirwayDeviationBean();
                                double[] ceta = nav.CETA(startHanglu.getWeidu(), startHanglu.getJingdu(), startHanglu.getGaodu(), endBean.getPosition().latitude, endBean.getPosition().longitude, endHanglu.getGaodu(), bean.flySpeed, bean.upOrDownSpeed, bean.flyAngle);
                                int deviateDistance = (int) nav.CXTK(startBean.getPosition().latitude, startBean.getPosition().longitude, startHanglu.getGaodu(), endBean.getPosition().latitude, endBean.getPosition().longitude, endHanglu.getGaodu(), bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight);
                                Message msg2 = new Message();
                                msg2.what = 2;
//                                airwayDeviationBean.ceta = ceta;
//                                airwayDeviationBean.deviateDistance = deviateDistance;
//                                msg2.obj = airwayDeviationBean;
                                msg2.obj = deviateDistance;
                                landHandler.sendMessage(msg2);
                            }


                            //这个是距离最后的终点的距离.这里部门不以航路的最后一个,我们以设置的机场为最终的目的地.
                            if (destination != null) {
                                double distance = DistanceUtil.getInstance().getDistance(homeLatlng, destination);
                                //还需要计算飞机和机场的角度
                                Nav nav = new Nav(getApplicationContext());
                                PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
                                double caz = nav.CAz(homeLatlng.latitude, homeLatlng.longitude, homePlane.flyHeight, Double.parseDouble(airportSet.airportLat), Double.parseDouble(airportSet.airportLon), Double.parseDouble(airportSet.airportHeight));
//                                if (caz >= (Double.parseDouble(airportSet.runAngle) - 60) && caz <= (Double.parseDouble(airportSet.runAngle) + 60)) {
                                if (distance <= Double.parseDouble(airportSet.fafDistance)) {
                                    //拿到当时飞机的高度
                                    double flyHeight = bean.flyHeight;
                                    bean.distanceWithHomePlane = distance;
                                    //然后开始弹出界面.
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = homePlane;
                                    landHandler.sendMessage(msg);
                                }
//                                }
                            }
                        }
                    }
                }
            }
        }
    };

    //着陆导航,一直比较本机现在的经纬度和最后的目的机场的经纬度做比较,如果到达了一定范围内的话,就启动着陆导航界面.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
