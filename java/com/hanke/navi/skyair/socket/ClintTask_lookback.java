package com.hanke.navi.skyair.socket;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.framwork.arith.Warnning;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.GaojingSetBean;
import com.hanke.navi.skyair.pop.bean.ImpactWarningBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.tsqpop.HintPop;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;
import com.hanke.navi.skyair.util.RadixConversionUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Created by mahao on 2017/9/26.
 */

public class ClintTask_lookback extends AsyncTask<Void, Void, String> {
    Activity activity;
    static final String TAG = "123";
    String ip = null;
    int port = 0;
    Context context;
    InputStream inputStream;
    private ArrayList<String> Msg_bd_mc;
    private ArrayList<String> Msg_bd_ga;
    String Msg_adsb, Msg_bd;
    Map<String, List<String>> map;
    //北斗 维度,经度,高度,速度
    private double wd, jd, hb;
    public double speed;
    double wda, wdb, jda, jdb, gda, gdb;
    public List<Double> list_wd, list_jd, list_gd;
    public String dateFromGps, timeFromGps;
    //ADSB
    int index_a, index_b;
    String strfs0, strfs1, strfs2, strfs3, strfs4;
    List<String> list_msgadsb;
    List<String> list_esweidz;
    List<String> list_string;
    public float angle;
    //控制刷新的时间
    public int flag = 0;
    public double upOrDownSpeed;
    public int adsbCount;
    public int beidouCount;
    public boolean adsbHasNew;
    public boolean beidouHasNew;
    public String gpsState;
    private HintPop hintPop;
    public boolean isConnectWithService;
    private Warnning warnning;
    private GaojingPreference preference;
    //是否是回放
    //当前的行数
    public long rowLine;
    //,如果设定从第n行读,那么就从第n行开始
    public int totalCount;
    public boolean refreshOther;
    public boolean refreshHomePlane;
    public double wd_ads, jd_ads;
    private double dzs_24b_ads;
    public double hb_ads;
    public double sudu_gro_ads, sudu_ver_ads, fly_ang_ads;
    public int zong;
    List<String> list_z;
    int size;
    Air[] arrays = new Air[size];
    Air fei;
    Map<Double, Air> airList = new HashMap<>();
    ArrayList<String> strfsList;
    public double lastHb;
    public int lastAdsbCount = 0;
    public int lastBeidouCount = 0;


    public ClintTask_lookback(Context context, boolean isback, long hopeRow) {
        this.context = context;
        this.context = context;
        list_wd = new ArrayList<>();
        list_jd = new ArrayList<>();
        list_gd = new ArrayList<>();
        list_msgadsb = new ArrayList<>();
        list_esweidz = new ArrayList<>();
        list_string = new ArrayList<>();
        list_z = new ArrayList<>();
        map = new HashMap<>();
//        latAndLonMap = new HashMap<>();
    }

