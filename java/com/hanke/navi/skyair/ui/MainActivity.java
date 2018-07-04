package com.hanke.navi.skyair.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.framwork.utils.Constants;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.circle.OptionCircleView;
import com.hanke.navi.skyair.pop.bean.HangLuBean;
import com.hanke.navi.skyair.pop.bean.HangXianBean;
import com.hanke.navi.skyair.pop.dxpop.DiXingPop;
import com.hanke.navi.skyair.pop.msgpop.MSGPop;
import com.hanke.navi.skyair.pop.navpop.hx.HXPop;
import com.hanke.navi.skyair.pop.setpop.SetCorrPop;
import com.hanke.navi.skyair.pop.tcpop.LayerPop;
import com.hanke.navi.skyair.pop.tcpop.XinXiPop;
import com.hanke.navi.skyair.pop.tsqpop.HintPop;
import com.hanke.navi.skyair.scale.VerticalScaleScrollViewLeft;
import com.hanke.navi.skyair.scale.VerticalScaleScrollViewRight;
import com.hanke.navi.skyair.service.AirSpaceWarningService;
import com.hanke.navi.skyair.socket.ClientTask_new_new;
import com.hanke.navi.skyair.socket.ClintTask_lookback;
import com.hanke.navi.skyair.util.CurveChart;
import com.hanke.navi.skyair.util.GaojingPreference;
import com.hanke.navi.skyair.util.PermissionsCheckerUtil;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import static com.hanke.navi.skyair.MyApplication.getAppContext;
import static com.hanke.navi.skyair.MyApplication.getMyApplication;

public class MainActivity extends BaseActivity implements OfflineMapManager.OfflineMapDownloadListener, SeekBar.OnSeekBarChangeListener {

    public static MainActivity instence;
    private static final String TAG = "MainActivity";
    private boolean isBack = true;//用于监听onKeyDown
    public TextView tv_tuceng, tv_daohang, tv_msg, tv_dixing, tv_home, tv_tishiquan, tv_shezhi;
    public VerticalScaleScrollViewLeft verticalScaleLeft;//左边指针刻度尺
    public VerticalScaleScrollViewRight verticalScaleRight;//左边指针刻度尺
    public Button xiazai;
    public ImageButton dingwei, jia, jian;
    public List<PopupWindow> listPop;//所有pop
    public List<Polyline> listPolyline;//航路点画线
    public List<Polyline> listPolyline_color;//只装红色的.
    public List<Polygon> listPolygon;//多边形空域画线
    public List<Text> listText;//多边形空域的名字
    public List<Circle> listCirclejg, listCirclezy;//警告提示圈 、注意提示圈
    public int pz_flag;
    public List<Polyline> pzlist_polyline;
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_CONFIGURATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    private PermissionsCheckerUtil mPermissionsChecker;
    private AlertDialog dialog;
    private OfflineMapManager amapManager;
    private List<OfflineMapProvince> provinceList;
    public TextView right_date;
    public TextView right_time;
    public TextView beidou_state;
    public TextView adsb_state;
    public TextView air_space_state;
    public TextView impact_state;
    public TextView impact_land_state;
    public TextView next_time;
    public TextView remain_time;
    public TextView deviate_distance;
    public TextView connect_service_state;
    public SeekBar lookback_seekbar;
    private GaojingPreference preference;
    private TextView start_stop_lookback;
    private TextView lookback_speed;
    private TextView speed_chengyi;
    private TextView speed_chuyi;
    private TextView lookback_file_choose;
    private int[] multipleArr;
    //回放倍数当前的选择index
    private int lookBackIndex;
    public LinearLayout lookback_set_ll;
    private TextView exit_lookbakc;
    public TextView is_look_back;
    public boolean isLookBackShow;
    public LinearLayout look_back_text_choose_ll;
    public ClintTask_lookback lookback;
    private LinearLayout ll_jia_and_jian;
    private DiXingPop diXingPop;
    private CurveChart curveChart;
    //高层查询越界提示textview
    public TextView heightfindoverfloor;
    public LinearLayout ll_lookback;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void beforeInitView() {
        instence = MainActivity.this;
        //设置竖屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            for (int x = 0; x < PERMISSIONS.length; x++) {
                int i = ContextCompat.checkSelfPermission(this, PERMISSIONS[x]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    startRequestPermission();
                }
            }
        }
        preference = new GaojingPreference(getApplicationContext());
        multipleArr = new int[]{1, 2, 4, 8, 16};

        listPop = new ArrayList<PopupWindow>();
        listPolyline = new ArrayList<Polyline>();
        listPolygon = new ArrayList<Polygon>();
        listText = new ArrayList<Text>();
        listCirclejg = new ArrayList<Circle>();
        listCirclezy = new ArrayList<Circle>();
        pzlist_polyline = new ArrayList<Polyline>();
