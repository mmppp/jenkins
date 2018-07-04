package com.hanke.navi.skyair;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.framwork.arith.Warnning;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.framwork.utils.Constants;
import com.hanke.navi.skyair.pop.bean.KongYuBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.infowindowpop.InfoWinAdapter;
import com.hanke.navi.skyair.pop.msgpop.msgbean.HangBanGJBean;
import com.hanke.navi.skyair.pop.tcpop.XinXiPop;
import com.hanke.navi.skyair.pop.tsqpop.HintPop;
import com.hanke.navi.skyair.service.LandNavService;
import com.hanke.navi.skyair.socket.ClientTask_new_new;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MyApplication extends Application implements OfflineMapManager.OfflineMapDownloadListener {

    private static MyApplication myApplication;
    public ClientTask_new_new clientTask;
    public Marker marker;
    public Text homeToDestinationMarker;
    public Nav nav;
    public Warnning warnning;
    public XinXiPop xinXiPop;
    public Circle circlejg, circlezy;//警告、注意提示圈
    public Socket socket;
    InfoWinAdapter infoWinAdapter;
    public HashMap<String, Marker> otherMarkerMap;
    public HashMap<String, Marker> winfoInfoMarkerMap;
    public HashMap<String, Long> lastLatlngMap;
    public HashMap<String, LatLng> winInfoLastLatlngMap;
    public HashMap<String, Double> lastAngleMap;
    public HashMap<String, PlaneInfoBean> latLngHashMap;
    public LatLng homePlaneLatlng;
    public PlaneInfoBean homePlane;
    public ArrayList<LatLng> flyLineList;
    public ArrayList<Polyline> flyLinePolyline;
    public HashMap<String, ArrayList<Marker>> otherMarkerFlyLineLatlng;
    //    public HashMap<String,ArrayList<Polyline>> otherMarkerFlyLinePolyline;
    public Polyline flyLine;
    public int flyLineMaxSize = 30;
    public boolean circleJGDraw;
    public boolean circleZYDraw;
    public int nextPointTime;
    public int remainderTime;
    //这个时间,需要不停的从clienttask里面去获取
    public String timeFromGps;
    public String dateFromGps;
    //回放的时候的timeFromGps的时间
    public String timeFromGps_lookback;
    public String dateFromGps_lookback;
    //偏行距
    public double deviateDistance;
    private GaojingPreference preference;
    public ArrayList<Marker> hlMarkerList;
    public String flyingPlanName;
    public boolean isPianzhiDoing = true;//false代表在执行 true代表没有在执行
    public HashMap<String, HangBanGJBean> homeplaneWarningInfoList;
    public List<KongYuBean> data_ky;
    //文件总共的行数
    public long totalRow;
    //是否正在回放
    public boolean isBack;
    //是否点击了回放,回放的选择栏已经出现
    public boolean isClickLookBak;
    //期望跳到的行数
    public long hopeRow;
    //快进倍数
    public int multipleNuml;
    //回放的开始时间
    public String lookbackCurrentTime;
    //回放结束时间
    public String lookbackEndTime;
    public boolean isNeedRemove = false;
    public String needRemoveKey = "";
    public boolean addFlyLine = false;
    public float lastAngle = 0;
    public static final String AppFolderName = "Air";//应用文件夹
    public static final String OfflineMapFolderName = "offlinemap";//离线地图文件夹
    private static final String SdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();//sd卡路径
    //应用路径
    private static final String AppPath = SdcardPath + File.separator + AppFolderName;
    private static final String OfflineMapPath = AppPath + File.separator + OfflineMapFolderName;

    private List<String> kymingdList = new ArrayList<String>();//最小空域高度
    private List<String> kymaxgdList = new ArrayList<String>();//最大空域高度
    private List<String> kydsList = new ArrayList<String>();//空域点数
    private List<List<String>> kywdList = new ArrayList<List<String>>();//空域纬度
    private List<List<String>> kyjdList = new ArrayList<List<String>>();//空域经度
    private List<String> listLine = new ArrayList<String>();//每一行数据的集合
    private List<Integer> listy = new ArrayList<Integer>();//大写字母开头所在行的下标的集合
    private List<String> listJWLine;//文本中所有经纬度所在行的集合
    private List<String> listqwd = new ArrayList<String>();//文本中所有纬度数据的集合
    private List<String> listqjd = new ArrayList<String>();//文本中所有经度数据的集合
    public List<String> kymcList = new ArrayList<String>();//空域名称
    private int y = -1;//大写字母开头所在行的下标
    private String str;
    public int lastBeidouCount;
    private int a;
    private int b;
    public Circle circle;
    //回放时候的轨迹,便于清除
    public ArrayList<Marker> lookback_Point;
    //正常飞行的轨迹,便于清除
    public ArrayList<Marker> flying_Point;
    //回放是否暂停
    public boolean isLookbackPause;
    public long pauseTempRowLine;
    //是否处于60s之内撞地的危险
    public boolean isImpactLandDanger;
    //是否处于30s之内撞地的危险
    public boolean isImpactLandDanger30;
    //着陆的时候,飞机距离正常高度的距离
    public double planeWithNormalHeightDValue;
    //着陆的时候,飞机的左后偏差
    public double planeWithNormalLeftAndRightDValue;
    //高层读取超过了范围了
    public boolean readHeightIsOverFloor = false;
    //地形打开了
    public boolean dixingIsOpen = false;

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        nav = new Nav();
        warnning = new Warnning();

        otherMarkerMap = new HashMap<>();
        lastLatlngMap = new HashMap<>();
        lastAngleMap = new HashMap<>();
        winfoInfoMarkerMap = new HashMap<>();
        winInfoLastLatlngMap = new HashMap<>();
        homePlane = new PlaneInfoBean();
        flyLineList = new ArrayList<>();
        flyLinePolyline = new ArrayList<>();
        otherMarkerFlyLineLatlng = new HashMap<>();
        homeplaneWarningInfoList = new HashMap<>();
        lookback_Point = new ArrayList<>();
        flying_Point = new ArrayList<>();
        latLngHashMap = new HashMap<>();
        circleJGDraw = true;
        circleZYDraw = true;

        preference = new GaojingPreference(getApplicationContext());

//        clientTask = new ClientTask(getAppContext(), false, 0);
//        clientTask.execute();
//        CrashHandler.getInstance().init(this);
        clientTask = new ClientTask_new_new(getAppContext(), false, 0);
        clientTask.execute();
        timerCount.schedule(taskCount, 0, 10000);
        if (data_ky == null) {
            data_ky = new ArrayList<>();
        }
//        Stetho.initializeWithDefaults(this);

        readky();
        //开启后台的service,用来检测是否需要启动着陆导航
        Intent intent = new Intent(getApplicationContext(), LandNavService.class);
        startService(intent);
        Log.i("hhh", "startService");
//        Intent appListenerIntent = new Intent(getApplicationContext(), AppListenerService.class);
//        startService(appListenerIntent);
    }


    public String getOfflineMapPath() {
        checkExist(OfflineMapPath);
        return OfflineMapPath;
    }

    public void checkExist(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static synchronized MyApplication getMyApplication() {
        return myApplication;
    }

    public static Context getAppContext() {
        return myApplication.getApplicationContext();
    }

    public int getWidth() {
//        Log.e("APP屏幕宽", "width_app = " + getAppContext().getResources().getDisplayMetrics().widthPixels);
        return getAppContext().getResources().getDisplayMetrics().widthPixels;
    }

    public int getHeight() {
//        Log.e("APP屏幕高", "height_app = " + getAppContext().getResources().getDisplayMetrics().heightPixels);
        return getAppContext().getResources().getDisplayMetrics().heightPixels;
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    //这里测试过,没有问题.
    public List<KongYuBean> readky() {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "kongyu" + File.separator + "ky.txt";
        File file = new File(path);
        if (file.exists()) {
            InputStreamReader inputStreamReader = null;
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, "GB2312");
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line = "";
                StringBuffer sb = new StringBuffer();
                //将空域文件读取然后以模型的形式存到集合里面.
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    listLine.add(line);
                }
                for (int i = 0; i < listLine.size(); i++) {
                    if (listLine.get(i).substring(0, 1).toCharArray()[0] >= 'A' &&
                            listLine.get(i).substring(0, 1).toCharArray()[0] <= 'Z') {
                        this.y = i;
                        listy.add(y);
                        kymcList.add(listLine.get(i));//空域名字
                        kymingdList.add(listLine.get(i + 1));//最小高度
                        kymaxgdList.add(listLine.get(i + 2));//最大高度
                        kydsList.add(listLine.get(i + 3));//某一条空域包含的点数
                    }
                }
                for (int i = 0; i < listy.size(); i++) {
                    if (i < listy.size() - 1)
                        listJWLine = listLine.subList(listy.get(i) + 4, listy.get(i + 1));
                    if (i == listy.size() - 1)
                        listJWLine = listLine.subList(listy.get(i) + 4, listLine.size());
                    for (int j = 0; j < listJWLine.size(); j++) {

                        StringBuffer s = new StringBuffer(listJWLine.get(j));
                        int dxb = listJWLine.get(j).indexOf(".");
                        int kxb = listJWLine.get(j).indexOf(" ", dxb);
                        Log.e("xxx", "kxb =" + kxb);
                        str = s.replace(kxb, kxb + 1, ",").toString();

                        String str_w = str.split("[,]")[0].replaceAll("\\s{1,}", "");
                        String str_j = str.split("[,]")[1].replaceAll("\\s{1,}", "");

                        int xb_w = str_w.indexOf(".");//xb_w = 5
//                        Log.e("x5x6x", "xb_j =" + xb_j);
                        double du_w = Double.parseDouble(String.valueOf(Double.parseDouble(str_w) / 100).split("[.]")[0]);//du_j = 109
                        Log.e("x5x6x", "du_w =" + du_w);
                        double fen_w = Double.parseDouble(str_w.substring(xb_w - 2, xb_w)) / 60;//fen_j
                        Log.e("x5x6x", "fen_w =" + fen_w);
                        StringBuffer sb_w = new StringBuffer(str_w.split("[.]")[1]);
                        Log.e("x5x6x", "sb_w =" + sb_w.toString());
                        double miao_w;
                        if (sb_w.toString().length() < 2) {
                            miao_w = Double.parseDouble(sb_w.toString()) / 3600;
                        } else {
                            miao_w = Double.parseDouble(sb_w.insert(2, '.').toString()) / 3600;//miao_j
                        }
                        Log.e("x5x6x", "miao_w =" + miao_w);
                        double wd = Double.parseDouble(new BigDecimal(du_w + fen_w + miao_w).setScale(8, BigDecimal.ROUND_HALF_UP).toString());
                        Log.e("x5x6x", "wd =" + wd);

                        int xb_j = str_j.indexOf(".");//xb_j = 5
//                        Log.e("x5x6x", "xb_j =" + xb_j);
                        double du_j = Double.parseDouble(String.valueOf(Double.parseDouble(str_j) / 100).split("[.]")[0]);//du_j = 109
                        Log.e("x5x6x", "du_j =" + du_j);
                        double fen_j = Double.parseDouble(str_j.substring(xb_j - 2, xb_j)) / 60;//fen_j
                        Log.e("x5x6x", "fen_j =" + fen_j);
                        StringBuffer sb_j = new StringBuffer(str_j.split("[.]")[1]);
                        Log.e("x5x6x", "sb_j =" + sb_j.toString());
                        double miao_j = 0;
                        if (sb_j.toString().length() < 2) {
                            miao_j = Double.parseDouble(sb_j.toString()) / 3600;
                        } else {
                            miao_j = Double.parseDouble(sb_j.insert(2, '.').toString()) / 3600;//miao_j
                        }
                        Log.e("x5x6x", "miao_j =" + miao_j);
                        double jd = Double.parseDouble(new BigDecimal(du_j + fen_j + miao_j).setScale(8, BigDecimal.ROUND_HALF_UP).toString());
                        Log.e("x5x6x", "jd =" + jd);

                        listqwd.add(String.valueOf(wd));
                        listqjd.add(String.valueOf(jd));

                    }
                }
                for (int i = 0; i < kymcList.size(); i++) {
                    KongYuBean bean = new KongYuBean();
                    bean.setKymc(kymcList.get(i));
                    bean.setKymingd(Integer.parseInt(kymingdList.get(i)));
                    bean.setKymaxgd(Integer.parseInt(kymaxgdList.get(i)));
                    bean.setKyds(Integer.parseInt(kydsList.get(i)));
                    if (i >= 0 && i < kymcList.size()) {
                        b = a + Integer.parseInt(kydsList.get(i));
                        kywdList.add(listqwd.subList(a, b));
                        kyjdList.add(listqjd.subList(a, b));
                        a = b;
                    }
                    bean.setKywds(kywdList.get(i));
                    bean.setKyjds(kyjdList.get(i));
                    bean.isSelect = true;
                    data_ky.add(bean);
                }
                inputStreamReader.close();
                fileInputStream.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "预读空域文件不存在", Toast.LENGTH_SHORT).show();
        }
        return data_ky;
    }

    Timer timerCount = new Timer();
    TimerTask taskCount = new TimerTask() {
        @Override
        public void run() {
            if (MainActivity.instence != null) {
                if (MyApplication.getMyApplication().isBack) {
                    if (MainActivity.instence.lookback.isConnectWithService) {
                        myHandler.sendEmptyMessage(106);
                    } else {
                        myHandler.sendEmptyMessage(105);
                    }
                } else {
                    if (MyApplication.getMyApplication().clientTask.isConnectWithService) {
                        myHandler.sendEmptyMessage(106);
                    } else {
                        myHandler.sendEmptyMessage(105);
                    }
                }
            }

//            //adsb的比较
            if (isBack) {
                //正在回放状态
                if (MainActivity.instence.lookback.lastAdsbCount == 0) {
                    MainActivity.instence.lookback.lastAdsbCount = MainActivity.instence.lookback.adsbCount;
                } else {
                    if (MainActivity.instence.lookback.lastAdsbCount == MainActivity.instence.lookback.adsbCount) {
                        MainActivity.instence.lookback.adsbHasNew = false;
                        myHandler.sendEmptyMessage(103);
                    } else {
                        MainActivity.instence.lookback.adsbHasNew = true;
                        myHandler.sendEmptyMessage(104);
                        MainActivity.instence.lookback.lastAdsbCount = MainActivity.instence.lookback.adsbCount;
                    }
                }
            } else {
                if (clientTask.lastAdsbCount == 0) {
                    clientTask.lastAdsbCount = clientTask.adsbCount;
                } else {
                    if (clientTask.lastAdsbCount == clientTask.adsbCount) {
                        myHandler.sendEmptyMessage(103);
                    } else {
                        myHandler.sendEmptyMessage(104);
                        clientTask.lastAdsbCount = clientTask.adsbCount;
                    }
                }
            }

            if (isBack) {
                //正在回放状态
                if (MainActivity.instence.lookback.lastBeidouCount == 0) {
                    MainActivity.instence.lookback.lastBeidouCount = MainActivity.instence.lookback.beidouCount;
                } else {
                    if (MainActivity.instence.lookback.lastBeidouCount != MainActivity.instence.lookback.beidouCount && MainActivity.instence.lookback.isConnectWithService) {
                        myHandler.sendEmptyMessage(102);
                    } else {
                        myHandler.sendEmptyMessage(101);
                        MainActivity.instence.lookback.lastBeidouCount = MainActivity.instence.lookback.beidouCount;
                    }
                }
            } else {
                if (clientTask.lastBeidouCount == 0) {
                    clientTask.lastBeidouCount = clientTask.beidouCount;
                } else {
                    if (clientTask.lastBeidouCount != clientTask.beidouCount && MyApplication.getMyApplication().clientTask.isConnectWithService) {
                        myHandler.sendEmptyMessage(102);
                        clientTask.lastBeidouCount = clientTask.beidouCount;
                    } else {
                        myHandler.sendEmptyMessage(101);
                    }
                }
            }


        }
    };

    public Handler myHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.SHUAXIN:
                    setDefault();
