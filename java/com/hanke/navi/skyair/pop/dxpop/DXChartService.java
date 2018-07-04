package com.hanke.navi.skyair.pop.dxpop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.scale.BaseScaleView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

public class DXChartService {
    private GraphicalView mGraphicalView;
    private XYMultipleSeriesDataset multipleSeriesDataset = new XYMultipleSeriesDataset();// 数据集容器
    private XYMultipleSeriesRenderer multipleSeriesRenderer;// 渲染器容器
    private XYSeries mSeries,mSeries2,mSeries3;// 单条曲线数据集
    private XYSeriesRenderer mRenderer;// 单条曲线渲染器
    private Context context;

    public DXChartService(Context context) {
        this.context = context;
    }

    //获取图表
    public GraphicalView getGraphicalView() {
        mGraphicalView = ChartFactory.getCubeLineChartView(context,
                multipleSeriesDataset, multipleSeriesRenderer, 0.3f);
        return mGraphicalView;
    }

    //获取数据集，及xy坐标的集合
    public void setXYMultipleSeriesDataset() {
        mSeries = new XYSeries("");
        if (multipleSeriesDataset!=null)
            multipleSeriesDataset.removeSeries(mSeries);
        multipleSeriesDataset.addSeries(mSeries);
        setXYSeriesRenderer(Color.parseColor("#8f92D050"));
    }
    public void setXYMultipleSeriesDataset02() {
        mSeries2 = new XYSeries("");
        if (multipleSeriesDataset!=null)
            multipleSeriesDataset.removeSeries(mSeries2);
        multipleSeriesDataset.addSeries(mSeries2);
        setXYSeriesRenderer2(context.getResources().getColor(R.color.red));
    }
    public void setXYMultipleSeriesDataset03() {
        mSeries3 = new XYSeries("");
        multipleSeriesDataset.addSeries(mSeries3);
        setXYSeriesRenderer3(Color.parseColor("#931397"));
    }

    public void setRange(double minY,double maxY,int yLables){
        multipleSeriesRenderer.setRange(new double[]{0, 35, minY, maxY});//xy轴的范围
        multipleSeriesRenderer.setYLabels(yLables);//设置 y 轴刻度个数
    }

