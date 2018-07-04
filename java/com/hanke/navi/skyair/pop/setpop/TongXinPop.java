package com.hanke.navi.skyair.pop.setpop;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hanke.navi.R;
import com.hanke.navi.framwork.share.SharepreferenceHelper;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TongXinPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "TongXinPop";
    private Context context;
    private EditText ed_ip, ed_port;
    public String ips;
    public int ports;
    private Button ed_save;
    public int popupHeight, popupWidth;
    private InputMethodManager mInputMethodManager;

    public TongXinPop(Context context) {
        this(context, null);
    }

    public TongXinPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TongXinPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();

    }

    public void initView() {
        View view = View.inflate(context, R.layout.change_ip, null);
        ed_ip = (EditText) view.findViewById(R.id.ed_ip);
        ed_port = (EditText) view.findViewById(R.id.ed_port);

        //这里拿到sp里面保存的数据,然后显示出来
        String ip = SharepreferenceHelper.getInstence(context).getIp();
        int port = SharepreferenceHelper.getInstence(context).getPort();
        //如果之前保存过,就设置保存过的值,否则就设置一个默认的值
        if (!TextUtils.isEmpty(ip)) {
            ed_ip.setText(ip);
        } else {
            ed_ip.setText("192.168.31.156");
        }
        if (port != 0) {
            ed_port.setText(port + "");
        } else {
            ed_port.setText("8001");
        }

        ed_save = (Button) view.findViewById(R.id.ed_save);
        ed_save.setOnClickListener(this);
        ed_ip.setOnClickListener(this);
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.setContentView(view);
        this.setWidth(4 * MyApplication.getMyApplication().getWidth() / 8);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);


        //获取自身的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();
    }

    public void initData() {
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";//限定ip的输入格式

        String port = "[0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5]";//限定端口号的输入格式
        Pattern p_ip = Pattern.compile(ip);
        Pattern p_port = Pattern.compile(port);
        ips = ed_ip.getText().toString();
        String ports_str = ed_port.getText().toString();
        Matcher m_ip = p_ip.matcher(ips);
        Matcher m_port = p_port.matcher(ports_str);
        boolean b_ip = m_ip.matches();
        boolean b_port = m_port.matches();
        if (ips.equals("") || ports_str.equals("")) {
            Toast.makeText(context, "内容不可为空!", Toast.LENGTH_SHORT).show();
        } else {
            if (b_ip == false) {
                Toast.makeText(context, "IP格式输入不合法", Toast.LENGTH_SHORT).show();
            } else if (b_port == false) {
                Toast.makeText(context, "端口号输入不合法", Toast.LENGTH_SHORT).show();
            } else if (b_ip == true && b_port == true) {
                SharepreferenceHelper.getInstence(context).setIp(ips);
                ports = Integer.parseInt(ports_str);
                SharepreferenceHelper.getInstence(context).setPort(ports);
                Log.e("2323", "ips = " + ips + " , ports = " + ports);
                dismissPopWindow();
                MainActivity.instence.dialog();
            }
        }

    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            showAtLocation(view, Gravity.CENTER, 0, 0);
            MainActivity.instence.listPop.add(this);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ed_save:
                initData();
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