    public ClintTask_lookback(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public double getGda() {
        return gda;
    }

    public double getGdb() {
        return gdb;
    }

    public double getJda() {
        return jda;
    }

    public double getJdb() {
        return jdb;
    }

    public double getWda() {
        return wda;
    }

    public double getWdb() {
        return wdb;
    }

    public double getWd() {
        return wd;
    }

    public double getJd() {
        return jd;
    }

    public double getHb() {
        return hb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {//可以在此处刷新UI

        //其他飞机
        if (refreshOther) {
            //其他飞机的信息
            MyApplication.getMyApplication().setWindowwInfoMarker2();
            MyApplication.getMyApplication().setOtherMarker();
            refreshOther = false;
        }
//                MyApplication.getMyApplication().setMarker();
        MyApplication.getMyApplication().setMarker_new(true, getWd(), getJd(), hb, angle, upOrDownSpeed, speed);
        MyApplication.getMyApplication().setRefreshCursor(hb, speed);
        //用来显示导航时的飞行信息
//            MyApplication.getMyApplication().setHomePlaneToDestination();
        MyApplication.getMyApplication().setCenterPos(getWd(), getJd());
        //用来展示gps里面解析出来了的时间和日期
        MyApplication.getMyApplication().setTimeAndDate(dateFromGps, timeFromGps);
//        MyApplication.getMyApplication().setServiceState();
        MyApplication.getMyApplication().setTSQCirclejg(new LatLng(getWd(), getJd()));
        MyApplication.getMyApplication().setTSQCirclezy(new LatLng(getWd(), getJd()));
        //更新seekbar
        MyApplication.getMyApplication().setSeekBarPaocess(rowLine);
        MyApplication.getMyApplication().setTwoTime();
//            MyApplication.getMyApplication().setMarker1();

        super.onProgressUpdate(values);
    }

    @Override
    public synchronized String doInBackground(Void... param) {
        try {
            //一 通过网络连接得来的数据.
            String path = "";
            File fileDir = null;
            File file = null;
            FileWriter fos = null;
            Log.e(TAG, "---等待连接服务器---");
            if (Msg_bd_mc == null) {
                Msg_bd_mc = new ArrayList<>();
            }
            if (Msg_bd_ga == null) {
                Msg_bd_ga = new ArrayList<>();
            }
            path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "lookback";
            fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            warnning = new Warnning(context);
            preference = new GaojingPreference(context);
            //走进这里就是在读取回放数据了
            if (MyApplication.getMyApplication().isBack) {
                Log.i("hahaha", "走到while这里面来了");
                //这里文件就是你在列表里面选择的文件了
                String hh = preference.getLookBackFile();
                File readFile = new File(fileDir, hh);
                FileReader fr = new FileReader(readFile);
                FileReader fr_first = new FileReader(readFile);
                BufferedReader br = new BufferedReader(fr);
                BufferedReader br_first = new BufferedReader(fr_first);
                //用文件的大小来表达.
                //这里先读取文件,知道整个的时长,整个的行数.
                String temp = "";
                String lastRow = "";
                String firRow = br_first.readLine();
                isConnectWithService = true;
                while ((temp = br_first.readLine()) != null) {
                    totalCount++;
                    lastRow = temp;
                }
                //循环玩了之后,temp里面就是最后一行的内容,可以记录
                String[] split_last = lastRow.split("-");
                MyApplication.getMyApplication().lookbackEndTime = split_last[0];
                MyApplication.getMyApplication().lookbackCurrentTime = firRow.split("-")[0];
                MyApplication.getMyApplication().totalRow = readFile.length();
                String str = "";

                //没有暂停
                while ((str = br.readLine()) != null) {
                    //这个就是已经读了的字节长度
                    rowLine = rowLine + str.getBytes().length;
                    //用来控制拖动进度条的 hopeRow是拖动到的位置
                    if (rowLine >= MyApplication.getMyApplication().hopeRow) {
                        String[] split = str.split("-");
                        MyApplication.getMyApplication().lookbackCurrentTime = split[0];
                        String content = split[1];
                        String substring = content.substring(0, 2);
                        if (substring.equals("15")) {
//                    //adsb报文
                            Msg_adsb = content;
                            adsbCount++;
                            AdsbMsg_new();
                        } else if (content.contains("GPRMC") || content.contains("GPGGA")) {
//                    //北斗报文
                            beidouCount++;
                            Msg_bd = content;// 0为要解码的第一个 byte 的索引 若为10，则从第九个byte 开始索引
                            //然后切割完毕之后,单数的是GPRMC,除了0之外的双数的是GPGGA
                            String[] msgStrArr = Msg_bd.split("\\$");
                            if (msgStrArr[1].contains("GPRMC")) {
                                Msg_bd_mc.add(msgStrArr[1]);
                                Log.i("2222", "添加了mc1");
                                beidouMsgNew_mc();
                                //这个可以用来控制快进的进度,这个方法并不是非常好的方法,并不是非常的准确.这个只能多次的实验.
                                //只有在读了一次本机的时候,才进行时间的控制,否则读一条消息停一下,这样的话,时间就不对头了
                                if (MyApplication.getMyApplication().multipleNuml == 0 || MyApplication.getMyApplication().multipleNuml == 1) {
                                    //单架飞机测试过关,
                                    Thread.sleep(1700);
                                } else if (MyApplication.getMyApplication().multipleNuml == 2) {
                                    //单架飞机测试过关
                                    Thread.sleep(900);
                                } else if (MyApplication.getMyApplication().multipleNuml == 4) {
                                    //单架飞机测试过关
                                    Thread.sleep(300);
                                } else if (MyApplication.getMyApplication().multipleNuml == 8) {
                                    //单架飞机测试过关
                                    Thread.sleep(150);
                                } else if (MyApplication.getMyApplication().multipleNuml == 16) {
                                    Thread.sleep(80);
                                }
                            } else if (msgStrArr[1].contains("GPGGA")) {
                                Log.i("2222", "添加了ga1");
                                Msg_bd_ga.add(msgStrArr[1]);
                                beidouMsgNew_ga();
                            }

                            Msg_bd_mc.clear();
                            Msg_bd_ga.clear();
                        }

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("hahaha", "报异常了" + e.getMessage());
        } finally {
            if (MyApplication.getMyApplication().getSocket() != null) {
                try {
                    Log.i("hahaha", "服务器断开了");
                    MyApplication.getMyApplication().getSocket().close();
                    MyApplication.getMyApplication().setSocket(null);
                    list_msgadsb.clear();
                    list_esweidz.clear();
                    isConnectWithService = false;
                    publishProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void beidouMsgNew_mc() {
        //首先解析mc,也就是gps
        for (int x = 0; x < Msg_bd_mc.size(); x++) {
            String msgMc = Msg_bd_mc.get(x);
            String nr[] = msgMc.split(",");

            //时间,需要进行转换,就算在回放里面,时间也用的是当时的北斗消息的时间,并不是当时的电脑时间.但是确实计算eta,etc的时间出现问题了.是电脑时间
            String timeFromGps = nr[1];
            int timeHour = Integer.parseInt(timeFromGps.substring(0, 2)) + 8;
            if (timeHour == 24) {
                timeHour = 0;
            }
            int timeMin = Integer.parseInt(timeFromGps.substring(2, 4));
            int timeDec = Integer.parseInt(timeFromGps.substring(4));
            if (timeDec < 16) {
                timeDec = 60 - (16 - timeDec);
                timeMin--;
            } else {
                timeDec = timeDec - 16;
            }
            if (timeMin < 10) {
                if (timeDec < 10) {
                    this.timeFromGps = timeHour + ":0" + timeMin + ":0" + timeDec;
                } else {
                    this.timeFromGps = timeHour + ":0" + timeMin + ":" + timeDec;
                }
            } else {
                if (timeDec < 10) {
                    this.timeFromGps = timeHour + ":" + timeMin + ":0" + timeDec;
                } else {
                    this.timeFromGps = timeHour + ":" + timeMin + ":" + timeDec;
                }
            }
            //gps连接状态
            gpsState = nr[2];
            //维度
            String latFromGps = nr[3];
            String latZheng = latFromGps.split("\\.")[0].substring(0, 2);

            double lateXiao = Double.parseDouble(latFromGps.substring(2)) / 60;
            String lat = latZheng + "." + (lateXiao + "").split("\\.")[1];
            wd = Double.parseDouble(lat);

            //经度
            String lonFromGps = nr[5];
            String lonZheng = lonFromGps.split("\\.")[0];
            double lonXiao = 0;
            if (lonZheng.length() == 5) {
                lonZheng = lonZheng.substring(0, 3);
                lonXiao = Double.parseDouble(lonFromGps.substring(3)) / 60;
            } else {
                lonZheng = lonZheng.substring(0, 2);
                lonXiao = Double.parseDouble(lonFromGps.substring(2)) / 60;
            }
            jd = Integer.parseInt(lonZheng) + lonXiao;
            //储存一下经纬度,以便于下次进来的时候知道上次结束的时候的经纬度
            if (wd != 0 && jd != 0) {
                preference.saveStartLatAndLon(wd + "", jd + "");
            }
            speed = Double.parseDouble(nr[7]);
            angle = Float.parseFloat(nr[8]);
            dateFromGps = "20" + nr[9].substring(0, 2) + "/" + nr[9].substring(2, 4) + "/" + nr[9].substring(4);
            refreshHomePlane = true;
            publishProgress();
        }
    }

    private void beidouMsgNew_ga() {
        //首先解析mc,也就是gps
        if (Msg_bd_ga != null && Msg_bd_ga.size() != 0) {
            for (int x = 0; x < Msg_bd_ga.size(); x++) {
                String msgGa = Msg_bd_ga.get(x);
                String nrga[] = msgGa.split(",");
                Log.i("111111", "hb=" + Double.parseDouble(nrga[9]));
                hb = Double.parseDouble(nrga[9]);
                if (lastHb == 0) {
                    lastHb = hb;
                } else {
                    upOrDownSpeed = hb - lastHb;
                    lastHb = hb;
                }
            }
        }
        refreshHomePlane = true;
        publishProgress();
    }


    private void AdsbMsg_new() {
        //用来储存接下来有几个fs的集合.
        strfsList = new ArrayList<>();
//        String s_2 = hexStringToBinaryString(Msg_adsb.substring(2, 6));
        //总长度
//        int totalLength = binaryStringToDecString(s_2);
        for (int x = 6; x < 16; x += 2) {
            //开始去fs0到fs4,如果哪个末尾是0的话,那么就不取了.
            //判断末尾是不是0,如果是的话,那么终止循环,否则吧取出来的加入到集合里面去
            String temp1 = Msg_adsb.substring(x, x + 2);
            String temp = RadixConversionUtil.hexStringToBinaryString(temp1).trim();
            char temp2 = temp.charAt(temp.length() - 1);
            Log.i("hahaha", temp + "......." + temp1);
            if ((temp.charAt(temp.length() - 1) + "").equals("1")) {
                strfsList.add(temp);
            } else {
                //跳出循环
                strfsList.add(temp);
                break;
            }
        }
        //现在通过判断list的长度就知道里面有fs0到fs几了.
        flagFS0_new();

    }

    private void flagFS0_new() {
        String strs0 = strfsList.get(0);
        PlaneInfoBean bean = new PlaneInfoBean();
        //求出经纬度应该存在的起始坐标
        int indexLatLon = 6 + strfsList.size() * 2 + parseInt(strs0.charAt(0) + "") * 4 + parseInt(strs0.charAt(1) + "") * 4 + parseInt(strs0.charAt(2) + "") * 6;
        Log.i("hahaha", Msg_adsb.substring(indexLatLon, indexLatLon + 8).replaceAll("\\s{1,}", ""));
        String latStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(indexLatLon, indexLatLon + 8).replaceAll("\\s{1,}", ""));

        double latitude = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_jwd_new(latStr));

        String lonStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(indexLatLon + 8, indexLatLon + 16).replaceAll("\\s{1,}", ""));
        double lonitude = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_jwd_new(lonStr));
        Log.i("hahaha", latStr.length() + ",latitude=" + latitude + "lonitude=" + lonitude);

        //24位地址值
        Log.i("hahaha", "24地址值" + Msg_adsb.substring(indexLatLon + 16, indexLatLon + 22));
        String adressNum_2 = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(indexLatLon + 16, indexLatLon + 22).replaceAll("\\s{1,}", ""));

        //海拔高度
        String flyHeightStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(indexLatLon + 22, indexLatLon + 26).replaceAll("\\s{1,}", ""));
        double flyHeight = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_pa(flyHeightStr));
        Log.i("hahaha", latStr.length() + ",latitude=" + latitude + "lonitude=" + lonitude + "24地址值" + adressNum_2 + "hbs_ads=" + flyHeight);
        bean.latLng = new LatLng(latitude, lonitude);
        bean.flyHeight = flyHeight;
        bean.currentTimeMillis = System.currentTimeMillis();

        MyApplication.getMyApplication().latLngHashMap.put(adressNum_2, bean);

        if (strfsList.size() >= 1) {
            flagFS1_new(30, adressNum_2, bean);
        }
    }

    private void flagFS1_new(int startIndex, String key, PlaneInfoBean bean) {
        String strs1 = strfsList.get(1);
        int endIndex = startIndex + 13 * 2;
        if (strfsList.size() >= 2) {
            flagFS2_new(endIndex, key, bean);
        }
    }

    private void flagFS2_new(int startIndex, String key, PlaneInfoBean bean) {
        String strs2 = strfsList.get(2);
        //升降速度
        String upDownSpeedStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(startIndex, startIndex + 4).replaceAll("\\s{1,}", ""));
        double upDownSpeed = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_ve(upDownSpeedStr));

        //飞行速度
        String flySpeedStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(startIndex + 4, startIndex + 8).replaceAll("\\s{1,}", ""));
        double flySpeed = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_gs(flySpeedStr));

        //航向角
        String flyAngleStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(startIndex + 8, startIndex + 12).replaceAll("\\s{1,}", ""));
        double flys_ang_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_fa(flyAngleStr));
        Log.i("hahaha", "upDownSpeed=" + upDownSpeed + "flySpeed=" + flySpeed + "flys_ang_ads" + flys_ang_ads);

        //然后是航班号,但是这里要做一个判断.
        int temp = parseInt(strs2.charAt(2) + "");
        String flyNumber = null;
        if (temp == 0) {
            //如果这一位是0的话,那么航班号的话,就不用前面加了
            String flyNumberStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(startIndex + 12, startIndex + 24).replaceAll("\\s{1,}", ""));
            StringBuffer sb = new StringBuffer();
            int num;
            for (int i = 0; i < flyNumberStr.length(); i += 6) {
                if (i + 6 <= flyNumberStr.length()) {
                    num = RadixConversionUtil.binaryStringToDecString(flyNumberStr.substring(i, i + 6));
                } else {
                    num = RadixConversionUtil.binaryStringToDecString(flyNumberStr.substring(i, flyNumberStr.length()));
                }
                if (0 < num && num < 48) {
                    num = num + 64;
                }
                flyNumber = sb.append((char) num).toString().replaceAll("[^0-9a-zA-Z]", "");
            }
            Log.i("hahaha", "flyNumber=" + flyNumber);
        } else {
            int x = 12;
            //如果这一位存在的话,那么航班号的前面就要加上一个数字了
            while (true) {
                String tempStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(startIndex + x, startIndex + x + 2).replaceAll("\\s{1,}", ""));
                x += 2;
                if (tempStr.charAt(tempStr.length() - 1) == '0') {
                    break;
                }
            }
            //然后这里拿到x,然后进行计算航班号.
            String flyNumberStr = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(startIndex + x, startIndex + x + 12).replaceAll("\\s{1,}", ""));
            StringBuffer sb = new StringBuffer();
            int num;
            for (int i = 0; i < flyNumberStr.length(); i += 6) {
                if (i + 6 <= flyNumberStr.length()) {
                    num = RadixConversionUtil.binaryStringToDecString(flyNumberStr.substring(i, i + 6));
                } else {
                    num = RadixConversionUtil.binaryStringToDecString(flyNumberStr.substring(i, flyNumberStr.length()));
                }
                if (0 < num && num < 48) {
                    num = num + 64;
                }
                flyNumber = sb.append((char) num).toString().replaceAll("[^0-9a-zA-Z]", "");
            }
            Log.i("hahaha", "flyNumber=" + flyNumber);
        }

        bean.flyAngle = flys_ang_ads;
        bean.flySpeed = flySpeed;
        bean.planeNum = flyNumber;
        bean.upOrDownSpeed = upDownSpeed;

        double speedEast = Math.sin(flys_ang_ads * Math.PI / 180) * flySpeed / 3.6;
        double speedNorth = Math.cos(flys_ang_ads * Math.PI / 180) * flySpeed / 3.6;
        PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
        double homePlaneSpeedEast = Math.sin((360 - homePlane.flyAngle) * Math.PI / 180) * homePlane.flySpeed / 3.6;
        double homePlaneSpeedNorth = Math.cos((360 - homePlane.flyAngle) * Math.PI / 180) * homePlane.flySpeed / 3.6;
        GaojingSetBean gaoJingSetInfo = preference.getGaoJingSetInfo();
