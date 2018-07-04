package com.hanke.navi.skyair.socket;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.framwork.arith.Warnning;
import com.hanke.navi.framwork.share.SharepreferenceHelper;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.GaojingSetBean;
import com.hanke.navi.skyair.pop.bean.ImpactWarningBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.tsqpop.HintPop;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;
import com.hanke.navi.skyair.util.RadixConversionUtil;
import com.hanke.navi.skyair.util.TimeUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static java.lang.Integer.parseInt;

public class ClientTask extends AsyncTask<Void, Void, String> {

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
    List<String> list_msgadsb;
    List<String> list_esweidz;
    List<String> list_string;
    public float angle;
    //控制刷新的时间
    public int flag = 0;
    public double upOrDownSpeed;
    public int adsbCount;
    public int beidouCount;
    public boolean beidouHasNew;
    public String gpsState;
    private HintPop hintPop;
    public boolean isConnectWithService;
    private Warnning warnning;
    private GaojingPreference preference;
    //是否是回放
    //当前的行数
    public int rowLine;
    //,如果设定从第n行读,那么就从第n行开始
    public long hopeRow;
    public int lastAdsbCount = 0;
    public int lastBeidouCount = 0;
    public int lastBeidou = 0;
    public double lastHb;
    public boolean refreshOther;
    public boolean refreshHomePlane;
    List<String> list_z;
    ArrayList<String> strfsList;
    public boolean isSetDefault;
    private final Timer connectServiceTimer;

