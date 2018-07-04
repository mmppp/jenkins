package com.hanke.navi.skyair.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.skyair.db.GaocengDataDBHelper;
import com.hanke.navi.skyair.pop.bean.GaocengDataBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by mahao on 2017/10/31.
 */

public class ReadGaocengFileUtil {

    public static float OriginalResut = -1001001;

    /**
     * 还有问题,一直执行的话,会导致程序内存占用升高
     * seek代表跳过的字节数
     *
     * @return
     */
    public static float getHeight(double lat, double lon) {
        float result = OriginalResut;
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "gaocheng" + File.separator + "DEM.txt";
        File file = new File(path);
        int seek = 0;
        //需要通过经纬度,计算出需要
        int[] matNumArr = new int[4];
        //矩阵个数
        int matNum = 0;
        //表示矩阵代表的经纬度范围集合.例如latlng存储 34 108 代表的就是34.0-34.99 108.0-108,99这个范围内的经纬度
        ArrayList<LatLng> matLatAndLonList = new ArrayList<>();
        //表示在哪个矩阵中
        int inWhichMat = 0;
        int latAndLonNum = 0;
        //从这里开始后面才是高度数据的信息
        int start = 0;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            //首先读出有多少个矩阵块,然后才知道有多少的经纬度字节,这里应该是从右向左读取的
            for (int x = 0; x < 4; x++) {
                matNumArr[x] = raf.read();
            }
            //然后要把这四个字节表示的十六进制,转化为十进制
            String hexString = Integer.toHexString(matNumArr[3]) + Integer.toHexString(matNumArr[2]) + Integer.toHexString(matNumArr[1]) + Integer.toHexString(matNumArr[0]);
            //这就是矩阵数量
            matNum = Integer.parseInt(hexString, 16);

            //然后经纬度的话,开始分析,现在是什么位置的.从度分秒,例如是37度45分60秒,116度50分12秒
            //确定这个经纬度所在的高度,在哪个矩阵里面,例如在第5个矩阵里面,就要用4 * 3600 ,然后第五个矩阵的
            for (int x = 0; x < matNum; x++) {
                //经度
                String readLon1str = Integer.toHexString(raf.read());
                String readLon2str = Integer.toHexString(raf.read());
                int readLon = Integer.parseInt(readLon2str + readLon1str, 16);
                //维度
                String readLat1str = Integer.toHexString(raf.read());
                String readLat2str = Integer.toHexString(raf.read());
                int readLat = Integer.parseInt(readLat2str + readLat1str, 16);
                //存到集合里面去.
                LatLng latlng = new LatLng(readLat, readLon);
                matLatAndLonList.add(latlng);
            }
            //这是经纬度占用的字节
            latAndLonNum = matNum * 4;
            start = 4 + latAndLonNum;
            raf.close();

            //用来查询的纬度
            double zhengLat = Math.floor(lat);
            double tempLat = lat - zhengLat;
            double v_lat = tempLat * 3600;
            long round_lat = 0;
            long temp = 0;
            if (tempLat > 0.99) {
                temp = Math.round(v_lat) - 1;
            } else {
                temp = Math.round(v_lat) + 1;
            }
            if (temp != 0) {
                temp--;
            }
            round_lat = temp * 3600;
            //用来查询的经度
            double zhengLon = Math.floor(lon);
            double tempLon = lon - zhengLon;
            double v_lon = tempLon * 3600;
            long round_lon = 0;
            if (tempLon > 0.99) {
                round_lon = Math.round(v_lon) - 1;
            } else {
                round_lon = Math.round(v_lon) + 1;
            }

            if (round_lon != 0) {
                round_lon--;
            }

            //这里需要判断,在第几个矩阵块里面.首先计算出来经纬度,然后跳过不在里面的矩阵块
            //这里如果没有找到在任何一个矩阵块里面的话,给一个标示,直接返回没有找打
            boolean isInWhichMat = false;
            for (int x = 0; x < matLatAndLonList.size(); x++) {
                LatLng latLng = matLatAndLonList.get(x);
                if (lat - latLng.latitude >= 0 && lat - latLng.latitude <= 1 && lon - latLng.longitude >= 0 && lon - latLng.longitude <= 1) {
                    //那么就是在这个矩阵里面
                    inWhichMat = x;
                    isInWhichMat = true;
                }
            }
            if (isInWhichMat) {
                //例如这里在第4个矩阵里面.那么就应该跳过,前面三个矩阵里面所有的点
                //需要偏移的高度数
                seek = (int) (start + inWhichMat * 3600 * 3600 * 2 + (round_lat + round_lon) * 2);
                raf = new RandomAccessFile(file, "rw");
                //跳过的字节数
                raf.skipBytes(seek);
                int read1 = raf.read();
                String s1 = "";
                if (read1 <= 15) {
                    s1 = "0" + Integer.toHexString(read1);
                } else {
                    s1 = Integer.toHexString(read1);
                }
                int read2 = raf.read();
                String s2 = "";
                if (read2 <= 15) {
                    s2 = "0" + Integer.toHexString(read2);
                } else {
                    s2 = Integer.toHexString(read2);
                }
                result = Integer.parseInt(s2 + s1, 16);
                Log.i("readreadread", "msg:" + result);
            }
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static void readGaocengFile(Context context) {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "gaocheng" + File.separator + "gc.txt";
        File file = new File(path);
        GaocengDataDBHelper dbHelper = new GaocengDataDBHelper(context);
        if (file.exists()) {
            InputStreamReader inputStreamReader = null;
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");

                BufferedReader reader = new BufferedReader(inputStreamReader);
//                StringBuffer sb = new StringBuffer();
                ArrayList<GaocengDataBean> temp = new ArrayList<>();
                String line = "";
                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                    sb.append("\n");
//                    Log.e("msg", "line = " + line);
                    String nr[] = line.split(",");
                    //list里面存储了高度
                    if (nr.length >= 3) {
                        GaocengDataBean bean = new GaocengDataBean();
                        bean.gaocengLon = nr[0];
                        bean.gaocengLat = nr[1];
                        bean.gaocengHeight = nr[2];
                        temp.add(bean);
                        if (temp.size() > 150000) {
                            dbHelper.insertTestResult(temp);
                            temp.clear();
                        }
//                        sb.delete(0, sb.length());
                    }
                }
                //这里应该还有一些剩余的不到3万的,在temp里面
                dbHelper.insertTestResult(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }


}
