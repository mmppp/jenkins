package com.hanke.navi.framwork.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.circle.OptionCircleView;
import com.hanke.navi.skyair.offline.OffLineMapUtils;
import com.hanke.navi.skyair.ui.MainActivity;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public static BaseActivity instance = null;
    public TextView tv_error, tv_msg_zy, tv_msg_jg;
    public LinearLayout bottom_bar;
    public MapView mapView;
    public AMap aMap;
    public LocationSource.OnLocationChangedListener mListener;
    public AMapLocationClient aMapLocationClient;
    public AMapLocationClientOption aMapLocationClientOption;
    public AMapLocationListener aMapLocationListener;
    public double latitude, longitude, haiba;
    public double speed, height_ground;
    public double bearing;
    public int width_include, height_include;
    //点击标记
    public Marker marker;
    public MarkerOptions markerOptions;
    public double distance;
    public OptionCircleView circle_deng;
    public Circle circle_nei_ben, circle_wai_ben;
    public int radius_nei = 8000, radius_wai = 15000;
    public Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = BaseActivity.this;
        // 设置应用单独的地图存储目录，在下载离线地图或初始化地图时设置
        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);
        setContentView(getContentViewId());
        initId();

        //必须回调MapView的onCreate()方法
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
//            initLocation();
        }
//        downloadOfflineMap();
        setaMap();
//        initLocation();
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setTranslucentStatus();
        setHideStatus();

        beforeInitView();
        initView();
        initData();

    }




    public void initLocation() {
        //初始化定位
        if (aMapLocationClient == null) {
            aMapLocationClient = new AMapLocationClient(MyApplication.getAppContext());
//            初始化AMapLocationClientOption对象
            aMapLocationClientOption = new AMapLocationClientOption();
            aMapLocationClient.setLocationListener(aMapLocationListener);
        }
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
        aMapLocationClientOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        aMapLocationClientOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        aMapLocationClientOption.setOnceLocation(true);//单次定位
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
//        aMapLocationClient.startLocation();
        latitude = MyApplication.getMyApplication().clientTask.getWd();
        longitude = MyApplication.getMyApplication().clientTask.getJd();
        Log.e("100", "latitude = " + latitude);
        Log.e("100", "longitude = " + longitude);

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));//14是500m

    }

    public void initId() {
        mapView = (MapView) findViewById(R.id.mapView);
//        tv_error = (TextView) findViewById(R.id.tv_error);
        bottom_bar = findViewByIdNoCast(R.id.bottom_bar);
//        tv_msg_zy = findViewByIdNoCast(R.id.tv_msg_zy);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(12 * MyApplication.getMyApplication().getWidth() / 16, MyApplication.getMyApplication().getWidth() / 10, 0, 0);
//        tv_msg_zy.setLayoutParams(layoutParams);
//        tv_msg_jg = findViewByIdNoCast(R.id.tv_msg_jg);
//        tv_msg_jg.setLayoutParams(layoutParams);
    }

    //设置aMap的属性
    public void setaMap() {

        // 自定义定位蓝点图标
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.zfj));
//        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
//        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
//        aMap.setMyLocationStyle(myLocationStyle);// 将自定义的 myLocationStyle 对象添加到地图上

        aMap.moveCamera(CameraUpdateFactory.zoomTo(11));//设置缩放级别
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//卫星地图
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(false  );//设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);//设置定位模式
        aMap.getUiSettings().setZoomControlsEnabled(false);//设置缩放控件是否显示
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
        aMap.getUiSettings().setCompassEnabled(true);// 设置指南针是否显示
        aMap.getUiSettings().setScaleControlsEnabled(true);// 设置比例尺是否显示
        aMap.getUiSettings().setZoomGesturesEnabled(true);//设置缩放手势
        aMap.getUiSettings().setScrollGesturesEnabled(true);//设置平移手势
        aMap.getUiSettings().setRotateGesturesEnabled(false);//设置旋转手势
        aMap.getUiSettings().setTiltGesturesEnabled(false);//设置倾斜手势
//        aMap.getUiSettings().setAllGesturesEnabled(true);//设置所有手势
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_MARGIN_LEFT);
        aMap.getUiSettings().setLogoMarginRate(-100, 200);

        //设置定位回调监听
//        aMapLocationListener = new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                if (mListener != null && aMapLocation != null) {
//                    if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
//
//                        mListener.onLocationChanged(aMapLocation);
//                        aMapLocation.getLatitude();
//                        aMapLocation.getLongitude();
//                        Log.e("000", "longitude = " + longitude + "|||" + "latitude = " + latitude + "|||" + "haiba = " + haiba);
//                        bearing = aMapLocation.getBearing();//获取方向角信息,即角速度
//                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyApplication.getMyApplication().clientTask.getWd(), MyApplication.getMyApplication().clientTask.getJd()), 9));// 设置当前地图显示为当前位置以及比例尺
//                        if (mListener != null) {
//                            mListener.onLocationChanged(aMapLocation);
//                        }
//                    } else {
//                        String msg_err = null;
//                        String[] str = aMapLocation.getErrorInfo().split(" ");
//                        for (int i = 0; i < str.length; i++) {
//                            msg_err = "定位失败，" + aMapLocation.getErrorCode() + ":" + str[0];
//                        }
//                        tv_error.setVisibility(View.VISIBLE);
//                        tv_error.setText(msg_err);
//                    }
//                }
//            }
//        };
//        initLocation();

