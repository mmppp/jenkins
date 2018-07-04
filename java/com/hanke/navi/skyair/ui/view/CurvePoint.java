package com.hanke.navi.skyair.ui.view;

import android.graphics.Path;
import android.graphics.Region;

public class CurvePoint {
	public float x = 0;
	public float y = 0;
	public long timeMills;
	private Path path;
	private Region region;

	public CurvePoint() {
		super();
		timeMills = System.currentTimeMillis();
	}

	public CurvePoint(float x, float y) {
		super();
		this.x = x;
		this.y = y;
		timeMills = System.currentTimeMillis();
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

}