//        xiazai = findViewByIdNoCast(R.id.xiazai);
        dingwei = findViewByIdNoCast(R.id.dingwei);
        ll_jia_and_jian = findViewByIdNoCast(R.id.ll_jia_and_jian);
        jia = findViewByIdNoCast(R.id.jia);
        jian = findViewByIdNoCast(R.id.jian);
        tv_tuceng = findViewByIdNoCast(R.id.tv_tuceng);
        tv_daohang = findViewByIdNoCast(R.id.tv_daohang);
        tv_msg = findViewByIdNoCast(R.id.tv_msg);
        tv_dixing = findViewByIdNoCast(R.id.tv_dixing);
        tv_home = findViewByIdNoCast(R.id.tv_home);
        tv_tishiquan = findViewByIdNoCast(R.id.tv_tishiquan);
        tv_shezhi = findViewByIdNoCast(R.id.tv_shezhi);
        circle_deng = (OptionCircleView) findViewById(R.id.circle_deng);
        //gps里面的解析的日期和时间
        right_date = (TextView) findViewById(R.id.right_date);
        right_time = (TextView) findViewById(R.id.right_time);
        beidou_state = (TextView) findViewById(R.id.beidou_state);
        adsb_state = (TextView) findViewById(R.id.adsb_state);
        //空域状态
        air_space_state = (TextView) findViewById(R.id.air_space_state);
        //防碰撞状态
        impact_state = (TextView) findViewById(R.id.impact_state);
        //防撞地状态
        impact_land_state = (TextView) findViewById(R.id.impact_land_state);
        //回放seekbar
        lookback_seekbar = (SeekBar) findViewById(R.id.lookback_seekbar);
        //seekbar监听器
        lookback_seekbar.setOnSeekBarChangeListener(this);
        verticalScaleLeft = findViewByIdNoCast(R.id.verticalScaleLeft);
        verticalScaleRight = findViewByIdNoCast(R.id.verticalScaleRight);
        //回放操控的按钮
        ll_lookback = (LinearLayout) findViewById(R.id.ll_lookback);
        lookback_set_ll = (LinearLayout) findViewById(R.id.lookback_set_ll);
        look_back_text_choose_ll = (LinearLayout) findViewById(R.id.look_back_text_choose_ll);
        start_stop_lookback = (TextView) findViewById(R.id.start_stop_lookback);
        start_stop_lookback.setOnClickListener(this);
        is_look_back = (TextView) findViewById(R.id.is_look_back);
        lookback_speed = (TextView) findViewById(R.id.lookback_speed);
        speed_chengyi = (TextView) findViewById(R.id.speed_chengyi);
        speed_chengyi.setOnClickListener(this);
        speed_chuyi = (TextView) findViewById(R.id.speed_chuyi);
        speed_chuyi.setOnClickListener(this);
        lookback_file_choose = (TextView) findViewById(R.id.lookback_file_choose);
        lookback_file_choose.setOnClickListener(this);
        exit_lookbakc = (TextView) findViewById(R.id.exit_lookbakc);
        exit_lookbakc.setOnClickListener(this);
        heightfindoverfloor = (TextView) findViewById(R.id.heightfindoverfloor);


        //导航时候显示的到达时间和剩余时间
        next_time = (TextView) findViewById(R.id.next_time);
        remain_time = (TextView) findViewById(R.id.remain_time);
        deviate_distance = (TextView) findViewById(R.id.deviate_distance);

        //显示服务器的连接状态
        connect_service_state = (TextView) findViewById(R.id.connect_service_state);
        lookback = new ClintTask_lookback(getApplicationContext(), true, 0);

        //不管北斗有没有发送位置,这里先定位到上次结束的地方,如果没有发送的话,也不会停在一个奇怪的地方
        MyApplication.getMyApplication().setDefault();
        MyApplication.getMyApplication().setCenterPos();
