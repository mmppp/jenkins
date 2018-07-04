package com.hanke.navi.skyair.pop.infowindowpop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;

import java.util.Timer;
import java.util.TimerTask;

public class InfoWinPop extends PopupWindow {

    private static final String TAG = "InfoWinPop";
    private Context context;
    private TextView ifw_hangbanhao, ifw_gaodu, ifw_sudu, ifw_jingdu, ifw_weidu, ifw_shengjiang;
    private int popupHeight, popupWidth;
    private Marker marker;


    public InfoWinPop(Context context, Marker marker) {
        this.context = context;
        this.marker = marker;
    }

    public InfoWinPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoWinPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        timer.schedule(task, 0, 1000);
//        initData();
    }

    public void initView() {
        View view = View.inflate(context, R.layout.infowin, null);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        ifw_hangbanhao = (TextView) view.findViewById(R.id.ifw_hangbanhao);
        ifw_gaodu = (TextView) view.findViewById(R.id.ifw_gaodu);
        ifw_sudu = (TextView) view.findViewById(R.id.ifw_sudu);

        //获取自身的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();

    }

    public void initData() {
        ifw_hangbanhao.setText(marker.getOptions().getTitle());
        ifw_gaodu.setText(marker.getOptions().getZIndex() + " m");
        ifw_sudu.setText(marker.getOptions().getSnippet() + " m/s");

    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = handler.obtainMessage();
            message.what = 0x02;
            handler.sendMessage(message);
        }
    };

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x02:
                    initData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void showPopWindow(View view) {
        initView();
        initData();
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.qfj);
        LatLng latLng = new LatLng(marker.getOptions().getPosition().latitude, marker.getOptions().getPosition().longitude);
        Point point = BaseActivity.instance.aMap.getProjection().toScreenLocation(latLng);//从地图位置转换来的屏幕位置
        showAtLocation(view, Gravity.NO_GRAVITY, point.x - popupWidth, point.y - popupHeight - image.getHeight() / 2);

    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
            timer.cancel();
            timer = null;
        }
    }


}