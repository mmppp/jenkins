package com.hanke.navi.skyair.pop.setpop;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.AirportSetBean;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.pop.jcpop.JinjinPop;
import com.hanke.navi.skyair.pop.jcpop.ZhuoLuPop;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.DistanceUtil;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.ArrayList;

public class JiChangPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    private Context context;
    public Button jichang_save;
    private EditText plan_run_angle;
    private EditText plane_down_angle;
    private EditText faf_distance;
    private EditText set_lat;
    private EditText tv_airset_lon;
    private EditText set_height;
    private TextView join_in_turn_left;
    private TextView join_in_turn_right;
    private String[] airportNameStrs = new String[]{"成都双流机场", "绵阳南郊机场", "北京首都国际机场", "上海浦东机场", "洛阳北郊机场", "重庆江北国际机场"};
    private ArrayList<AirportSetBean> falseData;
    private TextView tv_airport_name;
    private boolean join_in_direction;//true代表左边 false代表右边
    private GaojingPreference preference;

    public JiChangPop(Context context) {
        this(context, null);
    }

    public JiChangPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiChangPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initData();
        MainActivity.instence.listPop.add(this);
    }

    public void initView() {
        preference = new GaojingPreference(context);

        View view = View.inflate(context, R.layout.jichang_set, null);
        this.setContentView(view);
        this.setWidth(4 * MyApplication.getMyApplication().getWidth() / 8);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);

        initData();

        tv_airport_name = (TextView) view.findViewById(R.id.tv_airport_name);
        tv_airport_name.setOnClickListener(this);

        //跑到航向
        plan_run_angle = (EditText) view.findViewById(R.id.plan_run_angle);
        //下滑角
        plane_down_angle = (EditText) view.findViewById(R.id.plane_down_angle);
        //FAF距离
        faf_distance = (EditText) view.findViewById(R.id.faf_distance);
        //维度
        set_lat = (EditText) view.findViewById(R.id.set_lat);
        //经度
        tv_airset_lon = (EditText) view.findViewById(R.id.set_lon);
        //高度
        set_height = (EditText) view.findViewById(R.id.set_height);
        //进场左
        join_in_turn_left = (TextView) view.findViewById(R.id.join_in_turn_left);
        //进场右
        join_in_turn_right = (TextView) view.findViewById(R.id.join_in_turn_right);

        jichang_save = (Button) view.findViewById(R.id.jichang_save);
        jichang_save.setOnClickListener(this);

        AirportSetBean airportSet = preference.getAirportSet();
        if (!TextUtils.isEmpty(airportSet.airportName)) {
            tv_airport_name.setText(airportSet.airportName);
            plan_run_angle.setText(airportSet.runAngle);
            plane_down_angle.setText(airportSet.planeDownAngle);
            faf_distance.setText(airportSet.fafDistance);
            set_lat.setText(airportSet.airportLat);
            tv_airset_lon.setText(airportSet.airportLon);
            set_height.setText(airportSet.airportHeight);
            if (airportSet.joininLeftOrRight.equals("左")) {
                join_in_turn_left.setTextColor(Color.parseColor("#1AD5FD"));
                join_in_direction = true;
            } else {
                join_in_turn_right.setTextColor(Color.parseColor("#1AD5FD"));
                join_in_direction = false;
            }

        }


    }

    public void initData() {
        falseData = new ArrayList<>();
        for (int x = 0; x < airportNameStrs.length; x++) {
            AirportSetBean bean = new AirportSetBean();
            bean.airportHeight = 1500 + "";
            bean.airportLat = 30.341 + "";
            bean.airportLon = 103.55 + "";
            bean.airportName = airportNameStrs[x];
            bean.fafDistance = 2000 + "";
            bean.joininLeftOrRight = "left";
            bean.runAngle = 90 + "";
            bean.planeDownAngle = 30 + "";

            falseData.add(bean);
        }

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

    class ViewHolder{
        TextView airportName;
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return falseData.size();
        }

        @Override
        public Object getItem(int position) {
            return falseData == null ? 0 : falseData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = View.inflate(context,R.layout.item_dialog,null);
                holder.airportName = (TextView) convertView.findViewById(R.id.tv_airport_name);

                convertView.setTag(holder);

            }else{
               holder = (ViewHolder) convertView.getTag();
            }

            holder.airportName.setText(falseData.get(position).airportName);

            return convertView;
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jichang_save:

                //这里了只保存一个机场的信息,那么这里就存到sharedperference里面去就可以了.
                //机场名称
                String str_airport_name = tv_airport_name.getText().toString();
                //跑到航向
                String str_plan_run_angle = plan_run_angle.getText().toString().trim();
                //下滑角
                String str_plane_down_angle = plane_down_angle.getText().toString().trim();
                //faf距离
                String str_faf_distance = faf_distance.getText().toString().trim();
                //维度
                String str_set_lat = set_lat.getText().toString().trim();
                //经度
                String str_airset_lon = tv_airset_lon.getText().toString().trim();
                //高度
                String str_set_height = set_height.getText().toString().trim();
                //进场转弯方向
                String str_jion_in_direction = join_in_direction ? "左" : "右";

                AirportSetBean bean = new AirportSetBean();
                bean.airportName = str_airport_name;
                bean.runAngle = str_plan_run_angle;
                bean.planeDownAngle = str_plane_down_angle;
                bean.fafDistance = str_faf_distance;
                bean.airportLat = str_set_lat;
                bean.airportLon = str_airset_lon;
                bean.airportHeight = str_set_height;
                bean.joininLeftOrRight = str_jion_in_direction;

                preference.setAirport(bean);


                ZhuoLuPop zhuoLuPop = new ZhuoLuPop(MyApplication.getAppContext(), MyApplication.getMyApplication().homePlane.flyHeight);
                Log.i("hahaha", MyApplication.getMyApplication().homePlane.flyHeight + "高度高度");
                zhuoLuPop.showPopWindow(MainActivity.instence.mapView);

                JinjinPop jinjinPop = new JinjinPop(MyApplication.getAppContext(), 4000);
                jinjinPop.showPopWindow(MainActivity.instence.mapView);
                dismissPopWindow();

                break;
            case R.id.tv_airport_name:
                //点击的话,弹出dialog
                View inflate = View.inflate(context, R.layout.dialog_layout, null);
                ListView airport_choose_lv = (ListView) inflate.findViewById(R.id.airport_choose_lv);
//                ListView viewById = (ListView) inflate.findViewById(R.id.lv_choose_airport);
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.style_spinner, airportNameStrs);
//                arrayAdapter.setDropDownViewResource(R.layout.select_dialog_singlechoice);
                MyAdapter adapter = new MyAdapter();
                airport_choose_lv.setAdapter(adapter);

                final Dialog dialog = new Dialog(MainActivity.instence);
                dialog.setContentView(inflate);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER);

                WindowManager m = MainActivity.instence.getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
                p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
                dialogWindow.setAttributes(p);
                dialogWindow.setAttributes(lp);

                airport_choose_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AirportSetBean airportSetBean = falseData.get(position);
                        plan_run_angle.setText(airportSetBean.runAngle);
                        plane_down_angle.setText(airportSetBean.planeDownAngle);
                        faf_distance.setText(airportSetBean.fafDistance);
                        set_lat.setText(airportSetBean.airportLat);
                        tv_airset_lon.setText(airportSetBean.airportLon);
                        set_height.setText(airportSetBean.airportHeight);
                        if (airportSetBean.joininLeftOrRight.equals("left")) {
                            join_in_turn_left.setTextColor(Color.parseColor("#1AD5FD"));
                            join_in_direction = true;
                        } else {
                            join_in_turn_right.setTextColor(Color.parseColor("#1AD5FD"));
                            join_in_direction = false;
                        }
                        tv_airport_name.setText(airportSetBean.airportName);
                        dialog.dismiss();
                    }
                });

                dialog.show();
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
                //得到x y方向上面分别的移动距离.
                offsetX = (int) event.getRawX() - orgX;
                offsetY = (int) event.getRawY() - orgY;
                //这里传入-1是因为我们只是改变移动,并不改变宽高,所以传入-1
                this.update(offsetX, offsetY, -1, -1, true);
                break;
        }
        return true;
    }

}