//        new1 = new ClientTask_new();

//        setUpdateCursor();
//        genXin();
    }

    //构造OfflineMapManager对象
    OfflineMapManager.OfflineMapDownloadListener listener = new OfflineMapManager.OfflineMapDownloadListener() {
        @Override
        public void onDownload(int i, int i1, String s) {
            //离线地图下载完成,但是只可以现在普通地图,不能下载卫星地图.
            Log.i("offlinemapdown", "正在下载" + i + "..." + i1 + "..." + s);
//            if (i1 == 100) {
//                Toast.makeText(MainActivity.this, "离线地图下载完成", Toast.LENGTH_SHORT).show();
//            }
        }

        @Override
        public void onCheckUpdate(boolean b, String s) {

        }

        @Override
        public void onRemove(boolean b, String s, String s1) {

        }
    };

    private void downloadOfflineMap() {
        final OfflineMapManager amapManager = new OfflineMapManager(this, listener);
//        ArrayList<OfflineMapProvince> offlineMapProvinceList = amapManager.getOfflineMapProvinceList();
        //按照cityname下载
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //先查看是否有更新
                    //根据当前所在的城市名称来下载离线地图
                    amapManager.downloadByProvinceName("陕西省");
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void initView() {
//        xiazai.setOnClickListener(this);
        dingwei.setOnClickListener(this);
        jia.setOnClickListener(this);
        jian.setOnClickListener(this);
        tv_tuceng.setOnClickListener(this);
        tv_daohang.setOnClickListener(this);
        tv_msg.setOnClickListener(this);
        tv_dixing.setOnClickListener(this);
        tv_home.setOnClickListener(this);
        tv_tishiquan.setOnClickListener(this);
        tv_shezhi.setOnClickListener(this);
        //画空域
        drawAirSpace();
        //提前吧地形pop的对象new出来,这样才可以一直计算数据
        diXingPop = new DiXingPop(this);
    }

    private void drawAirSpace() {
        XinXiPop xinXiPop = new XinXiPop(this);
        int count = xinXiPop.getCounts();//item总数目
        for (int i = 0; i < count; i++) {
            xinXiPop.setSpaceAir(i);
        }
        //启动service
        Intent intent = new Intent(this, AirSpaceWarningService.class);
        startService(intent);
    }

    @Override
    public void initData() {
//        new ClientTask(MyApplication.getAppContext(), MainActivity.this).execute();
    }

    public boolean flag_zx = true;//false代表在执行导航 true代表没有执行导航
    public boolean flag1 = true;
    public boolean flag2 = true;
    public boolean flag3 = true;
    public boolean flag4 = true;
    //true代表没有打开,false代表打开了
    public boolean flag5 = true;
    public boolean flag6 = true;
    public boolean flag_notice = true;

    public List<HangXianBean> data_hx_ap;
    public List<HangLuBean> data_hl_ap;

    @Override
    public void onClick(final View v) {
        //图层
        LayerPop layerPop = new LayerPop(this);
        layerPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                flag1 = true;
            }
        });
        //导航
        HXPop hxPop = new HXPop(this);
        hxPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                flag2 = true;
            }
        });

        this.speed = VerticalScaleScrollViewLeft.scale_v;
        this.height_ground = VerticalScaleScrollViewRight.scale_h;
        //MSG
        LatLng homePlaneLatlng = getMyApplication().homePlaneLatlng;
        MSGPop msgPop = new MSGPop(this, getMyApplication().homePlane);
        msgPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                flag3 = true;
            }
        });

        //提示图
        HintPop hintPop = new HintPop(this);
        hintPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                flag4 = true;
            }
        });

        //地形
        diXingPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //这里把高层那个字也消失了
                MyApplication.getMyApplication().dixingIsOpen = false;
                heightfindoverfloor.setVisibility(View.GONE);
                flag5 = true;
                //这里地形pop消失了,这个时候如果正在回放的话,那么把回放的空间给还原到之前的位置上去
                if (MyApplication.getMyApplication().isClickLookBak) {
                    ViewGroup.MarginLayoutParams margin_set = new ViewGroup.MarginLayoutParams(lookback_set_ll.getLayoutParams());
                    int left = (MyApplication.getMyApplication().getWidth() - margin_set.width) / 2;
                    int top = MyApplication.getMyApplication().getHeight() - 3 * margin_set.height;
                    margin_set.setMargins(left, top, 0, 0);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin_set);
                    lookback_set_ll.setLayoutParams(layoutParams);

                    //seekbar
                    ViewGroup.MarginLayoutParams margin_lookback_seekbar = new ViewGroup.MarginLayoutParams(lookback_seekbar.getLayoutParams());
                    int top_seekbar = top - margin_lookback_seekbar.height;
                    margin_lookback_seekbar.setMargins(left, top_seekbar, 0, 0);
                    RelativeLayout.LayoutParams layoutParams_margin_lookback_seekbar = new RelativeLayout.LayoutParams(margin_lookback_seekbar);
                    lookback_seekbar.setLayoutParams(layoutParams_margin_lookback_seekbar);

                    ViewGroup.MarginLayoutParams margin_textchoose = new ViewGroup.MarginLayoutParams(look_back_text_choose_ll.getLayoutParams());
                    int top_textchoose = top_seekbar - margin_textchoose.height;
                    margin_textchoose.setMargins(left, top_textchoose, 0, 0);
                    RelativeLayout.LayoutParams layoutParams_margin_textchoose = new RelativeLayout.LayoutParams(margin_textchoose);
                    look_back_text_choose_ll.setLayoutParams(layoutParams_margin_textchoose);
                }
            }
        });

        //设置
        SetCorrPop setCorrPop = new SetCorrPop(this);
        setCorrPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                flag6 = true;
            }
        });

        switch (v.getId()) {
            case R.id.exit_lookbakc:
                //点击退出回放按钮,重新连接服务器接收数据,这里已经验证过是可以的了.
                //隐藏回放栏
                //将控件上面的字也还原
                start_stop_lookback.setText("开始回放");
                lookback_speed.setText("快进倍数:1");
                lookback_file_choose.setText("点击选择回放文件");
                //然后隐藏掉回放的控件
                ll_lookback.setVisibility(View.GONE);
                lookback_set_ll.setVisibility(View.GONE);
                look_back_text_choose_ll.setVisibility(View.GONE);
                lookback_seekbar.setVisibility(View.GONE);
                is_look_back.setVisibility(View.GONE);
                if (MyApplication.getMyApplication().isBack) {
                    //这说明点了回放,才需要做接下来的操作,否则就不需要做接下来的操作
                    MyApplication.getMyApplication().marker.remove();
                    MyApplication.getMyApplication().marker = null;
                    if (lookback != null) {
                        lookback.cancel(true);
                    }
                    //去掉之前回放时候的轨迹
                    ArrayList<Marker> lookback_point = MyApplication.getMyApplication().lookback_Point;
                    for (int x = 0; x < lookback_point.size(); x++) {
                        lookback_point.get(x).remove();
                    }
                    lookback_point.clear();

                    MyApplication.getMyApplication().isClickLookBak = false;
                    MyApplication.getMyApplication().isBack = false;
                    MyApplication.getMyApplication().clientTask = new ClientTask_new_new(getApplicationContext(), false, 0);
//                    MyApplication.getMyApplication().clientTask = new ClientTask_new(getApplicationContext(), false, 0);
                    MyApplication.getMyApplication().clientTask.execute();
                }

                break;

            case R.id.start_stop_lookback:
                MyApplication.getMyApplication().isBack = true;
                lookback_seekbar.setVisibility(View.VISIBLE);
                //把正在回放的字显示出来,并且闪烁
                is_look_back.setVisibility(View.VISIBLE);
                //这里可以使用handler来完成循环的操作
                handler.sendEmptyMessage(156);
                look_back_text_choose_ll.setVisibility(View.VISIBLE);
                MyApplication.getMyApplication().clientTask.cancel(true);
                MyApplication.getMyApplication().hopeRow = 0;
                MyApplication.getMyApplication().multipleNuml = Integer.valueOf(lookback_speed.getText().toString().split(":")[1]);

                if (start_stop_lookback.getText().toString().equals("开始回放")) {
                    if (lookback_file_choose.getText().toString().equals("点击选择回放文件")) {
                        //说明没有选择回放文件,不启动clinttask
                        Toast.makeText(this, "请选择回放文件", Toast.LENGTH_SHORT).show();
                    } else {
                        //储存回放文件
                        preference.setLookBackFile(lookback_file_choose.getText().toString());
                        //清除掉之前正常飞行时候的轨迹,但是这里是更改ui的操作
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                ArrayList<Marker> flying_point = MyApplication.getMyApplication().flying_Point;
                                for (int x = 0; x < flying_point.size(); x++) {
                                    flying_point.get(x).remove();
                                }
                                flying_point.clear();

                            }
                        }.start();

                        start_stop_lookback.setText("暂停回放");
                        //这里开始正式的回放
                        if (lookback == null) {
                            //走到这里说明是暂停过又开始了
                            lookback = new ClintTask_lookback(this, true, 0);
                            MyApplication.getMyApplication().hopeRow = MyApplication.getMyApplication().pauseTempRowLine;
                        }
                        lookback.executeOnExecutor(Executors.newCachedThreadPool());
//                          lookback.execute();

                    }
                } else {
                    start_stop_lookback.setText("开始回放");
                    //这里暂时暂停回放
                    lookback.cancel(true);
                    //因为重新执行了,所以是重新从第一行开始读了,这个时候,我相当于让进度条拖动到刚才读到的位置.因为没有new对象.所以rowline还是刚才已经读到的位置
                    MyApplication.getMyApplication().pauseTempRowLine = lookback.rowLine;
                    lookback = null;
                }

                break;

            case R.id.speed_chengyi:
                if (lookBackIndex < multipleArr.length - 1) {
                    lookBackIndex++;
                } else {
                    lookBackIndex = 0;
                }
                lookback_speed.setText("快进倍数:" + multipleArr[lookBackIndex] + "");
                MyApplication.getMyApplication().multipleNuml = Integer.valueOf(lookback_speed.getText().toString().split(":")[1]);
                break;
            case R.id.speed_chuyi:
                if (lookBackIndex != 0) {
                    lookBackIndex--;
                } else {
                    lookBackIndex = multipleArr.length - 1;
                }
                lookback_speed.setText("快进倍数:" + multipleArr[lookBackIndex] + "");
                MyApplication.getMyApplication().multipleNuml = Integer.valueOf(lookback_speed.getText().toString().split(":")[1]);
                break;
            case R.id.lookback_file_choose:
                String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "lookback";
                File file = new File(path);
                final ArrayList<String> data = new ArrayList<>();
                if (file.exists()) {
                    if (file.isDirectory()) {
                        String[] filelist = file.list();
                        for (int i = 0; i < filelist.length; i++) {
                            File readfile = new File(path + "\\" + filelist[i]);
                            String fileName = readfile.getName();
                            data.add(fileName.substring(9));
                        }
                    }
                }
                View inflate = View.inflate(this, R.layout.choose_airport, null);
                final Dialog dialog = new Dialog(this, R.style.dialog);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                WindowManager windowManager = MainActivity.instence.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = (int) (display.getWidth() * 0.5); //设置宽度
                lp.height = (int) (display.getHeight() * 0.4);
                dialog.getWindow().setAttributes(lp);
                dialog.setContentView(inflate);
                ListView viewById = (ListView) inflate.findViewById(R.id.lv_choose_airport);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.style_spinner, data);
                arrayAdapter.setDropDownViewResource(R.layout.select_dialog_singlechoice);
                viewById.setAdapter(arrayAdapter);
                viewById.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = data.get(position);
                        lookback_file_choose.setText(name);
                        dialog.cancel();
                    }
                });
                break;

            case R.id.dingwei:
                if (MyApplication.getMyApplication().isBack) {
                    if (MainActivity.instence.lookback.getWd() == 0 && MainActivity.instence.lookback.getJd() == 0) {
                        String[] latAndLon = preference.getLatAndLon();
                        if (TextUtils.isEmpty(latAndLon[0])) {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.XIANYANG, 11));
                        } else {
                            LatLng latLng = new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1]));
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        }
                    } else {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.instence.lookback.getWd(), MainActivity.instence.lookback.getJd()), 9));
                    }
                } else {
                    if (getMyApplication().clientTask.getWd() == 0 && getMyApplication().clientTask.getJd() == 0) {
                        String[] latAndLon = preference.getLatAndLon();
                        if (TextUtils.isEmpty(latAndLon[0])) {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.XIANYANG, 11));
                        } else {
                            LatLng latLng = new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1]));
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                        }
                    } else {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getMyApplication().clientTask.getWd(), getMyApplication().clientTask.getJd()), 11));
                    }
                }