    public ClientTask(Context context, boolean isback, long hopeRow) {
        this.context = context;
        list_wd = new ArrayList<>();
        list_jd = new ArrayList<>();
        list_gd = new ArrayList<>();
        list_msgadsb = new ArrayList<>();
        list_esweidz = new ArrayList<>();
        list_string = new ArrayList<>();
        list_z = new ArrayList<>();
        map = new HashMap<>();
        this.hopeRow = hopeRow;
        connectServiceTimer = new Timer();
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

        if (MyApplication.getMyApplication().getSocket() != null && !MyApplication.getMyApplication().isBack) {
            //其他飞机
            if (refreshOther) {
                //其他飞机的信息
                MyApplication.getMyApplication().setWindowwInfoMarker2();
                MyApplication.getMyApplication().setOtherMarker();
                refreshOther = false;
            }
            if (refreshHomePlane) {
                //本机
//                MyApplication.getMyApplication().setMarker();
                MyApplication.getMyApplication().setMarker_new(getWd(), getJd(), hb, angle, upOrDownSpeed, speed);
                MyApplication.getMyApplication().setRefreshCursor();
                refreshHomePlane = false;
            }
            if (isSetDefault) {
                MyApplication.getMyApplication().setDefault();
            }
            //用来显示导航时的飞行信息
//            MyApplication.getMyApplication().setHomePlaneToDestination();
            MyApplication.getMyApplication().setCenterPos();
            //用来展示gps里面解析出来了的时间和日期
            MyApplication.getMyApplication().setTimeAndDate();
//            MyApplication.getMyApplication().setServiceState();
            MyApplication.getMyApplication().setTSQCirclejg(new LatLng(getWd(), getJd()));
            MyApplication.getMyApplication().setTSQCirclezy(new LatLng(getWd(), getJd()));
            //更新seekbar
            MyApplication.getMyApplication().setTwoTime();
//            MyApplication.getMyApplication().setMarker1();
        }
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
//                //否则就连接服务器,读取数据
            if (MyApplication.getMyApplication().getSocket() == null) {
                ip = SharepreferenceHelper.getInstence(context).getIp();
                port = SharepreferenceHelper.getInstence(context).getPort();
                MyApplication.getMyApplication().setSocket(new Socket(ip, port));
                //MyApplication.getMyApplication().socket.setTcpNoDelay(true);
                Log.e(TAG, "---服务器连接成功---");
                isConnectWithService = true;
                publishProgress();
                //连接成功服务器一次,就重新命名一个文件
                path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "lookback";
                fileDir = new File(path);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                file = new File(fileDir, TimeUtil.getCurrentTime() + ".txt");
                fos = new FileWriter(file);
            } else {
                Log.e("hahaha", "---服务器连接失败---");
            }
            //这样设计有一个问题就是说,如果进来的时候北斗没有发送报文的话,那么后面就算北斗连接上来了的话,也是接收不到报文的,现在应该设置定时
            //如果开始没有接收到北斗的报文的话,那么隔一段时间继续连接一下.
            if (MyApplication.getMyApplication().getSocket().isConnected() && !MyApplication.getMyApplication().getSocket().isClosed()) {
                getData(fos, file);
            }
//            else {
//                //这里代表没有收到北斗或者是adsb的消息,那么就setDefault
//                Log.i("hahaha", "走到这里开始重新");
//                isSetDefault = true;
//                publishProgress();
//                //定时的去查看是否成功的连接上服务器了,10秒看一次
//                connectServiceTimer.schedule(task, 0, 10000);
//            }
//            }
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

//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            try {
//                ip = SharepreferenceHelper.getInstence(context).getIp();
//                port = SharepreferenceHelper.getInstence(context).getPort();
//                MyApplication.getMyApplication().setSocket(new Socket(ip, port));
//                Log.e(TAG, "---服务器连接成功---");
//                isConnectWithService = true;
//                publishProgress();
//                if (MyApplication.getMyApplication().getSocket().isConnected() && !MyApplication.getMyApplication().getSocket().isClosed()) {
////                    getData(fos, file);
//                    Log.i("hahaha", "服务器没有断开");
//                } else {
//                    Log.i("hahaha", "服务器断开了.....");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

    public void getData(FileWriter fos, File file) {
        try {
            //停止定时器
//            connectServiceTimer.cancel();
            isSetDefault = false;
//                if (!MyApplication.getMyApplication().isBack) {
            inputStream = MyApplication.getMyApplication().getSocket().getInputStream();
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }
            if (count < 1024)
                count = 1024;
            Log.e(TAG, "---count--- = " + count);
            byte[] b = new byte[count];
            warnning = new Warnning(context);
            preference = new GaojingPreference(context);
            //不回放的时候走这里,联网读取数据
            while (!MyApplication.getMyApplication().isBack) {
                Log.e(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (MyApplication.getMyApplication().getSocket() != null) {
                    int length = inputStream.read(b);
                    Log.i("bbbbbbbbb", b.toString());
                    Log.e(TAG, "---接收数据的原始长度为--- = " + length);
                    Log.e(TAG, "---接收b[0]为--- = " + b[0]);
                    //if(b[0] == 0x15)
                    if (b[0] == 0x15) {//ADS_B数据
                        Msg_adsb = RadixConversionUtil.bytesToHexString(b).replaceAll("\\s{1,}", "").substring(0, 2 * length);
                        String timeIndex = this.timeFromGps + "-";
                        String content = timeIndex + Msg_adsb;
                        try {
                            fos.write(content);
                            fos.write("\r\n");
                            fos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "接收的数据Msg_adsb = " + Msg_adsb);
                        if (!list_msgadsb.contains(Msg_adsb)) {
                            list_msgadsb.clear();
                            list_msgadsb.add(Msg_adsb);
                        }
                        Log.e(TAG, "list_msgadsb的长度 = " + list_msgadsb.size());
                        adsbCount++;
                        AdsbMsg_new();
//                            AdsbMsg();//空中的其它飞机
//                            MsgNews();
                    } else if ((char) b[0] == '$') {
                        beidouCount++;
                        Msg_bd = new String(b, 0, length).replaceAll("\\s{1,}", "");// 0为要解码的第一个 byte 的索引 若为10，则从第九个byte 开始索引
                        //然后切割完毕之后,单数的是GPRMC,除了0之外的双数的是GPGGA
                        String[] msgStrArr = Msg_bd.split("\\$");
                        if (msgStrArr.length == 2) {
                            if (msgStrArr[1].contains("GPRMC")) {
                                Msg_bd_mc.add(msgStrArr[1]);
                                Log.i("2222", "添加了mc1");
                                beidouMsgNew_mc(file, fos, b, length);
                            } else if (msgStrArr[1].contains("GPGGA")) {
                                Log.i("2222", "添加了ga1");
                                Msg_bd_ga.add(msgStrArr[1]);
                                beidouMsgNew_ga(file, fos, b, length);
                            }
                        } else {
                            for (int x = 1; x < msgStrArr.length; x++) {
                                if (msgStrArr[x].contains("GPRMC")) {
                                    Msg_bd_mc.add(msgStrArr[x]);
                                    Log.i("hahaha", "添加了mc.....");

                                } else if (msgStrArr[x].contains("GPGGA")) {
                                    Log.i("hahaha", "添加了ga");
                                    Msg_bd_ga.add(msgStrArr[x]);
                                }
                            }
                        }
                        Msg_bd_mc.clear();
                        Msg_bd_ga.clear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beidouMsgNew_mc(File file, FileWriter fos, byte[] b, int legth) {
        //首先解析mc,也就是gps
        for (int x = 0; x < Msg_bd_mc.size(); x++) {
            String msgMc = Msg_bd_mc.get(x);
            String nr[] = msgMc.split(",");

            //时间,需要进行转换
            String timeFromGps = nr[1];
            int timeHour = Integer.parseInt(timeFromGps.substring(0, 2)) + 8;
            int timeMin = Integer.parseInt(timeFromGps.substring(2, 4));
            String secand = timeFromGps.substring(4);
            int dec = 0;
            if (secand.contains(".")) {
                double v = Double.parseDouble(secand);
                double floor = Math.floor(v);
                dec = (int) floor;
            } else {
                dec = Integer.parseInt(timeFromGps.substring(4));
            }
            int timeDec = dec;
            if (timeDec < 16) {
                timeDec = 60 - (16 - timeDec);
                timeMin--;
            } else {
                timeDec = timeDec - 16;
            }
            if (timeMin < 10) {
                this.timeFromGps = timeHour + ":0" + timeMin + ":" + timeDec;
            } else {
                this.timeFromGps = timeHour + ":" + timeMin + ":" + timeDec;
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

            //这篱笆经纬度储存一下,如果已经存过了就进行替换.

            speed = Double.parseDouble(nr[7]);
            angle = Float.parseFloat(nr[8]);
            dateFromGps = "20" + nr[9].substring(0, 2) + "/" + nr[9].substring(2, 4) + "/" + nr[9].substring(4);

            if (wd != 0 && jd != 0) {
                preference.saveStartLatAndLon(wd + "", jd + "");
            }

            //在这里把数据存在文件里面去
            String timeIndex = this.timeFromGps + "-";
            String s = new String(b, 0, legth).replaceAll("\\s{1,}", "");
            String content = timeIndex + s;
            try {
                fos.write(content);
                fos.write("\r\n");
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            refreshHomePlane = true;
            publishProgress();
        }
    }

    private void beidouMsgNew_ga(File files, FileWriter fos, byte[] b, int legth) {
        //首先解析mc,也就是gps
        if (Msg_bd_ga.size() != 0) {
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
            String timeIndex = timeFromGps + "-";
            String s = new String(b, 0, legth).replaceAll("\\s{1,}", "");
            try {
                fos.write(timeIndex + s);
                fos.write("\r\n");
                fos.flush();
//                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("hahaha", "Msgga的size为0");
        }
        refreshHomePlane = true;
        publishProgress();
    }

    private void AdsbMsg_new() {
        //用来储存接下来有几个fs的集合.
        strfsList = new ArrayList<>();
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
        if (homePlane != null && homePlane.latLng != null && bean != null && bean.latLng != null) {
            double distance = DistanceUtil.getInstance().getDistance(homePlane.latLng, bean.latLng);
            ImpactWarningBean bean1 = warnning.safe_level(homePlane.latLng.latitude, homePlane.latLng.longitude, homePlane.flyHeight, homePlaneSpeedEast, homePlaneSpeedNorth, homePlane.upOrDownSpeed,
                    bean.latLng.latitude, bean.latLng.longitude, bean.flyHeight, speedEast, speedNorth, upDownSpeed,
                    Double.parseDouble(gaoJingSetInfo.caz_distance), Double.parseDouble(gaoJingSetInfo.caz_height), Double.parseDouble(gaoJingSetInfo.caz_time),
                    Double.parseDouble(gaoJingSetInfo.paz_distance), Double.parseDouble(gaoJingSetInfo.paz_height), Double.parseDouble(gaoJingSetInfo.paz_time), distance);

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


}