//                    setCenterPos();
                    break;

                case 101:
                    if (MainActivity.instence != null) {
                        MainActivity.instence.beidou_state.setTextColor(Color.RED);
                        MainActivity.instence.beidou_state.setText("GPS状态:未定位");
                        Log.i("weidingwei", "走了未定位");
                    }

                    break;

                case 102:
                    if (MainActivity.instence != null) {
                        MainActivity.instence.beidou_state.setTextColor(Color.GREEN);
                        MainActivity.instence.beidou_state.setText("GPS状态:已定位");
                    }
                    break;

                case 103:
                    if (MainActivity.instence != null) {
                        MainActivity.instence.adsb_state.setTextColor(Color.RED);
                        MainActivity.instence.adsb_state.setText("ADSB状态:未连接");
                        //删除掉所有的小飞机
                        if (!MyApplication.getMyApplication().isBack) {
                            Set<Map.Entry<String, Marker>> entries = otherMarkerMap.entrySet();
                            Iterator<Map.Entry<String, Marker>> iterator = entries.iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String, Marker> next = iterator.next();
                                Marker value = next.getValue();
                                value.remove();
                                winfoInfoMarkerMap.get(next.getKey()).remove();
                                ArrayList<Marker> markers = otherMarkerFlyLineLatlng.get(next.getKey());
                                for (int x = 0; x < markers.size(); x++) {
                                    markers.get(x).remove();
                                }
                            }
                            HashMap<String, HangBanGJBean> homeplaneWarningInfoList = MyApplication.getMyApplication().homeplaneWarningInfoList;
                            //如果这里是最后一个飞机,如果还有告警信息的话,那么就是这架飞机的,直接删掉
                            if (homeplaneWarningInfoList.get("adsb") != null) {
                                MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adsb");
                                Log.i("hahaha", "走了这里的删除额......");
                            }
//                            latLngHashMap.clear();
                            otherMarkerMap.clear();
                            lastLatlngMap.clear();
                            lastAngleMap.clear();
                            winfoInfoMarkerMap.clear();
                        }
                    }
                    break;

                case 104:
                    if (MainActivity.instence != null) {
                        MainActivity.instence.adsb_state.setTextColor(Color.GREEN);
                        MainActivity.instence.adsb_state.setText("ADSB状态:连接");
                    }
                    break;

                case 105:
                    if (MainActivity.instence != null) {
                        MainActivity.instence.connect_service_state.setText("服务器状态:断开");
                        MainActivity.instence.connect_service_state.setTextColor(Color.RED);
                    }
                    break;

                case 106:
                    if (MainActivity.instence != null) {
                        MainActivity.instence.connect_service_state.setText("服务器状态:正常");
                        MainActivity.instence.connect_service_state.setTextColor(Color.GREEN);
                    }
                    break;

            }
            super.handleMessage(msg);
        }
    };

    //如果没有北斗的信息发送的话,那么这个时候就setDefault,吧飞机放在上次北斗结束的地方,如果有的话,就直接接收北斗的信息定位了
    public void setDefault() {
        HintPop hintPop = new HintPop(getAppContext());
        MarkerOptions markerOptions = new MarkerOptions();
        if (clientTask.getWd() == 0 && clientTask.getJd() == 0) {
            if (marker != null) {
                marker.remove();
            }
            String[] latAndLon = preference.getLatAndLon();
            if (TextUtils.isEmpty(latAndLon[0])) {
                markerOptions.position(Constants.XIANYANG);
                BaseActivity.instance.aMap.moveCamera(CameraUpdateFactory.changeLatLng(Constants.XIANYANG));
            } else {
                //显示之前最后一条发送的位置,作为下次的初始位置
                markerOptions.position(new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1])));
                BaseActivity.instance.aMap.moveCamera(CameraUpdateFactory.changeLatLng((new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1])))));
            }

            markerOptions.draggable(false);
            markerOptions.setFlat(false);
            markerOptions.perspective(true);
            markerOptions.period(1);//值越小刷新的越快
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.zfj)));
            markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
            markerOptions.zIndex(0);
            //这里如果缓存了之前的角度,这里也进行翻转
            String angle = preference.getAngle();
            Log.i("angele...", angle);
            if (!TextUtils.isEmpty(angle)) {
                markerOptions.rotateAngle(360 - Float.parseFloat(angle));
            } else {
                markerOptions.rotateAngle(0);
            }
            if (BaseActivity.instance != null) {
                marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
            }
            Point point = BaseActivity.instance.aMap.getProjection().toScreenLocation(Constants.XIANYANG);