//        int safe = warnning.is_safe(homePlane.latLng.latitude, homePlane.latLng.longitude, homePlane.flyHeight, homePlaneSpeedEast, homePlaneSpeedNorth, homePlane.upOrDownSpeed, bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight, speedEast, speedNorth, upDownSpeed, distance);
        if (homePlane != null && homePlane.latLng != null && bean != null && bean.latLng != null) {
            double distance = DistanceUtil.getInstance().getDistance(homePlane.latLng, bean.latLng);
            ImpactWarningBean bean1 = warnning.safe_level(homePlane.latLng.latitude, homePlane.latLng.longitude, homePlane.flyHeight, homePlaneSpeedEast, homePlaneSpeedNorth, homePlane.upOrDownSpeed,
                    bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight, speedEast, speedNorth, upDownSpeed,
                    Double.parseDouble(gaoJingSetInfo.caz_distance), Double.parseDouble(gaoJingSetInfo.caz_height), Double.parseDouble(gaoJingSetInfo.caz_time),
                    Double.parseDouble(gaoJingSetInfo.paz_distance), Double.parseDouble(gaoJingSetInfo.paz_height), Double.parseDouble(gaoJingSetInfo.paz_time), distance);
            ImpactWarningBean bean2 = warnning.safe_level(33.9935, 108.6166, 1000, 300 / 3.6, 0, 0, 34, 109, 1000, 0, 0, 0, 6000, 605, 60, 12000, 800, 300, 35434);
//            Log.i("hahaha", "bean2的数据" + bean2.result);

            //储存飞机的报警信息和级别
            bean.warningInfo = bean1.result;
            bean.warningLevel = bean1.level;
        } else {
            Log.i("hahaha", "warninglevel为null");
        }
        MyApplication.getMyApplication().latLngHashMap.put(key, bean);

        flag++;
        if (flag == 2 * MyApplication.getMyApplication().latLngHashMap.size()) {
            flag = 0;
            refreshOther = true;
            publishProgress();
        }

    }


    private void flagFS3() {//FS3 = 6b，查找152,160,157,155字段
        String vertical_rate_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 13, index_b + 17)).replaceAll("\\s{1,}", "");//155字段 00 33 ——气压升降速度（替换地理升降速度）
