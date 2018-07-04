package com.hanke.navi.skyair.pop.jcpop;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.animatorPath.AnimatorPath;
import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.GaojingPreference;


public class JinjinPop extends PopupWindow {

    private Context context;
    RelativeLayout lin;
    private ImageView fab;
    private TextView jinjin_distance;
    AnimatorPath path;
    int width_bitmap, height_bitmap;
    private Nav nav;
    private PlaneInfoBean homePlane;
    private AirportSetBean airportSet;
    int deviationX = 0;//x方向上的偏移
    int deviationY = 0;//y方向上的偏移
    private boolean temp;
    private double distanceWithAirport;
    private double planeWithDian;
    private LatLng airportLatlng;
    private double dianPosition;
    private TextView left_and_right_deviation;

    public JinjinPop(Context context, double distance) {
        this(context, null);
        this.distanceWithAirport = distance;
    }

    public JinjinPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JinjinPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        MainActivity.instence.listPop.add(this);
    }

    public void initView() {
        View view = View.inflate(context, R.layout.jinjin, null);
        GaojingPreference preference = new GaojingPreference(context);

        this.setContentView(view);
        lin = (RelativeLayout) view.findViewById(R.id.lin);
        left_and_right_deviation = (TextView) view.findViewById(R.id.left_and_right_deviation);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(24 * MyApplication.getMyApplication().getHeight() / 35);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        handler.sendEmptyMessage(1);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = DecimalUtil.remainDecimal(MyApplication.getMyApplication().planeWithNormalLeftAndRightDValue, 2);
            left_and_right_deviation.setText("偏离航线: " + s);

            sendEmptyMessageDelayed(1, 1000);
        }
    };


    public void showPopWindow(View view) {
        if (!isShowing()) {
            showAtLocation(view, Gravity.TOP, 0, 0);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }
}