//            if (point.x < BaseActivity.instance.height_include / 2 || point.x > getWidth() - BaseActivity.instance.height_include / 2 ||
//                    point.y < BaseActivity.instance.height_include / 2 || point.y > getHeight() - 3 * BaseActivity.instance.height_include / 2) {
//                BaseActivity.instance.aMap.moveCamera(CameraUpdateFactory.changeLatLng(Constants.XIANYANG));
//            }
            if (hintPop != null) {
                if (!TextUtils.isEmpty(latAndLon[0]) && !TextUtils.isEmpty(latAndLon[1])) {
                    if (MainActivity.instence != null) {
                        if (MainActivity.instence.listCirclejg.size() != 0) {
                            for (int i = 0; i < MainActivity.instence.listCirclejg.size(); i++) {
                                MainActivity.instence.listCirclejg.get(i).remove();
                            }
                        }
                        MainActivity.instence.listCirclejg.clear();
                        Circle circlejg = hintPop.addCirclejg(new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1])), hintPop.radius_jg, Color.RED);
                        MainActivity.instence.listCirclejg.add(circlejg);
                        if (MainActivity.instence.listCirclezy.size() != 0) {
                            for (int i = 0; i < MainActivity.instence.listCirclezy.size(); i++) {
                                MainActivity.instence.listCirclezy.get(0).remove();
                            }
                        }
                        MainActivity.instence.listCirclezy.clear();
                        Circle circlezy = hintPop.addCirclezy(new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1])), hintPop.radius_zy, Color.parseColor("#FFE1C300"));
                        MainActivity.instence.listCirclezy.add(circlezy);
                    }
                } else {
                    if (MainActivity.instence != null) {
                        if (MainActivity.instence.listCirclejg.size() != 0) {
                            for (int i = 0; i < MainActivity.instence.listCirclejg.size(); i++) {
                                MainActivity.instence.listCirclejg.get(i).remove();
                            }
                        }
                        MainActivity.instence.listCirclejg.clear();
                        hintPop.addCirclejg(Constants.XIANYANG, hintPop.radius_jg, Color.RED);
                        MainActivity.instence.listCirclejg.add(circlejg);
                        if (MainActivity.instence.listCirclezy.size() != 0) {
                            for (int i = 0; i < MainActivity.instence.listCirclezy.size(); i++) {
                                MainActivity.instence.listCirclezy.get(0).remove();
                            }
                        }
                        MainActivity.instence.listCirclezy.clear();
                        hintPop.addCirclezy(Constants.XIANYANG, hintPop.radius_zy, Color.parseColor("#FFE1C300"));
                        MainActivity.instence.listCirclezy.add(circlezy);
                    }

                }
                if (MainActivity.instence != null) {
                    MainActivity.instence.verticalScaleLeft.setSDmCountScale(20);
                    MainActivity.instence.verticalScaleLeft.invalidate();
                    MainActivity.instence.verticalScaleRight.setGDmCountScale(204);
                    MainActivity.instence.verticalScaleRight.invalidate();
                }
            }
        }
    }

    public Circle setTSQCirclejg(LatLng latLng) {
        HintPop hintPop = new HintPop(getAppContext());
        if (circlejg != null)
            circlejg.remove();
        if (MainActivity.instence != null) {
            if (MainActivity.instence.listCirclejg.size() != 0) {
                for (int i = 0; i < MainActivity.instence.listCirclejg.size(); i++) {
                    MainActivity.instence.listCirclejg.get(i).remove();
                }
                MainActivity.instence.listCirclejg.clear();
                if (marker != null) {
                    if (latLng.latitude == 0 && latLng.longitude == 0) {
                        circlejg = hintPop.addCirclejg(marker.getPosition(), hintPop.radius_jg, Color.RED);
                    } else {
                        circlejg = hintPop.addCirclejg(latLng, hintPop.radius_jg, Color.RED);
                    }
                }
            } else {
                if (circleJGDraw) {
                    circlejg = hintPop.addCirclejg(latLng, hintPop.radius_jg, Color.RED);
                    MainActivity.instence.listCirclejg.add(circlejg);
                }
            }
        }
        return circlejg;
    }

    public Circle setTSQCirclezy(LatLng latLng) {//刷新两个提示圈：跟随本机的位置移动
        HintPop hintPop = new HintPop(getAppContext());
        if (circlezy != null)
            circlezy.remove();
        if (MainActivity.instence != null) {
            if (MainActivity.instence.listCirclezy.size() != 0) {
                for (int i = 0; i < MainActivity.instence.listCirclezy.size(); i++) {
                    MainActivity.instence.listCirclezy.get(i).remove();
                }
                MainActivity.instence.listCirclezy.clear();
                if (marker != null) {
                    if (latLng.latitude == 0 && latLng.longitude == 0) {
                        circlezy = hintPop.addCirclezy(marker.getPosition(), hintPop.radius_zy, Color.parseColor("#FFE1C300"));
                    } else {
                        circlezy = hintPop.addCirclezy(latLng, hintPop.radius_zy, Color.parseColor("#FFE1C300"));
                    }
                }
            } else {
                if (circleZYDraw) {
                    circlezy = hintPop.addCirclezy(latLng, hintPop.radius_zy, Color.parseColor("#FFE1C300"));
                    MainActivity.instence.listCirclezy.add(circlezy);
                }
            }
        }
        return circlezy;
    }


    public void setRefreshCursor() {//刷新左右刻度尺
        Log.i("hahaha", "刷新了右边的高度" + clientTask.getHb());
        if (MainActivity.instence != null) {
            MainActivity.instence.verticalScaleLeft.setSDmCountScale(clientTask.speed);//此处传收到的速度值
            MainActivity.instence.verticalScaleLeft.invalidate();

            MainActivity.instence.verticalScaleRight.setGDmCountScale(clientTask.getHb());//此处传收到的高度值
            MainActivity.instence.verticalScaleRight.invalidate();
        }
    }

    public void setRefreshCursor(double height, double speed) {//刷新左右刻度尺
        Log.i("hahaha", "刷新了右边的高度" + clientTask.getHb());
        if (MainActivity.instence != null) {
            MainActivity.instence.verticalScaleLeft.setSDmCountScale(speed);//此处传收到的速度值
            MainActivity.instence.verticalScaleLeft.invalidate();

            MainActivity.instence.verticalScaleRight.setGDmCountScale(height);//此处传收到的高度值
            MainActivity.instence.verticalScaleRight.invalidate();
        }
    }

    public void setCenterPos() {//刷新飞机位置不出屏幕
        Point point;
        LatLng latLng;
        String[] latAndLon = preference.getLatAndLon();
        if (TextUtils.isEmpty(latAndLon[0])) {
            latLng = Constants.XIANYANG;
        } else {
            latLng = new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1]));
        }
        if (BaseActivity.instance != null) {
            point = BaseActivity.instance.aMap.getProjection().toScreenLocation(latLng);//从地图位置转换来的屏幕位置
//            if (point.x < BaseActivity.instance.height_include / 2 || point.x > getWidth() - BaseActivity.instance.height_include / 2 ||
//                    point.y < BaseActivity.instance.height_include / 2 || point.y > getHeight() - 3 * BaseActivity.instance.height_include / 2) {
            BaseActivity.instance.aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
//            }
        }
    }

    public void setCenterPos(double lat, double lon) {//刷新飞机位置不出屏幕
        Point point;
        LatLng latLng;
        String[] latAndLon = preference.getLatAndLon();
        if (TextUtils.isEmpty(latAndLon[0])) {
            latLng = Constants.XIANYANG;
        } else {
            latLng = new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1]));
        }
        point = BaseActivity.instance.aMap.getProjection().toScreenLocation(latLng);//从地图位置转换来的屏幕位置
        if (point.x < BaseActivity.instance.height_include / 2 || point.x > getWidth() - BaseActivity.instance.height_include / 2 ||
                point.y < BaseActivity.instance.height_include / 2 || point.y > getHeight() - 3 * BaseActivity.instance.height_include / 2) {
            BaseActivity.instance.aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }

    //给每一架飞机天机挂牌信息2.0
    public void setWindowwInfoMarker2() {
//        latLngHashMap = clientTask.latAndLonMap;
        Set<Map.Entry<String, PlaneInfoBean>> entries = latLngHashMap.entrySet();
        Iterator<Map.Entry<String, PlaneInfoBean>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            PlaneInfoBean value = (PlaneInfoBean) entry.getValue();
            Marker marker = winfoInfoMarkerMap.get(key);
            //这里不设置图表.使用自定义view.但是view里面的控件需要设置text.
            TextView ifw_hangbanhao = new TextView(getApplicationContext());
            ifw_hangbanhao.setGravity(Gravity.CENTER);
//                        textView.setBackgroundResource(R.drawable.icon_gcoding);
            ifw_hangbanhao.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            ifw_hangbanhao.setTextColor(Color.parseColor("#1AD5FD"));
            ifw_hangbanhao.setShadowLayer(3, 0, 0, Color.BLACK);
            ifw_hangbanhao.setGravity(Gravity.LEFT);
            ifw_hangbanhao.setText("航班号:" + value.planeNum + "\r\n" + " H:" + (int) value.flyHeight + "\r\n" + " GS :" + (int) value.flySpeed);
            ifw_hangbanhao.destroyDrawingCache();
            ifw_hangbanhao.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            ifw_hangbanhao.layout(0, 0, ifw_hangbanhao.getMeasuredWidth(), ifw_hangbanhao.getMeasuredHeight());
            ifw_hangbanhao.setDrawingCacheEnabled(true);
            Bitmap bitmapText = ifw_hangbanhao.getDrawingCache(true);
            BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmapText);
            //这里直接使用adsb的经纬度会造成遮挡的情况.
            float scalePerPixel = BaseActivity.instance.aMap.getScalePerPixel();
            float addLat = scalePerPixel / 1300;
            double showLat = value.latLng.latitude - addLat;
            double showLon = value.latLng.longitude + addLat;
            MarkerOptions oo = new MarkerOptions().icon(bd).position(new LatLng(showLat, showLon)).period(1);

            //如果之前添加过,那么删除这个marker
            if (marker != null) {
                marker.remove();
            }
            //然后在集合了里面替换掉之前显示的这个marker,这里之所以需要一直remov和add,使用因为这些标签信息是需要一直更新的,
            //如果只是使用挪动位置的话,那么如果信息改变了的话就不能够进行改变了
            Marker marker1 = BaseActivity.instance.aMap.addMarker(oo);
            winfoInfoMarkerMap.put(key, marker1);
        }
    }

    //其他飞机
    public void setOtherMarker() {
        myHandler.sendEmptyMessage(104);
        Set<Map.Entry<String, PlaneInfoBean>> entries = latLngHashMap.entrySet();
        Iterator<Map.Entry<String, PlaneInfoBean>> iterator = entries.iterator();
//        Log.i("hahaha", clientTask.latAndLonMap.size() + "other");
        while (iterator.hasNext()) {
//            isNeedRemove = false;
//            needRemoveKey = "";
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            PlaneInfoBean value = (PlaneInfoBean) entry.getValue();
            Marker marker = otherMarkerMap.get(key);
            Long lastMills = lastLatlngMap.get(key);
            //如果之前已经存在过这个marker了,那么刷新位置就可以了
            if (marker != null) {
                if (lastMills != null && lastMills == value.currentTimeMillis) {
                    marker.remove();
                    if (winfoInfoMarkerMap.get(key) != null) {
                        winfoInfoMarkerMap.get(key).remove();
                    }
                    //没有消失掉的原因是因为这里remove了之后.标志又被add上去了
                    winfoInfoMarkerMap.remove(key);
                    isNeedRemove = true;
                    needRemoveKey = key;
                    addFlyLine = false;
                    HashMap<String, HangBanGJBean> homeplaneWarningInfoList = MyApplication.getMyApplication().homeplaneWarningInfoList;
                    //如果那个飞机已经被删除了的话,那么告警信息也应该相应的删除掉
                    if (homeplaneWarningInfoList.get("adsb") != null) {
                        if (value.planeNum.equals(homeplaneWarningInfoList.get("adsb").getHangbanhao_gj())) {
                            MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adsb");
                        }
                    }
                    //清楚告警测试里面的.在哪里删除呢
                    otherMarkerMap.remove(key);
                    lastLatlngMap.remove(key);
                    lastAngleMap.remove(key);
                    ArrayList<Marker> markers = otherMarkerFlyLineLatlng.get(key);
                    for (int x = 0; x < markers.size(); x++) {
                        markers.get(x).remove();
                    }
                    otherMarkerFlyLineLatlng.remove(key);
                    break;

                } else {
//                    if (lastAngleMap.get(key) != null && !lastAngleMap.get(key).equals(value.flyAngle)) {
                    marker.remove();
                    marker.getOptions().rotateAngle((float) (360 - latLngHashMap.get(key).flyAngle));
                    marker.getOptions().position(value.latLng);
                    marker.getOptions().title("航班号" + latLngHashMap.get(key).planeNum);
                    marker.getOptions().snippet("GS " + latLngHashMap.get(key).flySpeed + "\r\n" + "H:" + latLngHashMap.get(key).flyHeight);
                    marker = BaseActivity.instance.aMap.addMarker(marker.getOptions());
//                        marker.showInfoWindow();
                    addFlyLine = true;
                    lastAngleMap.put(key, value.flyAngle);
                    lastLatlngMap.put(key, value.currentTimeMillis);
                    otherMarkerMap.put(key, marker);
//                    } else {
//                        float scalePerPixel = BaseActivity.instance.aMap.getScalePerPixel();
//                        Log.i("hahaha", "maxzoom" + BaseActivity.instance.aMap.getMaxZoomLevel() + "minZoom:" + BaseActivity.instance.aMap.getMinZoomLevel());
//                        Log.i("hahaha", "pixel" + scalePerPixel);
//                        marker.getOptions().title("航班号" + latLngHashMap.get(key).planeNum);
//                        marker.getOptions().snippet("GS " + latLngHashMap.get(key).flySpeed + "\r\n" + "H:" + latLngHashMap.get(key).flyHeight);
//                        marker.setPosition(value.latLng);
////                        marker.showInfoWindow();
//                        addFlyLine = true;
//                        lastLatlngMap.put(key, value.currentTimeMillis);
//                        lastAngleMap.put(key, value.flyAngle);
//                    }

                }
            } else {
                MarkerOptions markerOptions1 = new MarkerOptions();
                if (latLngHashMap.get(key).latLng.latitude == 0 && latLngHashMap.get(key).latLng.longitude == 0) {
                    if (marker != null) {
                        marker.setVisible(true);
                    }
                } else {
                    markerOptions1.position(new LatLng(latLngHashMap.get(key).latLng.latitude, latLngHashMap.get(key).latLng.longitude));
                }
                markerOptions1.draggable(false);
                markerOptions1.setFlat(false);
                markerOptions1.perspective(true);
                markerOptions1.period(1);//值越小刷新的越快
                markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.qfj)));
                markerOptions1.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
                markerOptions1.title("航班号" + latLngHashMap.get(key).planeNum);
