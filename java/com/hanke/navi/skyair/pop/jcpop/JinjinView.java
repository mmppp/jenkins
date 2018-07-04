package com.hanke.navi.skyair.pop.jcpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

public class JinjinView extends View {

    private Paint paint;
    private Nav nav;
    private AirportSetBean airportSet;
    float deviationX = 0;//x方向上的偏移
    float deviationY = 0;//y方向上的偏移
    private Bitmap bitmapPlane;
    private LatLng airportLatlng;
    private int yMax;

    public JinjinView(Context context) {
        this(context, null);
    }

    public JinjinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JinjinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(MyApplication.getMyApplication().getWidth() / 280);
        nav = new Nav(MyApplication.getMyApplication());
        GaojingPreference preference = new GaojingPreference(MyApplication.getMyApplication());
        airportSet = preference.getAirportSet();
        airportLatlng = new LatLng(Double.parseDouble(preference.getAirportSet().airportLat), Double.parseDouble(preference.getAirportSet().airportLon));
        yMax = 23 * MyApplication.getMyApplication().getHeight() / 35 - 4 * MyApplication.getMyApplication().getHeight() / 21;
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(MyApplication.getMyApplication().getWidth() / 2, MyApplication.getMyApplication().getHeight() / 21,
                MyApplication.getMyApplication().getWidth() / 2, 23 * MyApplication.getMyApplication().getHeight() / 35, paint);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.biaofen);
        canvas.drawBitmap(bitmap, MyApplication.getMyApplication().getWidth() / 2 - bitmap.getWidth() / 2,
                4 * MyApplication.getMyApplication().getHeight() / 21, paint);


        bitmapPlane = BitmapFactory.decodeResource(getResources(), R.mipmap.zfj);
        PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
        if (homePlane != null && homePlane.latLng != null && !TextUtils.isEmpty(airportSet.airportLat)) {
            double fangweijiao = 360 - nav.CAz(homePlane.latLng.latitude, homePlane.latLng.longitude, homePlane.flyHeight, Double.parseDouble(airportSet.airportLat), Double.parseDouble(airportSet.airportLon), Double.parseDouble(airportSet.airportHeight));
            double runAngle = Double.parseDouble(airportSet.runAngle);
            double distance = DistanceUtil.getInstance().getDistance(new LatLng(homePlane.latLng.latitude, airportLatlng.longitude), new LatLng(Double.parseDouble(airportSet.airportLat), Double.parseDouble(airportSet.airportLon)));
            double result = Math.sin((fangweijiao - runAngle) * Math.PI / 180) * distance;
            result = Double.parseDouble(DecimalUtil.remainDecimal(result, 2));
            MyApplication.getMyApplication().planeWithNormalLeftAndRightDValue = result;
            //求出的正数,飞机右.负数.飞机向左
            //这里需要做一下处理,如果是值非常大的话,那么就应该除以的多一些,如果是值小的话,就应该除以的少一些
            if (result >= 0) {
                if (result > 15000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 40));
                } else if (result <= 15000 && result > 12000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 37));
                } else if (result <= 12000 && result > 10000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 30));
                } else if (result <= 10000 && result > 8000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 25));
                } else if (result <= 8000 && result > 6000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 20));
                } else if (result <= 6000 && result > 4000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 15));
                } else if (result <= 4000 && result > 2000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 10));
                } else if (result <= 2000 && result > 500) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 8));
                } else {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + result);
                }
            } else if (result < 0) {
                if (result < -15000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 40));
                } else if (result >= -15000 && result < -12000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 37));
                } else if (result >= -12000 && result < -10000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 30));
                } else if (result >= -10000 && result < -8000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 25));
                } else if (result >= -8000 && result < -6000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 20));
                } else if (result >= -6000 && result < -4000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 15));
                } else if (result >= -4000 && result < -2000) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 10));
                } else if (result >= -2000 && result < -500) {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + (result / 8));
                } else {
                    deviationX = (float) (MyApplication.getMyApplication().getWidth() / 2 - bitmapPlane.getWidth() / 2 + result);
                }
            }
            //Y的基础值就是绿线的起始点.最大值就是绿线的长度.然后distance和faf的比值,然后乘以最大长度.就是飞机移动的距离
            deviationY = (float) ((yMax * (distance / Integer.parseInt((airportSet.fafDistance)))) + 4 * MyApplication.getMyApplication().getHeight() / 21);
            canvas.drawBitmap(bitmapPlane, deviationX, deviationY, paint);
            postInvalidate();
        }
    }

}
