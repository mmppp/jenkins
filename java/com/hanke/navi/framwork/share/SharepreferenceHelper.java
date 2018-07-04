package com.hanke.navi.framwork.share;

import android.content.Context;

public class SharepreferenceHelper extends PreferenceWrapper{

    private static SharepreferenceHelper sharepreferenceHelper;

    private SharepreferenceHelper(Context context) {
        super(context);
    }

    public static SharepreferenceHelper getInstence(Context context){
        if(sharepreferenceHelper == null)
            sharepreferenceHelper = new SharepreferenceHelper(context);
        return sharepreferenceHelper;
    }

    //设置航线item
    public void setHXitem(String hXitem){
        putString("hXitem",hXitem);
    }
    public String getHXitem(){
        return getString("hXitem");
    }
    //设置航路item
    public void setHLitem(String hLitem){
        putString("hLitem",hLitem);
    }
    public String getHLitem(){
        return getString("hLitem");
    }

    //航路点添加
    public void setHLumingcheng(String HLumingcheng){
        putString("HLumingcheng",HLumingcheng);
    }
    public String getHLumingcheng(){
        return getString("HLumingcheng");
    }
    public void setHLuweidu(String HLuweidu){
        putString("HLuweidu",HLuweidu);
    }
    public String getHLuweidu(){
        return getString("HLuweidu");
    }
    public void setHLujingdu(String HLujingdu){
        putString("HLujingdu",HLujingdu);
    }
    public String getHLujingdu(){
        return getString("HLujingdu");
    }
    public void setHLugaodu(String HLugaodu){
        putString("HLugaodu",HLugaodu);
    }
    public String getHLugaodu(){
        return getString("HLugaodu");
    }

    //航路点添加
    public void setChangeHLumingcheng(String change_mingcheng){
        putString("change_mingcheng",change_mingcheng);
    }

    //偏置距离
    public void setPZjuli(String PZjuli){
        putString("PZjuli",PZjuli);
    }
    public String getPZjuli(){
        return getString("PZjuli");
    }

    //ip  port
    public void setIp(String ip){
        putString("ip",ip);
    }
    public String getIp(){
        return getString("ip");
    }
    public void setPort(int port){
        putInt("port",port);
    }
    public int getPort(){
        return getInt("port");
    }

}