//                markerOptions1.zIndex((float) latLngHashMap.get(key).flyHeight);
                markerOptions1.snippet("GS " + latLngHashMap.get(key).flySpeed + "\r\n" + "H:" + latLngHashMap.get(key).flyHeight);
                markerOptions1.rotateAngle((float) (360 - latLngHashMap.get(key).flyAngle));
                markerOptions1.setInfoWindowOffset(50, 100);
                float scalePerPixel = BaseActivity.instance.aMap.getScalePerPixel();
                Log.i("hahaha", "pixel" + scalePerPixel);
                Marker newMarker = BaseActivity.instance.aMap.addMarker(markerOptions1);//添加图标
//                newMarker.showInfoWindow();
                addFlyLine = true;
                lastLatlngMap.put(key, value.currentTimeMillis);
                lastAngleMap.put(key, value.flyAngle);
                otherMarkerMap.put(key, newMarker);
            }
            //给其他飞机话航迹.这里使用点
            ArrayList<Marker> latlngList = otherMarkerFlyLineLatlng.get(key);
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(new LatLng(latLngHashMap.get(key).latLng.latitude, latLngHashMap.get(key).latLng.longitude));
            markerOptions1.draggable(false);
            markerOptions1.setFlat(false);
            markerOptions1.perspective(true);
            markerOptions1.period(1);//值越小刷新的越快
