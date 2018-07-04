package com.hanke.navi.skyair.ui.view;

import java.util.ArrayList;

public class CurveLine {
	public static final int LINE_TYPE_FOLD = 1;// 折线
	public static final int LINE_TYPE_BEZIER = 2;// 贝塞尔曲线
	public static final int LINE_TYPE_FITTING = 3;// 拟合曲线

	private ArrayList<CurvePoint> points = new ArrayList<CurvePoint>();
	private int color;
	private int lineType = 2;
	private boolean showPoints = false;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getLineType() {
		return lineType;
	}

	public void setLineType(int lineType) {
		this.lineType = lineType;
	}

	public ArrayList<CurvePoint> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<CurvePoint> points) {
		this.points = points;
	}

	public void addPoint(CurvePoint point) {
		points.add(point);
	}

	public CurvePoint getPoint(int index) {
		return points.get(index);
	}

	public int getSize() {
		return points.size();
	}

	public boolean isShowingPoints() {
		return showPoints;
	}

	public void setShowingPoints(boolean showPoints) {
		this.showPoints = showPoints;
	}

}
