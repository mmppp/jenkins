package com.hanke.navi.skyair.pop.navpop.hl;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.framwork.utils.Constants;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.callback.AddHLItemCallback;
import com.hanke.navi.skyair.callback.DeleteCallback;
import com.hanke.navi.skyair.db.HLModel;
import com.hanke.navi.skyair.db.HXModel;
import com.hanke.navi.skyair.pop.bean.HangLuBean;
import com.hanke.navi.skyair.pop.bean.HangXianBean;
import com.hanke.navi.skyair.pop.navpop.hx.HXPop;
import com.hanke.navi.skyair.service.LandNavService;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.ui.view.DragAndDropListView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class HLPop extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    public static HLPop instence;
    private Context context;
    private DragAndDropListView lv_hanglu;
    public HLAdapter hlAdapter;
    public ArrayList<HangLuBean> data_hl;
    public TextView hangxian_title, hanglu_jiahao, hangxian_pianzhi, hangxian_zhixing;
    private int posi;
    private boolean islight;
    public HLModel hlModel;
    public HXModel hxModel;
    public HangXianBean hangxian;
    private DragAndDropListViewAdapter mDragAndDropListViewAdapter;
    private TextView back_to_hangxian;


    public HLPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HLPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        MainActivity.instence.listPop.add(this);
        instence = HLPop.this;
        beforeInitView();
        initView();
        initData();
    }

    public HLPop(Context context, HangXianBean hangLuBean) {
        this.context = context;
        this.hangxian = hangLuBean;
        MainActivity.instence.listPop.add(this);
        instence = HLPop.this;
        beforeInitView();
        initView();
        initData();
    }

    //将存在航线数据库里面的航路给查询出来.
    public void beforeInitView() {
        if (data_hl == null)
            data_hl = new ArrayList<HangLuBean>();
        hlModel = new HLModel();
        hxModel = new HXModel();
        if (hangxian != null && hangxian.getHanglu() != null && hangxian.getHanglu().length() != 0) {
            //不止一个航路点
            if (hangxian.getHanglu().contains(",")) {
                String[] strHLName = hangxian.getHanglu().split(",");
                for (int x = 0; x < strHLName.length; x++) {
                    data_hl.add(hlModel.getHangLuBeanById(strHLName[x]));
                }
            } else {
                //如果只有一个航路点
                HangLuBean bean = new HangLuBean();
                bean.hanglu = hangxian.getHanglu();
                data_hl.add(bean);
            }

        }
        if (MyApplication.getMyApplication().hlMarkerList == null) {
            MyApplication.getMyApplication().hlMarkerList = new ArrayList<>();
        }
    }

    public void initView() {
        View view = View.inflate(context, R.layout.bt_hanglu, null);
        lv_hanglu = (DragAndDropListView) view.findViewById(R.id.lv_hanglu);

        mDragAndDropListViewAdapter = new DragAndDropListViewAdapter();
        lv_hanglu.setDropListener(mDropListener);
        lv_hanglu.setRemoveListener(mRemoveListener);

        hangxian_title = (TextView) view.findViewById(R.id.hangxian_title);
        hangxian_title.setText(hangxian.getHangxian());
        hanglu_jiahao = (TextView) view.findViewById(R.id.hanglu_jiahao);
        hanglu_jiahao.setOnClickListener(this);
        hangxian_pianzhi = (TextView) view.findViewById(R.id.hangxian_pianzhi);
        hangxian_pianzhi.setOnClickListener(this);
        if (!MyApplication.getMyApplication().isPianzhiDoing) {
            hangxian_pianzhi.setTextColor(Color.YELLOW);
        } else {
            hangxian_pianzhi.setTextColor(Color.parseColor("#26cfe9"));
        }
        hangxian_zhixing = (TextView) view.findViewById(R.id.hangxian_zhixing);
        hangxian_zhixing.setOnClickListener(this);
        back_to_hangxian = (TextView) view.findViewById(R.id.back_to_hangxian);
        back_to_hangxian.setOnClickListener(this);
//        hlAdapter = new HLAdapter(context);
//        hlAdapter.setHLData(data_hl);
        lv_hanglu.setAdapter(mDragAndDropListViewAdapter);


        if (MainActivity.instence.flag_zx == false && hangxian.getHangxian().equals(MyApplication.getMyApplication().flyingPlanName)) {
            hangxian_zhixing.setTextColor(Color.YELLOW);
        } else {
            hangxian_zhixing.setTextColor(context.getResources().getColor(R.color.daohangqianblue));
        }


        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 10);
        this.setHeight(MyApplication.getMyApplication().getHeight() / 2);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        view.setOnTouchListener(this);
    }

    private class ViewHolder {
        TextView tv_air_way_item;
    }

    private float OldListY = -1;

    public void initData() {
        lv_hanglu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HangLuBean hangLuBean = data_hl.get(position);
                HLZengShanPop hlZengShanPop = new HLZengShanPop(context, position);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int Pos[] = {-1, -1};  //保存当前坐标的数组
                view.getLocationOnScreen(Pos);  //获取选中的 Item 在屏幕中的位置，以左上角为原点 (0, 0)
                OldListY = (float) Pos[1];  //只取 Y 坐标
                int heng = location[0] + view.getWidth() - HLZengShanPop.instance.popupWidth;
                int shu = (int) OldListY - HLZengShanPop.instance.popupHeight / 4;
                hlZengShanPop.showAtLocation(BaseActivity.instance.mapView, Gravity.NO_GRAVITY, heng, shu);
                hlZengShanPop.setHangLuBean(hangLuBean);
                hlZengShanPop.setDeleteCallback(new DeleteCallback() {
                    @Override
                    public void deletePosition(int position) {
                        if (MainActivity.instence.flag_zx == false) {
                            Toast.makeText(context, "航路点正在执行中，无法删除", Toast.LENGTH_SHORT).show();
                        } else {
                            data_hl.remove(position);
                            //然后需要删掉这个里面的内容
                            String temp = "";
                            for (int x = 0; x < data_hl.size(); x++) {
                                if (x != position) {
                                    temp = temp + (data_hl.get(x).getHanglu() + ",");
                                }
                            }
                            HangXianBean bean = new HangXianBean();
                            if (!temp.equals("")) {
                                bean.hanglu = temp.substring(0, temp.length() - 1);
                            } else {
                                bean.hanglu = "";
                            }
                            bean.hangxian = hangxian.getHangxian();
                            hxModel.insertHL(bean);
                            mDragAndDropListViewAdapter.notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }
        });
    }

    private class DragAndDropListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data_hl.size();
        }

        @Override
        public HangLuBean getItem(int position) {
            HangLuBean bean = data_hl.get(position);
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            pos = position;
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(context, R.layout.air_way_item, null);
                viewHolder.tv_air_way_item = (TextView) convertView.findViewById(R.id.tv_air_way_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_air_way_item.setText(data_hl.get(position).getHanglu());
            return convertView;
        }
    }

    private DragAndDropListView.DropListener mDropListener = new DragAndDropListView.DropListener() {
        public void drop(int from, int to) {

            Log.i("hahaha", "from" + from + "..to" + to);

            HangLuBean item = mDragAndDropListViewAdapter.getItem(from);
            data_hl.remove(item);
            data_hl.add(to, item);
            //这里,其实需要对hx里面的hl字段进行重新的存储,这样的话下次进入的顺序才是修改了之后的顺序.
            String hanglu = "";
            for (int x = 0; x < data_hl.size(); x++) {
                hanglu += data_hl.get(x).getHanglu() + ",";
            }
            if (hanglu.contains(",")) {
                hangxian.hanglu = hanglu.substring(0, hanglu.length() - 1);
            } else {
                hangxian.hanglu = hanglu;
            }
            Log.i("hahaha", hangxian.getHangxian() + "..." + hangxian.getHanglu());
            hxModel.updateHL(hangxian);
            mDragAndDropListViewAdapter.notifyDataSetChanged();
        }
    };

    private DragAndDropListView.RemoveListener mRemoveListener = new DragAndDropListView.RemoveListener() {
        public void remove(int which) {
            mDragAndDropListViewAdapter.notifyDataSetChanged();
        }
    };

    public void showPopWindow(View view) {
        if (!isShowing()) {
            this.showAtLocation(view, Gravity.CENTER, 0, 0);
            MainActivity.instence.data_hl_ap = data_hl;
        }
    }

    public void dismissPopWindow() {
        if (this.context != null && this.isShowing()) {
            this.dismiss();
        }
    }

    boolean isFirst = true;
    Polyline polyline = null;
    int pos;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hanglu_jiahao:
                HLAddJiaHaoPop hlAddJiaHaoPop = new HLAddJiaHaoPop(context);
                hlAddJiaHaoPop.showPopWindow(BaseActivity.instance.mapView);
                hlAddJiaHaoPop.setAddHLItemCallback(new AddHLItemCallback() {
                    @Override
                    public void addHLItem(int position, HangLuBean hangLuBean) {
                        if (MainActivity.instence.flag_zx == false) {
                            Toast.makeText(context, "航路点正在执行中，无法添加", Toast.LENGTH_SHORT).show();
                        } else {
                            //这里面需要往航线里面添加,如果这个是自己创建的新的航路点的话,那么就往航线和航路里面都要添加
                            data_hl.add(hangLuBean);
                            String str = "";
                            for (int x = 0; x < data_hl.size(); x++) {
                                str += data_hl.get(x).getHanglu() + ",";
                            }
                            HangXianBean bean = new HangXianBean();
                            bean.hanglu = str.substring(0, str.length() - 1);
                            bean.hangxian = hangxian.getHangxian();
                            hxModel.insertHL(bean);
                            if (!hlModel.isExist(bean.hanglu)) {
                                hlModel.insertHL(hangLuBean);
                            }
//                            hlAdapter.setHLData(data_hl);
                            mDragAndDropListViewAdapter.notifyDataSetChanged();
                        }
                    }
                });

                break;
            case R.id.hangxian_zhixing:
                //没有执行的话
                if (MainActivity.instence.flag_zx) {
                    open();
                } else {
                    close();
                    if (!hangxian.getHangxian().equals(MyApplication.getMyApplication().flyingPlanName)) {
                        open();
                    }
                }

                break;
            case R.id.hangxian_pianzhi:
                PianZhiPop pianZhiPop = new PianZhiPop(context, data_hl, hangxian.getHangxian());
                pianZhiPop.showPopWindow(BaseActivity.instance.mapView);
                break;

            case R.id.back_to_hangxian:
                //打开航路pop
                HXPop pop = new HXPop(context);
                pop.showPopWindow(BaseActivity.instance.mapView);
                dismissPopWindow();
                break;

        }
    }

    public void open() {
        setGuiJi(false);
        MyApplication.getMyApplication().flyingPlanName = hangxian.getHangxian();
        hangxian_zhixing.setTextColor(Color.YELLOW);
        MainActivity.instence.flag_zx = false;
        MainActivity.instence.handler.sendEmptyMessage(Constants.ZHIXING);
        //然后startservice,启动着陆service
        Intent intent = new Intent(context, LandNavService.class);
        context.startService(intent);
    }

    public void close() {
        //正在执行的话
        hangxian_zhixing.setTextColor(MyApplication.getMyApplication().getApplicationContext().getResources().getColor(R.color.daohangqianblue));
        //线关闭landNavService,不然不能删除掉marker点
        Intent intent = new Intent(context, LandNavService.class);
        context.stopService(intent);
        if (MainActivity.instence.listPolyline.size() == 0) {
            new Throwable().printStackTrace();
        } else {
            try {
                for (int x = 0; x < MyApplication.getMyApplication().hlMarkerList.size(); x++) {
                    Marker marker = MyApplication.getMyApplication().hlMarkerList.get(x);
                    marker.remove();
                }

                for (int i = 0; i < MainActivity.instence.listPolyline.size(); i++) {
                    MainActivity.instence.listPolyline.get(i).remove();
                }

                MyApplication.getMyApplication().hlMarkerList.clear();
                MainActivity.instence.listPolyline.clear();
                MyApplication.getMyApplication().flyLinePolyline.clear();
                MainActivity.instence.flag_zx = true;
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
    }


    public void setGuiJi(boolean isDottedLine) {
        if (isDottedLine) {
            //这里需要给每一个轨迹点画上marker
            for (int x = 0; x < MyApplication.getMyApplication().hlMarkerList.size(); x++) {
                MyApplication.getMyApplication().hlMarkerList.get(x).remove();
                if (x != MyApplication.getMyApplication().hlMarkerList.size() - 1) {
                    MainActivity.instence.listPolyline.get(x).remove();
                }
            }
            MyApplication.getMyApplication().hlMarkerList.clear();
            MainActivity.instence.listPolyline.clear();
        }

        int currentIndex = LandNavService.getService().currentIndex;
        for (int i = 0; i < data_hl.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(data_hl.get(i).getWeidu(), data_hl.get(i).getJingdu()));
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
            if (i != data_hl.size() - 1) {
                PolylineOptions polylineOptions = new PolylineOptions();
                if (currentIndex <= 1) {
                    polylineOptions.color(Color.WHITE);
                } else {
                    if (currentIndex - i > 1) {
                        polylineOptions.color(Color.RED);
                    } else {
                        polylineOptions.color(Color.WHITE);
                    }
                }
                if (isDottedLine) {
                    polylineOptions.setDottedLine(isDottedLine);
                }
                Log.i("polyline","lat"+data_hl.get(i).getWeidu()+"lon"+data_hl.get(i).getJingdu());
                Polyline polyline = BaseActivity.instance.aMap.addPolyline(polylineOptions.add(new LatLng(data_hl.get(i).getWeidu(), data_hl.get(i).getJingdu()), new LatLng(data_hl.get(i + 1).getWeidu(), data_hl.get(i + 1).getJingdu())).width(5));
                MainActivity.instence.listPolyline.add(polyline);
            }
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