//                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(MyApplication.clientTask.getWd(), MyApplication.clientTask.getJd())));
                break;
            case R.id.jia:
                aMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.jian:
                aMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
//            case R.id.xiazai:
//                Intent intent = new Intent(MainActivity.this, OfflineMapActivity.class);
//                startActivity(intent);
//                break;
            case R.id.tv_tuceng:
//                popsDismiss();
                if (flag1) {
                    layerPop.showPopWindow(tv_tuceng);
                    flag1 = !flag1;
                }

//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        Log.i("readread", "开始了");
//                        handler.sendEmptyMessage(1111);
//                        ReadGaocengFileUtil.readGaocengFile(getApplicationContext());
//                        handler.sendEmptyMessage(2222);
//                        Log.i("readread", "结束了");
//                    }
//                }.start();

                break;
            case R.id.tv_daohang:
//                popsDismiss();
                if (flag2) {
                    hxPop.showPopWindow(tv_daohang);
                    if (hxPop.data_hx != null) {
                        data_hx_ap = hxPop.data_hx;
                    }
                    flag2 = !flag2;
                }
                //查询某一个点的速度
//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        float height = ReadGaocengFileUtil.getHeight(35.58748, 111.83487);
//                        Log.i("testtesthaha", "height:" + height);
//                    }
//                }.start();

                //执行temp方法,来测试效果怎么样了