//        aMap.setInfoWindowAdapter(new InfoWinAdapter(MyApplication.getAppContext()));
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {// 设置点击marker事件监听器
            @Override
            public boolean onMarkerClick(Marker marker) {
//                if (aMap!=null){
//                    InfoWinPop infoWinPop = new InfoWinPop(MyApplication.getAppContext(),marker);
//                    infoWinPop.showPopWindow(bottom_bar);
//                    return true;
//                }
                return false;
            }
        });

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(lastZoom == 0){
                    lastZoom = cameraPosition.zoom;
                }else{
                    if(lastZoom != cameraPosition.zoom){
                        MyApplication.getMyApplication().setWindowwInfoMarker2();
                    }
                    lastZoom = cameraPosition.zoom;
                }
                float zoom = cameraPosition.zoom;
                Log.i("hahaha", "zoom=" + zoom);
                //如果我在这里改变那个的话,就可以实时的刷新了.
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });

    }

    public float lastZoom = 0;

    public void addCircleNeiBen(LatLng latLng, int radius_nei) {
        CircleOptions circleOptions_nei = new CircleOptions();
        circleOptions_nei.center(latLng);
        circleOptions_nei.radius(radius_nei);
        circleOptions_nei.strokeWidth(6);
        circleOptions_nei.strokeColor(Color.RED);
        if (circleOptions_nei != null)
            circle_nei_ben = aMap.addCircle(circleOptions_nei);
    }

    public void addCircleWaiBen(LatLng latLng, int radius_wai) {
        CircleOptions circleOptions_wai = new CircleOptions();
        circleOptions_wai.center(latLng);
        circleOptions_wai.radius(radius_wai);
        circleOptions_wai.strokeWidth(6);
        circleOptions_wai.strokeColor(Color.YELLOW);
        if (circleOptions_wai != null)
            circle_wai_ben = aMap.addCircle(circleOptions_wai);
    }

    private final double EARTH_RADIUS = 6378137.0;

    // 返回单位是米
    public double getDistance(double latitude1, double longitude1,
                              double latitude2, double longitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        int bottom_bar_minHeight = MyApplication.getMyApplication().getHeight() / 21;
        bottom_bar.setMinimumHeight(bottom_bar_minHeight);
        width_include = bottom_bar.getWidth();
        height_include = bottom_bar.getHeight();
        if (height_include != bottom_bar_minHeight) {
            height_include = bottom_bar_minHeight;
        }
    }

    public abstract int getContentViewId();//放layoutId

    public abstract void beforeInitView();//初始化View之前做的事

    public abstract void initView();//初始化View

    public abstract void initData();//初始化数据

    public void setHideStatus() {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;//定义全屏参数
        Window window = BaseActivity.this.getWindow();//获得当前窗体对象
        window.setFlags(flag, flag);//设置当前窗体为全屏显示
    }

    public void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(layoutParams);
        }
    }

    public <T extends View> T findViewByIdNoCast(int id) {
        return (T) findViewById(id);
    }

    public void stopLocation() {
        mListener = null;
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        aMapLocationClient = null;
    }

    private double jingdu, weidu, sudu, gaodu, hangxiang;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        jingdu = MyApplication.getMyApplication().clientTask.getJd();
        outState.putDouble("jingdu", jingdu);
        weidu = MyApplication.getMyApplication().clientTask.getWd();
        outState.putDouble("weidu", weidu);
        sudu = MyApplication.getMyApplication().nav.CRange(MyApplication.getMyApplication().clientTask.getWda(),
                MyApplication.getMyApplication().clientTask.getJda(),
                MyApplication.getMyApplication().clientTask.getGda(),
                MyApplication.getMyApplication().clientTask.getWdb(),
                MyApplication.getMyApplication().clientTask.getJdb(),
                MyApplication.getMyApplication().clientTask.getGdb()) * 6 / 1;
        outState.putDouble("sudu", sudu);
        gaodu = MyApplication.getMyApplication().clientTask.getHb();
        outState.putDouble("gaodu", gaodu);
        hangxiang = 360 - MyApplication.getMyApplication().nav.getAngle(
                MyApplication.getMyApplication().clientTask.getWda(), MyApplication.getMyApplication().clientTask.getJda(),
                MyApplication.getMyApplication().clientTask.getWdb(), MyApplication.getMyApplication().clientTask.getJdb());
        outState.putDouble("hangxiang", hangxiang);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            jingdu = savedInstanceState.getDouble("jingdu");
            weidu = savedInstanceState.getDouble("weidu");
            sudu = savedInstanceState.getDouble("sudu");
            gaodu = savedInstanceState.getDouble("gaodu");
            hangxiang = savedInstanceState.getDouble("hangxiang");
        }
    }


}
