package com.hanke.navi.skyair.pop.dxpop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.db.GaocengDataDBHelper;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.ui.view.CurvesView_two;
import com.hanke.navi.skyair.util.CurveChart;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.GaojingPreference;
import com.hanke.navi.skyair.util.ReadGaocengFileUtil;

import org.achartengine.GraphicalView;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class DiXingPop extends PopupWindow {

    private Context context;
    private GraphicalView graphicalView;
    private DXChartService dxChartService;
    public Timer timer;
    private List<String> list;
    private List<Double> listx, listhb;
    private double t = 0;
    private RelativeLayout rel;
    private ImageView plane;
    private List<String> readList;
    public int start = 0, end = 35;
    //飞机在曲线图上面距离顶部的距离
    public int planeHeight = 0;
    //曲线图y轴的最大周
    public int maxHeight = 3000;
    //曲线图y轴的最小周
    public int minHeight = 0;
    //飞机真是的飞行高度
    public double planeFlyHeight = 0;
    //y轴的刻度数量
    public int yLables = 0;
    public int graphicalViewHeight;
    public boolean isReadGCAlready;
    private BufferedReader reader;
    private String line;
    private Nav nav;
    private GaocengDataDBHelper dbHelper;
    private Paint paint;
    private CurvesView_two curvesView;
    public CurveChart curveChart;
    private float onepiece;
    private float piece = 256;
    //判断前方多少米的高层信息
    public float fronDistance = 15;
    //60s以内会撞地
    private boolean isImapctLand = false;
    //30s以内会撞地
    private boolean is30SecandImpactLand = false;
    private GaojingPreference preference;

    public DiXingPop(Context context) {
        this(context, null);
    }

    public DiXingPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiXingPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        list = new ArrayList<>();
        listx = new ArrayList<>();
        listhb = new ArrayList<>();
        timer = new Timer();
        nav = new Nav(context);
        dbHelper = new GaocengDataDBHelper(context);
        paint = new Paint();
        preference = new GaojingPreference(context);
        initView();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Canvas canvas = new Canvas();
                Bundle data = msg.getData();
                ArrayList<Float> xValueList = (ArrayList<Float>) data.getSerializable("xValueList");
                ArrayList<Float> yValueList = (ArrayList<Float>) data.getSerializable("yValueList");
//                curveChart.drawPoint(canvas, xValueList, yValueList);
                curveChart.setxValues(xValueList);
                curveChart.setyValues(yValueList);
                curveChart.invalidate();
                super.handleMessage(msg);
            } else if (msg.what == 2) {
                //走到这里的时候,说明有撞地的危险
                MyApplication.getMyApplication().isImpactLandDanger = true;
            } else if (msg.what == 3) {
                MyApplication.getMyApplication().isImpactLandDanger = false;
                MyApplication.getMyApplication().isImpactLandDanger30 = false;
            } else if (msg.what == 4) {
                if (MyApplication.getMyApplication().dixingIsOpen) {
                    MainActivity.instence.heightfindoverfloor.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == 5) {
                MainActivity.instence.heightfindoverfloor.setVisibility(View.GONE);
            } else if (msg.what == 6) {
                MyApplication.getMyApplication().isImpactLandDanger30 = true;
            }

        }
    };

    public void initView() {
        onepiece = fronDistance / piece;
        View view = View.inflate(context, R.layout.shandi, null);
        this.setContentView(view);
        rel = (RelativeLayout) view.findViewById(R.id.rel);
        this.setWidth(MyApplication.getMyApplication().getWidth() - 130);
        Log.i("widthwidth", "dd" + (MyApplication.getMyApplication().getWidth() - 130));
        graphicalViewHeight = 5 * MyApplication.getMyApplication().getHeight() / 18 - MyApplication.getMyApplication().getHeight() / 105;
        this.setHeight(graphicalViewHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(rel.getLayoutParams());
//        this.setContentView(graphicalView);
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        //屏蔽触摸
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        curveChart = (CurveChart) view.findViewById(R.id.curvechart);

        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
//                    synchronized (this) {
                    PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
                    LatLng latLng = homePlane.latLng;
                    ArrayList<Float> xVlaueList = new ArrayList<>();
                    ArrayList<Float> yVlaueList = new ArrayList<>();
                    if (homePlane != null && latLng != null) {
                        for (int x = 0; x < piece; x++) {
                            //拿到当前的实时的经纬度
                            xVlaueList.add(onepiece * x);
                            double[] doubles = nav.PosVd11(Double.parseDouble(DecimalUtil.remainDecimal(latLng.latitude, 5)), Double.parseDouble(DecimalUtil.remainDecimal(latLng.longitude, 5)), 360 - homePlane.flyAngle, onepiece * (x + 1) * 1000);
                            if (x == 255) {
                                double[] a = nav.PosVd11(Double.parseDouble(DecimalUtil.remainDecimal(latLng.latitude, 5)), Double.parseDouble(DecimalUtil.remainDecimal(latLng.longitude, 5)), 360 - homePlane.flyAngle, onepiece * (x + 1) * 1000);
                                Log.i("posv", "zhi:" + latLng.latitude + "," + latLng.longitude + "," + (360 - homePlane.flyAngle) + "," + (onepiece * (x + 1) * 1000));
                            }
                            //这里去高层文件里面读取,如果没有在文件里面的话,返回一个特殊的值
                            float height = ReadGaocengFileUtil.getHeight(doubles[0], doubles[1]);
                            if (x == piece - 1) {
                                Log.i("latlon", doubles[0] + "," + doubles[1]);
                            }
                            if (height == ReadGaocengFileUtil.OriginalResut) {
                                //说明已经超过了范围了
                                MyApplication.getMyApplication().readHeightIsOverFloor = true;
                                handler.sendEmptyMessage(4);
                            } else {
                                handler.sendEmptyMessage(5);
                                MyApplication.getMyApplication().readHeightIsOverFloor = false;
                                Log.i("ReadGaocengFileUtil", height + "");
                                yVlaueList.add(height);
                            }
                        }

                        if (!MyApplication.getMyApplication().readHeightIsOverFloor) {
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("xValueList", xVlaueList);
                            bundle.putSerializable("yValueList", yVlaueList);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                            //一分钟的时间按照当前的升降速度,将会经过的高度.
                            //计算按照当前的升降速度,去算出来30s或者是60s之后的一个高度
                            isImapctLand = false;
                            is30SecandImpactLand = false;

                            //256个点,每个点之间是多少距离.
                            int pieceNum = (int) (homePlane.flySpeed / 3.6 * 60 / (fronDistance * 1000) * piece);
                            if (pieceNum > 256) {
                                pieceNum = 255;
                            }
                            //60s之后的高度
                            double height60 = homePlane.flyHeight + 60 * homePlane.upOrDownSpeed;
                            if (height60 <= yVlaueList.get(pieceNum)) {
                                isImapctLand = true;
                                if (isImapctLand) {
                                    //如果60s撞地了,开始计算是否会30s撞地
                                    int pieceNum30 = (int) (homePlane.flySpeed / 3.6 * 30 / (fronDistance * 1000) * piece);
                                    if (pieceNum30 > piece) {
                                        pieceNum30 = (int) piece;
                                    }
                                    double height30 = homePlane.flyHeight + 30 * homePlane.upOrDownSpeed;
                                    if (height30 <= yVlaueList.get(pieceNum30)) {
                                        is30SecandImpactLand = true;
                                    }
                                }
                            }

//                            for (int x = 0; x < pieceNum; x++) {
//                                //一份的时间
//                                double time = (60 / pieceNum) * (x + 1);
//                                //计算在这个时间范围内的高度变化
//                                double v = homePlane.flyHeight + time * homePlane.upOrDownSpeed;
//                                if (x < yVlaueList.size()) {
//                                    if (v <= yVlaueList.get(x)) {
//                                        isImapctLand = true;
//                                        //如果进入到60s之内会撞地的话,那么再开始计算30s之内是否会撞地
////                                        double time30 = (30 / pieceNum) * (x + 1);
////                                        double v30 = homePlane.flyHeight + time30 * homePlane.upOrDownSpeed;
////                                        if (v30 <= yVlaueList.get(x)) {
////                                            is30SecandImpactLand = true;
////                                        }
//                                        break;
//                                    }
//                                }
//                            }

                            //然后根据这个高度,去15km里面找是否有高于这个高度的目标,如果有,那么就是提示会撞地,如果没有,就不提示撞地
//                            double v = homePlane.flyHeight + 60 * homePlane.upOrDownSpeed;
//                            Collections.sort(yVlaueList);
//                            if(yVlaueList.get(yVlaueList.size() - 1) >= v){
//                                isImapctLand = true;
//                            }
//
//                            if(isImapctLand){
//                                double v1 = homePlane.flyHeight + 30 * homePlane.upOrDownSpeed;
//                                Collections.sort(yVlaueList);
//                                if(yVlaueList.get(yVlaueList.size() - 1) >= v1){
//                                    is30SecandImpactLand = true;
//                                }
//                            }
//                            //存在撞地的危险
                            Message msg2 = new Message();
                            if (isImapctLand) {
                                if (is30SecandImpactLand) {
                                    msg.what = 6;
                                } else {
                                    msg2.what = 2;
                                }
                            } else {
                                msg2.what = 3;
                            }
                            handler.sendMessage(msg2);
                        }

                    } else {
                        String[] latAndLon = preference.getLatAndLon();
                        if (!TextUtils.isEmpty(latAndLon[0]) && !TextUtils.isEmpty(latAndLon[1]) && !TextUtils.isEmpty(preference.getAngle())) {
                            for (int x = 0; x < piece; x++) {
                                //拿到当前的实时的经纬度
                                xVlaueList.add(onepiece * x);
                                double[] doubles = nav.PosVd11(Double.parseDouble(DecimalUtil.remainDecimal(Double.parseDouble(latAndLon[0]), 3)), Double.parseDouble(DecimalUtil.remainDecimal(Double.parseDouble(latAndLon[1]), 3)), 360 - Double.parseDouble(preference.getAngle()), onepiece * (x + 1) * 1000);
                                float height = ReadGaocengFileUtil.getHeight(doubles[0], doubles[1]);
//                                float height = ReadGaocengFileUtil.getHeight(36.06, 108.7);
                                yVlaueList.add(height);
                            }
                            Message msg = new Message();
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("xValueList", xVlaueList);
                            bundle.putSerializable("yValueList", yVlaueList);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }.start();

    }


    public void showPopWindow(View view) {
        if (!isShowing()) {
//            initView();
//            readGc();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            showAtLocation(view, Gravity.NO_GRAVITY, 65, MyApplication.getMyApplication().getHeight() - MyApplication.getMyApplication().getHeight() / 21 - this.getHeight());
        }

    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
            timer.cancel();
        }
    }

}

