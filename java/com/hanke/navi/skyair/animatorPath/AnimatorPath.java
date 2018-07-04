package com.hanke.navi.skyair.animatorPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnimatorPath {
    //一系列的轨迹记录动作
    private List<PathPoint> mPoints = new ArrayList<PathPoint>();

    //移动位置到:
    public void moveTo(float x,float y){
        mPoints.add(PathPoint.moveTo(x,y));
    }

    //直线移动
    public void lineTo(float x,float y){
        mPoints.add(PathPoint.lineTo(x,y));
    }

    // 二阶贝塞尔曲线移动
    public void secondBesselCurveTo(float c0X, float c0Y,float x,float y){
        mPoints.add(PathPoint.secondBesselCurveTo(c0X,c0Y,x,y));
    }

    //三阶贝塞尔曲线移动
    public void thirdBesselCurveTo(float c0X, float c0Y, float c1X, float c1Y, float x, float y){
        mPoints.add(PathPoint.thirdBesselCurveTo(c0X,c0Y,c1X,c1Y,x,y));
    }
    // 返回移动动作集合
    public Collection<PathPoint> getPoints(){
        return mPoints;
    }
}
