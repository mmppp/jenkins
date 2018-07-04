package com.hanke.navi.skyair.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.jcpop.ZhuoLuPop;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CurvesView extends View {

    private static final int PADDING_TEXT_SIZE = 10;
    private static final int PARTS_COLOR = 0x22FFFFFF;
    private static final int TEXT_COLOR_GREY = 0xFFFFFFFF;
    private ArrayList<CurveLine> lines = new ArrayList<CurveLine>();
    Paint paint = new Paint();
    private Float setMinX, setMaxX;
    private Float setMinY, setMaxY;
    private int lineToFill = -1;
    private int indexSelected = -1;
    private OnPointClickedListener listener;
    private float minRangeY = 10;
    private Context context;
    private Bitmap plane;
    private AirportSetBean airportSet;
    private Nav nav;

    public CurvesView(Context context) {
        super(context);
        this.context = context;
        //画飞机
        plane = BitmapFactory.decodeResource(context.getResources(), R.mipmap.plane);
        //这个时候需要拿到正在执行的导航的目的地的经纬度信息.
        GaojingPreference preference = new GaojingPreference(context);
        airportSet = preference.getAirportSet();
        nav = new Nav(context);
    }

    public CurvesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMinY(float min) {
        setMinY = min;
    }

    public void setMaxY(float max) {
        setMaxY = max;
    }

    public void setMinX(float min) {
        setMinX = min;
    }

    public void setMaxX(float max) {
        setMaxX = max;
    }

    public void removeAllLines() {
        while (lines.size() > 0) {
            lines.remove(0);
        }
        postInvalidate();
    }

    public void addLine(CurveLine line) {
        lines.add(line);
        postInvalidate();
    }

    public ArrayList<CurveLine> getLines() {
        return lines;
    }

    public void setLineToFill(int indexOfLine) {
        this.lineToFill = indexOfLine;
        postInvalidate();
    }

    public int getLineToFill() {
        return lineToFill;
    }

    public void setLines(ArrayList<CurveLine> lines) {
        this.lines = lines;
        postInvalidate();
    }

    public CurveLine getLine(int index) {
        return lines.get(index);
    }

    public int getSize() {
        return lines.size();
    }

    public float getMinRangeY() {
        return minRangeY;
    }

    public void setMinRangeY(float minRangeY) {
        this.minRangeY = minRangeY;
    }

    private static final int[] Y_LEVELS_NUMERATOR = new int[]{3, 4, 3, 4, 3,
            4, 5};
    private static final int[] Y_LEVELS_DENOMINATOR = new int[]{2, 3, 2, 3,
            2, 3, 4};

    public float getMaxY() {
        if (setMaxY != null) {
            return setMaxY;
        } else {
            if (lines == null || lines.size() == 0) {
                left_levels = null;
                return 1;
            }
            float minX = getMinX();
            float max = lines.get(0).getPoint(0).y;
            for (CurveLine line : lines) {
                for (CurvePoint point : line.getPoints()) {
                    if (point.x >= minX && point.y > max) {
                        max = point.y;
                    }
                }
            }
            max = max - getMinY();
            int j = 0;
            int minRangeInt = 10;
            while (max * 11 / minRangeY > minRangeInt) {
                // minRange = (minRange * Y_LEVELS_NUMERATOR[j])
                // / Y_LEVELS_DENOMINATOR[j];
                minRangeInt = minRangeInt * Y_LEVELS_NUMERATOR[j];
                minRangeInt = minRangeInt / Y_LEVELS_DENOMINATOR[j];
                j++;
                if (j == Y_LEVELS_NUMERATOR.length) {
                    j = 0;
                }
            }
            float minRange = minRangeInt * minRangeY / 10;
            if (((int) (minRangeInt * 10000f)) % 3 == 0) {
                left_levels = new float[]{minRange, minRange * 2 / 3,
                        minRange / 3};
            } else {
                left_levels = new float[]{minRange, minRange * 3 / 4,
                        minRange / 2, minRange / 4};
            }
            return minRange + getMinY();
        }

    }

    public float getMinY() {
        if (setMinY != null) {
            return setMinY;
        } else {
            float min = lines.get(0).getPoint(0).y;
            for (CurveLine line : lines) {
                for (CurvePoint point : line.getPoints()) {
                    if (point.y < min)
                        min = point.y;
                }
            }
            return min;
        }
    }

    public float getMaxX() {
        if (setMaxX != null) {
            return setMaxX;
        } else {
            float max = lines.get(0).getPoint(0).x;
            for (CurveLine line : lines) {
                for (CurvePoint point : line.getPoints()) {
                    if (point.x > max)
                        max = point.x;
                }
            }
            return max;
        }
    }

    public float getMinX() {
        if (setMinX != null) {
            return setMinX;
        } else {
            float min = lines.get(0).getPoint(0).x;
            for (CurveLine line : lines) {
                for (CurvePoint point : line.getPoints()) {
                    if (point.x < min)
                        min = point.x;
                }
            }
            return min;
        }
    }

    int bottomPadding = 120, topPadding = 10;
    int leftPadding = 120, rightPadding = 10;

    private float[] left_levels;
    private static final DecimalFormat NUMBER_FORMATER = new DecimalFormat(
            "#.####");

    static {
        NUMBER_FORMATER.setRoundingMode(RoundingMode.HALF_UP);
    }

    public void setLeftLevels(float[] levels) {
        left_levels = levels;
    }

    /***
     * Draw LeftLabels on the bottom
     */
    private void drawLeftLabels(Canvas canvas) {
        if (left_levels == null) {
            return;
        }
        Rect rect = new Rect();
        int startY = getHeight() - 10 - rect.height() - getPaddingBottom();
        // Y轴的长度
        int lengthY = startY - getPaddingTop() - 20;
        // y轴每个刻度的长度
        float perLengthY = 1.0f * lengthY / left_levels.length;
        Paint labelPaint = new Paint();
        labelPaint.setColor(TEXT_COLOR_GREY);
        labelPaint.setTextSize(dp2sp(PADDING_TEXT_SIZE));
        labelPaint.setAntiAlias(true);

        int high = getHeight() - topPadding - bottomPadding;
        float labelX = leftPadding;
        float part = (float) high / left_levels.length;

        FontMetrics fm = labelPaint.getFontMetrics();
        float labelHigh = (float) (fm.descent - fm.ascent);

        for (int i = 0; i < left_levels.length; i++) {
            String s = NUMBER_FORMATER.format(left_levels[i] + getMinY());
            float centerY = topPadding + part * i;
            float labelWidth = labelPaint.measureText(s);
            float labelY;
//            if (i == 0) {
//                labelY = topPadding;
//            } else if (i == left_levels.length - 1) {
//                labelY = topPadding + labelHigh;
//            } else {
            labelY = centerY + labelHigh / 2 - 30;
//            }
            float y = startY - i * perLengthY - 15;
            if (i >= 0) {
                canvas.drawText(s, labelX - labelWidth - 10, y + rect.height() / 2, labelPaint);
            }
        }

    }

    public void setBottomLablels(String[] bottomLabels) {
        if (bottomLabels == null) {
            return;
        }
        this.bottomLabels = bottomLabels;
        postInvalidate();
    }

    private String[] bottomLabels;

    /***
     * Draw bottomLabels on the bottom
     */
    private void drawBottomLabels(Canvas canvas) {
        if (bottomLabels == null) {
            return;
        }
        Paint labelPaint = new Paint();
        labelPaint.setColor(TEXT_COLOR_GREY);
        labelPaint.setTextSize(dp2sp(PADDING_TEXT_SIZE));
        labelPaint.setAntiAlias(true);

        String str = bottomLabels[bottomLabels.length - 1];
        float lastWidth = labelPaint.measureText(str);
        int width = getWidth() - leftPadding - rightPadding - (int) lastWidth;

        float labelY = getHeight() - bottomPadding / 6f;
        float part = (float) width / (bottomLabels.length - 1);

        for (int i = 0; i < bottomLabels.length; i++) {
            String s = bottomLabels[i];
            float centerX = leftPadding + part * i;
            float labelWidth = labelPaint.measureText(s);
            float labelX;
            if (i == 0) {
                labelX = leftPadding;
            } else if (i == bottomLabels.length - 1) {
                labelX = getWidth() - rightPadding - labelWidth;
            } else {
            }
            labelX = centerX;
            canvas.drawText(s, labelX, labelY, labelPaint);
        }

    }

    public void setTopLablels(String[] topLabels) {
        if (topLabels == null) {
            return;
        }
        this.topLabels = topLabels;
        postInvalidate();
    }

    private String[] topLabels;

    /***
     * Draw bottomLabels on the bottom
     */
    private void drawTopLabels(Canvas canvas) {
        if (topLabels == null) {
            return;
        }
        Paint labelPaint = new Paint();
        labelPaint.setColor(TEXT_COLOR_GREY);
        labelPaint.setTextSize(dp2sp(PADDING_TEXT_SIZE));
        labelPaint.setAntiAlias(true);

        String str = topLabels[topLabels.length - 1];
        float lastWidth = labelPaint.measureText(str);
        int width = getWidth() - leftPadding - rightPadding - (int) lastWidth;

        FontMetrics fm = labelPaint.getFontMetrics();
        float labelY = -fm.ascent + 1f;
        float part = (float) width / (topLabels.length - 1);

        for (int i = 0; i < topLabels.length; i++) {
            String s = topLabels[i];
            float centerX = leftPadding + part * i;
            float labelWidth = labelPaint.measureText(s);
            float labelX;
            if (i == 0) {
                labelX = leftPadding;
            } else if (i == topLabels.length - 1) {
                labelX = getWidth() - rightPadding - labelWidth;
            } else {
            }
            labelX = centerX;
            canvas.drawText(s, labelX, labelY, labelPaint);
        }

    }

    private void resetChartPadding() {
        Paint labelPaint = new Paint();
        labelPaint.setTextSize(dp2sp(PADDING_TEXT_SIZE));
        FontMetrics fm = labelPaint.getFontMetrics();
        float fff = (float) (fm.descent - fm.ascent) + 8;
        if (bottomLabels == null) {
            bottomPadding = 10;
        } else {
            bottomPadding = (int) fff;
        }
        if (topLabels == null) {
            topPadding = 10;
        } else {
            topPadding = (int) fff;
        }
        if (left_levels == null) {
            leftPadding = rightPadding;
        } else {
            float maxLeft = rightPadding;
            for (int i = 0; i < left_levels.length; i++) {
                String s = NUMBER_FORMATER.format(left_levels[i] + getMinY());
                float labelWidth = labelPaint.measureText(s);
                if (maxLeft < labelWidth) {
                    maxLeft = labelWidth;
                }
            }
            leftPadding = (int) (maxLeft + 16);
        }
    }

    public void setStokeWidth(int width) {
        stokeWidth = width;
    }

    private int stokeWidth = 2;

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        float maxY = 0;
        float minY = 0;
        float maxX = 0;
        float minX = 0;
        try {
            maxY = getMaxY();
            minY = getMinY();
            maxX = getMaxX();
            minX = getMinX();
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetChartPadding();
        drawBottomLabels(canvas);
        drawTopLabels(canvas);
        paint.reset();
        Path path = new Path();

        float usableHeight = getHeight() - bottomPadding - topPadding - 50;
        float usableWidth = getWidth() - leftPadding - rightPadding;

        // 画斜线
        if (lineToFill > (-1)) {
            int lineCount = 0;
            for (CurveLine line : lines) {
                int count = 0;
                float minXPixels = getWidth() - rightPadding, maxXPixels = leftPadding;
                float newXPixels = 0, newYPixels = 0;
                float lastXPixels = 0, lastYPixels = 0;

                if (lineCount == lineToFill) {
                    paint.setColor(PARTS_COLOR);
                    paint.setAlpha(30);
                    paint.setStrokeWidth(2);
                    // 在横轴上方画斜线（全部）
                    for (int i = 10; i - getWidth() < getHeight(); i = i + 20) {
                        canvas.drawLine(i, getHeight() - bottomPadding, 0,
                                getHeight() - bottomPadding - i, paint);
                    }

                    // 清除目标线段线上方的斜线
                    paint.reset();
                    paint.setXfermode(new PorterDuffXfermode(
                            android.graphics.PorterDuff.Mode.CLEAR));
                    for (CurvePoint p : line.getPoints()) {
                        float yPercent = (p.y - minY) / (maxY - minY);
                        float xPercent = (p.x - minX) / (maxX - minX);
                        if (count == 0) {
                            lastXPixels = leftPadding
                                    + (xPercent * usableWidth);
                            lastYPixels = getHeight() - bottomPadding
                                    - (usableHeight * yPercent);
                            path.moveTo(lastXPixels, lastYPixels);
                        } else {
                            newXPixels = leftPadding + (xPercent * usableWidth);
                            newYPixels = getHeight() - bottomPadding
                                    - (usableHeight * yPercent);
                            path.lineTo(newXPixels, newYPixels);
                            Path pa = new Path();
                            pa.moveTo(lastXPixels, lastYPixels);
                            pa.lineTo(newXPixels, newYPixels);
                            pa.lineTo(newXPixels, 0);
                            pa.lineTo(lastXPixels, 0);
                            pa.close();
                            canvas.drawPath(pa, paint);
                            lastXPixels = newXPixels;
                            lastYPixels = newYPixels;
                        }
                        if (lastXPixels < minXPixels) {
                            minXPixels = lastXPixels;
                        }
                        if (lastXPixels > maxXPixels) {
                            maxXPixels = lastXPixels;
                        }
                        count++;
                    }

                    // 清除左边多余斜线
                    path.reset();
                    path.moveTo(0, getHeight() - bottomPadding);
                    path.lineTo(minXPixels, getHeight() - bottomPadding);
                    path.lineTo(minXPixels, 0);
                    path.lineTo(0, 0);
                    path.close();
                    canvas.drawPath(path, paint);

                    // 清除右边多余斜线
                    path.reset();
                    path.moveTo(getWidth(), getHeight() - bottomPadding);
                    path.lineTo(maxXPixels, getHeight() - bottomPadding);
                    path.lineTo(maxXPixels, 0);
                    path.lineTo(getWidth(), 0);
                    path.close();

                    canvas.drawPath(path, paint);

                }

                lineCount++;
            }
        }
        drawLeftLabels(canvas);
        //画飞机
        drawPlane(canvas);

        paint.reset();

        paint.setColor(Color.WHITE);
        paint.setAlpha(120);
        paint.setAntiAlias(true);
        // 画横轴
//		if (left_levels != null) {
//			if (left_levels.length == 3) {
//				float part = (getHeight() - bottomPadding - topPadding) / 3;
//				canvas.drawLine(leftPadding, topPadding + part, getWidth()
//						- rightPadding, topPadding + part, paint);
//				canvas.drawLine(leftPadding, topPadding + part * 2, getWidth()
//						- rightPadding, topPadding + part * 2, paint);
//			} else {
//				float part = (getHeight() - bottomPadding - topPadding) / 4;
////				canvas.drawLine(leftPadding, topPadding + part, getWidth()
////						- rightPadding, topPadding + part, paint);
////				canvas.drawLine(leftPadding, topPadding + part * 2, getWidth()
////						- rightPadding, topPadding + part * 2, paint);
////				canvas.drawLine(leftPadding, topPadding + part * 3, getWidth()
////						- rightPadding, topPadding + part * 3, paint);
//			}
//		}
        // 画边框 上 - 下 - 左 - 右
//		 canvas.drawLine(leftPadding, topPadding, getWidth() - rightPadding,
//		 topPadding, paint);
        canvas.drawLine(leftPadding, getHeight() - bottomPadding, getWidth()
                - rightPadding, getHeight() - bottomPadding, paint);
        canvas.drawLine(leftPadding, getHeight() - bottomPadding,
                leftPadding, topPadding, paint);
        // canvas.drawLine(getWidth() - rightPadding, getHeight() -
        // bottomPadding,
        // getWidth() - rightPadding, topPadding, paint);
        paint.setAlpha(255);
        paint.setStyle(Paint.Style.STROKE);
        // 画所有线段
        for (CurveLine line : lines) {
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(stokeWidth);
            Path pathLine = new Path();

            switch (line.getLineType()) {
                case CurveLine.LINE_TYPE_FOLD:
                    buildFoldPath(
                            pathLine,
                            adjustPoints(usableWidth, usableHeight, minX, maxX,
                                    minY, maxY, line.getPoints()));
                    break;
                case CurveLine.LINE_TYPE_BEZIER:
                    buildBezierPath(
                            pathLine,
                            adjustPoints(usableWidth, usableHeight, minX, maxX,
                                    minY, maxY, line.getPoints()));
                    break;
                case CurveLine.LINE_TYPE_FITTING:
                    buildFittingPath(
                            pathLine,
                            adjustPoints(usableWidth, usableHeight, minX, maxX,
                                    minY, maxY, line.getPoints()), 0.3f);
                    break;

            }

            canvas.drawPath(pathLine, paint);
        }

        paint.setStyle(Paint.Style.FILL);

        int pointCount = 0;

        if (touchAble) {
            // 画节点圆圈 处理点击效果
            for (CurveLine line : lines) {
                paint.setColor(line.getColor());
                paint.setStrokeWidth(6);
                paint.setStrokeCap(Paint.Cap.ROUND);

                if (line.isShowingPoints()) {
                    for (CurvePoint p : line.getPoints()) {
                        float yPercent = (p.y - minY) / (maxY - minY);
                        float xPercent = (p.x - minX) / (maxX - minX);
                        float xPixels = leftPadding + (xPercent * usableWidth);
                        float yPixels = getHeight() - bottomPadding
                                - (usableHeight * yPercent);

                        paint.setColor(Color.GRAY);
                        canvas.drawCircle(xPixels, yPixels, 10, paint);
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(xPixels, yPixels, 5, paint);

                        Path path2 = new Path();
                        path2.addCircle(xPixels, yPixels, 30, Direction.CW);
                        p.setPath(path2);
                        p.setRegion(new Region((int) (xPixels - 30),
                                (int) (yPixels - 30), (int) (xPixels + 30),
                                (int) (yPixels + 30)));

                        if (indexSelected == pointCount && listener != null) {
                            paint.setColor(Color.parseColor("#33B5E5"));
                            paint.setAlpha(100);
                            canvas.drawPath(p.getPath(), paint);
                            paint.setAlpha(255);
                        }

                        pointCount++;
                    }
                }
            }
        }

    }

    float x = 152;
    float y = 0;

    //这里飞机的移动距离是需要进行计算,然后不停的改变的.
    public void drawPlane(Canvas canvas) {
        //不停的拿到本机的经纬度
        PlaneInfoBean bean = MyApplication.getMyApplication().homePlane;
        LatLng homeLatlng = bean.latLng;
        //距离终点的距离
        if (homeLatlng != null) {
            //下面的上下摆动是俯仰角和下滑角做差转换成长度
            double fuyangjiao = nav.CEle(bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight, Double.parseDouble(airportSet.airportLat), Double.parseDouble(airportSet.airportLon), Double.parseDouble(airportSet.airportHeight));
            double xiahuajiao = Double.parseDouble(airportSet.planeDownAngle);
            double distance1 = DistanceUtil.getInstance().getDistance(homeLatlng, new LatLng(Double.parseDouble(airportSet.airportLat), Double.parseDouble(airportSet.airportLon)));

            //现在我还需要得到正常的高度,才可以知道差值然后影响了多少.
            //距离终点的距离 / 总距离
//            double percentX = distance1 / setMaxX;
//            //x的偏移距离
//            x = (float) ((MyApplication.getMyApplication().getWidth() - 152) * (1 - percentX));
//
//            //这个就是距离那跟线的距离
            double v = Math.sin((fuyangjiao - xiahuajiao) * Math.PI / 180) * distance1;
            //我们不停的更新这个v,然后就可以不停的显示飞机距离线的距离
            MyApplication.getMyApplication().planeWithNormalHeightDValue = v;
//            //然后我们通过知道x的坐标求得y的坐标,然后加上偏移量,就是y的坐标
//            CurvePoint point = lines.get(0).getPoint(0);
//            float yMax = point.y;
//            double v1 = yMax * percentX;
//            float v2 = (float) (v1 + v);

            ArrayList<CurvePoint> list = new ArrayList<>();
            CurvePoint point1 = new CurvePoint((float) (getMaxX() - distance1), (float) bean.flyHeight);
            list.add(point1);
            CurvePoint[] curvePoints = adjustPoints(1464, 508, 0.0f, getMaxX(), 0.0f, getMaxY(), list);

            //y的偏移距离
            //实时飞行高度
//            double realFlyHeight = bean.flyHeight;
//            double maxFlyHeight = left_levels[1];
//            double percenY = realFlyHeight / maxFlyHeight;
            //这个就是y方面移动的最大值
            //y方面偏移量
//            y = (float) (yMax * (1 - percenY));
//            Log.i("hhh", "percentX" + percentX + "x." + x + "percenY" + percenY + " y" + y);
            canvas.drawBitmap(plane, curvePoints[0].x, curvePoints[0].y, paint);
            ZhuoLuPop pop = ZhuoLuPop.getInstance();
//            pop.zhuolu_pianyi.setText("着陆偏移:" + v);
            postInvalidate();
        }


//		if (x > MyApplication.getMyApplication().getWidth()) {
//			Toast.makeText(context, "飞机已经着陆", Toast.LENGTH_SHORT).show();
//		} else {
//			x = x + 4;
//			postInvalidateDelayed(20);
//		}
//		if (x > 250 && x < 350) {
//			this.y = this.y + 2;
//		} else if (x > 350 && x < 750) {
//
//		} else {
//			this.y = this.y + 4;
//		}

    }

    private CurvePoint[] adjustPoints(float chartWidth, float chartHeight,
                                      float minX, float maxX, float minY, float maxY,
                                      List<CurvePoint> originalList) {
        CurvePoint[] coordPoints = new CurvePoint[originalList.size()];

        for (int i = 0; i < originalList.size(); i++) {
            CurvePoint p = originalList.get(i);
            CurvePoint newPoint = new CurvePoint();
            float yPercent = (p.y - minY) / (maxY - minY);
            float xPercent = (p.x - minX) / (maxX - minX);
            newPoint.x = leftPadding + (xPercent * chartWidth);
            newPoint.y = getHeight() - bottomPadding - (chartHeight * yPercent);
            coordPoints[i] = newPoint;
        }
        return coordPoints;
    }

    /**
     * 折线
     */
    private void buildFoldPath(Path path, CurvePoint[] coordPoints) {
        // Important!
        path.reset();
        path.moveTo(coordPoints[0].x, coordPoints[0].y);

        for (int i = 0; i < coordPoints.length; i++) {
            path.lineTo(coordPoints[i].x, coordPoints[i].y);
        }
    }

    /**
     * 贝塞尔曲线
     */
    private void buildBezierPath(Path path, CurvePoint[] coordPoints) {
        // Important!
        path.reset();

        path.moveTo(coordPoints[0].x, coordPoints[0].y);
        int pointSize = coordPoints.length;

        for (int i = 0; i < coordPoints.length - 1; i++) {
            float pointX = (coordPoints[i].x + coordPoints[i + 1].x) / 2;
            float pointY = (coordPoints[i].y + coordPoints[i + 1].y) / 2;

            float controlX = coordPoints[i].x;
            float controlY = coordPoints[i].y;

            path.quadTo(controlX, controlY, pointX, pointY);
        }
        path.quadTo(coordPoints[pointSize - 1].x, coordPoints[pointSize - 1].y,
                coordPoints[pointSize - 1].x, coordPoints[pointSize - 1].y);
    }

    /**
     * 拟合曲线
     */
    private void buildFittingPath(Path path, CurvePoint[] coordPoints,
                                  float ratio) {
        // Important!
        path.reset();

        path.moveTo(coordPoints[0].x, coordPoints[0].y);

        CurvePoint remRightP = null;
        for (int i = 1; i < coordPoints.length; i++) {

            CurvePoint leftP = null;
            CurvePoint rightP = null;
            if (i < coordPoints.length - 1) {
                CurvePoint p1 = coordPoints[i - 1];
                CurvePoint p2 = coordPoints[i];
                CurvePoint p3 = coordPoints[i + 1];
                if ((p2.y - p1.y) * (p3.y - p2.y) < 0) {
                    leftP = new CurvePoint(p2.x + ratio * (p1.x - p2.x), p2.y);
                    rightP = new CurvePoint(p2.x + ratio * (p3.x - p2.x), p2.y);
                } else {
                    CurvePoint cutP = new CurvePoint();
                    float cut = (float) Math
                            .sqrt((Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y
                                    - p1.y, 2))
                                    / (Math.pow(p2.x - p3.x, 2) + Math.pow(p2.y
                                    - p3.y, 2)));
                    cutP.x = cut * (p1.x - p2.x) + p2.x;
                    cutP.y = cut * (p1.y - p2.y) + p2.y;

                    if (cut > 1) {
                        leftP = new CurvePoint();
                        leftP.x = p2.x + (cutP.x - p3.x) * ratio / 2;
                        leftP.y = p2.y + (cutP.y - p3.y) * ratio / 2;

                        rightP = new CurvePoint();
                        rightP.x = p2.x + (p3.x - cutP.x) * ratio / cut / 2;
                        rightP.y = p2.y + (p3.y - cutP.y) * ratio / cut / 2;
                    } else {
                        leftP = new CurvePoint();
                        leftP.x = p2.x + (cutP.x - p3.x) * ratio * cut * cut
                                / 2;
                        leftP.y = p2.y + (cutP.y - p3.y) * ratio * cut * cut
                                / 2;

                        rightP = new CurvePoint();
                        rightP.x = p2.x + (p3.x - cutP.x) * ratio * cut / 2;
                        rightP.y = p2.y + (p3.y - cutP.y) * ratio * cut / 2;
                    }
                }

            }
            if (remRightP == null && leftP == null) {
                path.lineTo(coordPoints[i].x, coordPoints[i].y);
            } else if (remRightP == null) {
                path.quadTo(leftP.x, leftP.y, coordPoints[i].x,
                        coordPoints[i].y);

            } else if (leftP == null) {
                path.quadTo(remRightP.x, remRightP.y, coordPoints[i].x,
                        coordPoints[i].y);
            } else {
                path.cubicTo(remRightP.x, remRightP.y, leftP.x, leftP.y,
                        coordPoints[i].x, coordPoints[i].y);
            }
            remRightP = rightP;

        }
    }

    public void setTouchAble(boolean touchAble) {
        this.touchAble = touchAble;
    }

    private boolean touchAble = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchAble) {
            return true;
        }
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        int count = 0;
        int lineCount = 0;
        int pointCount = 0;

        Region r = new Region();
        for (CurveLine line : lines) {
            pointCount = 0;
            for (CurvePoint p : line.getPoints()) {

                if (p.getPath() != null && p.getRegion() != null) {
                    r.setPath(p.getPath(), p.getRegion());
                    if (r.contains((int) point.x, (int) point.y)
                            && event.getAction() == MotionEvent.ACTION_DOWN) {
                        indexSelected = count;
                    } else if (event.getAction() == MotionEvent.ACTION_UP
                            || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        if (r.contains((int) point.x, (int) point.y)
                                && listener != null) {
                            listener.onClick(lineCount, pointCount);
                        }
                        indexSelected = -1;
                    }
                }

                pointCount++;
                count++;
            }
            lineCount++;

        }

        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            postInvalidate();
        }

        return true;
    }

    public void setOnPointClickedListener(OnPointClickedListener listener) {
        this.listener = listener;
    }

    public interface OnPointClickedListener {
        abstract void onClick(int lineIndex, int pointIndex);
    }

    private float dp2sp(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
