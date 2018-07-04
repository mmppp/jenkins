package com.hanke.navi.skyair.pop.navpop.hx;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.callback.AddHXItemCallBack;
import com.hanke.navi.skyair.callback.DeleteCallback;
import com.hanke.navi.skyair.db.HXModel;
import com.hanke.navi.skyair.pop.bean.HangXianBean;
import com.hanke.navi.skyair.pop.navpop.hl.HLPop;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class HXPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    public static HXPop instence;
    private Context context;
    public ListView lv_hangxian;
    public HXAdapter hxAdapter;
    public List<HangXianBean> data_hx;
    private TextView hangxian_jiahao, tv_air_line_item;
    private int position;
    public HXModel hxModel;
    private float OldListY = -1;

    public HXPop(Context context) {
        this(context, null);
    }

    public HXPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HXPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        instence = HXPop.this;
        beforeInitView();
        initView();
        initData();
    }

    public void beforeInitView() {
        if (data_hx == null)
            data_hx = new ArrayList<HangXianBean>();
        hxModel = new HXModel();
        data_hx = hxModel.getAll();
    }

    public void initView() {
        View view = View.inflate(context, R.layout.bt_hangxian, null);
        lv_hangxian = (ListView) view.findViewById(R.id.lv_hangxian);
        tv_air_line_item = (TextView) view.findViewById(R.id.tv_air_line_item);
        hangxian_jiahao = (TextView) view.findViewById(R.id.hangxian_jiahao);
        hangxian_jiahao.setOnClickListener(this);
        hxAdapter = new HXAdapter(context);
        hxAdapter.setHXData(data_hx);
        lv_hangxian.setAdapter(hxAdapter);

        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 10);
        this.setHeight(MyApplication.getMyApplication().getHeight() / 2);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);

    }


    public void initData() {
        //长按删除航线
        lv_hangxian.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                HXZengShanPop hxZengShanPop = new HXZengShanPop(context, position);
                hxZengShanPop.setDeleteCallback(new DeleteCallback() {
                    @Override
                    public void deletePosition(int position) {
                        data_hx.remove(position);
                        hxModel.deleteHX(position);
                        hxAdapter.setHXData(data_hx);
                        hxAdapter.notifyDataSetChanged();
                    }
                });
                hxAdapter.setSelectHXItem(position);
                hxAdapter.notifyDataSetChanged();

                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int Pos[] = {-1, -1};  //保存当前坐标的数组
                view.getLocationOnScreen(Pos);  //获取选中的 Item 在屏幕中的位置，以左上角为原点 (0, 0)
                OldListY = (float) Pos[1];  //只取 Y 坐标就行
                int heng = location[0] + view.getWidth() - hxZengShanPop.popupWidth;
                int shu = (int) OldListY - hxZengShanPop.popupHeight / 4;

                hxZengShanPop.showAtLocation(BaseActivity.instance.mapView, Gravity.NO_GRAVITY, heng, shu);
                hxZengShanPop.setShowHLPopCallBack(new ShowHLPopCallBack() {
                    @Override
                    public void showHLPop(Point point) {
                        HLPop hlPop = new HLPop(context, data_hx.get(position));
                        hlPop.showPopWindow(BaseActivity.instance.mapView);
                        dismissPopWindow();
                    }
                });

                return true;
            }
        });

        //单机进入航线,设置具体的航路
        lv_hangxian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HLPop hlPop = new HLPop(context, data_hx.get(position));
                hlPop.showPopWindow(BaseActivity.instance.mapView);
                dismissPopWindow();
            }
        });

    }

    public void showPopWindow(View view) {
        if (!isShowing()) {
            this.showAtLocation(view, Gravity.CENTER, 0, 0);
            MainActivity.instence.listPop.add(this);
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hangxian_jiahao:
                HXAddJiaHaoPop hxAddJiaHaoPop = new HXAddJiaHaoPop(context, position);
                hxAddJiaHaoPop.showPopWindow(BaseActivity.instance.mapView);
                hxAddJiaHaoPop.setAddHXItemCallBack(new AddHXItemCallBack() {
                    @Override
                    public void addHXItem(int position, HangXianBean hangXianBean) {
                        int pos = lv_hangxian.getLastVisiblePosition();
                        if (data_hx.size() == 0) {
                            data_hx.add(position, hangXianBean);
                        } else
                            data_hx.add(pos + 1, hangXianBean);
                        hxModel.insertHX(hangXianBean);
                        hxAdapter.setHXData(data_hx);
                        hxAdapter.notifyDataSetChanged();
                    }
                });
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

    public interface ShowHLPopCallBack {
        void showHLPop(Point point);
    }
}