//            BitmapDescriptor descriptor = get
            markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.flyline)));
            markerOptions1.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
            Marker newMarker = BaseActivity.instance.aMap.addMarker(markerOptions1);//添加图标
            if (latlngList == null) {
                latlngList = new ArrayList<>();
            }
            latlngList.add(newMarker);
            if (latlngList.size() > flyLineMaxSize) {
                //然后要把之前的marke也给清除掉
                latlngList.get(0).remove();
                //这个时候我们删除第一个.
                latlngList.remove(0);
            }
            otherMarkerFlyLineLatlng.put(key, latlngList);
        }
        if (isNeedRemove) {
            Log.i("remove了", "remove....");
            latLngHashMap.remove(needRemoveKey);
        }
    }

    //如果开启了导航模式的话,在本机上面增加marker,用来显示本机距离最后的目的地的距离.
    public Text setHomePlaneToDestination() {//刷新飞机图标：根据接收到的经纬度
        if (!MainActivity.instence.flag_zx) {
            //如果正在导航中的时候.
            if (homeToDestinationMarker != null) {
                homeToDestinationMarker.remove();
            }
            TextOptions markerOptions = new TextOptions();
            if (clientTask.getWd() == 0 && clientTask.getJd() == 0) {
                markerOptions.position(Constants.XIANYANG);
            }
            markerOptions.position(new LatLng(clientTask.getWd() + 0.06, clientTask.getJd()));
//            if (homePlaneLatlng != null && TextUtils.isEmpty(homePlaneLatlng.latitude + "") && TextUtils.isEmpty(homePlaneLatlng.longitude + "")) {
//                double distance = DistanceUtil.getInstance().getDistance(homePlaneLatlng, destination) / 1000;
//                String parm = "距离目的地距离:" + DecimalUtil.remainDecimal(distance, 3) + "公里";
//                markerOptions.fontSize(26);
//                markerOptions.text(parm);
//                markerOptions.fontColor(Color.parseColor("#1AD5FD"));
//                markerOptions.backgroundColor(Color.parseColor("#7f000000"));
//                homeToDestinationMarker = BaseActivity.instance.aMap.addText(markerOptions);//添加图标
//            }
        } else {
            if (homeToDestinationMarker != null) {
                homeToDestinationMarker.remove();
            }
        }
        return homeToDestinationMarker;
    }

    //回放
    public Marker setMarker_new(boolean isBack, double lat, double lon, double height, double angle, double updownspeed, double speed) {
//        Marker marker = null;
        myHandler.sendEmptyMessage(102);
        homePlaneLatlng = new LatLng(lat, lon);
        homePlane.latLng = new LatLng(lat, lon);
        homePlane.flyHeight = height;
        //从这里说明,如果传入过来的是90度,那么转动的应该是270度,因为传入过来的时候的角度是顺时针计算的,而高德地图是逆时针计算的
        homePlane.flyAngle = 360 - angle;
        homePlane.upOrDownSpeed = updownspeed;
        homePlane.flySpeed = speed;
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(homePlaneLatlng);
        markerOptions1.draggable(false);
        markerOptions1.setFlat(false);
        markerOptions1.perspective(true);
        markerOptions1.period(1);//值越小刷新的越快
//            BitmapDescriptor descriptor = get
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.flyline)));
        markerOptions1.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
        markerOptions1.zIndex(1);
        if (homePlaneLatlng.longitude != 0.0) {
//            flyLineList.add(homePlaneLatlng);
            lookback_Point.add(BaseActivity.instance.aMap.addMarker(markerOptions1));//添加图标
        }
        if (marker == null) {
            //如果是空的话,代表没有add,这个时候addMarker.
            MarkerOptions markerOptions = new MarkerOptions();
            if (lat == 0 && lon == 0) {
                markerOptions.position(Constants.XIANYANG);
            }
            markerOptions.position(new LatLng(lat, lon));
            markerOptions.draggable(false);
            markerOptions.setFlat(false);
            markerOptions.perspective(true);
            markerOptions.period(1);//值越小刷新的越快
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.zfj)));
            markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
            //这里的航向角和发送模拟器上面的有什么关系
            markerOptions.rotateAngle(Float.parseFloat(homePlane.flyAngle + ""));
            markerOptions.zIndex(12);
            lastAngle = Float.parseFloat(homePlane.flyAngle + "");
            marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
        } else {
            if (homePlane.flyAngle != lastAngle) {
                //说明更改了航向,需要重新绘制
                MarkerOptions markerOptions = new MarkerOptions();
                if (lat == 0 && lon == 0) {
                    markerOptions.position(Constants.XIANYANG);
                }
                markerOptions.position(new LatLng(lat, lon));
                markerOptions.draggable(false);
                markerOptions.setFlat(false);
                markerOptions.perspective(true);
                markerOptions.period(1);//值越小刷新的越快
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.zfj)));
                markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
                markerOptions.rotateAngle(Float.parseFloat(homePlane.flyAngle + ""));
                markerOptions.zIndex(12);
                lastAngle = Float.parseFloat(homePlane.flyAngle + "");
                marker.remove();
                if (BaseActivity.instance != null) {
                    marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
                }
                lastAngle = Float.parseFloat(homePlane.flyAngle + "");
            } else {
                marker.setPosition(new LatLng(lat, lon));
            }
        }
