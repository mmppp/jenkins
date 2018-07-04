package com.hanke.navi.skyair.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hanke.navi.skyair.pop.bean.GaocengDataBean;

import java.util.ArrayList;

/**
 * Created by mahao on 2017/10/31.
 */

public class GaocengDataDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "HeBeiSence.db";
    private static int db_version = 1;
    private static GaocengDataDBHelper db;

    public static class GaocengDataTable {
        public static final String TABLE_NAME = "GaocengDataTable";
        public static final String ID = "id";
        // 有哪些字段
        public static final String GAOCENG_LAT = "gaocenglat";
        public static final String GAOCENG_LON = "gaocenglon";
        public static final String GAOCENG_HEIGHT = "gaocengheight";
    }

    public GaocengDataDBHelper(Context context) {
        super(context, DB_NAME, null, db_version);

    }

    public static GaocengDataDBHelper getInstance(Context context) {
        if (db == null) {
            db = new GaocengDataDBHelper(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        //创建表
        db.execSQL("CREATE TABLE " + GaocengDataTable.TABLE_NAME + " (" + GaocengDataTable.ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + GaocengDataTable.GAOCENG_LAT + " TEXT,"
                + GaocengDataTable.GAOCENG_LON + " TEXT," + GaocengDataTable.GAOCENG_HEIGHT + " TEXT);");
        //创建索引
        db.execSQL("CREATE INDEX IND_LAT ON " + GaocengDataTable.TABLE_NAME + " (" + GaocengDataTable.GAOCENG_LAT + ");");
        db.execSQL("CREATE INDEX IND_LON ON " + GaocengDataTable.TABLE_NAME + " (" + GaocengDataTable.GAOCENG_LON + ");");
        Log.i("readread", "CREATE INDEX IND_LAT ON " + GaocengDataTable.TABLE_NAME + " (" + GaocengDataTable.GAOCENG_LAT + ");");
    }

    public synchronized boolean insertTestResult(ArrayList<GaocengDataBean> list) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            for (int x = 0; x < list.size(); x++) {
                GaocengDataBean bean = list.get(x);
//                String sql = "insert into " + GaocengDataTable.TABLE_NAME + " (" + GaocengDataTable.GAOCENG_LAT + ","
//                        + GaocengDataTable.GAOCENG_LON + "," + GaocengDataTable.GAOCENG_HEIGHT + ") values ('" + bean.gaocengLat + "','"
//                        + bean.gaocengLon + "','" + bean.gaocengHeight + "')";
//                Log.i("hahaha", "sql:" + sql);
                ContentValues values = new ContentValues();
                values.put(GaocengDataTable.GAOCENG_LAT, bean.gaocengLat);
                values.put(GaocengDataTable.GAOCENG_LON, bean.gaocengLon);
                values.put(GaocengDataTable.GAOCENG_HEIGHT, bean.gaocengHeight);
                db.insert(GaocengDataTable.TABLE_NAME, null, values);
//                db.execSQL(sql);
            }
            //加上的代码
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public synchronized GaocengDataBean getHeight(String lat, String lon) {
        Log.i("readread", "开始了");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        GaocengDataBean result = null;
        try {
            db = getWritableDatabase();
            String sqlQuery = "select * from " + GaocengDataTable.TABLE_NAME + " where gaocenglat = '" + lat + "' and gaocenglon = '" + lon + "'";
            // 返回回来的就是表里面所有的数据
            Log.i("readread", "sql:" + sqlQuery);
            cursor = db.rawQuery(sqlQuery, null);
            while (cursor.moveToNext()) {
                try {
                    result = new GaocengDataBean();
                    result.gaocengLat = lat;
                    result.gaocengLon = lon;
                    result.gaocengHeight = cursor.getString(cursor.getColumnIndex(GaocengDataTable.GAOCENG_HEIGHT));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i("readread", "结束了:" + result);

        return result;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < db_version) {
            dropTable(db);
        }
    }

    /**
     * 清空数据库的操作
     */

    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + GaocengDataTable.TABLE_NAME);
        createTable(db);
        db.setVersion(db_version);
    }
}
