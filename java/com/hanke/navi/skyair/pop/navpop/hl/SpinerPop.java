package com.hanke.navi.skyair.pop.navpop.hl;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.HangLuBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SpinerPop extends PopupWindow {
    private Context context;
    public ListView lv_spiner;
    public List<HangLuBean> data_hl;
    public HLAdapter hlAdapter;
    public static SpinerPop instance = null;
    private int type;

    public SpinerPop(Context context, int type) {
        super(context);
        this.type = type;
        this.context = context;
        instance = SpinerPop.this;
        beforeInitView();
        initView();
//        readhldku();
        initData();
    }

    public SpinerPop(Context context) {
        this(context, null);
    }

    public SpinerPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinerPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void beforeInitView() {
        if (data_hl == null)
            data_hl = new ArrayList<HangLuBean>();
        hlAdapter = new HLAdapter(context);
    }

    public void initView() {
        View view = View.inflate(context, R.layout.spiner_listview, null);
        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 8);
        this.setHeight(1 * MyApplication.getMyApplication().getHeight() / 4);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        lv_spiner = (ListView) view.findViewById(R.id.lv_spiner);
        hlAdapter.setHLData(data_hl);
        lv_spiner.setAdapter(hlAdapter);
    }


    public void initData() {
        lv_spiner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                hlAdapter.setSelectHLItem(position);
                hlAdapter.notifyDataSetChanged();
                String item_mc = data_hl.get(position).getHanglu();
                double item_wd = data_hl.get(position).getWeidu();
                double item_jd = data_hl.get(position).getJingdu();
                double item_gd = data_hl.get(position).getGaodu();
                if (type == 0)
                    zhijiahao(item_mc, item_wd, item_jd, item_gd);
                if (type == 1)
                    zhibianji(item_mc, item_wd, item_jd, item_gd);
                dismissPopWindow();
            }
        });
    }

    private void zhibianji(String item_mc, double item_wd, double item_jd, double item_gd) {
        HLBianJiPop hlBianJiPop = new HLBianJiPop(context);
        hlBianJiPop.mingcheng.setText(item_mc);
        hlBianJiPop.weidu.setText(String.valueOf(item_wd));
        hlBianJiPop.jingdu.setText(String.valueOf(item_jd));
        hlBianJiPop.gaodu.setText(String.valueOf(item_gd));
    }

    private void zhijiahao(String item_mc, double item_wd, double item_jd, double item_gd) {
        HLAddJiaHaoPop.instence.mc_jiahao.setText(item_mc);
        HLAddJiaHaoPop.instence.wd_jiahao.setText(String.valueOf(item_wd));
        HLAddJiaHaoPop.instence.jd_jiahao.setText(String.valueOf(item_jd));
        HLAddJiaHaoPop.instence.gd_jiahao.setText(String.valueOf(item_gd));
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

    List<String> hlmcList = new ArrayList<String>();
    List<String> hlwdList = new ArrayList<String>();
    List<String> hljdList = new ArrayList<String>();
    List<String> hlgdList = new ArrayList<String>();

    public void readhldku() {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator +
                "amap" + File.separator + "hldku" + File.separator + "hldku.txt";
        File file = new File(path);
        if (file.exists()) {
            InputStreamReader inputStreamReader = null;
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line = "";
                StringBuffer sb = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    Log.e("wjsp", "line=====" + line);
                    String test = line.replaceAll("\\s{1,}", ",");//替换空格
                    line = test;
                    String hld[] = line.split(",");
                    if (hld.length == 4) {
                        hlmcList.add(hld[0]);//名称
                        hlwdList.add(hld[1]);//纬度
                        hljdList.add(hld[2]);//经度
                        hlgdList.add(hld[3]);//高度

                    }
                }

                for (int i = 0; i < hlmcList.size(); i++) {//名称
                    HangLuBean hangLubean = new HangLuBean();
                    hangLubean.setHanglu(hlmcList.get(i));
                    hangLubean.setWeidu(Double.parseDouble(hlwdList.get(i)));
                    hangLubean.setJingdu(Double.parseDouble(hljdList.get(i)));
                    hangLubean.setGaodu(Double.parseDouble(hlgdList.get(i)));
                    data_hl.add(hangLubean);
                }
                hlAdapter.setHLData(data_hl);
                hlAdapter.notifyDataSetChanged();

                inputStreamReader.close();
                fileInputStream.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, "预读航路点库文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

    }

}