package com.hanke.navi.skyair.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mahao on 2017/10/30.
 */

public class AppListenerService extends Service {
    private boolean isAppStart = false;// 判断软件是否打开，过滤重复执行
    private String packageName_now = "";//记录当前所在应用的包名
    private GaojingPreference preference;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        preference = new GaojingPreference(getApplicationContext());
        timer.schedule(task, 0, 5000); //开始监听应用，每500毫秒查询一次，用这种方式循环比while更节约资源，而且更好用，这个项目刚开始用了while，把我坑坏了
        super.onCreate();
    }

    Handler handler_listen = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;//获取到栈顶最顶层的activity所对应的应用
                String packageName = cn.getPackageName();//从ComponentName对象中获取到最顶层的应用包名
                if (!packageName_now.equals(packageName)) {//如果两个包名不相同，那么代表切换了应用
                    packageName_now = packageName;//更新当前的应用包名
                    isAppStart = false;//将是否是监听的应用包名的状态修改为false
                }
                if (packageName.equals("com.hanke.navi")) {//这里举例监听QQ
//                    if (!isAppStart) {
//                        isAppStart = true;//这里就是让他重复的执行,然后一直更新当前的经纬度,一旦退出的话,就不更新了,然后这样的话,就可以在下次进来的时候保持之前推出去的经纬度.
                    //。。。。逻辑处理
                    Log.i("hahaha", "app没有关闭");
                    //这里保存一下经纬度,方便下次进入的时候初始经纬度正确..
                    if (MyApplication.getMyApplication().isBack) {
                        if (MainActivity.instence != null) {
                            preference.saveStartLatAndLon(MainActivity.instence.lookback.getWd() + "", MainActivity.instence.lookback.getJd() + "");
                        }
                    } else {
                        if (MyApplication.getMyApplication().clientTask.getWd() == 0 && MyApplication.getMyApplication().clientTask.getJd() == 0) {
                        } else {
                            preference.saveStartLatAndLon(MyApplication.getMyApplication().clientTask.getWd() + "", MyApplication.getMyApplication().clientTask.getJd() + "");
                        }
//                    }
                    }
                    super.handleMessage(msg);
                }

            }
        }
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler_listen.sendMessage(message);
        }
    };

    public void onDestroy() {
        timer.cancel();//销毁服务的时候同时关闭定时器timer
        super.onDestroy();
    }

}