//                byte[] temp = { 48, 48, 53, 48, 44, 65, 44, 51,36, 71, 80, 82, 77, 67, 44, 48, 51, 48, 48, 53, 48, 44, 65, 44, 51, 51, 53, 57, 46, 57, 48, 48, 48, 48, 44, 78, 44, 49, 48, 56, 52, 57, 46, 56, 55, 48, 56, 48, 44, 68, 44, 51, 48, 48, 44, 57, 48};
//                new1.temp(temp);
                break;
            case R.id.tv_msg:
//                popsDismiss();
                if (flag3) {
                    msgPop.showPopWindow(tv_msg);
                    flag3 = !flag3;
                }
                break;
            case R.id.tv_home:
                flag1 = true;
                flag2 = true;
                flag3 = true;
                flag4 = true;
                flag5 = true;
                flag6 = true;
                flag_notice = true;

//                popsDismiss();

                if (listPop.size() == 0) {
                    new Throwable().printStackTrace();
                } else {
                    try {
                        for (int i = 0; i < listPop.size(); i++) {
                            listPop.get(i).dismiss();
                        }
                        listPop.clear();
                    } catch (ConcurrentModificationException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.tv_tishiquan:
//                popsDismiss();
                if (flag4) {
                    hintPop.showPopWindow(tv_tishiquan);
                    flag4 = !flag4;
                }
                break;
            case R.id.tv_dixing:
//                popsDismiss();
                //这里需要置顶定位和缩放的按钮
                MyApplication.getMyApplication().dixingIsOpen = true;
                MainActivity.instence.listPop.add(diXingPop);
                //如果这个时候处于回放的时候,那么回放的那个界面就应该在地形界面的上面.而不是被挡住了.
                if (MyApplication.getMyApplication().isClickLookBak) {
//                    ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(look_back_text_choose_ll.getLayoutParams());
//                    int left = MyApplication.getMyApplication().getWidth() - look_back_text_choose_ll.getWidth();
//                    int top = MyApplication.getMyApplication().getHeight() - 5 * MyApplication.getMyApplication().getHeight() / 18 - MyApplication.getMyApplication().getHeight() / 105 - look_back_text_choose_ll.getHeight();
//                    params.setMargins(left / 2, top, 0, 0);
//                    look_back_text_choose_ll.setLayoutParams(params);
                    //文件选择
                    ViewGroup.MarginLayoutParams margin_set = new ViewGroup.MarginLayoutParams(lookback_set_ll.getLayoutParams());
                    int left = (MyApplication.getMyApplication().getWidth() - margin_set.width) / 2;
                    int graphicalViewHeight = 5 * MyApplication.getMyApplication().getHeight() / 18 - MyApplication.getMyApplication().getHeight() / 105;
                    int top = MyApplication.getMyApplication().getHeight() - 2 * margin_set.height - graphicalViewHeight - 25;
                    margin_set.setMargins(left, top, 0, 0);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin_set);
                    lookback_set_ll.setLayoutParams(layoutParams);

                    //seekbar
                    ViewGroup.MarginLayoutParams margin_lookback_seekbar = new ViewGroup.MarginLayoutParams(lookback_seekbar.getLayoutParams());
                    int top_seekbar = top - margin_lookback_seekbar.height;
                    margin_lookback_seekbar.setMargins(left, top_seekbar, 0, 0);
                    RelativeLayout.LayoutParams layoutParams_margin_lookback_seekbar = new RelativeLayout.LayoutParams(margin_lookback_seekbar);
                    lookback_seekbar.setLayoutParams(layoutParams_margin_lookback_seekbar);

                    ViewGroup.MarginLayoutParams margin_textchoose = new ViewGroup.MarginLayoutParams(look_back_text_choose_ll.getLayoutParams());
                    int top_textchoose = top_seekbar - margin_textchoose.height;
                    margin_textchoose.setMargins(left, top_textchoose, 0, 0);
                    RelativeLayout.LayoutParams layoutParams_margin_textchoose = new RelativeLayout.LayoutParams(margin_textchoose);
                    look_back_text_choose_ll.setLayoutParams(layoutParams_margin_textchoose);

//                    look_back_text_choose_ll.setTop(100);
//                    look_back_text_choose_ll.invalidate();
                }

                if (flag5) {
                    diXingPop.showPopWindow(tv_dixing);
                    flag5 = !flag5;
                }
                break;
            case R.id.tv_shezhi:
//                popsDismiss();
                if (flag6) {
                    setCorrPop.showPopWindow(tv_shezhi);
                    flag6 = !flag6;
                }
                break;
        }
    }