//        if (MainActivity.instence.handler != null) {
//            MainActivity.instence.handler.sendEmptyM essage(156);
//        }
        return marker;
    }

    //刷新本机的图标,不是回放
    public Marker setMarker_new(double lat, double lon, double height, double angle, double updownspeed, double speed) {
//        Marker marker = null;
        myHandler.sendEmptyMessage(106);
        myHandler.sendEmptyMessage(102);
        homePlaneLatlng = new LatLng(clientTask.getWd(), clientTask.getJd());
        homePlane.latLng = new LatLng(clientTask.getWd(), clientTask.getJd());
        homePlane.flyHeight = clientTask.getHb();
        homePlane.flyAngle = 360 - clientTask.angle;
        homePlane.upOrDownSpeed = clientTask.upOrDownSpeed;
        homePlane.flySpeed = clientTask.speed;

//        homePlaneLatlng = new LatLng(lat, lon);
//        homePlane.latLng = new LatLng(lat, lon);
//        homePlane.flyHeight = height;
//        //从这里说明,如果传入过来的是90度,那么转动的应该是270度,因为传入过来的时候的角度是顺时针计算的,而高德地图是逆时针计算的
//        homePlane.flyAngle = 360 - angle;
//        homePlane.upOrDownSpeed = updownspeed;
//        homePlane.flySpeed = speed;

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(homePlaneLatlng);
        markerOptions1.draggable(false);
        markerOptions1.setFlat(false);
        markerOptions1.perspective(true);
        markerOptions1.period(1);//值越小刷新的越快
//            BitmapDescriptor descriptor = get
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.flyline)));
        markerOptions1.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
        markerOptions1.zIndex(1);
        if (homePlaneLatlng.longitude != 0.0) {
//            flyLineList.add(homePlaneLatlng);
            if (BaseActivity.instance != null) {
                flying_Point.add(BaseActivity.instance.aMap.addMarker(markerOptions1));//添加图标
            }
        }
        if (marker == null) {
            //如果是空的话,代表没有add,这个时候addMarker.
            MarkerOptions markerOptions = new MarkerOptions();
            if (clientTask.getWd() == 0 && clientTask.getJd() == 0) {
                markerOptions.position(Constants.XIANYANG);
            }
            markerOptions.position(new LatLng(lat, lon));
            markerOptions.draggable(false);
            markerOptions.setFlat(false);
            markerOptions.perspective(true);
            markerOptions.period(1);//值越小刷新的越快
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.zfj)));
            markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
            markerOptions.rotateAngle(Float.parseFloat(homePlane.flyAngle + ""));
            markerOptions.zIndex(12);
            lastAngle = Float.parseFloat(homePlane.flyAngle + "");
            if (BaseActivity.instance != null) {
                marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
            }
        } else {
            if (homePlane.flyAngle != lastAngle) {
                //说明更改了航向,需要重新绘制
                MarkerOptions markerOptions = new MarkerOptions();
                if (clientTask.getWd() == 0 && clientTask.getJd() == 0) {
                    markerOptions.position(Constants.XIANYANG);
                }
                markerOptions.position(new LatLng(clientTask.getWd(), clientTask.getJd()));
                markerOptions.draggable(false);
                markerOptions.setFlat(false);
                markerOptions.perspective(true);
                markerOptions.period(1);//值越小刷新的越快
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.zfj)));
                markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
                markerOptions.rotateAngle(Float.parseFloat(homePlane.flyAngle + ""));
                markerOptions.zIndex(12);
                lastAngle = Float.parseFloat(homePlane.flyAngle + "");
                marker.remove();
                if (BaseActivity.instance != null) {
                    marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
                }
                lastAngle = Float.parseFloat(homePlane.flyAngle + "");
            } else {
                marker.setPosition(new LatLng(lat, lon));
            }
        }
