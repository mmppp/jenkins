package com.hanke.navi.skyair.pop.msgpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.framwork.arith.Warnning;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.msgpop.msgadapter.HangBanGJListViewAdapter;
import com.hanke.navi.skyair.pop.msgpop.msgadapter.HangBanJTListViewAdapter;
import com.hanke.navi.skyair.pop.msgpop.msgbean.HangBanGJBean;
import com.hanke.navi.skyair.pop.msgpop.msgbean.HangBanJTBean;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MSGPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    private Context context;
    private ListView lv_jiaotong, lv_gaojing;
    public List<HangBanJTBean> data_jt;
    public List<HangBanGJBean> data_gj;
    public HangBanJTListViewAdapter hangBanJTListViewAdapter;
    public HangBanGJListViewAdapter hangBanGJListViewAdapter;
    private TextView tv_benji, tv_jiaotong, tv_gaojing;
    private TextView[] textViews;
    private TextView jt_msg_num, gj_msg_num;
    public TextView msg_ed_jingdu, msg_ed_weidu, msg_ed_sudu, msg_ed_gaodu, msg_ed_hangxiang;
    private LinearLayout msg_benji, msg_jiaotong, msg_gaojing;
    private LinearLayout[] linearLayouts;
    //    private double longitude, latitude, haiba;
    private double speed, height;
    //    public double bearing;
    public PlaneInfoBean bean;
    public DecimalUtil decimalUtil;
    private Warnning warnningUtil;

    public MSGPop(Context context, PlaneInfoBean bean) {
//        this(context, null);
        MainActivity.instence.listPop.add(this);
        this.context = context;
        this.bean = bean;
        decimalUtil = new DecimalUtil();
        initView();
    }

    public MSGPop(Context context) {
        super(context);
    }

    public MSGPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MSGPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView() {
        View view = View.inflate(context, R.layout.bt_msg, null);
        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 10);
        this.setHeight(MyApplication.getMyApplication().getHeight() / 2);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);

        tv_benji = (TextView) view.findViewById(R.id.tv_benji);
        tv_benji.setOnClickListener(this);
        tv_jiaotong = (TextView) view.findViewById(R.id.tv_jiaotong);
        tv_jiaotong.setOnClickListener(this);
        tv_gaojing = (TextView) view.findViewById(R.id.tv_gaojing);
        tv_gaojing.setOnClickListener(this);
        //本机信息
        msg_benji = (LinearLayout) view.findViewById(R.id.msg_benji);
        msg_ed_jingdu = (TextView) view.findViewById(R.id.msg_ed_jingdu);
        msg_ed_weidu = (TextView) view.findViewById(R.id.msg_ed_weidu);
        msg_ed_hangxiang = (TextView) view.findViewById(R.id.msg_ed_hangxiang);
        msg_ed_gaodu = (TextView) view.findViewById(R.id.msg_ed_gaodu);
        msg_ed_sudu = (TextView) view.findViewById(R.id.msg_ed_sudu);

        //显示之前储存了的信息
        GaojingPreference preference = new GaojingPreference(context);
        String startHeight = preference.getStartHeight();
        String startSpeed = preference.getStartSpeed();
        String[] latAndLon = preference.getLatAndLon();
        String angle = preference.getAngle();

        if (!TextUtils.isEmpty(latAndLon[0]) && !TextUtils.isEmpty(latAndLon[1])) {
            msg_ed_jingdu.setText(DecimalUtil.remainDecimal(Double.parseDouble(latAndLon[0]), 2));
            msg_ed_weidu.setText(DecimalUtil.remainDecimal(Double.parseDouble(latAndLon[1]), 2));
            msg_ed_hangxiang.setText(angle);
            msg_ed_gaodu.setText(startHeight);
            msg_ed_sudu.setText(startSpeed);
        }

        //交通信息
        msg_jiaotong = (LinearLayout) view.findViewById(R.id.msg_jiaotong);
        msg_jiaotong.setVisibility(View.GONE);
        lv_jiaotong = (ListView) view.findViewById(R.id.lv_jiaotong);
        jt_msg_num = (TextView) view.findViewById(R.id.jt_msg_num);
        //告警信息
        msg_gaojing = (LinearLayout) view.findViewById(R.id.msg_gaojing);
        msg_gaojing.setVisibility(View.GONE);
        lv_gaojing = (ListView) view.findViewById(R.id.lv_gaojing);
        gj_msg_num = (TextView) view.findViewById(R.id.gj_msg_num);

        warnningUtil = new Warnning(context);

        hangBanJTListViewAdapter = new HangBanJTListViewAdapter(context);
        hangBanGJListViewAdapter = new HangBanGJListViewAdapter(context);
        initData();
    }

    public void initData() {
        textViews = new TextView[]{tv_benji, tv_jiaotong, tv_gaojing};
        linearLayouts = new LinearLayout[]{msg_benji, msg_jiaotong, msg_gaojing};
        if (bean.latLng != null) {
            msg_ed_jingdu.setText(decimalUtil.remainDecimal(bean.latLng.longitude, 3));
            msg_ed_weidu.setText(decimalUtil.remainDecimal(bean.latLng.latitude, 3));
            //这里的这个,不是发过来的,而是高德需要转动的角度,所以需要用360-这个度数,就是发过来的度数
            if (bean.flyAngle != 360) {
                msg_ed_hangxiang.setText(decimalUtil.remainDecimal(360 - bean.flyAngle, 2));
            } else {
                msg_ed_hangxiang.setText("0");
            }
            msg_ed_gaodu.setText("" + bean.flyHeight);
            msg_ed_sudu.setText("" + bean.flySpeed);
            homePlanTimer.schedule(homePlanTask, 0, 500);
        }
        transTimer.schedule(transTask, 0, 1000);
        alarmTimer.schedule(alarmTask, 0, 1000);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    ArrayList<PlaneInfoBean> data = (ArrayList<PlaneInfoBean>) msg.obj;
                    jt_msg_num.setText(data.size() + "");
                    hangBanJTListViewAdapter.notifyDataSetInvalidated();
                    break;

                case 3:
                    //刷新一下textview的显示内容
                    msg_ed_jingdu.setText(decimalUtil.remainDecimal(bean.latLng.longitude, 3));
                    msg_ed_weidu.setText(decimalUtil.remainDecimal(bean.latLng.latitude, 3));
                    if (bean.flyAngle != 360) {
                        msg_ed_hangxiang.setText(decimalUtil.remainDecimal(360 - bean.flyAngle, 2));
                    } else {
                        msg_ed_hangxiang.setText("0");
                    }
                    msg_ed_gaodu.setText("" + bean.flyHeight);
                    msg_ed_sudu.setText("" + bean.flySpeed);
                    break;
                case 4:
                    HashMap<String, HangBanGJBean> result = (HashMap<String, HangBanGJBean>) msg.obj;
                    HangBanGJBean homeplane_ky = result.get("homeplane_ky");
                    HangBanGJBean adsb = result.get("adsb");
                    int resultSize = result.size();
                    if (homeplane_ky !=null && TextUtils.isEmpty(homeplane_ky.jinggaoxinxi_gj)) {
                        //因为如果是本机的空域和撞地没有告警信息的话,那么
                        resultSize = resultSize - 1;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("homeplane_ky");
                    }
                    if (adsb !=null && TextUtils.isEmpty(adsb.hangbanhao_gj)) {
                        //因为如果是本机的空域和撞地没有告警信息的话,那么
                        resultSize = resultSize - 1;
                        MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adsb");
                    }
                    gj_msg_num.setText(resultSize + "");
                    hangBanGJListViewAdapter.setHangBanGJData(result);
                    hangBanGJListViewAdapter.notifyDataSetChanged();
                    break;
                case 1001:
                    //这个时候交通信息为空了
                    jt_msg_num.setText("0");
                    ArrayList<PlaneInfoBean> data1 = new ArrayList<>();
                    hangBanJTListViewAdapter.setHangBanJTData(data1);
                    hangBanJTListViewAdapter.notifyDataSetChanged();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    //控制交通信息的定时器
    Timer transTimer = new Timer();
    TimerTask transTask = new TimerTask() {
        public void run() {
            //每隔一秒钟,去拿一下最新的数据,然后显示.
            ArrayList<PlaneInfoBean> data = new ArrayList<PlaneInfoBean>();
            HashMap<String, PlaneInfoBean> latLngHashMap = MyApplication.getMyApplication().latLngHashMap;
            if (latLngHashMap != null) {
                Set<Map.Entry<String, PlaneInfoBean>> entries = latLngHashMap.entrySet();
                Iterator<Map.Entry<String, PlaneInfoBean>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    PlaneInfoBean bean = (PlaneInfoBean) entry.getValue();
                    if (bean.latLng != null && MyApplication.getMyApplication().homePlaneLatlng != null) {
                        double distanceWithHomePlane = DistanceUtil.getInstance().getDistance(bean.latLng, MyApplication.getMyApplication().homePlaneLatlng);
                        bean.distanceWithHomePlane = distanceWithHomePlane;
                        data.add(bean);
                    }
                }
                //这里需要对数据进行一个排序
                Collections.sort(data);
                hangBanJTListViewAdapter.setHangBanJTData(data);

                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = data;
                handler.sendMessage(message);
            } else {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = data;
                handler.sendMessage(message);
            }
        }
    };

    //控制告警信息的定时器
    Timer alarmTimer = new Timer();
    TimerTask alarmTask = new TimerTask() {
        @Override
        public void run() {
            //这里需要传入本机之外其他飞机的经纬度和速度信息,去计算是否需要生成报警信息.
            //这里的意思就是有多少架飞机,就显示listview打的多少个item,然后每个item用他们的信息去通过算法计算出来得出结果,显示就可以了
            HashMap<String, HangBanGJBean> homeplaneWarningInfoList = MyApplication.getMyApplication().homeplaneWarningInfoList;
            Message message = handler.obtainMessage();
            message.what = 4;
            message.obj = homeplaneWarningInfoList;
            handler.sendMessage(message);
        }
    };

    //控制本机信息刷新的定时器
    Timer homePlanTimer = new Timer();
    TimerTask homePlanTask = new TimerTask() {
        @Override
        public void run() {
            PlaneInfoBean bean = MyApplication.getMyApplication().homePlane;

            Message msg = new Message();
            msg.obj = bean;
            msg.what = 3;
            handler.sendMessage(msg);
        }
    };


    public void showPopWindow(View view) {
        if (!isShowing()) {
            this.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            if (transTimer != null) {
                transTimer.cancel();
                transTimer = null;
            }
            if (alarmTimer != null) {
                alarmTimer.cancel();
                alarmTimer = null;
            }
            if (homePlanTimer != null) {
                homePlanTimer.cancel();
                homePlanTimer = null;
            }

            this.dismiss();
        }
    }

    public void setSelect(int position) {
        for (int i = 0; i < textViews.length; i++) {
            if (i == position) {
                textViews[i].setTextColor(Color.parseColor("#26cfe9"));
                textViews[i].setEnabled(false);
                textViews[i].setTypeface(Typeface.DEFAULT_BOLD);
                linearLayouts[i].setVisibility(View.VISIBLE);
            } else {
                textViews[i].setTextColor(Color.BLACK);
                textViews[i].setEnabled(true);
                textViews[i].setTypeface(Typeface.DEFAULT);
                linearLayouts[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_benji:
                setSelect(0);
                break;
            case R.id.tv_jiaotong:
                data_jt = new ArrayList<>();
                // 这里需要显示的是飞机markermap的size
                ArrayList<PlaneInfoBean> data = new ArrayList<PlaneInfoBean>();
                HashMap<String, PlaneInfoBean> latLngHashMap = MyApplication.getMyApplication().latLngHashMap;
                if (latLngHashMap != null) {
                    jt_msg_num.setText(latLngHashMap.size() + "");
                    Set<Map.Entry<String, PlaneInfoBean>> entries = latLngHashMap.entrySet();
                    Iterator<Map.Entry<String, PlaneInfoBean>> iterator = entries.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        PlaneInfoBean bean = new PlaneInfoBean();
                        bean = (PlaneInfoBean) entry.getValue();
                        data.add(bean);
                    }
                }

                lv_jiaotong.setAdapter(hangBanJTListViewAdapter);
                setSelect(1);
                break;
            case R.id.tv_gaojing:
                //取消交通的定时器,开启告警的定时器
                HangBanGJBean homeplane_ky = MyApplication.getMyApplication().homeplaneWarningInfoList.get("homeplane_ky");
                int resultSize = MyApplication.getMyApplication().homeplaneWarningInfoList.size();
                HangBanGJBean adsb = MyApplication.getMyApplication().homeplaneWarningInfoList.get("adsb");
                if (homeplane_ky !=null && TextUtils.isEmpty(homeplane_ky.jinggaoxinxi_gj)) {
                    //因为如果是本机的空域和撞地没有告警信息的话,那么
                    resultSize = resultSize - 1;
                    MyApplication.getMyApplication().homeplaneWarningInfoList.remove("homeplane_ky");
                }
                if (adsb !=null && TextUtils.isEmpty(adsb.hangbanhao_gj)) {
                    //因为如果是本机的空域和撞地没有告警信息的话,那么
                    resultSize = resultSize - 1;
                    MyApplication.getMyApplication().homeplaneWarningInfoList.remove("adbs");
                }

                gj_msg_num.setText(resultSize + "");
                hangBanGJListViewAdapter.setHangBanGJData(MyApplication.getMyApplication().homeplaneWarningInfoList);
                lv_gaojing.setAdapter(hangBanGJListViewAdapter);
                setSelect(2);
                break;
        }
    }


    int orgX, orgY;
    int offsetX, offsetY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                orgX = (int) event.getX();
                orgY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = (int) event.getRawX() - orgX;
                offsetY = (int) event.getRawY() - orgY;
                this.update(offsetX, offsetY, -1, -1, true);
                break;
        }
        return true;
    }


}
