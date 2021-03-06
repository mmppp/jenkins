package com.hanke.navi.skyair.pop.jcpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.hanke.navi.skyair.scale.BaseScaleView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

public class ZLChartService {
    private GraphicalView mGraphicalView;
    private XYMultipleSeriesDataset multipleSeriesDataset = new XYMultipleSeriesDataset();// 数据集容器
    private XYMultipleSeriesRenderer multipleSeriesRenderer;// 渲染器容器
    private XYSeries mSeries,mSeries2,mSeries3;// 单条曲线数据集
    private XYSeriesRenderer mRenderer;// 单条曲线渲染器
    private Context context;

    public ZLChartService(Context context) {
        this.context = context;
    }

    //获取图表
    public GraphicalView getGraphicalView() {
        mGraphicalView = ChartFactory.getLineChartView(context, multipleSeriesDataset, multipleSeriesRenderer);
        return mGraphicalView;
    }

    /**
     * 获取数据集，及xy坐标的集合
     */
    public void setXYMultipleSeriesDataset() {
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        mSeries = new XYSeries("");
        mSeries.add(0,1500);
        mSeries.add(6,0);
        multipleSeriesDataset.addSeries(mSeries);
        setXYSeriesRenderer(Color.GREEN);
    }
//    public void setXYMultipleSeriesDataset02() {
//        mSeries2 = new XYSeries("");
//        multipleSeriesDataset.addSeries(mSeries2);
//        setXYSeriesRenderer2(Color.parseColor("#931397"));
//    }
//    public void setXYMultipleSeriesDataset03() {
//        mSeries3 = new XYSeries("");
//        multipleSeriesDataset.addSeries(mSeries3);
//        setXYSeriesRenderer3(Color.parseColor("#931397"));
//    }
    /**
     * 获取渲染器
     *
     * @param maxX       x轴最大值
     * @param maxY       y轴最大值
     * @param xLableVal 曲线的标题
     * @param textsize     x轴标题
     */

    public void setXYMultipleSeriesRenderer(double maxX, double maxY,String[] xLableVal, int textsize) {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        multipleSeriesRenderer.setXLabels(0);// 设置 X 轴不显示数字（改用我们手动添加的文字标签）
//        multipleSeriesRenderer.clearXTextLabels();
//        for (int i = 0; i < xLableVal.length; i++) {
//            multipleSeriesRenderer.addXTextLabel(i, xLableVal[i]);;
//        }
        multipleSeriesRenderer.setRange(new double[]{0, 6, 0, maxY});//xy轴的范围
//        multipleSeriesRenderer.setXAxisMin(6);
//        multipleSeriesRenderer.setXAxisMax(0);
//        multipleSeriesRenderer.setYAxisMin(0);
//        multipleSeriesRenderer.setYAxisMax(1500);
        multipleSeriesRenderer.setXLabels(7);//设置 x 轴刻度个数
        multipleSeriesRenderer.setYLabels(4);//设置 y 轴刻度个数
//        multipleSeriesRenderer.setXLabelsAlign(Align.RIGHT);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multipleSeriesRenderer.setAxisTitleTextSize(20);//设置 xy 轴标题字体大小
        multipleSeriesRenderer.setLabelsTextSize(textsize);//设置xy轴刻度文字大小
        multipleSeriesRenderer.setLegendTextSize(20);
        multipleSeriesRenderer.setPointSize(2f);//曲线描点尺寸
        multipleSeriesRenderer.setFitLegend(true);
//        multipleSeriesRenderer.setMargins(new int[]{shang,zuo,xia,you});//参数一：上  参数二：左  参数三：下  参数四：右
        multipleSeriesRenderer.setMargins(new int[]{20,28* BaseScaleView.width_view / 32,0,28* BaseScaleView.width_view / 32});
        multipleSeriesRenderer.setShowGrid(true);
        multipleSeriesRenderer.setZoomEnabled(true, false);
//        multipleSeriesRenderer.setZoomInLimitX(maxX);
        multipleSeriesRenderer.setPanEnabled(false, false);//设置滑动,这边是横向可以滑动,竖向不可滑动
        multipleSeriesRenderer.setAxesColor(Color.WHITE);//坐标轴颜色
        multipleSeriesRenderer.setGridColor(0);//网格颜色
        multipleSeriesRenderer.setBackgroundColor(Color.parseColor("#9f000000"));//背景色
        multipleSeriesRenderer.setMarginsColor(Color.parseColor("#00ffffff"));//边距背景色，默认背景色为黑色，这里修改为白色
        multipleSeriesRenderer.setZoomLimits(new double[] {0, 6 , 0, maxY });
        multipleSeriesRenderer.setShowLegend(false);//设置是否显示图例
        multipleSeriesRenderer.setApplyBackgroundColor(true);//设置是否应用背景色
        multipleSeriesRenderer.setXLabelsPadding(10);
        multipleSeriesRenderer.setYLabelsPadding(20);
        multipleSeriesRenderer.setYLabelsVerticalPadding(-multipleSeriesRenderer.getLabelsTextSize()/2);

    }
    private void setXYSeriesRenderer(int color) {
        mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(color);//曲线颜色
        mRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
        mRenderer.setLineWidth(2.5f);//设置画的线条的宽度
        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
    }
//    private void setXYSeriesRenderer2(int color) {
//        mRenderer = new XYSeriesRenderer();
//        mRenderer.setColor(color);//曲线颜色
//        mRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
//        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(
//                XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
//        fill.setColor(Color.parseColor("#5f931397"));//山体填充区的颜色
//        mRenderer.addFillOutsideLine(fill);
//        mRenderer.setLineWidth(2.5f);//设置画的线条的宽度
//        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
//    }
//    private void setXYSeriesRenderer3(int color) {
//        mRenderer = new XYSeriesRenderer();
//        mRenderer.setColor(color);//曲线颜色
//        mRenderer.setPointStyle(PointStyle.POINT);//描点风格，可以为圆点，方形点等等
//        mRenderer.setLineWidth(2.5f);//设置画的线条的宽度
//        multipleSeriesRenderer.addSeriesRenderer(mRenderer);
//    }

    //根据新加的数据，更新曲线，只能运行在主线程
    public void updateChart(double x, double y) {
        mSeries.add(x, y);
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    //添加新的数据，多组，更新曲线，只能运行在主线程
    public void updateChart(List<Double> xList, List<Double> yList) {
        for (int i = 0; i < xList.size(); i++) {
            mSeries.add(xList.get(i), yList.get(i));
        }
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    public void updateChart(double x, List<String> yList) {
        for (int i = 0; i < yList.size(); i++) {
            mSeries.add(x, Double.parseDouble(yList.get(i)));
            x+=1;
            mGraphicalView.repaint();//此处也可以调用invalidate()
        }
    }


}