//            Log.e(TAG, "@@@sudu_ads@@@ = " + vertical_rate_ads);
        double vers_rate_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_ve(vertical_rate_ads));
        Log.e(TAG, "@@@地理升降速度vers_rate_ads@@@ = " + vers_rate_ads);
        this.sudu_ver_ads = vers_rate_ads;

        String ground_speed_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 17, index_b + 21)).replaceAll("\\s{1,}", "");//160字段 04 be ——地速
        double gros_sp_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_gs(ground_speed_ads));
        Log.e(TAG, "@@@地速gros_sp_ads@@@ = " + gros_sp_ads);
        this.sudu_gro_ads = gros_sp_ads;


        String fly_angle_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 21, index_b + 25)).replaceAll("\\s{1,}", "");//160字段 b1 01 ——航向角
        double flys_ang_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_fa(fly_angle_ads));
        Log.e(TAG, "@@@航向角flys_ang_ads@@@ = " + flys_ang_ads);
        this.fly_ang_ads = flys_ang_ads;

        if (strfs3.charAt(strfs3.length() - 1) == '1') {// FS3 = 6b，判断bb最低位为1，有FS4，查找170字段
            flagFS4();
        }
    }

    public String hbh_ads;

    private void flagFS4() {//FS4 = ca，查找170字段
        String number_fly_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 31, index_b + 43)).replaceAll("\\s{1,}", "");//170字段 0c 76 b7 c7 1c 20 ——航班号
        Log.e(TAG, "@@@number_fly_ads@@@ = " + number_fly_ads);
        StringBuffer sb = new StringBuffer();
        int num;
        String hangbanhao_ads = null;
        for (int i = 0; i < number_fly_ads.length(); i += 6) {
            if (i + 6 <= number_fly_ads.length()) {
                num = RadixConversionUtil.binaryStringToDecString(number_fly_ads.substring(i, i + 6));
            } else {
                num = RadixConversionUtil.binaryStringToDecString(number_fly_ads.substring(i, number_fly_ads.length()));
            }
            if (0 < num && num < 48) {
                num = num + 64;
            }
            hangbanhao_ads = sb.append((char) num).toString().replaceAll("[^0-9a-zA-Z]", "");
        }
        Log.e(TAG, "@@@航班号hangbanhao@@@ = " + hangbanhao_ads);
        this.hbh_ads = hangbanhao_ads;

        //这里将经纬度封装到Latlng模型里面,使用24位地址当做键,存入到map里面去.这里不止需要放经纬度,还需要放入其他需要显示的信息
        String dizhi_24b_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_a + 47, index_a + 53)).replaceAll("\\s{1,}", "");//080字段 78 0f 1a ——目标地址
        PlaneInfoBean bean = new PlaneInfoBean();
        bean.latLng = new LatLng(wd_ads, jd_ads);
        String pa_hei_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 7, index_b + 11)).replaceAll("\\s{1,}", "");//145字段 01 b6 ——气压高度（替换地理高度）
