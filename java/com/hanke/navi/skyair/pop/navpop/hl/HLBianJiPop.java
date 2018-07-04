package com.hanke.navi.skyair.pop.navpop.hl;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.framwork.share.SharepreferenceHelper;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.HangLuBean;

import java.util.ArrayList;

public class HLBianJiPop extends PopupWindow implements View.OnClickListener{

    public static HLBianJiPop instence=null;
    private Context context;
    public EditText mingcheng,weidu,jingdu,gaodu;
    private Button xuanze,baocun;
    private ArrayList<HangLuBean> data_hl;
    private HLAdapter hlAdapter;
    private HangLuBean hangLuBean;
    private SpinerPop spinerPop;

    public HLBianJiPop(Context context) {
        this(context,null);
    }

    public HLBianJiPop(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HLBianJiPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        initView();
    }

    public void setHangLuBean(HangLuBean hangLuBean) {
        this.hangLuBean = hangLuBean;
        if (hangLuBean != null){
            mingcheng.setText(hangLuBean.getHanglu());
            mingcheng.setSelection(hangLuBean.getHanglu().length());
            weidu.setText(""+hangLuBean.getWeidu());
            gaodu.setText(""+hangLuBean.getGaodu());
            jingdu.setText(""+hangLuBean.getJingdu());
        }
    }

    public void init(){
        data_hl = new ArrayList<HangLuBean>();
        hlAdapter = new HLAdapter(context);
        spinerPop = new SpinerPop(context,1);
    }

    public void initView() {
        View view = View.inflate(context, R.layout.hl_bianji, null);

        mingcheng = (EditText) view.findViewById(R.id.mingcheng);
        weidu = (EditText) view.findViewById(R.id.weidu);
        jingdu = (EditText) view.findViewById(R.id.jingdu);
        gaodu = (EditText) view.findViewById(R.id.gaodu);
        baocun = (Button) view.findViewById(R.id.baocun);
        baocun.setOnClickListener(this);
        xuanze = (Button) view.findViewById(R.id.xuanze);
        xuanze.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(4* MyApplication.getMyApplication().getWidth()/8);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            this.showAtLocation(view, Gravity.CENTER,0, 0);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    public void initData(){
        String mingchengHLu = mingcheng.getText().toString();
        String weiduHLu = weidu.getText().toString();
        String jingduHLu = jingdu.getText().toString();
        String gaoduHLu = gaodu.getText().toString();
        HangLuBean bean = new HangLuBean();
        if (mingchengHLu.length()==0||
                weiduHLu.length()==0||
                jingduHLu.length()==0||
                gaoduHLu.length()==0)
            Toast.makeText(context,"填写内容不能为空",Toast.LENGTH_SHORT).show();
        else{
            bean.setHanglu(mingchengHLu);
            bean.setWeidu(Double.parseDouble(weiduHLu));
            bean.setJingdu(Double.parseDouble(jingduHLu));
            bean.setGaodu(Double.parseDouble(gaoduHLu));
            data_hl.add(bean);
            hlAdapter.setHLData(data_hl);
            hlAdapter.notifyDataSetChanged();
            dismissPopWindow();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.xuanze:
                showSpinerWindow();
                break;
            case R.id.baocun:
                initData();
                break;
        }
    }

    public void showSpinerWindow(){
        spinerPop.readhldku();
        int[] location = new int[2];
        mingcheng.getLocationOnScreen(location);
        int h=mingcheng.getHeight();
        int x = location[0];
        int y = location[1];
        spinerPop.setWidth(mingcheng.getWidth());
        spinerPop.showAtLocation(BaseActivity.instance.mapView, Gravity.NO_GRAVITY, x, y+h);
    }

}
