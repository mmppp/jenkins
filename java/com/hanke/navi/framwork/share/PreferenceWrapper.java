package com.hanke.navi.framwork.share;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceWrapper {
    private SharedPreferences sharedPreferences;


    private static final String SP_NAME="nav";
    public PreferenceWrapper(Context context) {
       sharedPreferences= context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);//Context.MODE_MULTI_PROCESS支持跨进程访问
    }

    protected void putString(String key,String value){
        sharedPreferences.edit().putString(key, value).commit();
    }

    protected String getString(String key,String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }

    protected String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    protected void setBoolean(String key,boolean value){
        sharedPreferences.edit().putBoolean(key,value).commit();
    }

    protected boolean getBoolean(String key,boolean defaultValue){
        return sharedPreferences.getBoolean(key,defaultValue);
    }

    protected void putInt(String key,int a){
        sharedPreferences.edit().putInt(key, a).commit();
    }

    protected int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * 清空sharedpreference文件
     */
    public void clear(){
        sharedPreferences.edit().clear().commit();
    }


}
