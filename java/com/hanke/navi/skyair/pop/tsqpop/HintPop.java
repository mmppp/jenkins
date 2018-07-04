package com.hanke.navi.skyair.pop.tsqpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.GaojingSetBean;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.ConcurrentModificationException;

public class HintPop extends PopupWindow implements View.OnClickListener {


    private static final String TAG = "HintPop";
    private Context context;
    public TextView tv_warning, tv_attention;
    public int radius_jg = 6000, radius_zy = 12000;
    public int popupHeight, popupWidth;
    public boolean clientTaskDraw;
    private GaojingPreference preference;

    public HintPop(Context context) {
        this(context, null);
    }

    public HintPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HintPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.instence != null) {
                    MainActivity.instence.listPop.add(HintPop.this);
                }
            }
        }, 10);
        initView();
        initData();
    }

    public void initView() {
        View view = View.inflate(context, R.layout.bt_tishiq, null);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        tv_warning = (TextView) view.findViewById(R.id.tv_warning);
        tv_attention = (TextView) view.findViewById(R.id.tv_attention);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();

        preference = new GaojingPreference(context);
        GaojingSetBean gaoJingSetInfo = preference.getGaoJingSetInfo();
        if (!TextUtils.isEmpty(gaoJingSetInfo.caz_distance)) {
            radius_jg = Integer.parseInt(gaoJingSetInfo.caz_distance);
        }
        if (!TextUtils.isEmpty(gaoJingSetInfo.paz_distance)) {
            radius_zy = Integer.parseInt(gaoJingSetInfo.paz_distance);
        }

    }

    public void initData() {
        tv_warning.setOnClickListener(this);
        tv_attention.setOnClickListener(this);

        if (MainActivity.instence != null) {
            if (MainActivity.instence.listCirclejg.size() == 0) {
                tv_warning.setText("开启警告");
                flag_jg = false;
            } else {
                tv_warning.setText("关闭警告");
                flag_jg = true;
            }

            if (MainActivity.instence.listCirclezy.size() == 0) {
                tv_attention.setText("开启注意");
                flag_zy = false;
            } else {
                tv_attention.setText("关闭注意");
                flag_zy = true;
            }
        }

    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - popupWidth / 2, location[1] - popupHeight - (BaseActivity.instance.height_include - view.getHeight()) / 2);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    public boolean flag_jg;
    public boolean flag_zy;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_warning://开启警告提示圈
                if (!flag_jg) {
                    //这里，如果没有联网的话，因为得到的经纬度是从clienttask里面拿刀的，所以是空的，然后就无法画出来圈。
                    //如果没有联网的话,就从prefrence里面拿到
                    if (MainActivity.instence.beidou_state.getText().toString().contains("未连接")) {
                        String[] latAndLon = preference.getLatAndLon();
                        addCirclejg(new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1])), radius_jg, Color.RED);
                    } else {
                        addCirclejg(new LatLng(MyApplication.getMyApplication().clientTask.getWd(), MyApplication.getMyApplication().clientTask.getJd()), radius_jg, Color.RED);
                    }
                    tv_warning.setText("关闭警告");
                    flag_jg = !flag_jg;
                } else {
                    if (MainActivity.instence.listCirclejg.size() == 0)
                        new Throwable().printStackTrace();
                    else {
                        try {
                            for (int i = 0; i < MainActivity.instence.listCirclejg.size(); i++) {
                                MainActivity.instence.listCirclejg.get(i).remove();
                            }
                            MainActivity.instence.listCirclejg.clear();
                            tv_warning.setText("开启警告");
                            flag_jg = false;
                            MyApplication.getMyApplication().circleJGDraw = false;
                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.tv_attention://开启注意提示圈
                if (!flag_zy) {
                    if (MainActivity.instence.beidou_state.getText().toString().contains("未连接")) {
                        String[] latAndLon = preference.getLatAndLon();
                        addCirclezy(new LatLng(Double.parseDouble(latAndLon[0]), Double.parseDouble(latAndLon[1])), radius_zy, Color.parseColor("#FFE1C300"));
                    } else {
                        addCirclezy(new LatLng(MyApplication.getMyApplication().clientTask.getWd(), MyApplication.getMyApplication().clientTask.getJd()), radius_zy, Color.parseColor("#FFE1C300"));
                    }
                    tv_attention.setText("关闭注意");
                    flag_zy = !flag_zy;
                } else {
                    if (MainActivity.instence.listCirclezy.size() == 0)
                        new Throwable().printStackTrace();
                    else {
                        try {
                            for (int i = 0; i < MainActivity.instence.listCirclezy.size(); i++) {
                                MainActivity.instence.listCirclezy.get(i).remove();
                            }
                            MainActivity.instence.listCirclezy.clear();
                            tv_attention.setText("开启注意");
                            flag_zy = false;
                            MyApplication.getMyApplication().circleZYDraw = false;
                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    //    public Circle circlejg;
    public Circle addCirclejg(LatLng latLng, int radius, int color) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(6);
        circleOptions.strokeColor(color);
        if (circleOptions != null)
            MyApplication.getMyApplication().circlejg = BaseActivity.instance.aMap.addCircle(circleOptions);
        MainActivity.instence.listCirclejg.add(MyApplication.getMyApplication().circlejg);
        return MyApplication.getMyApplication().circlejg;
    }

    //    public Circle circlezy;
    public Circle addCirclezy(LatLng latLng, int radius, int color) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(6);
        circleOptions.strokeColor(color);
        if (circleOptions != null)
            MyApplication.getMyApplication().circlezy = BaseActivity.instance.aMap.addCircle(circleOptions);
        MainActivity.instence.listCirclezy.add(MyApplication.getMyApplication().circlezy);
        return MyApplication.getMyApplication().circlezy;
    }

}