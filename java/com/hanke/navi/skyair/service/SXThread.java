package com.hanke.navi.skyair.service;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hanke.navi.framwork.utils.Constants;
import com.hanke.navi.skyair.MyApplication;

public class SXThread implements Runnable {

    public SXThread() {
    }

    @Override
    public synchronized void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
//                if (MyApplication.getMyApplication().getSocket()!=null){
                    Message message = MyApplication.getMyApplication().myHandler.obtainMessage();
                    message.what = Constants.SHUAXIN;
                    MyApplication.getMyApplication().myHandler.sendMessage(message);
//                }
                if (MyApplication.getMyApplication().getSocket()==null){
                    Log.e("123", "socket已经中断");
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }
}