//        Log.e(TAG, "@@@pa_hei_ads@@@ = " + pa_hei_ads);
        double hbs_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_pa(pa_hei_ads));
        bean.flyHeight = hbs_ads;
        String ground_speed_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 17, index_b + 21)).replaceAll("\\s{1,}", "");//160字段 04 be ——地速
        double gros_sp_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_gs(ground_speed_ads));
        bean.flySpeed = gros_sp_ads;
        bean.planeNum = hangbanhao_ads;
        String fly_angle_ads = RadixConversionUtil.hexStringToBinaryString(Msg_adsb.substring(index_b + 21, index_b + 25)).replaceAll("\\s{1,}", "");//160字段 b1 01 ——航向角
        double flys_ang_ads = Double.parseDouble(RadixConversionUtil.binaryStringToDecString_fa(fly_angle_ads));
        bean.flyAngle = flys_ang_ads;
        MyApplication.getMyApplication().latLngHashMap.put(dizhi_24b_ads, bean);

        if (strfs4.charAt(strfs4.length() - 1) == '0') {// FS4 = ca，判断ca最低位为0，无扩展
//            Log.e(TAG, "---最后的socket真假为--- = " + MyApplication.getMyApplication().getSocket().isClosed() + MyApplication.getMyApplication().getSocket().isConnected());
            return;
        }
    }
}
