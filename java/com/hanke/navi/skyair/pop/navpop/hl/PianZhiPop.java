package com.hanke.navi.skyair.pop.navpop.hl;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Nav;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.framwork.share.SharepreferenceHelper;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.HangLuBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.service.LandNavService;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.ArrayList;
import java.util.List;

import static com.hanke.navi.skyair.MyApplication.getMyApplication;

public class PianZhiPop extends PopupWindow implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Context context;
    private Switch bt_switch;
    private EditText tv_pianzhi;
    private Button bt_save;
    private List<HangLuBean> data_hl;
    private String hangxianName;
    private TextView pianzhi_start;
    private TextView pianzhi_end;
    public String[] pianzhiArr;
    private int pianzhi_startIndex;
    private int pianzhi_endIndex;
    List<LatLng> list = new ArrayList<>();
    List<Double> list_d = new ArrayList<>();
    Polyline pz_polyline = null;

    public PianZhiPop(Context context, List<HangLuBean> data, String hangxianName) {
        super(context);
        this.context = context;
        this.data_hl = data;
        this.hangxianName = hangxianName;
        initView();
        if (MainActivity.instence.pz_flag == 0) {
            bt_switch.setChecked(false);
        } else
            bt_switch.setChecked(true);
    }

    public PianZhiPop(Context context) {
        super(context);
    }

    public PianZhiPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PianZhiPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void initView() {
        View view = View.inflate(context, R.layout.pianzhi_direction, null);
        tv_pianzhi = (EditText) view.findViewById(R.id.tv_pianzhi);
        tv_pianzhi.setText(SharepreferenceHelper.getInstence(context).getPZjuli());
        tv_pianzhi.setSelection(tv_pianzhi.getText().length());
        bt_save = (Button) view.findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        if (MyApplication.getMyApplication().isPianzhiDoing) {
            //之前没有做偏置
            bt_save.setText("执行偏置");
        } else {
            bt_save.setText("取消偏置");
        }


        bt_switch = (Switch) view.findViewById(R.id.bt_switch);
        bt_switch.setOnCheckedChangeListener(this);

        pianzhi_start = (TextView) view.findViewById(R.id.pianzhi_start);
        pianzhi_start.setOnClickListener(this);
        pianzhi_end = (TextView) view.findViewById(R.id.pianzhi_end);
        pianzhi_end.setOnClickListener(this);

        pianzhiArr = new String[data_hl.size()];
        for (int x = 0; x < data_hl.size(); x++) {
            pianzhiArr[x] = data_hl.get(x).getHanglu();
        }

        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 15);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            this.showAtLocation(view, Gravity.CENTER, 0, 0);

        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.bt_switch:
                if (isChecked) {//开启   右
                    MainActivity.instence.pz_flag = 1;

                } else {// 左
                    MainActivity.instence.pz_flag = 0;
                }
                break;
        }


    }


    public void pzFangfa() {//  1海里=1852米
        //先将不偏置的点加进来
        for (int x = 0; x < pianzhi_startIndex; x++) {
            list.add(new LatLng(data_hl.get(x).weidu, data_hl.get(x).jingdu));
        }
        double pz_tv;
        String str = tv_pianzhi.getText().toString();
        SharepreferenceHelper.getInstence(context).setPZjuli(str);
        if (str == null || str.length() <= 0 || str.equals("0")) {
            pz_tv = 0;
        } else
            pz_tv = Double.parseDouble(str) * 1852;
        Log.e("jiao", "pz_tv = " + pz_tv);

        for (int i = pianzhi_startIndex; i <= pianzhi_endIndex; i++) {
            //如果在偏置起始点里面,才偏置这些点
            double wd_item = 0;
            double jd_item = 0;
            double gd_item = 0;

            double wds_item = 0;
            double jds_item = 0;
            double gds_item = 0;

            if (i == 0) {
                wd_item = data_hl.get(i + 1).getWeidu();
                jd_item = data_hl.get(i + 1).getJingdu();
                gd_item = data_hl.get(i + 1).getGaodu();

                wds_item = data_hl.get(i).getWeidu();
                jds_item = data_hl.get(i).getJingdu();
                gds_item = data_hl.get(i).getGaodu();
            } else {
                wd_item = data_hl.get(i - 1).getWeidu();
                jd_item = data_hl.get(i - 1).getJingdu();
                gd_item = data_hl.get(i - 1).getGaodu();

                wds_item = data_hl.get(i).getWeidu();
                jds_item = data_hl.get(i).getJingdu();
                gds_item = data_hl.get(i).getGaodu();
            }

            double dajiaodu = getMyApplication().nav.CAz(wd_item, jd_item, gd_item, wds_item, jds_item, gds_item);//大角度




//            Log.e("jiao", "传参角大角度" + i + " = " + dajiaodu);
//            while (dajiaodu > 360) {
//                dajiaodu = dajiaodu - 360;
//            }
            double chuancanjiao = 0;
            if (MainActivity.instence.pz_flag == 0) {
//                //左偏
////                if (dajiaodu <= 90) {
////                    chuancanjiao = 90 - dajiaodu;
////                } else if (dajiaodu > 90 && dajiaodu <= 270) {
////                    chuancanjiao = 270 - dajiaodu;
////                } else {
////                    chuancanjiao = 450 - dajiaodu;
////                }
//
//
//                if (dajiaodu > 90) {
//                    chuancanjiao = 90 - dajiaodu;
//                } else {
//                    chuancanjiao = 270 - dajiaodu;
//                }
                chuancanjiao  =  dajiaodu-90;
            } else {
//                //右偏
////                if (dajiaodu > 90) {
////                    chuancanjiao = 450 - dajiaodu;
////                } else {
////                    chuancanjiao = 270 - dajiaodu;
////                }
//
//
//                if (dajiaodu <= 90) {
//                    chuancanjiao = 90 - dajiaodu;
//                } else if (dajiaodu > 90 && dajiaodu <= 270) {
//                    chuancanjiao = 270 - dajiaodu;
//                } else {
//                    chuancanjiao = 450 - dajiaodu;
//                }
                chuancanjiao  =  dajiaodu+90;
            }
            if(chuancanjiao>=360)
                chuancanjiao -=360;

            if (chuancanjiao<0)
                chuancanjiao +=360;

//            Log.i("hahaha", "传参角" + chuancanjiao);
            double[] aa = getMyApplication().nav.PosVd11(wds_item, jds_item, chuancanjiao, pz_tv);
            double lat = aa[0];
            Log.e("jiao", "lat" + i + " = " + lat);
            double lot = aa[1];
            Log.e("jiao", "lot" + i + " = " + lot);
            list.add(new LatLng(lat, lot));
            Log.i("hahaha", "添加点2");
        }
        //将不偏置的点加进去
        for (int x = pianzhi_endIndex + 1; x < data_hl.size(); x++) {
            list.add(new LatLng(data_hl.get(x).weidu, data_hl.get(x).jingdu));
        }
    }

    public void pzXian() {
        int currentIndex = LandNavService.getService().currentIndex;
        for (int x = 0; x < MyApplication.getMyApplication().hlMarkerList.size(); x++) {
            MyApplication.getMyApplication().hlMarkerList.get(x).remove();
            Log.i("hahaha", "删掉了之前添加的");
        }
        for (int x = 0; x < MainActivity.instence.listPolyline.size(); x++) {
            MainActivity.instence.listPolyline.get(x).remove();
        }
        MyApplication.getMyApplication().hlMarkerList.clear();
        MainActivity.instence.listPolyline.clear();


        for (int i = 0; i < list.size(); i++) {
            Log.i("hahaha", "for执行次数");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(list.get(i).latitude, list.get(i).longitude));
            markerOptions.draggable(false);
            markerOptions.setFlat(false);
            markerOptions.perspective(true);
            markerOptions.period(1);//值越小刷新的越快
            if (currentIndex <= 1) {
                if (i == 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.biaolan)));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.biaobai)));
                }
            } else {
                if (i == currentIndex) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.biaofen)));
                } else if (i > currentIndex) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.biaobai)));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.biaolan)));
                }
            }

            markerOptions.anchor(0.5f, 0.5f);//默认（0.5f, 1.0f）水平居中，垂直下对齐
            Marker marker = BaseActivity.instance.aMap.addMarker(markerOptions);//添加图标
            MyApplication.getMyApplication().hlMarkerList.add(marker);
            if (i != list.size() - 1) {
                PolylineOptions polylineOptions = new PolylineOptions();
                if (currentIndex <= 1) {
                    polylineOptions.color(Color.WHITE);
                } else {
                    if (currentIndex - i == 1) {
                        polylineOptions.color(Color.RED);
                    } else if (currentIndex - i > 1) {
                        polylineOptions.color(Color.BLUE);
                    } else if (currentIndex <= i) {
                        polylineOptions.color(Color.WHITE);
                    }
                }
                polylineOptions.setDottedLine(true);
                Log.i("polyline", "lat" + list.get(i).latitude + "lon" + list.get(i).longitude);
                Polyline polyline = BaseActivity.instance.aMap.addPolyline(polylineOptions.add(new LatLng(list.get(i).latitude, list.get(i).longitude), new LatLng(list.get(i + 1).latitude, list.get(i + 1).longitude)).width(5));
                MainActivity.instence.listPolyline.add(polyline);
            }

        }

        //画完了之后,应该重新计算.到达时间,和剩余时间.
        PlaneInfoBean homePlane = MyApplication.getMyApplication().homePlane;
        if (homePlane != null && homePlane.latLng != null) {
//            Marker marker = list.get(currentIndex);
//            int nextPointTime = (DistanceUtil.getInstance().caculateArriveTime(homePlane.latLng, list.get(currentIndex), homePlane.flySpeed));
            Nav nav = new Nav();
            HangLuBean hangLuBean = data_hl.get(currentIndex);
//                                    nextPointTime = (DistanceUtil.getInstance().caculateArriveTime(homeLatlng, newmarker.getPosition(), bean.flySpeed));
            double[] ceta = nav.CETA(homePlane.latLng.latitude, homePlane.latLng.longitude, homePlane.flyHeight,
                    hangLuBean.getWeidu(), hangLuBean.getJingdu(), hangLuBean.getGaodu(), homePlane.flySpeed, homePlane.upOrDownSpeed, 360 - homePlane.flyAngle);
            Log.i("ceta33333", "hour" + ceta[0] + "min" + ceta[1] + "sec" + ceta[2]);
//            MyApplication.getMyApplication().nextPointTime = nextPointTime;
            GaojingPreference preference = new GaojingPreference(context);
            if (MyApplication.getMyApplication().isBack) {
                preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps_lookback);
            } else {
                preference.setNextTime(ceta, MyApplication.getMyApplication().timeFromGps);
            }

            int totalTime = 0;
            List<Marker> markers = MyApplication.getMyApplication().hlMarkerList.subList(currentIndex, MyApplication.getMyApplication().hlMarkerList.size());
            if (MyApplication.getMyApplication().hlMarkerList.size() > 2) {
                //计算还剩下的点之间的距离.
                double allHLPointTotalDistance_partone = DistanceUtil.getInstance().getAllHLPointTotalDistance(markers);
                double allHLPointTotalDistance_partwo = DistanceUtil.getInstance().getDistance(homePlane.latLng, list.get(currentIndex));
                double totalDistance = allHLPointTotalDistance_partone + allHLPointTotalDistance_partwo;
                //剩余时间
                totalTime = (int) (totalDistance / (homePlane.flySpeed / 3.6));
            } else {
                totalTime = (int) (ceta[0] * 3600 + ceta[1] * 60 + ceta[2]);
            }

            MyApplication.getMyApplication().remainderTime = totalTime;
        }
    }


    public void pzPolyline() {
        if (MainActivity.instence.pzlist_polyline.size() != 0) {
            for (int i = 0; i < MainActivity.instence.pzlist_polyline.size(); i++) {
                MainActivity.instence.pzlist_polyline.get(i).remove();
            }
            MainActivity.instence.pzlist_polyline.clear();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                if (MyApplication.getMyApplication().isPianzhiDoing) {
                    //这里需要判断之前已经选择了起始点和结束点,否则会造成崩溃
                    if (!pianzhi_start.getText().toString().equals("选择起始点") && !pianzhi_end.getText().toString().equals("选择终点")) {
                        //代表之前没有执行偏置
                        Intent intent = new Intent(context, LandNavService.class);
                        context.stopService(intent);
                        //开始执行偏置
                        MainActivity.instence.flag_zx = false;
                        MyApplication.getMyApplication().isPianzhiDoing = false;
                        MyApplication.getMyApplication().flyingPlanName = hangxianName;
                        HLPop.instence.hangxian_pianzhi.setTextColor(Color.YELLOW);
                        HLPop.instence.hangxian_zhixing.setTextColor(Color.YELLOW);
                        pzPolyline();
                        pzFangfa();
                        pzXian();
                        dismissPopWindow();
                        //然后startservice,启动着陆service
                        MainActivity.instence.flag_zx = false;
                        Intent intent2 = new Intent(context, LandNavService.class);
                        context.startService(intent2);
                    } else {
                        Toast.makeText(context, "请选择起始点和终点再执行偏置", Toast.LENGTH_SHORT).show();
                    }
                    break;
                } else {
                    //代表之前正在执行偏置,取消的话,我们应该继续执行普通的导航
                    //这个时候应该重新清除掉之前的线,画普通导航的线.
                    dismissPopWindow();
                    MyApplication.getMyApplication().isPianzhiDoing = true;
                    HLPop.instence.hangxian_pianzhi.setTextColor(Color.parseColor("#26cfe9"));
                    for (int x = 0; x < MyApplication.getMyApplication().hlMarkerList.size(); x++) {
                        MyApplication.getMyApplication().hlMarkerList.get(x).remove();
                        Log.i("hahaha", "删掉了之前添加的");
                    }
                    for (int x = 0; x < MainActivity.instence.listPolyline.size(); x++) {
                        MainActivity.instence.listPolyline.get(x).remove();
                    }
                    MyApplication.getMyApplication().hlMarkerList.clear();
                    MainActivity.instence.listPolyline.clear();

                    HLPop.instence.setGuiJi(false);

                }
                break;

            case R.id.pianzhi_start:
                //点击的话,弹出dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog alertDialog = builder.create();
                View inflate = View.inflate(context, R.layout.choose_airport, null);
                alertDialog.setView(inflate);
                ListView viewById = (ListView) inflate.findViewById(R.id.lv_choose_airport);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.style_spinner, pianzhiArr);
                arrayAdapter.setDropDownViewResource(R.layout.select_dialog_singlechoice);
                viewById.setAdapter(arrayAdapter);

                viewById.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pianzhi_start.setText(pianzhiArr[position]);
                        pianzhi_startIndex = position;
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

                break;

            case R.id.pianzhi_end:
                //点击的话,弹出dialog
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                final AlertDialog alertDialog2 = builder2.create();
                View inflate2 = View.inflate(context, R.layout.choose_airport, null);
                alertDialog2.setView(inflate2);
                ListView viewById2 = (ListView) inflate2.findViewById(R.id.lv_choose_airport);
                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(context, R.layout.style_spinner, pianzhiArr);
                arrayAdapter2.setDropDownViewResource(R.layout.select_dialog_singlechoice);
                viewById2.setAdapter(arrayAdapter2);

                viewById2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pianzhi_end.setText(pianzhiArr[position]);
                        pianzhi_endIndex = position;
                        alertDialog2.dismiss();
                    }
                });

                alertDialog2.show();

                break;
        }
    }
}