//    private void popsDismiss() {
////        for (PopupWindow pop : listPop) {
////            pop.dismiss();
////        }
//    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.KEYBACK://再点退出
                    isBack = true;
                    break;
                case Constants.ZHIXING://点击执行
                    flag_zx = false;
                    break;
                case 156:
//                    ChaoXian();
                    MyApplication.getMyApplication().setIsLookBackText();
                    Log.i("hahaha", "发了一次消息");
                    if (MyApplication.getMyApplication().isBack) {
                        handler.sendEmptyMessageDelayed(156, 500);
                    }

                    break;
                case 1111:
                    Toast.makeText(getAppContext(), "开始了", Toast.LENGTH_SHORT).show();
                    break;
                case 2222:
                    Toast.makeText(getAppContext(), "结束了", Toast.LENGTH_SHORT).show();
                    break;


                case 200:
                    Canvas canvas = new Canvas();
                    Bundle data = msg.getData();
                    ArrayList<Float> xValueList = (ArrayList<Float>) data.getSerializable("xValueList");
                    ArrayList<Float> yValueList = (ArrayList<Float>) data.getSerializable("yValueList");
                    if (diXingPop != null) {
                        if (curveChart == null) {
                            curveChart = diXingPop.curveChart;
                        }
                        if (curveChart != null) {
                            curveChart.setxValues(xValueList);
                            curveChart.setyValues(yValueList);
                            curveChart.invalidate();
                        }
                    }

                    break;

            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {//按下返回键
            if (isBack) {
                Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
                isBack = false;
                handler.sendEmptyMessageDelayed(Constants.KEYBACK, 2000);
            } else {
                //退出app
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("更改ip地址和端口号后需重启该应用程序\n是否重启？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //重启app代码
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
//                        Intent intent = new Intent(MyApplication.getAppContext(),MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        MyApplication.getAppContext().startActivity(intent);
//                        Process.killProcess(Process.myPid());

                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        Process.killProcess(Process.myPid());
                    }
                }, 500);

            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
        //view加载完成时回调,隐藏地图的logo
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup child = (ViewGroup) mapView.getChildAt(0);//地图框架
                if (child.getChildAt(2) != null)
                    child.getChildAt(2).setVisibility(View.GONE);//logo
            }
        });
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults.length != 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                        if (!b) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSettting();
                        } else
                            finish();
                    }
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限
    private void showDialogTipUserGoToAppSettting() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许应用请求权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, PERMISSIONS[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
        stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mapView != null) {
            mapView.onDestroy();
        }
        getMyApplication().clientTask.list_wd.clear();
        getMyApplication().clientTask.list_jd.clear();
        getMyApplication().clientTask.list_gd.clear();
    }

    public void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
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

    //seekbar监听的监听器
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress == 100) {
            //播放完成之后,进度条停留在最后面
            seekBar.setProgress(100);
            lookback.cancel(true);
            //然后这里有开始播放和暂停播放的文字,应该吧开始播放的文字改为暂停播放
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //拖动进度条之后,删除之前的飞机航迹.
        ArrayList<Marker> lookback_point = MyApplication.getMyApplication().lookback_Point;
        for (int x = 0; x < lookback_point.size(); x++) {
            lookback_point.get(x).remove();
        }
        lookback_point.clear();
        //然后再跳动到拖动的界面
        double process = seekBar.getProgress();
        double precent = process / 100;
        double total = (double) MyApplication.getMyApplication().totalRow;
        double progressRow = total * precent;
        int hopeRow = (int) Math.floor(progressRow);
        MyApplication.getMyApplication().hopeRow = hopeRow;
    }
}
