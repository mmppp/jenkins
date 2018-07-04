package com.hanke.navi.skyair.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hanke.navi.skyair.service.LandNavService;

/**
 * Created by Che on 2017/8/28.
 */
public class LandNavActivity extends Activity {

    //横坐标怎么和飞机联系起来呢...横坐标就是距离终点的距离
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //首先停止service,避免一直打开activity
        stopService(new Intent(getApplicationContext(), LandNavService.class));


    }
}
