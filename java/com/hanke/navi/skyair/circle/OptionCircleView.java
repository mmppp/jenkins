package com.hanke.navi.skyair.circle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.scale.BaseScaleView;

public class OptionCircleView extends ImageView {

    public static OptionCircleView instance = null;
    private Paint paint;
    int colorCircle;      // 圆圈颜色

    public OptionCircleView(Context context) {
        this(context, null);
    }

    public OptionCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = OptionCircleView.this;
        this.paint = new Paint();
        colorCircle = Color.GREEN;// 默认颜色
    }

    public void setColorCircle(int c) {
        this.colorCircle = c;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setDither(true);
        this.paint.setColor(Color.TRANSPARENT);
        this.paint.setStrokeWidth(BaseScaleView.width_view / 35);

        Paint paint_nei = new Paint();
        paint_nei.setAntiAlias(true);
        paint_nei.setStyle(Paint.Style.FILL);
        paint_nei.setColor(colorCircle);
        paint_nei.setDither(true);

        canvas.drawCircle(MyApplication.getMyApplication().getWidth() / 24, MyApplication.getMyApplication().getHeight() / 5,
                MyApplication.getMyApplication().getWidth() / 35, this.paint);// 画外圆圈
        canvas.drawCircle(MyApplication.getMyApplication().getWidth() / 24, MyApplication.getMyApplication().getHeight() / 5,
                MyApplication.getMyApplication().getWidth() / 36 , paint_nei);// 画内圆圈

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置View的宽高
        setMeasuredDimension(MyApplication.getMyApplication().getWidth(), MyApplication.getMyApplication().getWidth());
        //获取自定义View的宽高
        int width_circle = this.getMeasuredWidth();//自定义View的宽度
        int height_circle = this.getMeasuredHeight();//自定义View的高度
        Log.e("circle", "width_circle = " + width_circle + ", height_circle = " + height_circle);
        MyApplication.getMyApplication().setMargins(this, MyApplication.getMyApplication().getWidth() / 40, -MyApplication.getMyApplication().getWidth()/8, 0, 0);
    }
}
