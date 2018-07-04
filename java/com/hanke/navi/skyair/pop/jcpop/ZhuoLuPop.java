package com.hanke.navi.skyair.pop.jcpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.ui.view.CurveLine;
import com.hanke.navi.skyair.ui.view.CurvePoint;
import com.hanke.navi.skyair.ui.view.CurvesView;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 当这个pop弹出的时候,说明已经判断为距离机场在一定的范围内了.
 */
public class ZhuoLuPop extends PopupWindow {

    private Context context;
    private GraphicalView graphicalView;
    public int popupHeight, popupWidth;
    private ZLChartService zlChartService;
    private Timer timer, timer2, timer3, timer4;
    private List<Double> listhb, listjd, listwd, list3;
    private double t = 0, m = 0, n = 0;
    private RelativeLayout rel;
    public TextView zhuolu_pianyi;
    private ImageView plane;
    private double flyHeight;
    public static ZhuoLuPop instance;
    private TextView textView;

    public static ZhuoLuPop getInstance() {
        return instance;
    }

    public ZhuoLuPop(Context context, double flyHeight) {
        this(context, null);
        Log.i("hahaha", "flyHeight参数" + flyHeight);
        this.flyHeight = flyHeight;
        Log.i("hahaha", "flyHeight参数111" + this.flyHeight);
        initView();
    }

    public ZhuoLuPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZhuoLuPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        MainActivity.instence.listPop.add(this);
        this.context = context;
        listhb = new ArrayList<>();
        listwd = new ArrayList<>();
        listjd = new ArrayList<>();
        list3 = new ArrayList<>();
    }

    public boolean flag = true;
    int len;

    public void initView() {

//        String[] xLableVal = new String[]{"","10","9","8","7","6","5","4","3","2","1","0",""};
        View view = View.inflate(context, R.layout.zhuolu, null);
        rel = (RelativeLayout) view.findViewById(R.id.rel);
//        zhuolu_pianyi = (TextView) view.findViewById(R.id.zhuolu_pianyi);
        textView = new TextView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        textView.setText("着陆偏差:130米");
        textView.setTextColor(Color.WHITE);
        textView.setLayoutParams(layoutParams);

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(5 * MyApplication.getMyApplication().getHeight() / 18 - MyApplication.getMyApplication().getHeight() / 105);

        GaojingPreference pre = new GaojingPreference(context);
        AirportSetBean airportSet = pre.getAirportSet();
        int fafDistance = Integer.valueOf(airportSet.fafDistance);

        CurvesView curvesView = new CurvesView(context);
        curvesView.setBackgroundColor(Color.parseColor("#000000"));
        int i = (int) (fafDistance / 200) + 1;
        curvesView.setBottom(i);
        curvesView.setLeft(6);
        curvesView.setMinX(0);
        curvesView.setMinY(0);
        curvesView.setMaxX(Float.parseFloat(airportSet.fafDistance));
        //这里设置最大值.使用飞行的高度.
//        curvesView.setMaxY(1600);
        //这里需要参考的是飞机的faf距离.
        String[] arr = new String[i];
        int index = 0;
        for (int x = fafDistance; x >= 0; x -= 200) {
            arr[index] = x + "";
            index++;
        }
        curvesView.setBottomLablels(arr);
        //纵坐标数组的第一个不显示.

        //y轴的最大值.是tan角度 * faf距离
        Log.i("hahaha", "tantantan = " + Math.tan(Double.parseDouble(airportSet.planeDownAngle) * Math.PI / 180));
        int yMax = (int) (Math.tan(Double.parseDouble(airportSet.planeDownAngle) * Math.PI / 180) * Double.parseDouble(airportSet.fafDistance));
        curvesView.setMaxY(yMax);
        Log.i("hahaha", "flyHeight" + flyHeight);
        int height = (int) ((yMax) / 100) + 2;
        float[] leftLables = new float[height];
        int indexY = 0;
        for (int x = 0; x <= yMax + 100; x += 100) {
            leftLables[indexY] = x;
            indexY++;
        }
        curvesView.setLeftLevels(leftLables);

//        curvesView.setLeftLevels(new float[]{0,2500,2000, 1500, 1000, 500,0});
        CurveLine curveLine = new CurveLine();
        CurvePoint point1 = new CurvePoint(0, yMax);
        CurvePoint point2 = new CurvePoint(Float.parseFloat(airportSet.fafDistance), 0);
        curveLine.addPoint(point1);
        curveLine.addPoint(point2);
//        curvesView.setLineToFill(0);
        curvesView.addLine(curveLine);

        rel.addView(curvesView);
        rel.addView(textView);
//        rel.addView(plane);
        this.setContentView(rel);
        //屏蔽触摸
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        handler.sendEmptyMessageDelayed(1, 0);

    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText("着陆偏差: " + DecimalUtil.remainDecimal(MyApplication.getMyApplication().planeWithNormalHeightDValue, 2) + "米");
            sendEmptyMessageDelayed(1, 1000);
        }
    };

    public void showPopWindow(View view) {
        if (!isShowing()) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            showAtLocation(view, Gravity.NO_GRAVITY, 0, MyApplication.getMyApplication().getHeight() - MyApplication.getMyApplication().getHeight() / 21 - this.getHeight());
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
        timer.cancel();
    }

}

