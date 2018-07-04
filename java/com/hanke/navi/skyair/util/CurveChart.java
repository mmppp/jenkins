package com.hanke.navi.skyair.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.db.GaocengDataDBHelper;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mahao on 2017/11/2.
 */

public class CurveChart extends View {

    private int mWidth;
    private int mHeight;
    private PaintFlagsDrawFilter drawFilter;
    private Paint paint;
    private Paint textPaint;
    private Paint dashPaint;
    private boolean isFillDownLineColor;
    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;
    private ArrayList<Float> xValues_List;
    private ArrayList<Float> yValues_List;
    private ArrayList<Float> yValues_List_Zuobiaozhou;
    private float compareValue;
    private int scaleDistance;
    private int scaleLen;
    private float perLengthX;
    private float perLengthY;
    private Paint linePaint;
    private boolean stop;
    private Bitmap plane;
    private Nav nav;
    private GaocengDataDBHelper dbHelper;
    private Paint dashedPaint;
    private float piece = 256;
    //纵坐标的数字的分隔
    private int yPiece = 50;
    //画坐标轴刻度延伸出来的网格
    private Paint gridPaint;


    public CurveChart(Context context) {
        super(context);
        init(context);
    }

    public CurveChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CurveChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth() - 15;
        mHeight = getHeight() - 10;
        int widthSpectMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpectSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpectMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpectSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpectMode == MeasureSpec.AT_MOST
                && heightSpectMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpectMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpectSize);
        } else if (heightSpectMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpectSize, mHeight);
        }
    }


    private void init(Context context) {
        //在画布上去除锯齿
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        textPaint = new Paint();
        dashPaint = new Paint();
        gridPaint = new Paint();
        PathEffect pe = new DashPathEffect(new float[]{10, 10}, 1);
        // 要设置不是填充的，不然画一条虚线是没显示出来的
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(4);
        gridPaint.setPathEffect(pe);
        gridPaint.setColor(Color.WHITE);

        //用来画红色的虚线,表示飞机的预测高度变化线
        dashedPaint = new Paint();
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setPathEffect(pe);
        dashedPaint.setColor(Color.RED);
        scaleDistance = dip2px(context, scaleDistance);
        scaleLen = dip2px(context, scaleLen);
        //画飞机
        plane = BitmapFactory.decodeResource(context.getResources(), R.mipmap.plane);
        isFillDownLineColor = true;
        xStart = 0;
        yStart = 0;
        xEnd = 15;
        yEnd = 4000;
        //这里需要初始的值,这里使用当前的经纬度,15000的距离,然后计算出256个点
        xValues_List = new ArrayList<>();
        yValues_List = new ArrayList<>();
        yValues_List_Zuobiaozhou = new ArrayList<>();
        nav = new Nav(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(drawFilter);
        drawCoordinate(canvas);
        drawPoint(canvas, xValues_List, yValues_List);
        drawPlane(canvas);
    }


    //这里需要考虑一下飞机外面的边框
    private void drawPlane(Canvas canvas) {
        PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;

        if (!MainActivity.instence.beidou_state.getText().toString().contains("未连接")) {
            float starty = (float) (MyApplication.getMyApplication().getHeight() / 4 + ((MyApplication.getMyApplication().getHeight() / 180)) - (homePlane.flyHeight - yStart) * perLengthY);
            double v1 = homePlane.flyHeight + homePlane.upOrDownSpeed * 60 - yStart;
            float endy = (float) (MyApplication.getMyApplication().getHeight() / 4 + ((MyApplication.getMyApplication().getHeight() / 180)) - v1 * perLengthY);
            float v = (float) ((homePlane.flySpeed / 3.6) * 60 / 1000);
            float endx = (v - xStart) * perLengthX + 33;
            canvas.drawLine(36, starty, endx, endy, dashedPaint);

            float flyHeight = (float) homePlane.flyHeight;
            Log.i("padHeight", MyApplication.getMyApplication().getHeight() + "");
            float add = plane.getHeight() / 2;
            canvas.drawBitmap(plane, 10, starty - add, paint);


        }
    }

    public void drawPoint(Canvas canvas, ArrayList<Float> xValues_List, ArrayList<Float> yValues_List) {
        if (xValues_List.size() == piece && yValues_List.size() == piece) {
            //清空之前的值
            //每次在drawpoint之前.首先计算出来要画的256个点,并刷新yvalues.因为xvalues是不变的,所以就不用再刷新了.这样计算出新的yvalues之后,就可以重新显示值了
            //现在的问题就是高度的最大值,因为这个坐标系比较的死,如果设置的过高,显示效果就会比较的差.
            //还需要画线
            yValues_List_Zuobiaozhou.clear();
            PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
            //计算出来了之后,将y值坐标排序,拿到第一个和最后一个,然后重新改变y的start和end
            yValues_List_Zuobiaozhou.addAll(yValues_List);
            yValues_List_Zuobiaozhou.add((float) homePlane.flyHeight);
            Collections.sort(yValues_List_Zuobiaozhou);
            float max = yValues_List_Zuobiaozhou.get(yValues_List_Zuobiaozhou.size() - 1);
            float min = yValues_List_Zuobiaozhou.get(0);

//            yEnd = (int) (max + 50);
            yEnd = (((int) (max / 1000)) + 1) * 1000;
            yStart = ((int) (min / 1000)) * 1000;
//            if (yEnd - yStart <= 100) {
//                yPiece = 10;
//            } else if (yEnd - yStart > 100 && yEnd - yStart <= 500) {
//                yPiece = 50;
//            } else if (yEnd - yStart > 500) {
//                yPiece = 100;
//            }
            yPiece = (yEnd - yStart) / 8;
//            else {
//                yPiece = 200;
//            }
            if (yStart <= 0) {
                yStart = 0;
            }

            Log.i("maxmin", "max:" + max + "min" + min);
            Rect rect = new Rect();
            textPaint.getTextBounds("300", 0, 3, rect);
            int startX = getPaddingLeft() + rect.width() + scaleDistance + 15;
            Log.i("hahaha", "startX:" + startX);
            int startY = mHeight - rect.height() - getPaddingBottom()
                    - scaleDistance;

            linePaint = new Paint();
            linePaint.setColor(Color.GREEN);
            // 把拐点设置成圆的形式，参数为圆的半径，这样就可以画出曲线了
            PathEffect pe = new CornerPathEffect(45);
            // linePaint.setPathEffect(pe);
            if (!isFillDownLineColor) {
                linePaint.setStyle(Paint.Style.STROKE);
            }
            Path path = new Path();
            Path path2 = new Path();
            path.moveTo(startX + (xValues_List.get(0) - xStart) * perLengthX, startY
                    - (yValues_List.get(0) - yStart) * perLengthY);
            int count = xValues_List.size();
            for (int i = 0; i < count - 1; i++) {
                float x, y, x2, y2, x3, y3, x4, y4;
                x = startX + (xValues_List.get(i) - xStart) * perLengthX;
                x4 = (startX + (xValues_List.get(i + 1) - xStart) * perLengthX);
                x2 = x3 = (x + x4) / 2;
                // 乘以这个fraction是为了添加动画特效
                y = startY - (yValues_List.get(i) - yStart) * perLengthY;
                if (i == 0) {
                    Log.i("yyyyyy", "y" + y);
                }
                Log.i("yValues", "ysize:" + yValues_List.size() + "...i:" + i);
                y4 = startY - (yValues_List.get(i + 1) - yStart) * perLengthY;
                y2 = y;
                y3 = y4;
//            if (yValues[i] > compareValue) {
//                storageX[i] = x;
//                storageY[i] = y;
//            }
                if (!isFillDownLineColor && i == 0) {
                    path2.moveTo(x, y);
                    path.moveTo(x, y);
                    continue;
                }

                // 填充颜色
                if (isFillDownLineColor && i == 0) {
                    // 形成封闭的图形
                    path2.moveTo(x, y);
                    path.moveTo(x, startY);
                    path.lineTo(x, y);
                }
                // // 填充颜色
                // if (isFillDownLineColor && i == count - 1) {
                // path.lineTo(x, startY);
                // }
                path.cubicTo(x2, y2, x3, y3, x4, y4);
                path2.cubicTo(x2, y2, x3, y3, x4, y4);
            }
            if (isFillDownLineColor) {
                // 形成封闭的图形
                path.lineTo(startX + (xValues_List.get(count - 1) - xStart) * perLengthX, startY);
            }
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.BLUE);
            float left = startX + (xValues_List.get(0) - xStart) * perLengthX;
            float top = getPaddingTop();
            float right = startX + (xValues_List.get(count - 1) - xStart) * perLengthX;
            float bottom = startY;
            // 渐变的颜色
            LinearGradient lg = new LinearGradient(left, top, left, bottom, Color.parseColor("#00ffffff"), Color.parseColor("#bFffffff"), Shader.TileMode.CLAMP);// CLAMP重复最后一个颜色至最后
            rectPaint.setShader(lg);
            rectPaint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.SRC_ATOP));
            if (isFillDownLineColor) {
                canvas.drawPath(path, linePaint);
            }
            canvas.drawRect(left, top, right, bottom, rectPaint);
            // canvas.restoreToCount(layerId);
            rectPaint.setXfermode(null);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(Color.GRAY);
            canvas.drawPath(path2, linePaint);
            linePaint.setPathEffect(null);

