package com.hanke.navi.skyair.scale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class VerticalScaleScrollViewRight extends BaseScaleView {

    public static double scale_h;

    public VerticalScaleScrollViewRight(Context context) {
        this(context, null);
    }

    public VerticalScaleScrollViewRight(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalScaleScrollViewRight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initVar(AttributeSet attrs) {

        TypedArray ta = getContext().obtainStyledAttributes(attrs, ATTR);
        mMin = ta.getInteger(SCALE_MIN, 0);
        mMax = ta.getInteger(SCALE_MAX, 20000);
        mScaleMargin = ta.getDimensionPixelOffset(SCALE_MARGIN, 1);
        mScaleHeight = ta.getDimensionPixelOffset(SCALE_HEIGHT, 20);

        ta.recycle();
        mRectHeight = (mMax - mMin) * mScaleMargin;//总高度
        mRectWidth = mScaleHeight * 8;//总宽度
        mScaleMaxHeight = mScaleHeight * 2;//整刻度线的高度
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MyApplication.getMyApplication().setMargins(VerticalScaleScrollViewRight.this, 0, MyApplication.getMyApplication().getHeight() / 5, width_view / 32, 0);
    }

    @Override
    public void onDrawLine(Canvas canvas, Paint paint) {//画Y轴竖直线
        //第二个参数控制Y轴顶上出头部分。第四个参数控制Y轴底部出头部分
        canvas.drawLine(3 * width_view / 32, -mScaleMargin - height_view / 22, 3 * width_view / 32, (mMax + 1) * mScaleMargin + height_view / 22, paint);
    }

    @Override
    public void onDrawScale(Canvas canvas, Paint linepaint, Paint textpaint) {//画刻度线

        for (int i = 0, k = mMin; i <= mMax - mMin; i++) {
            if (k <= mMax) {
                if (i % 100 == 0) {//整值，第三个参数可以修改线的长短
                    canvas.drawLine(3 * width_view / 32, i * mScaleMargin, 17 * width_view / 32, i * mScaleMargin, linepaint);

                    //整值文字
                    canvas.drawText(String.valueOf(mMax - i), 11 * width_view / 50, i * mScaleMargin - height_view / 120, textpaint);
                    k += 100;
                } else if (i % 50 == 0) {//中刻度线
                    canvas.drawLine(3 * width_view / 32, i * mScaleMargin, 13 * width_view / 32, i * mScaleMargin, linepaint);
                }
            }
        }
    }


    // 指针滑动的刻度位置
    public void setGDmCountScale(double mCountScale) {
        if (mCountScale > mMax)
            mCountScale = mMax;
        if (mCountScale < 0)
            mCountScale = 0;
        //每一屏幕刻度的个数/2，实际上是控制指刻度的指针在哪个位置
        double countScale = mScaleScrollViewRange / mScaleMargin / 2;
        double scale = mMax - (mCountScale + countScale);
        double finalY = scale * mScaleMargin;
        mScroller.setFinalY((int) finalY); //纠正指针位置
    }

    public void shuaRightCursor() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 10, 200);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setGDmCountScale(MyApplication.getMyApplication().clientTask.getHb());//此处传收到的高度值
                    invalidate();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDrawPointer(Canvas canvas, Paint paint_san_wai, Paint paint_san_nei, Paint paint_kuang, Paint paint_nei, Paint middle_textpaint) {//画指针
        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalY = mScroller.getFinalY();
        int tmpCountScale = 0;
        //滑动的刻度
        tmpCountScale = (int) Math.rint((double) finalY / (double) mScaleMargin); //四舍五入取整
        //总刻度  countScale
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mMax - mCountScale);
        }

        // 绘制三角形外部
        if (MainActivity.instence.beidou_state.getText().toString().contains("已定位")) {
            Path path_wai = new Path();
            path_wai.moveTo(3 * width_view / 32 + Yzhou / 2, countScale * mScaleMargin + finalY);// 此点为多边形的起点
            path_wai.lineTo(8 * width_view / 32, countScale * mScaleMargin + finalY - 1 * height_view / 66);
            path_wai.lineTo(8 * width_view / 32, countScale * mScaleMargin + finalY + 1 * height_view / 66);
            path_wai.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path_wai, paint_san_wai);

            // 绘制三角形内部
            Path path_nei = new Path();
            path_nei.moveTo(3 * width_view / 32 + Yzhou / 2 + paint_san_nei.getStrokeWidth(), countScale * mScaleMargin + finalY);// 此点为多边形的起点
            path_nei.lineTo(8 * width_view / 32 - paint_san_nei.getStrokeWidth(), countScale * mScaleMargin + finalY - 1 * height_view / 66 + paint_san_nei.getStrokeWidth());
            path_nei.lineTo(8 * width_view / 32 - paint_san_nei.getStrokeWidth(), countScale * mScaleMargin + finalY + 1 * height_view / 66 - paint_san_nei.getStrokeWidth());
            path_nei.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path_nei, paint_san_nei);

            //画圆角矩形外边框
            RectF oval_kuang = new RectF(5 * width_view / 25,  //左
                    countScale * mScaleMargin + finalY - 1 * height_view / 22, //上
                    width_view - 2 * Yzhou, //右
                    countScale * mScaleMargin + finalY + 1 * height_view / 22);// 设置个新的长方形
            canvas.drawRoundRect(oval_kuang, width_view / 7, width_view / 7, paint_kuang);//第二个参数是x半径，第三个参数是y半径

            //画圆角矩形内部填充色
            RectF oval_nei = new RectF(5 * width_view / 25 + paint_nei.getStrokeWidth(),
                    countScale * mScaleMargin + finalY - 1 * height_view / 22 + paint_nei.getStrokeWidth(),
                    width_view - 2 * Yzhou - paint_nei.getStrokeWidth(),
                    countScale * mScaleMargin + finalY + 1 * height_view / 22 - paint_nei.getStrokeWidth());// 设置个新的长方形
            canvas.drawRoundRect(oval_nei, width_view / 7, width_view / 7, paint_nei);//第二个参数是x半径，第三个参数是y半径

            //方框里的字

            if (mMax - mCountScale >= mMax) {
                canvas.drawText(mMax + "", 15 * width_view / 64, countScale * mScaleMargin + finalY + 1 * height_view / 44, middle_textpaint);
                scale_h = mMax;
            } else if (mMax - mCountScale <= mMin) {
                canvas.drawText(mMin + "", 15 * width_view / 64, countScale * mScaleMargin + finalY + 1 * height_view / 44, middle_textpaint);
                scale_h = mMin;
            }
            canvas.drawText(mMax - mCountScale + "", 15 * width_view / 64, countScale * mScaleMargin + finalY + 1 * height_view / 44, middle_textpaint);
            scale_h = mMax - mCountScale;
        }
    }
}
