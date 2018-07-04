package com.hanke.navi.skyair.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.circle.OptionCircleView;
import com.hanke.navi.skyair.pop.bean.KongYuBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.bean.WarningResultBean;
import com.hanke.navi.skyair.pop.msgpop.msgbean.HangBanGJBean;
import com.hanke.navi.skyair.pop.tcpop.XinXiPop;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mahao on 2017/9/15.
 */

public class AirSpaceWarningService extends IntentService {
    public static Timer timer;
    public OptionCircleView circle_deng;
    private ArrayList<WarningResultBean> warningResultList;

    public AirSpaceWarningService() {
        super("AirSpaceWarningService");
    }

    //启动定时器,定时的检测当前飞机飞行的时候空域的状况.
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("hahaha", "airspaceservice启动了");
        circle_deng = MainActivity.instence.circle_deng;
        warningResultList = new ArrayList<>();
        timer = new Timer();
        timer.schedule(airSpaceWarningTask, 0, 800);
    }

    //需要的材料有维度,经度,高度,地速,升降速度,空域文件.
    TimerTask airSpaceWarningTask = new TimerTask() {
        @Override
        public void run() {
            PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
            if (homePlane != null && homePlane.latLng != null) {
                double lat = homePlane.latLng.latitude;
                double lon = homePlane.latLng.longitude;
                double flyHeight = homePlane.flyHeight;
                double flySpeed = homePlane.flySpeed;
                double upOrDownSpeed = homePlane.upOrDownSpeed;
                double flyAngle = homePlane.flyAngle;
                List<KongYuBean> data_ky = XinXiPop.instance.getData_ky();
                //计算出来结果,空域预警信息
                WarningResultBean kyWarningBean = new WarningResultBean();
                byte resultKy = 0;
                if (data_ky != null && data_ky.size() != 0) {
                    resultKy = MyApplication.getMyApplication().warnning.ky_warnning(lat, lon, flyHeight, flySpeed, upOrDownSpeed, 360 - flyAngle, data_ky);
                    kyWarningBean = getWarningLevel(resultKy);
                    kyWarningBean.warningTitle = "空域";
                }
                //这里需要询问一下,是否需要将角度减去90 180 270
                String warningLevel_impact = "5";
                int resultImpact = 0x10;
                String adsb_plane_num = "";
                HashMap<String, PlaneInfoBean> latLngHashMap = MyApplication.getMyApplication().latLngHashMap;
                if (latLngHashMap != null && latLngHashMap.size() != 0) {
                    Set<Map.Entry<String, PlaneInfoBean>> entries = latLngHashMap.entrySet();
                    Iterator<Map.Entry<String, PlaneInfoBean>> iterator = entries.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, PlaneInfoBean> next = iterator.next();
                        PlaneInfoBean value = next.getValue();
                        String warningLevel = value.warningLevel;
                        if (!TextUtils.isEmpty(warningLevel)) {
                            if (Integer.parseInt(warningLevel) < Integer.valueOf(warningLevel_impact)) {
                                warningLevel_impact = warningLevel;
                                resultImpact = value.warningInfo;
                                adsb_plane_num = value.planeNum;
                            }
                        }
                    }
                }
                //碰撞预警的等级和文字
                WarningResultBean impactWarningBean = new WarningResultBean();
                impactWarningBean.warningLevel = Integer.valueOf(warningLevel_impact);
                impactWarningBean.warningText = getImpactWarningText(resultImpact);
                impactWarningBean.textColor = getImpactWarningTextColor(resultImpact);
                impactWarningBean.adsbPlaneNum = adsb_plane_num;
                impactWarningBean.warningTitle = "碰撞";
                //撞地预警的等级和文字
                WarningResultBean impactLandBean = new WarningResultBean();
                boolean isImpactLandDanger = MyApplication.getMyApplication().isImpactLandDanger;
                boolean isImpactLandDanger30 = MyApplication.getMyApplication().isImpactLandDanger30;
//                int levelImpactLand = isImpactLandDanger ? 2 : 5;
                int levelImpactLand = 5;
                if (isImpactLandDanger30) {
                    levelImpactLand = 1;
                } else if (isImpactLandDanger) {
                    levelImpactLand = 2;
                }
                impactLandBean.warningLevel = levelImpactLand;
                if (levelImpactLand == 1) {
                    impactLandBean.warningText = "30s内即将撞地";
                    impactLandBean.textColor = Color.RED;
                } else if (levelImpactLand == 2) {
                    impactLandBean.warningText = "60s内即将撞地";
                    impactLandBean.textColor = Color.parseColor("#FFE1C300");
                } else {
                    impactLandBean.warningText = "";
                }
                impactLandBean.warningTitle = "撞地";

                //添加到集合里面去
                warningResultList.add(kyWarningBean);
                warningResultList.add(impactWarningBean);
                warningResultList.add(impactLandBean);

                Collections.sort(warningResultList);
                //这里用来控制灯
                Message message = Message.obtain();
                message.arg1 = warningResultList.get(0).warningLevel;
                circle_deng_Handler.sendMessage(message);
//                }

                //这里用来控制文字,这里按照优先级来觉定用第几行来显示文字.
                //优先级越高的显示在越上面
                Message msg1 = new Message();
                msg1.what = 1;
                msg1.obj = warningResultList.get(0);
                airspaceWarningHandler.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.what = 2;
                msg2.obj = warningResultList.get(1);
                airspaceWarningHandler.sendMessage(msg2);

                Message msg3 = new Message();
                msg3.what = 3;
                msg3.obj = warningResultList.get(2);
                airspaceWarningHandler.sendMessage(msg3);

                warningResultList.clear();

            }

        }
    };

    private boolean colorChange = false;
    private int temp = 0;


    Handler circle_deng_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int level = msg.arg1;
            if (level == 1) {
                if (colorChange) {
                    colorChange = !colorChange;
                    circle_deng.setColorCircle(Color.RED);
                } else {
                    colorChange = !colorChange;
                    circle_deng.setColorCircle(Color.TRANSPARENT);
                }
            } else if (level == 2) {
                if (colorChange) {
                    colorChange = !colorChange;
                    circle_deng.setColorCircle(Color.parseColor("#FFE1C300"));
                } else {
                    colorChange = !colorChange;
                    circle_deng.setColorCircle(Color.TRANSPARENT);
                }
            } else if (level == 3) {
                circle_deng.setColorCircle(Color.parseColor("#FFE1C300"));
                //绿闪
//                if (colorChange) {
//                    colorChange = !colorChange;
//                    circle_deng.setColorCircle(Color.parseColor("#FFE1C300"));
//                    temp = 0;
//                } else {
//                    temp = 0;
//                    colorChange = !colorChange;
//                    circle_deng.setColorCircle(Color.TRANSPARENT);
//                }
            } else if (level == 4) {
                if (colorChange) {
                    colorChange = !colorChange;
                    circle_deng.setColorCircle(Color.GREEN);
                    temp = 0;
                } else {
                    temp = 0;
                    colorChange = !colorChange;
                    circle_deng.setColorCircle(Color.TRANSPARENT);
                }
            } else if (level == 5) {
                circle_deng.setColorCircle(Color.GREEN);
            }
        }
    };

    Handler airspaceWarningHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                WarningResultBean bean = (WarningResultBean) msg.obj;
                MainActivity.instence.air_space_state.setTextColor(bean.textColor);
                MainActivity.instence.air_space_state.setText(bean.warningText);
                if (bean.warningTitle.equals("空域")) {
                    if (bean.warningLevel != 0xf) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "homeplane_ky";
                        banGJBean.jinggaoxinxi_gj = bean.warningText;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("homeplane_ky", banGJBean);
                    } else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("homeplane_ky");
                    }
                } else if (bean.warningTitle.equals("碰撞")) {
                    if (bean.warningLevel != 0x10) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = bean.adsbPlaneNum;
                        banGJBean.jinggaoxinxi_gj = bean.warningText;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("adsb", banGJBean);
                    } else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adsb");
                    }
                } else if (bean.warningTitle.equals("撞地")) {
                    if (bean.warningLevel == 1) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "本机";
                        banGJBean.jinggaoxinxi_gj = "30s内即将撞地";
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("impactland_ky", banGJBean);
                    } else if(bean.warningLevel == 2){
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "本机";
                        banGJBean.jinggaoxinxi_gj = "60s内即将撞地";
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("impactland_ky", banGJBean);
                    }else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("impactland_ky");
                    }
                }
            } else if (msg.what == 2) {
                //这里就是碰撞的
                WarningResultBean bean = (WarningResultBean) msg.obj;
                MainActivity.instence.impact_state.setTextColor(bean.textColor);
                MainActivity.instence.impact_state.setText(bean.warningText);
                if (bean.warningTitle.equals("空域")) {
                    if (bean.warningLevel != 0xf) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "homeplane_ky";
                        banGJBean.jinggaoxinxi_gj = bean.warningText;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("homeplane_ky", banGJBean);
                    } else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("homeplane_ky");
                    }
                } else if (bean.warningTitle.equals("碰撞")) {
                    if (bean.warningLevel != 0x10) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = bean.adsbPlaneNum;
                        banGJBean.jinggaoxinxi_gj = bean.warningText;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("adsb", banGJBean);
                    } else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adsb");
                    }
                } else if (bean.warningTitle.equals("撞地")) {
                    if (bean.warningLevel == 1) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "本机";
                        banGJBean.jinggaoxinxi_gj = "30s内即将撞地";
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("impactland_ky", banGJBean);
                    } else if(bean.warningLevel == 2){
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "本机";
                        banGJBean.jinggaoxinxi_gj = "60s内即将撞地";
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("impactland_ky", banGJBean);
                    }else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("impactland_ky");
                    }
                }
            } else if (msg.what == 3) {
                WarningResultBean bean = (WarningResultBean) msg.obj;
                MainActivity.instence.impact_land_state.setTextColor(bean.textColor);
                MainActivity.instence.impact_land_state.setText(bean.warningText);
                if (bean.warningTitle.equals("空域")) {
                    if (bean.warningLevel != 0xf) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "homeplane_ky";
                        banGJBean.jinggaoxinxi_gj = bean.warningText;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("homeplane_ky", banGJBean);
                    } else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("homeplane_ky");
                    }
                } else if (bean.warningTitle.equals("碰撞")) {
                    if (bean.warningLevel != 0x10) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = bean.adsbPlaneNum;
                        banGJBean.jinggaoxinxi_gj = bean.warningText;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("adsb", banGJBean);
                    } else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adsb");
                    }
                } else if (bean.warningTitle.equals("撞地")) {
                    if (bean.warningLevel == 1) {
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "本机";
                        banGJBean.jinggaoxinxi_gj = "30s内即将撞地";
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("impactland_ky", banGJBean);
                    } else if(bean.warningLevel == 2){
                        HangBanGJBean banGJBean = new HangBanGJBean();
                        banGJBean.hangbanhao_gj = "本机";
                        banGJBean.jinggaoxinxi_gj = "60s内即将撞地";
                        MyApplication.getMyApplication().homeplaneWarningInfoList.put("impactland_ky", banGJBean);
                    }else {
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("impactland_ky");
                    }
                }
            }
        }
    };

    //碰撞
    public int getImpactWarningTextColor(int info) {
        int result = 0;
        switch (info) {
            case 0x01:
                //红闪
                result = Color.RED;
                break;

            case 0x04:
                //黄闪
                result = Color.parseColor("#FFE1C300");
                break;

            case 0x02:
                result = Color.parseColor("#FFE1C300");
                break;

            case 0x10:
                break;

            case 0x08:
                result = Color.GREEN;
                break;

        }
        return result;
    }

    //碰撞
    public String getImpactWarningText(int info) {
        String result = "";
        switch (info) {
            case 0x01:
                //红闪
                result = "进入避碰区域";
                break;

            case 0x04:
                //黄
                result = "进入保护区域";
                break;

            case 0x02:
                //黄闪
                result = "即将进入避碰区域";
                break;

            case 0x10:
                result = "";
                break;

            case 0x08:
                //绿闪
                result = "即将进入保护区域";
                break;

        }
        return result;
    }

    //空域
    public WarningResultBean getWarningLevel(byte info) {
        WarningResultBean result = new WarningResultBean();
        switch (info) {
            case 1:
                //黄闪
                result.warningLevel = Integer.valueOf("2");
                result.warningText = "飞出可飞空域";
                result.textColor = Color.parseColor("#FFE1C300");
                break;

            case 4:
                //绿闪
                result.warningLevel = Integer.valueOf("4");
                result.warningText = "即将飞出空域";
                result.textColor = Color.GREEN;
                break;

            case 8:
                //绿闪
                result.warningLevel = Integer.valueOf("4");
                result.warningText = "即将高度超限";
                result.textColor = Color.GREEN;
                break;

            case 0x02:
                //黄闪
                result.warningLevel = Integer.valueOf("2");
                result.warningText = "高度超限";
                result.textColor = Color.parseColor("#FFE1C300");
                break;

            case 0xf:
                //绿
                result.warningLevel = Integer.valueOf("5");
                result.warningText = "";
                break;

        }
        return result;
    }


}