//        if (MainActivity.instence.handler != null) {
//            MainActivity.instence.handler.sendEmptyMessage(156);
//        }
        return marker;
    }

    //本机
    public Marker setMarker() {//刷新飞机图标：根据接收到的经纬度
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        if (clientTask.getWd() == 0 && clientTask.getJd() == 0) {
            markerOptions.position(Constants.XIANYANG);
        }
        homePlaneLatlng = new LatLng(clientTask.getWd(), clientTask.getJd());
        homePlane.latLng = new LatLng(clientTask.getWd(), clientTask.getJd());
        homePlane.flyHeight = clientTask.getHb();
        homePlane.flyAngle = 360 - clientTask.angle;
        homePlane.upOrDownSpeed = clientTask.upOrDownSpeed;
        markerOptions.position(new LatLng(clientTask.getWd(), clientTask.getJd()));
        markerOptions.draggable(false);
        markerOptions.setFlat(false);
        markerOptions.perspective(true);
        markerOptions.period(1);//值越小刷新的越快
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.zfj)));
        markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
//        float angle = (float) nav.getAngle(clientTask.getWda(), clientTask.getJda(), clientTask.getWdb(), clientTask.getJdb());
        markerOptions.rotateAngle(Float.parseFloat(homePlane.flyAngle + ""));
        if (BaseActivity.instance != null) {
            marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
            marker.showInfoWindow();
        }

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(homePlaneLatlng);
        markerOptions1.draggable(false);
        markerOptions1.setFlat(false);
        markerOptions1.perspective(true);
        markerOptions1.period(1);//值越小刷新的越快
//            BitmapDescriptor descriptor = get
        markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.flyline)));
        markerOptions1.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
        if (homePlaneLatlng.longitude != 0.0) {
//            flyLineList.add(homePlaneLatlng);
            if (BaseActivity.instance != null) {
                BaseActivity.instance.aMap.addMarker(markerOptions1);//添加图标
            }
        }

