package com.hanke.navi.skyair.pop.navpop.hx;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.callback.DeleteCallback;

public class HXZengShanPop extends PopupWindow implements View.OnClickListener{

    private Context context;
    public int popupHeight,popupWidth;
    private TextView bianji,shanchu;
    private DeleteCallback deleteCallback;
    private int position;
    private HXPop.ShowHLPopCallBack showHLPopCallBack;

    public HXZengShanPop(Context context, int position) {
        super(context);
        this.position = position;
        this.context = context;
        initView();
    }

    public HXZengShanPop(Context context) {
        this(context,null);
    }

    public HXZengShanPop(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HXZengShanPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public void setShowHLPopCallBack(HXPop.ShowHLPopCallBack showHLPopCallBack) {
        this.showHLPopCallBack = showHLPopCallBack;
    }

    public void initView(){
        View view = View.inflate(context, R.layout.add_or_del,null);
        bianji = (TextView) view.findViewById(R.id.bianji);
        bianji.setOnClickListener(this);
        shanchu = (TextView) view.findViewById(R.id.shanchu);
        shanchu.setOnClickListener(this);
        this.setContentView(view);
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止虚拟软键盘被弹出菜单遮住
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        //获取自身的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();
    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            int x = (int) view.getX();
            int y = (int) view.getY();
            Point point = new Point(x, y);
            this.showAtLocation(view, Gravity.CENTER, point.x+2* MyApplication.getMyApplication().getWidth()/8, point.y);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bianji:
                int x = (int) v.getX();
                int y = (int) v.getY();
                Point point = new Point(x,y);
                if(showHLPopCallBack!=null){
                    showHLPopCallBack.showHLPop(point);
                }
                this.dismissPopWindow();
                break;
            case R.id.shanchu:
                deleteCallback.deletePosition(position);
                this.dismissPopWindow();
                break;
        }
    }
}
