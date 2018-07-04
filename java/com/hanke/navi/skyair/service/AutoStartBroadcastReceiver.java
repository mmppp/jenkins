package com.hanke.navi.skyair.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
                Intent newIntent = context.getPackageManager().getLaunchIntentForPackage("com.hanke.navi");
                context.startActivity(newIntent);
        }
    }
}
