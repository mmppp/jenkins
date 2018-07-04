package com.hanke.navi.skyair.pop;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.socket.ClintTask_lookback;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mahao on 2017/9/26.
 */

public class LookBackPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    private Context context;
    private GaojingPreference gaojingPreference;
    private TextView tv_lookback_file;
    private TextView tv_lookback_chengyi2;
    private TextView tv_lookback_chuyi2;
    private TextView tv_lookback_speed;
    private TextView start_lookback;
    private int[] multipleArr;
    private int position;

    public LookBackPop(Context context) {
        this(context, null);
    }

    public LookBackPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LookBackPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        MainActivity.instence.listPop.add(this);
        initView();
    }

    private void initView() {
        View view = View.inflate(context, R.layout.lookback_pop, null);
        gaojingPreference = new GaojingPreference(context);
        multipleArr = new int[]{1, 2, 3, 5};

        tv_lookback_file = (TextView) view.findViewById(R.id.tv_lookback_file);
        tv_lookback_file.setOnClickListener(this);
        tv_lookback_chengyi2 = (TextView) view.findViewById(R.id.tv_lookback_chengyi2);
        tv_lookback_chengyi2.setOnClickListener(this);
        tv_lookback_chuyi2 = (TextView) view.findViewById(R.id.tv_lookback_chuyi2);
        tv_lookback_chuyi2.setOnClickListener(this);
        tv_lookback_speed = (TextView) view.findViewById(R.id.tv_lookback_speed);
        tv_lookback_speed.setOnClickListener(this);
        start_lookback = (TextView) view.findViewById(R.id.start_lookback);
        start_lookback.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(4 * MyApplication.getMyApplication().getWidth() / 8);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_lookback_file:
                String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "lookback";
                File file = new File(path);
                final ArrayList<String> data = new ArrayList<>();
                if (file.exists()) {
                    if (file.isDirectory()) {
                        String[] filelist = file.list();
                        for (int i = 0; i < filelist.length; i++) {
                            File readfile = new File(path + "\\" + filelist[i]);
                            String fileName = readfile.getName();
                            data.add(fileName.substring(9));
                        }
                    }
                }
                View inflate = View.inflate(context, R.layout.choose_airport, null);
                final Dialog dialog = new Dialog(context, R.style.dialog);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
                WindowManager windowManager = MainActivity.instence.getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = (int) (display.getWidth() * 0.5); //设置宽度
                lp.height = (int) (display.getHeight() * 0.4);
                dialog.getWindow().setAttributes(lp);
                dialog.setContentView(inflate);
                ListView viewById = (ListView) inflate.findViewById(R.id.lv_choose_airport);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.style_spinner, data);
                arrayAdapter.setDropDownViewResource(R.layout.select_dialog_singlechoice);
                viewById.setAdapter(arrayAdapter);
                viewById.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = data.get(position);
                        tv_lookback_file.setText(name);
                        gaojingPreference.setLookBackFile(name);
                        dialog.cancel();
                    }
                });
                break;
            case R.id.tv_lookback_chengyi2:
                if (position < multipleArr.length - 1) {
                    position++;
                } else {
                    position = 0;
                }
                tv_lookback_speed.setText(multipleArr[position] + "");
                break;
            case R.id.tv_lookback_chuyi2:
                if (position != 0) {
                    position--;
                } else {
                    position = multipleArr.length - 1;
                }
                tv_lookback_speed.setText(multipleArr[position] + "");
                break;

            case R.id.start_lookback:
                MainActivity.instence.lookback_seekbar.setVisibility(View.VISIBLE);
                //这里需要停止当前对的asynctask.然后开始新的
                MyApplication.getMyApplication().clientTask.cancel(true);
                MyApplication.getMyApplication().hopeRow = 0;
                MyApplication.getMyApplication().multipleNuml = Integer.valueOf(tv_lookback_speed.getText().toString());
                ClintTask_lookback clienttask = new ClintTask_lookback(context, true, 0);
                clienttask.execute();
                dismissPopWindow();

                break;
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