    /**
     * 获取渲染器
     *
     * @param maxX       x轴最大值
     * @param maxY       y轴最大值
     * @param chartTitle 曲线的标题
     * @param xTitle     x轴标题
     * @param yTitle     y轴标题
     * @param axeColor   坐标轴颜色
     * @param labelColor 标题颜色
     * @param curveColor 曲线颜色
     * @param gridColor  网格颜色
     */
    double maxY;
    public void setXYMultipleSeriesRenderer(double maxX, double minY,double maxY, int axeColor, int textsize) {
        this.maxY = maxY;
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        multipleSeriesRenderer.setRange(new double[]{0, maxX, 0, maxY});//xy轴的范围
        multipleSeriesRenderer.setXLabels(7);//设置 x 轴刻度个数
        multipleSeriesRenderer.setYLabels(3);//设置 y 轴刻度个数
//        multipleSeriesRenderer.setXLabelsAlign(Align.RIGHT);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setAxisTitleTextSize(20);//设置 xy 轴标题字体大小
        multipleSeriesRenderer.setLabelsTextSize(textsize);//设置xy轴刻度文字大小
        multipleSeriesRenderer.setLegendTextSize(20);
        multipleSeriesRenderer.setPointSize(2f);//曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);
//        multipleSeriesRenderer.setMargins(new int[]{shang,zuo,xia,you});//参数一：上  参数二：左  参数三：下  参数四：右
//        multipleSeriesRenderer.setMargins(new int[]{20,28* BaseScaleView.width_view / 32,0,28* BaseScaleView.width_view / 32});
        multipleSeriesRenderer.setMargins(new int[]{20,28* BaseScaleView.width_view / 45,0,28* BaseScaleView.width_view / 90});
        multipleSeriesRenderer.setShowGrid(true);
        multipleSeriesRenderer.setZoomEnabled(true, false);
//        multipleSeriesRenderer.setZoomInLimitX(maxX);
        multipleSeriesRenderer.setPanEnabled(true, false);//设置滑动,这边是横向可以滑动,竖向不可滑动
        multipleSeriesRenderer.setAxesColor(axeColor);//坐标轴颜色
        multipleSeriesRenderer.setGridColor(0);//网格颜色
        multipleSeriesRenderer.setBackgroundColor(Color.parseColor("#9f000000"));//背景色
        multipleSeriesRenderer.setMarginsColor(Color.parseColor("#00ffffff"));//边距背景色，默认背景色为黑色，这里修改为白色
//        multipleSeriesRenderer.setXLabelsColor();//设置x轴刻度文字颜色
//        multipleSeriesRenderer.setYLabelsColor();//设置y轴刻度文字颜色
//        multipleSeriesRenderer.setPanLimits(new double[]{Integer.MAX_VALUE,Integer.MAX_VALUE, Integer.MAX_VALUE, 0});//设置拉动的范围
//        multipleSeriesRenderer.setPanLimits(new double[]{0, 31,0,0});//设置拉动的范围
        multipleSeriesRenderer.setZoomLimits(new double[] {0, maxX , 0, maxY });
        multipleSeriesRenderer.setShowLegend(false);//设置是否显示图例
        multipleSeriesRenderer.setApplyBackgroundColor(true);//设置是否应用背景色
        multipleSeriesRenderer.setXLabelsPadding(10);
        multipleSeriesRenderer.setYLabelsPadding(20);
        multipleSeriesRenderer.setYLabelsVerticalPadding(-multipleSeriesRenderer.getLabelsTextSize()/2);


//        mRenderer = new XYSeriesRenderer();
//        mRenderer.setColor(curveColor);//曲线颜色
//        mRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
//        multipleSeriesRenderer.addSeriesRenderer(mRenderer);

    }
    private void setXYSeriesRenderer(int color) {//画山体剖面图
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(color);//曲线颜色
        mRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(
                XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
        fill.setColor(color);//山体填充区的颜色
        mRenderer.addFillOutsideLine(fill);
        mRenderer.setLineWidth(2.5f);//设置画的线条的宽度
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }
    //画飞机的高度计算虚线
    private void setXYSeriesRenderer2(int color) {
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(color);//曲线颜色
        mRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
        mRenderer.setLineWidth(3f);//设置画的线条的宽度
//        //可改变为虚线
        float[] dash = {MyApplication.getMyApplication().getWidth()/40,MyApplication.getMyApplication().getWidth()/60};//{线的长度,缺口的长度,线的长度,缺口的长度……}
        BasicStroke stroke = new BasicStroke(Paint.Cap.ROUND, Paint.Join.BEVEL, 10.0f, dash, 3.0f);
        mRenderer.setStroke(stroke);
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }
    //想要用来画飞机,但是不行
    private void setXYSeriesRenderer3(int color) {
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(color);//曲线颜色
        mRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
        mRenderer.setLineWidth(2.5f);//设置画的线条的宽度
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }

    /**
     * 根据新加的数据，更新曲线，只能运行在主线程
     *
     * @param x 新加点的x坐标
     * @param y 新加点的y坐标
     */
    public void updateChart(double x, double y) {
        mSeries.add(x, y);
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    /**
     * 添加新的数据，多组，更新曲线，只能运行在主线程
     *
     * @param xList
     * @param yList
     */
    public void updateChart(List<Double> xList, List<Double> yList) {
        for (int i = 0; i < xList.size(); i++) {
            mSeries2.add(xList.get(i), yList.get(i));
        }
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    public void updateChart(double x, List<String> yList) {
        mSeries.clear();
        for (int i = 0; i < yList.size(); i++) {
            mSeries.add(x, Double.parseDouble(yList.get(i)));
            x+=1;
//            mGraphicalView.repaint();//此处也可以调用invalidate()
            mGraphicalView.postInvalidate();//此处也可以调用invalidate()
        }
    }

    Canvas canvas = new Canvas();
    public void updateChart2(double y1,double x2,double y2) {
        //我这个线,所以需要的是两个点连接起来.
        mSeries2.clear();
        mSeries2.add(0, y1);
        mSeries2.add(x2, y2);
        mGraphicalView.postInvalidate();

    }

    double a;
    public void abc(){
        a = MyApplication.getMyApplication().clientTask.getHb();


    }

}