//            drawDashAndPoint(xValues_List, yValues_List, startY, canvas);

        }

    }

    private void drawDashAndPoint(float[] x, float[] y, float startY,
                                  Canvas canvas) {
        PathEffect pe = new DashPathEffect(new float[]{10, 10}, 1);
        // 要设置不是填充的，不然画一条虚线是没显示出来的
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setPathEffect(pe);
        dashPaint.setColor(Color.GRAY);
        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.GRAY);
        for (int i = 0; i < x.length; i++) {
            if (y[i] > 1) {
                canvas.drawCircle(x[i], y[i], 2, pointPaint);
                Path path = new Path();
                path.moveTo(x[i], startY);
                path.lineTo(x[i], y[i]);
                canvas.drawPath(path, dashPaint);
            }
        }
    }

    /**
     * 画坐标系
     *
     * @param canvas
     */
    private void drawCoordinate(Canvas canvas) {
        Rect rect = new Rect();
        textPaint.getTextBounds("300", 0, 3, rect);
        textPaint.setTextSize(14);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.WHITE);
        // 所画的坐标系的原点位置
        int startX = getPaddingLeft() + rect.width() + scaleDistance + 15;
        Log.i("startXstartX", startX + "");
        int startY = mHeight - rect.height() - getPaddingBottom() - scaleDistance;
        // X轴的长度
        int lengthX = mWidth - getPaddingRight() - startX;
        // Y轴的长度
        int lengthY = startY - getPaddingTop() - 20;
        float countX, countY;
        countX = xEnd - xStart;
        countY = yEnd - yStart;
        // x轴每个刻度的长度
        perLengthX = 1.0f * lengthX / countX;
        // y轴每个刻度的长度
        perLengthY = 1.0f * lengthY / countY;
        // 画横坐标
        canvas.drawLine(startX, startY, mWidth, startY, paint);
        // 画纵坐标
        canvas.drawLine(startX, startY, startX, getPaddingTop(), paint);
        // 画x轴的刻度

        for (int i = 0; i <= countX; i++) {
            if (i == 0) {
                // 画原点的数字
                canvas.drawText("" + (int) xStart, startX, mHeight - getPaddingBottom() + 5, textPaint);
                continue;
            }
            float x = startX + i * perLengthX;
            float y1 = startY - scaleLen;
            float y2 = startY - 2 * scaleLen;
//            if (i % 3 == 0) {
            // 加长一点
            canvas.drawLine(x, startY, x, startY - countY * perLengthY + rect.height() / 2, gridPaint);
            // 画下面的数字
            canvas.drawText("" + (int) (xStart + i), x - rect.width() / 2, mHeight - getPaddingBottom() + 5, textPaint);
//            } else {
//                canvas.drawLine(x, startY, x, y1, paint);
//            }
        }
        // 画y轴的刻度
        for (int i = 0; i <= countY; i++) {
            if (i == 0) {
                canvas.drawText("" + (int) yStart, getPaddingLeft(), startY, textPaint);
                continue;
            }
            float y = startY - i * perLengthY;
            float x1 = startX + scaleLen;
            float x2 = startX + 2 * scaleLen;
            float y2 = startY - 2 * scaleLen;
            //这里换成动态的.
            if (i % yPiece == 0) {
                // 加长一点
                canvas.drawLine(startX, y, mWidth, y, gridPaint);
                canvas.drawText("" + (int) (yStart + i), getPaddingLeft(), y + rect.height() / 2, textPaint);
            } else {
                canvas.drawLine(startX, y, x1, y, paint);
            }
        }
        postInvalidate();
    }

    public void setxStart(int xStart, int xEnd) {
        this.xStart = xStart;
        this.xEnd = xEnd;
    }

    public void setyStart(int yStart, int yEnd) {
        this.yStart = yStart;
        this.yEnd = yEnd;
    }

    public void setFillDownLineColor(boolean fillDownLineColor) {
        this.fillDownLineColor = fillDownLineColor;
    }

    private boolean fillDownLineColor;

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    private int fillColor;

    public void setCompareValue(float compareValue) {
        this.compareValue = compareValue;
    }

    public void setxValues(ArrayList<Float> xValues) {
        if (this.xValues_List != null) {
            xValues_List.clear();
        }
        this.xValues_List = xValues;
    }

    public void setyValues(ArrayList<Float> yValues) {
        if (yValues_List != null) {
            yValues_List.clear();
        }
        this.yValues_List = yValues;
    }

    public void show() {

    }
}
