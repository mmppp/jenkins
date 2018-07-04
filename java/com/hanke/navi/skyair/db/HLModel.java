package com.hanke.navi.skyair.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.hanke.navi.skyair.pop.bean.HangLuBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HLModel extends BaseModel{

    public static final String TABLE_NAME = "hlinfo";//航路点的表名
    private static Map<String, String> paramsMap = new HashMap<String, String>();

    static {
        paramsMap.put(_ID, "integer primary key autoincrement");
        paramsMap.put("namehl", "TEXT NOT NULL");//航路名
        paramsMap.put("wdhl", "TEXT NOT NULL");//航路纬度
        paramsMap.put("jdhl", "TEXT NOT NULL");//航路经度
        paramsMap.put("gdhl", "TEXT NOT NULL");//航路高度
    }

    //插入一条航路
    public void insertHL(HangLuBean hangLuBean){
        if (hangLuBean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("namehl",hangLuBean.hanglu);
        values.put("wdhl",hangLuBean.weidu);
        values.put("jdhl",hangLuBean.jingdu);
        values.put("gdhl",hangLuBean.gaodu);
        if (isExist(hangLuBean.hanglu)) {//航路名存在
            update(values, "namehl=?", new String[]{hangLuBean.hanglu});
        } else {//如果没有则插入数据
            insert(values);
        }
    }

    public boolean isExist(String content) {
        Cursor cursor = select("select * from " + TABLE_NAME + " where namehl = '" + content + "'");
        if (cursor != null && cursor.moveToNext())
            return true;
        return false;
    }

    //删除一条航路
    public void deleteHL(int position){
        String content="";
        Cursor cursor = selectAll();
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                content = cursor.getString(cursor.getColumnIndex("namehl"));
            }
            cursor.close();
        }
        delete("namehl=?", new String[]{content});
    }

    //删除最后一条航路
    public void deleteLastHL(String id) {
        String content = "";
        Cursor cursor = selectAllAsc(id);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                content = cursor.getString(cursor.getColumnIndex("namehl"));
            }
            cursor.close();
        }
        delete("namehl=?", new String[]{content});
    }

    //更新一条航路
    public void updadeHL(String id,HangLuBean hangLuBean,String[] whereArgs){
        if (hangLuBean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("namehl",hangLuBean.hanglu);
        update(values,id,whereArgs);
    }

    //查询一条航路
    public HangLuBean getHangLuBeanById(String id) {
        HangLuBean hangLuBean = new HangLuBean();
        String sql = "select * from " + TABLE_NAME + " where namehl='" + id+ "'";
        Cursor cursor = select(sql);
        if (cursor != null) {
            if (cursor.moveToNext()) {//找到namehl为XXXX的数据
                hangLuBean.hanglu = cursor.getString(cursor.getColumnIndex("namehl"));
                hangLuBean.weidu = cursor.getDouble(cursor.getColumnIndex("wdhl"));
                hangLuBean.jingdu = cursor.getDouble(cursor.getColumnIndex("jdhl"));
            }
        }
        return hangLuBean;
    }

    public List<HangLuBean> getAll() {
        List<HangLuBean> hangLuBeanList = null;
        Cursor cursor = selectAllAsc(_ID);
        if (cursor != null) {
            hangLuBeanList = new ArrayList<HangLuBean>();
            while (cursor.moveToNext()) {
                HangLuBean hangLuBean = new HangLuBean();
                hangLuBean.hanglu = cursor.getString(cursor.getColumnIndex("namehl"));
                hangLuBean.weidu = cursor.getDouble(cursor.getColumnIndex("wdhl"));
                hangLuBean.jingdu = cursor.getDouble(cursor.getColumnIndex("jdhl"));
                hangLuBean.gaodu = cursor.getDouble(cursor.getColumnIndex("gdhl"));
                hangLuBeanList.add(hangLuBean);
            }
            cursor.close();
        }
        return hangLuBeanList;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, String> getParamsMap() {
        return paramsMap;
    }
}
