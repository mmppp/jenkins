package com.hanke.navi.skyair.pop.tcpop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Polyline;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.service.AirSpaceWarningService;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.ConcurrentModificationException;
import java.util.List;


public class LayerPop extends PopupWindow implements View.OnClickListener {


    public static LayerPop instance = null;
    private static final String TAG = "LayerPop";
    private Context context;
    public TextView tv_map_putong, tv_map_weixing, tv_map_yejing, tv_show_notice, tv_air_space;
    private TextView[] textViews;
    private int popupHeight, popupWidth;

    public LayerPop(Context context) {
        this(context, null);
    }

    public LayerPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayerPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        instance = LayerPop.this;
        beforeInitView();
        initView();
        initData();
    }

    public void beforeInitView() {
    }

    public void initView() {
        View view = View.inflate(context, R.layout.bt_tuceng, null);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        int mapType = MainActivity.instence.aMap.getMapType();

        tv_map_putong = (TextView) view.findViewById(R.id.tv_map_putong);
        tv_map_weixing = (TextView) view.findViewById(R.id.tv_map_weixing);
        tv_map_yejing = (TextView) view.findViewById(R.id.tv_map_yejing);
        tv_show_notice = (TextView) view.findViewById(R.id.tv_show_notice);
        tv_air_space = (TextView) view.findViewById(R.id.tv_air_space);

        if(mapType == AMap.MAP_TYPE_NORMAL){
            tv_map_putong.setTextColor(Color.GREEN);
            tv_map_weixing.setTextColor(Color.WHITE);
            tv_map_yejing.setTextColor(Color.WHITE);
        }else if(mapType == AMap.MAP_TYPE_SATELLITE){
            tv_map_putong.setTextColor(Color.WHITE);
            tv_map_weixing.setTextColor(Color.GREEN);
            tv_map_yejing.setTextColor(Color.WHITE);
        }else if(mapType == AMap.MAP_TYPE_NIGHT){
            tv_map_putong.setTextColor(Color.WHITE);
            tv_map_weixing.setTextColor(Color.WHITE);
            tv_map_yejing.setTextColor(Color.GREEN);
        }

        //获取自身的宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();

        if (MainActivity.instence.listPolygon.size() == 0) {
            tv_air_space.setText("显示空域");
            flag = false;
        } else {
            tv_air_space.setText("关闭空域");
            flag = true;
        }
    }

    public void initData() {
        textViews = new TextView[]{tv_map_putong, tv_map_weixing, tv_map_yejing};
        tv_map_putong.setOnClickListener(this);
        tv_map_weixing.setOnClickListener(this);
        tv_map_yejing.setOnClickListener(this);
        tv_show_notice.setOnClickListener(this);
        tv_air_space.setOnClickListener(this);

    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            Log.e("位置0" + TAG, "location[0]=" + location[0]);
            Log.e("位置1" + TAG, "location[1]=" + location[1]);
            showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth() / 2 - popupWidth / 2, location[1] - popupHeight - (BaseActivity.instance.height_include - view.getHeight()) / 2);
            MainActivity.instence.listPop.add(this);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    public void setSelect(int position) {
        for (int i = 0; i < textViews.length; i++) {
            if (i == position) {
                textViews[i].setTextColor(Color.GREEN);
                textViews[i].setEnabled(false);
            } else {
                textViews[i].setTextColor(Color.WHITE);
                textViews[i].setEnabled(true);
            }
        }
    }

    private List<Polyline> list;
    boolean flag;

    @Override
    public void onClick(View v) {
        XinXiPop xinXiPop = new XinXiPop(context);
        switch (v.getId()) {
            case R.id.tv_map_putong:
                setSelect(0);
                BaseActivity.instance.aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                this.dismissPopWindow();
                break;
            case R.id.tv_map_weixing:
                setSelect(1);
                BaseActivity.instance.aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                this.dismissPopWindow();
                break;
            case R.id.tv_map_yejing:
                setSelect(2);
                BaseActivity.instance.aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                this.dismissPopWindow();
                break;
            case R.id.tv_show_notice:
                xinXiPop.showPopWindow(BaseActivity.instance.mapView);
                this.dismissPopWindow();
                break;
            case R.id.tv_air_space:
                //打开空域,这里打开空域预警的service
                if (!flag) {
                    int count = xinXiPop.getCounts();//item总数目
                    for (int i = 0; i < count; i++) {
                        xinXiPop.setSpaceAir(i);
                    }
                    tv_air_space.setText("关闭空域");
                    Intent intent = new Intent(context, AirSpaceWarningService.class);
                    context.startService(intent);
                    flag = !flag;
                } else {
                    //关闭service
                    Intent intent = new Intent(context, AirSpaceWarningService.class);
                    context.stopService(intent);
                    AirSpaceWarningService.timer.cancel();
                    if (MainActivity.instence.listPolygon.size() == 0 || MainActivity.instence.listText.size() == 0)
                        new Throwable().printStackTrace();
                    else {
                        try {
                            for (int i = 0; i < MainActivity.instence.listPolygon.size(); i++) {
                                MainActivity.instence.listPolygon.get(i).remove();
                                MainActivity.instence.listText.get(i).remove();
                            }
                            MainActivity.instence.listPolygon.clear();
                            MainActivity.instence.listText.clear();
                            tv_air_space.setText("显示空域");
                            flag = false;
                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.dismissPopWindow();
                break;
        }

    }

}
