package com.hanke.navi.skyair.pop.navpop.hl;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.callback.AddHLItemCallback;
import com.hanke.navi.skyair.db.HLModel;
import com.hanke.navi.skyair.pop.bean.HangLuBean;

import java.util.ArrayList;

public class HLAddJiaHaoPop extends PopupWindow implements View.OnClickListener,View.OnTouchListener{

    public static HLAddJiaHaoPop instence=null;
    private Context context;
    public EditText mc_jiahao,wd_jiahao,jd_jiahao,gd_jiahao;
    private Button xz_jiahao,bc_jiahao;
    private ArrayList<String> data;
    private HLAdapter hlAdapter;
    private AddHLItemCallback addHLItemCallback;
    private int position;
    private SpinerPop spinerPop;

    public void setAddHLItemCallback(AddHLItemCallback addHLItemCallback) {
        this.addHLItemCallback = addHLItemCallback;
    }

    public HLAddJiaHaoPop(Context context, int position) {
        this(context,null);

    }
    public HLAddJiaHaoPop(Context context) {
        this(context,null);
    }

    public HLAddJiaHaoPop(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HLAddJiaHaoPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        instence = HLAddJiaHaoPop.this;
        this.context = context;
        init();
        initView();
    }

    public void init(){
        data = new ArrayList<String>();
        hlAdapter = new HLAdapter(context);
        spinerPop = new SpinerPop(context,0);
    }

    public void initView() {
        View view = View.inflate(context, R.layout.hladd_jiahao, null);

        mc_jiahao = (EditText) view.findViewById(R.id.mc_jiahao);
        wd_jiahao = (EditText) view.findViewById(R.id.wd_jiahao);
        jd_jiahao = (EditText) view.findViewById(R.id.jd_jiahao);
        gd_jiahao = (EditText) view.findViewById(R.id.gd_jiahao);
        bc_jiahao = (Button) view.findViewById(R.id.bc_jiahao);
        bc_jiahao.setOnClickListener(this);
        xz_jiahao = (Button) view.findViewById(R.id.xz_jiahao);
        xz_jiahao.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(4* MyApplication.getMyApplication().getWidth()/8);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);
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
        String mingchengHLu_kong = mc_jiahao.getText().toString();
        String weiduHLu_kong = wd_jiahao.getText().toString();
        String jingduHLu_kong = jd_jiahao.getText().toString();
        String gaoduHLu_kong = gd_jiahao.getText().toString();
        HangLuBean hangLuBean = new HangLuBean();
        if (mingchengHLu_kong.length()==0||
                weiduHLu_kong.length()==0||
                jingduHLu_kong.length()==0||
                gaoduHLu_kong.length()==0)
            Toast.makeText(context,"填写内容不能为空",Toast.LENGTH_SHORT).show();
        else{
            hangLuBean.setHanglu(mingchengHLu_kong);
            hangLuBean.setWeidu(Double.parseDouble(weiduHLu_kong));
            hangLuBean.setJingdu(Double.parseDouble(jingduHLu_kong));
            hangLuBean.setGaodu(Double.parseDouble(gaoduHLu_kong));
            addHLItemCallback.addHLItem(position,hangLuBean);
            HLModel hlModel = new HLModel();
            hlModel.insertHL(hangLuBean);
            dismissPopWindow();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.xz_jiahao:
                showSpinerWindow();
                break;
            case R.id.bc_jiahao:
                initData();
                break;
        }
    }

    public void showSpinerWindow(){
        spinerPop.readhldku();
        int[] location = new int[2];
        mc_jiahao.getLocationOnScreen(location);
        int h=mc_jiahao.getHeight();
        int x = location[0];
        int y = location[1];
        spinerPop.setWidth(mc_jiahao.getWidth());
        spinerPop.showAtLocation(BaseActivity.instance.mapView, Gravity.NO_GRAVITY, x, y+h);
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
