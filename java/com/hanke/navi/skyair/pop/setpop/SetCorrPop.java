package com.hanke.navi.skyair.pop.setpop;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.Timer;

public class SetCorrPop extends PopupWindow implements View.OnClickListener {

    private static final String TAG = "SetCorrPop";
    private Context context;
    private TextView set_gaojing,set_jichang,set_tongxin,set_lookback;
    public int popupHeight, popupWidth;
    private Timer lookBackTextTimer;

    public SetCorrPop(Context context) {
        this(context, null);
    }

    public SetCorrPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetCorrPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        MainActivity.instence.listPop.add(this);
        initView();
//        initData();
    }

    public void initView() {
        View view = View.inflate(context, R.layout.bt_set, null);
        set_gaojing = (TextView) view.findViewById(R.id.set_gaojing);
        set_gaojing.setOnClickListener(this);
        set_jichang = (TextView) view.findViewById(R.id.set_jichang);
        set_jichang.setOnClickListener(this);
        set_tongxin = (TextView) view.findViewById(R.id.set_tongxin);
        set_tongxin.setOnClickListener(this);
        set_lookback = (TextView) view.findViewById(R.id.set_lookback);
        set_lookback.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();
    }

    public void initData() {

    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            Log.e("位置0" + TAG, "location[0]=" + location[0]);
            Log.e("位置1" + TAG, "location[1]=" + location[1]);
            showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - popupWidth / 2, location[1] - popupHeight - (BaseActivity.instance.height_include - view.getHeight()) / 2);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_gaojing:
                GaoJingPop gaoJingPop = new GaoJingPop(context);
                gaoJingPop.showPopWindow(BaseActivity.instance.mapView);
                break;
            case R.id.set_jichang:
                JiChangPop jiChangPop=new JiChangPop(context);
                jiChangPop.showPopWindow(BaseActivity.instance.mapView);
                break;
            case R.id.set_tongxin:
                TongXinPop tongXinPop = new TongXinPop(context);
                tongXinPop.showPopWindow(BaseActivity.instance.mapView);
                break;
            case R.id.set_lookback:
                //启动闪动正在回放文字的定时器
                //这里点击了回放按钮
                MyApplication.getMyApplication().isClickLookBak = true;
                MainActivity.instence.ll_lookback.setVisibility(View.VISIBLE);
                MainActivity.instence.lookback_set_ll.setVisibility(View.VISIBLE);
                MainActivity.instence.lookback_seekbar.setVisibility(View.VISIBLE);
                MainActivity.instence.look_back_text_choose_ll.setVisibility(View.VISIBLE);
                dismissPopWindow();
                break;
        }
    }
}

