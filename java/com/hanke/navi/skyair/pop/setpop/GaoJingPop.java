package com.hanke.navi.skyair.pop.setpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.GaojingSetBean;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.GaojingPreference;

public class GaoJingPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    private Context context;
    private Spinner spinner;
    private EditText gaojing_juli, gaojing_gaodu, gaojing_time, gaojing_ky_yj_time, gaojing_zd_yj_time;
    private TextView[] textViews;
    private TextView gaojing_caz, gaojing_pal;
    private EditText caz_input;
    private EditText paz_input;
    private EditText caz_height;
    private EditText paz_height;
    private EditText airspace_warning_time_input;
    private EditText impact_land_warning_time_input;
    private TextView gaojing_save;
    private GaojingPreference gaojingPreference;
    private EditText caz_time;
    private EditText paz_time;

    public GaoJingPop(Context context) {
        this(context, null);
    }

    public GaoJingPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GaoJingPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        MainActivity.instence.listPop.add(this);
        initView();
    }

    public void initView() {

        View view = View.inflate(context, R.layout.gaojingset_new, null);
        gaojingPreference = new GaojingPreference(context);
        this.setContentView(view);
        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 13);
        this.setHeight(MyApplication.getMyApplication().getHeight() / 3 + 50);
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

//        gaojing_juli = (EditText) view.findViewById(R.id.gaojing_juli);
//        gaojing_gaodu = (EditText) view.findViewById(R.id.gaojing_gaodu);
//        gaojing_time = (EditText) view.findViewById(R.id.gaojing_time);
//        gaojing_ky_yj_time = (EditText) view.findViewById(R.id.gaojing_ky_yj_time);
//        gaojing_zd_yj_time = (EditText) view.findViewById(R.id.gaojing_zd_yj_time);
//        gaojing_caz = (TextView) view.findViewById(R.id.gaojing_caz);
//        gaojing_pal = (TextView) view.findViewById(R.id.gaojing_pal);
//        gaojing_save = (Button) view.findViewById(R.id.gaojing_save);


        //红色的圈
        caz_input = (EditText) view.findViewById(R.id.caz_input);
        //黄色的圈
        paz_input = (EditText) view.findViewById(R.id.paz_input);
        //红色的高度
        caz_height = (EditText) view.findViewById(R.id.caz_height);
        //黄色的高度
        paz_height = (EditText) view.findViewById(R.id.paz_height);

        caz_time = (EditText) view.findViewById(R.id.caz_time);
        paz_time = (EditText) view.findViewById(R.id.paz_time);

        //空域超限预警时间
        airspace_warning_time_input = (EditText) view.findViewById(R.id.airspace_warning_time_input);

        //防撞地预警时间
        impact_land_warning_time_input = (EditText) view.findViewById(R.id.impact_land_warning_time_input);

        //保存按钮
        gaojing_save = (TextView) view.findViewById(R.id.gaojing_save);
        gaojing_save.setOnClickListener(this);

        //从sp里面拿到之前保存的内容,如果没有内容的话,就填入默认的值.
        GaojingSetBean gaoJingSetInfo = gaojingPreference.getGaoJingSetInfo();
        if (TextUtils.isEmpty(gaoJingSetInfo.caz_distance)) {
            caz_input.setText("6000");
        } else {
            caz_input.setText(gaoJingSetInfo.caz_distance);
        }
        if (TextUtils.isEmpty(gaoJingSetInfo.paz_distance)) {
            paz_input.setText("12000");
        } else {
            paz_input.setText(gaoJingSetInfo.paz_distance);
        }

        if (TextUtils.isEmpty(gaoJingSetInfo.caz_height)) {
            caz_height.setText("300");
        } else {
            caz_height.setText(gaoJingSetInfo.caz_height);
        }
        if (TextUtils.isEmpty(gaoJingSetInfo.paz_height)) {
            paz_height.setText("600");
        } else {
            paz_height.setText(gaoJingSetInfo.paz_height);
        }

        if (TextUtils.isEmpty(gaoJingSetInfo.caz_time)) {
            caz_time.setText("60");
        } else {
            caz_time.setText(gaoJingSetInfo.caz_time);
        }
        if (TextUtils.isEmpty(gaoJingSetInfo.paz_time)) {
            paz_time.setText("300");
        } else {
            paz_time.setText(gaoJingSetInfo.paz_time);
        }

        if (TextUtils.isEmpty(gaoJingSetInfo.airspace_warning_time)) {
            airspace_warning_time_input.setText("60");
        } else {
            airspace_warning_time_input.setText(gaoJingSetInfo.airspace_warning_time);
        }
        if (TextUtils.isEmpty(gaoJingSetInfo.impact_land_warning_time)) {
            impact_land_warning_time_input.setText("60");
        } else {
            impact_land_warning_time_input.setText(gaoJingSetInfo.impact_land_warning_time);
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

    public void setSelect(int position) {
        for (int i = 0; i < textViews.length; i++) {
            if (i == position) {
                textViews[i].setBackgroundResource(R.drawable.caz_pal_bgd);
                textViews[i].setTextColor(Color.parseColor("#1AD5FD"));
                textViews[i].setEnabled(false);
            } else {
                textViews[i].setBackgroundResource(R.drawable.gj_jc_set_bgd);
                textViews[i].setTextColor(Color.WHITE);
                textViews[i].setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gaojing_save:
                //拿到输入的内容,保存到sp里面去.
                if (gaojingPreference != null) {
                    String caz = caz_input.getText().toString().trim();
                    if (TextUtils.isEmpty(caz)) {
                        Toast.makeText(context, "请输入caz距离", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String paz = paz_input.getText().toString().trim();
                    if (TextUtils.isEmpty(paz)) {
                        Toast.makeText(context, "请输入paz距离", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String cazHeight = caz_height.getText().toString().trim();
                    if (TextUtils.isEmpty(cazHeight)) {
                        Toast.makeText(context, "请输入caz高度", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String pazHeight = paz_height.getText().toString().trim();
                    if (TextUtils.isEmpty(pazHeight)) {
                        Toast.makeText(context, "请输入paz高度", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String cazTime = caz_time.getText().toString().trim();
                    if (TextUtils.isEmpty(cazTime)) {
                        Toast.makeText(context, "请输入caz时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String pazTime = paz_time.getText().toString().trim();
                    if (TextUtils.isEmpty(pazTime)) {
                        Toast.makeText(context, "请输入paz时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String airspace = airspace_warning_time_input.getText().toString().trim();
                    if (TextUtils.isEmpty(airspace)) {
                        Toast.makeText(context, "空域预警时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String impactLand = impact_land_warning_time_input.getText().toString().trim();
                    if (TextUtils.isEmpty(impactLand)) {
                        Toast.makeText(context, "防撞地预警时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    gaojingPreference.setGaojingInfo(caz, paz, cazHeight, pazHeight, cazTime, pazTime, airspace, impactLand);
                    dismiss();
                }
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