//        if (MainActivity.instence.handler != null)
//            MainActivity.instence.handler.sendEmptyMessage(156);
        return marker;
    }


    @Override
    public void onDownload(int i, int i1, String s) {

    }


    @Override
    public void onCheckUpdate(boolean b, String s) {

    }

    @Override
    public void onRemove(boolean b, String s, String s1) {

    }

    public void setTimeAndDate() {
        //这样的话就可以在其他的地方得到来自北斗报文里面解析的gps时间了
        timeFromGps = clientTask.timeFromGps;
        dateFromGps = clientTask.dateFromGps;
        //刷新从gps里面解析来的时间和日期.
        if (MainActivity.instence != null) {
            MainActivity.instence.right_date.setText(clientTask.dateFromGps);
            MainActivity.instence.right_time.setText(clientTask.timeFromGps);
        }
//        刷新北斗和adsb状态的显示.
        if (lastBeidouCount == 0) {
            lastBeidouCount = clientTask.beidouCount;
        }
//        if (clientTask.lastBeidou == clientTask.beidouCount) {
//            myHandler.sendEmptyMessage(102);
//        }
//        if (!TextUtils.isEmpty(clientTask.gpsState)) {
//            if (clientTask.gpsState.equals("V")) {
//                clientTask.beidouHasNew = false;
//                myHandler.sendEmptyMessage(101);
//            } else if (clientTask.gpsState.equals("A")) {
//                clientTask.beidouHasNew = true;
////            clientTask.lastBeidou = clientTask.beidouCount;
//                myHandler.sendEmptyMessage(102);
//            }
//        }
//        }
    }

    public void setTimeAndDate(String dateFromGps, String timeFromGps) {
        //这样就可以在其他的地方得到来自回放报文里面的gps时间了
        timeFromGps_lookback = timeFromGps;
        dateFromGps_lookback = dateFromGps;
        //刷新从文件里面读取的时间
        if (MainActivity.instence != null) {
            MainActivity.instence.right_date.setText(dateFromGps);
            MainActivity.instence.right_time.setText(timeFromGps);
            //刷新北斗和adsb状态的显示.
//            if (!TextUtils.isEmpty(MainActivity.instence.lookback.gpsState)) {
//                if (MainActivity.instence.lookback.gpsState.equals("V")) {
//                    MainActivity.instence.lookback.beidouHasNew = false;
//                    myHandler.sendEmptyMessage(101);
//                } else {
//                    MainActivity.instence.lookback.beidouHasNew = true;
//                    myHandler.sendEmptyMessage(102);
//                }
//            }
        }
    }

    public void setTwoTime() {
        if (MainActivity.instence != null) {
            if (MainActivity.instence.flag_zx == false) {
                if (preference.isShowEtaTime()) {
                    MainActivity.instence.next_time.setVisibility(View.VISIBLE);
                    MainActivity.instence.remain_time.setVisibility(View.VISIBLE);
                } else {
                    MainActivity.instence.next_time.setVisibility(View.GONE);
                    MainActivity.instence.remain_time.setVisibility(View.GONE);
                }
                if (preference.isShowXtkDistance()) {
                    MainActivity.instence.deviate_distance.setVisibility(View.VISIBLE);
                } else {
                    MainActivity.instence.deviate_distance.setVisibility(View.GONE);
                }

                //nextPointTime现在是所需要的秒数,我需要把这个时间换算到当前时间上去.
                //16 * 3600 + 34 * 60 +50 + 5000 57600 2040
                String[] nextTime = preference.getNextTime();
                Log.i("ceta2222", "hour" + nextTime[0] + "min" + nextTime[1] + "sec" + nextTime[2]);
//            if (!TextUtils.isEmpty(nextTime[1])) {
//                timeFromGps = nextTime[1];
//            }
                if (!TextUtils.isEmpty(nextTime[1])) {
                    int hour = (int) (Double.parseDouble(nextTime[0]));
                    int minute = (int) (Double.parseDouble(nextTime[1]));
                    int secand = (int) (Double.parseDouble(nextTime[2]) * 60);
                    //这就是离下一个点的时分秒
                    String currentTime = nextTime[3];
                    String[] split = currentTime.split(":");
                    int currentTime_hour = Integer.parseInt(split[0]);
                    int currentTime_minute = Integer.parseInt(split[1]);
                    int currentTime_secand = Integer.parseInt(split[2]);

                    int nextTime_hour = hour + currentTime_hour;
                    int nextTime_minute = minute + currentTime_minute;
                    int nextTime_secand = secand + currentTime_secand;

                    if (nextTime_secand >= 60) {
                        nextTime_secand = nextTime_secand - 60;
                        nextTime_minute++;
                    }
                    if (nextTime_minute >= 60) {
                        nextTime_minute = nextTime_minute - 60;
                        nextTime_hour++;
                    }
                    if (nextTime_hour >= 24)
                        nextTime_hour = nextTime_hour - 24;


//                    int total = (int)(hour * 3600 + minute * 60 + secand + Integer.valueOf(nextTime[3]));

                    //到达时间的时分秒
//                    hour = total / 3600;
//                    minute = (total - hour * 3600) / 60;
//                    secand = (total - hour * 3600 - minute * 60);
                    String maohao_one = ":";
                    if (nextTime_minute < 10) {
                        maohao_one = ":0";
                    }
                    String maohao_two = ":";
                    if (nextTime_secand < 10) {
                        maohao_two = ":0";
                    }
                    //距离下一个航路点的时间
                    MainActivity.instence.next_time.setText("ETA :" + nextTime_hour + maohao_one + nextTime_minute + maohao_two + nextTime_secand);

                    //ETE就是方法计算出来的那个值
//                    int remain_hour = hour / 3600;
//                    int remain_minute = (remainderTime - remain_hour * 3600) / 60;
//                    int remain_secand = (remainderTime - remain_hour * 3600 - remain_minute * 60);
                    String remain_hour_str = hour +"小时";
                    if(hour == 0){
                        remain_hour_str = "";
                    }
                    String remain_minute_str = minute + "分钟";
                    if(minute == 0){
                        remain_minute_str = "";
                    }
                    //距离走完所有的航路点的时间
                    MainActivity.instence.remain_time.setText("ETE :" + remain_hour_str + remain_minute_str + secand + "秒");
                    MainActivity.instence.deviate_distance.setText("XTK:" + deviateDistance + "米");
                }


            } else {
                MainActivity.instence.next_time.setVisibility(View.GONE);
                MainActivity.instence.remain_time.setVisibility(View.GONE);
                MainActivity.instence.deviate_distance.setVisibility(View.GONE);
            }
        }
    }

//    public void setServiceState() {
//        if (MainActivity.instence != null) {
//            if (MyApplication.getMyApplication().isBack) {
//                //回放的话,使用回放里面的变量
//                if (MainActivity.instence.lookback.isConnectWithService) {
//                    MainActivity.instence.connect_service_state.setText("服务器状态:正常");
//                    MainActivity.instence.connect_service_state.setTextColor(Color.GREEN);
//                } else {
//                    MainActivity.instence.connect_service_state.setText("服务器状态:断开");
//                    MainActivity.instence.connect_service_state.setTextColor(Color.RED);
//                }
//            } else {
//                //这里不是回放
//                if (clientTask.isConnectWithService) {
//                    MainActivity.instence.connect_service_state.setText("服务器状态:正常");
//                    MainActivity.instence.connect_service_state.setTextColor(Color.GREEN);
//                } else {
//                    MainActivity.instence.connect_service_state.setText("服务器状态:断开");
//                    MainActivity.instence.connect_service_state.setTextColor(Color.RED);
//                }
//            }
//        }
//    }

    public void setSeekBarPaocess(long currentRow) {
        if (totalRow != 0) {
            double temp = (double) currentRow / totalRow;
            int l = (int) (temp * 100);
            MainActivity.instence.lookback_seekbar.setProgress(l);
        }
    }

    //控制正在回放字的闪烁
    public void setIsLookBackText() {
        if (MainActivity.instence != null) {
            if (MainActivity.instence.isLookBackShow) {
                MainActivity.instence.isLookBackShow = false;
                MainActivity.instence.is_look_back.setVisibility(View.GONE);
            } else {
                MainActivity.instence.isLookBackShow = true;
                MainActivity.instence.is_look_back.setVisibility(View.VISIBLE);
            }
        }
    }
}
