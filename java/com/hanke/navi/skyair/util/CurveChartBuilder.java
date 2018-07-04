//package com.hanke.navi.skyair.util;
//
///**
// * Created by mahao on 2017/11/2.
// */
//
//public class CurveChartBuilder {
//    private static CurveChart curveChart;
//    private static CurveChartBuilder cBuilder;
//
//    private CurveChartBuilder() {
//    }
//
//    public static CurveChartBuilder createBuilder(CurveChart curve) {
//        curveChart = curve;
//        synchronized (CurveChartBuilder.class) {
//            if (cBuilder == null) {
//                cBuilder = new CurveChartBuilder();
//            }
//        }
//        return cBuilder;
//    }
//
//    /**
//     * 设置x，y轴的刻度
//     *
//     * @param xStart X轴开始的刻度
//     * @param xEnd   X轴结束的刻度
//     * @param yStart
//     * @param yEnd
//     * @return
//     */
////    public CurveChartBuilder setXYCoordinate(int xStart, float xEnd, float yStart, float yEnd) {
//////        curveChart.setxStart(xStart, xEnd);
//////        curveChart.setyStart(yStart, yEnd);
//////        return cBuilder;
////    }
//
//    /**
//     * 是否填充曲线下面的颜色，默认值为true，
//     *
//     * @param isFillDownLineColor
//     * @return
//     */
//    public  CurveChartBuilder setIsFillDownColor(boolean isFillDownLineColor) {
//        curveChart.setFillDownLineColor(isFillDownLineColor);
//        return cBuilder;
//    }
//
//    /**
//     * 设置填充的颜色
//     *
//     * @param fillColor
//     * @return
//     */
//    public CurveChartBuilder setFillDownColor(int fillColor) {
//        curveChart.setFillColor(fillColor);
//        return cBuilder;
//    }
//
//    /**
//     * 比较的值，比这个值大就把这个点也绘制出来
//     *
//     * @param compareValue
//     * @return
//     */
//    public CurveChartBuilder setCompareValue(float compareValue) {
//        curveChart.setCompareValue(compareValue);
//        return cBuilder;
//    }
//
//
//
//    public void show() {
//        if (curveChart == null) {
//            throw new NullPointerException("CurveChart is null");
//        }
//        curveChart.show();
//    }
//}
