package com.hanke.navi.skyair.pop.tcpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.KongYuBean;
import com.hanke.navi.skyair.ui.MainActivity;
import com.hanke.navi.skyair.util.GaojingPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class XinXiPop extends PopupWindow implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {


    public static XinXiPop instance = null;
    private Context context;
    private CheckBox pihao_message, disu_message, gaodu_message, jixing_message, eta_time, xtk_pianhang;
    private ListView lv_kongyu;
    public KYAdapter kyAdapter;
    public Map<Integer, Integer> selectMap;
    public List<Integer> listint;
    private GaojingPreference preference;

    public List<KongYuBean> getData_ky() {
        return MyApplication.getMyApplication().data_ky;
    }

    public XinXiPop(Context context) {
        this(context, null);
    }

    public XinXiPop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XinXiPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        instance = XinXiPop.this;
        MainActivity.instence.listPop.add(this);
        beforeInitView();
        initView();
//        readky();
//        aaa();
        initData();
    }

    public void beforeInitView() {
        kyAdapter = new KYAdapter(context);
        listint = new ArrayList<>();
        selectMap = new HashMap<>();
    }

//    public void aaa() {
//        for(int x=0;x<MyApplication.getMyApplication().data_ky.size();x++){
//            String kyName = MyApplication.getMyApplication().data_ky.get(x).getKymc();
//            if(kyName)
//        }

//        if (MainActivity.instence.listPolygon.size() == 0 || MainActivity.instence.listText.size() == 0) {
//            new Throwable().printStackTrace();
//            Log.e("qq", "kyAdapter.pos = " + kyAdapter.pos);
//            for (int i = 0; i < getCounts(); i++) {
//                MyApplication.getMyApplication().data_ky.get(i).isSelect = false;
//            }
//        } else {
//            for (int i = 0; i < data_ky.size(); i++) {
//                data_ky.get(i).isSelect = true;
//            }
//        }
//    }

    public void initView() {
        View view = View.inflate(context, R.layout.show_notice, null);
        this.setContentView(view);
        this.setWidth(5 * MyApplication.getMyApplication().getWidth() / 8);
        this.setHeight(2 * MyApplication.getMyApplication().getHeight() / 5);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        view.setOnTouchListener(this);

        preference = new GaojingPreference(context);
        pihao_message = (CheckBox) view.findViewById(R.id.pihao_message);
        pihao_message.setOnCheckedChangeListener(this);
        disu_message = (CheckBox) view.findViewById(R.id.disu_message);
        disu_message.setOnCheckedChangeListener(this);
        gaodu_message = (CheckBox) view.findViewById(R.id.gaodu_message);
        gaodu_message.setOnCheckedChangeListener(this);
        jixing_message = (CheckBox) view.findViewById(R.id.jixing_message);
        jixing_message.setOnCheckedChangeListener(this);
        eta_time = (CheckBox) view.findViewById(R.id.eta_time);
        eta_time.setOnCheckedChangeListener(this);
        eta_time.setChecked(preference.isShowEtaTime());
        xtk_pianhang = (CheckBox) view.findViewById(R.id.xtk_pianhang);
        xtk_pianhang.setOnCheckedChangeListener(this);
        xtk_pianhang.setChecked(preference.isShowXtkDistance());
        lv_kongyu = (ListView) view.findViewById(R.id.lv_kongyu);

        kyAdapter.setKYData(MyApplication.getMyApplication().data_ky);
        lv_kongyu.setAdapter(kyAdapter);
    }

    public void initData() {
        lv_kongyu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                int key = getKeyByValue(selectMap,position);
//                Log.e("sa","key = "+key);
                if (kyAdapter.getKYData().get(position).isSelect) {//取消
                    MainActivity.instence.listText.get(position).remove();
                    MainActivity.instence.listPolygon.get(position).remove();
                    MainActivity.instence.listText.remove(position);
                    MainActivity.instence.listPolygon.remove(position);
//                    MainActivity.instence.listPolygon.remove(kyAdapter.getKYData().get(position));
//                    selectMap.remove(position);
                    kyAdapter.getKYData().get(position).isSelect = false;
                    MyApplication.getMyApplication().data_ky.get(position).isSelect = false;
                } else {//选中
                    setSpaceAir(position);
                    kyAdapter.getKYData().get(position).isSelect = true;
                    MyApplication.getMyApplication().data_ky.get(position).isSelect = true;
                }
                kyAdapter.notifyDataSetChanged();


            }
        });
    }

    public int getKeyByValue(Map map, Object value) {//通过value值得到key值
        int keys = 0;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object obj = entry.getValue();
            if (obj != null && obj.equals(value)) {
                keys = (int) entry.getKey();
            }
        }
        return keys;
    }

    public Polyline polyline;
    public Text text;
    public Polygon polygon;
    public int e, pos;

    public void setSpaceAir(int position) {
        KongYuBean kongYuBean = MyApplication.getMyApplication().data_ky.get(position);
        text = BaseActivity.instance.aMap.addText(new TextOptions().position(new LatLng(Double.parseDouble(kongYuBean.getKywds().get(0)),
                Double.parseDouble(kongYuBean.getKyjds().get(0)))).text(MyApplication.getMyApplication().kymcList.get(position)).backgroundColor(Color.TRANSPARENT)
                .fontColor(Color.CYAN).fontSize(MyApplication.getMyApplication().getWidth() / 60).typeface(Typeface.SANS_SERIF));

        PolygonOptions polygonOptions = new PolygonOptions();
        ArrayList<LatLng> list = new ArrayList<>();
        for (int x = 0; x < kongYuBean.kywds.size(); x++) {
            list.add(new LatLng(Double.parseDouble(kongYuBean.kywds.get(x)), Double.parseDouble(kongYuBean.kyjds.get(x))));
        }
        polygon = BaseActivity.instance.aMap.addPolygon(polygonOptions.addAll(list).strokeWidth(MyApplication.getMyApplication().getWidth() / 150).strokeColor(Color.BLUE).fillColor(Color.TRANSPARENT));
        MainActivity.instence.listText.add(position, text);
        MainActivity.instence.listPolygon.add(position, polygon);
//        List<LatLng> list = new ArrayList<LatLng>();
//        for (int i = position; i < position + 1; i++) {
//            for (int j = 0; j < Double.parseDouble(kydsList.get(i)); j++) {
//                double item_wd = Double.parseDouble(data_ky.get(position).getKywds().get(j));
//                double item_jd = Double.parseDouble(data_ky.get(position).getKyjds().get(j));
//                list.add(new LatLng(item_wd, item_jd));
//            }
//            list.add(new LatLng(Double.parseDouble(data_ky.get(position).getKywds().get(0)), Double.parseDouble(data_ky.get(position).getKyjds().get(0))));
//
//            PolygonOptions polygonOptions = new PolygonOptions();
//            text = BaseActivity.instance.aMap.addText(new TextOptions().position(new LatLng(Double.parseDouble(data_ky.get(position).getKywds().get(0)),
//                    Double.parseDouble(data_ky.get(position).getKyjds().get(0)))).text(kymcList.get(position)).backgroundColor(Color.TRANSPARENT)
//                    .fontColor(Color.CYAN).fontSize(MyApplication.getMyApplication().getWidth()/60).typeface(Typeface.SANS_SERIF));
//            polygon = BaseActivity.instance.aMap.addPolygon(polygonOptions.addAll(list).strokeWidth(MyApplication.getMyApplication().getWidth()/150).strokeColor(Color.BLUE).fillColor(Color.TRANSPARENT));
//            MainActivity.instence.listText.add(e,text);
//            MainActivity.instence.listPolygon.add(e,polygon);
//            selectMap.put(e,position);
//            listint.add(e);
//            e++;
//            pos = position;
//        }

    }

    public int getCounts() {
        return kyAdapter.getCount();
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.pihao_message:

                break;
            case R.id.disu_message:

                break;
            case R.id.gaodu_message:

                break;
            case R.id.jixing_message:

                break;
            case R.id.eta_time:
                //这里存到sp里面去
                preference.setEtaTime(b);
                break;
            case R.id.xtk_pianhang:
                preference.setXtkDistance(b);
                break;
        }
    }

//    private List<String> kymcList = new ArrayList<String>();//空域名称
//    private List<String> kymingdList = new ArrayList<String>();//最小空域高度
//    private List<String> kymaxgdList = new ArrayList<String>();//最大空域高度
//    private List<String> kydsList = new ArrayList<String>();//空域点数
//    private List<List<String>> kywdList = new ArrayList<List<String>>();//空域纬度
//    private List<List<String>> kyjdList = new ArrayList<List<String>>();//空域经度
//    private List<String> listLine = new ArrayList<String>();//每一行数据的集合
//    private List<Integer> listy = new ArrayList<Integer>();//大写字母开头所在行的下标的集合
//    private List<String> listJWLine;//文本中所有经纬度所在行的集合
//    private List<String> listqwd = new ArrayList<String>();//文本中所有纬度数据的集合
//    private List<String> listqjd = new ArrayList<String>();//文本中所有经度数据的集合
//    private int y = -1;//大写字母开头所在行的下标
//    private String str;
//    private double wd;
//    private double jd;
//    private int a;
//    private int b;

//    public List<KongYuBean> readky() {
//        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "kongyu" + File.separator + "ky.txt";
//        File file = new File(path);
//        if (file.exists()) {
//            InputStreamReader inputStreamReader = null;
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                inputStreamReader = new InputStreamReader(fileInputStream, "GB2312");
//                BufferedReader br = new BufferedReader(inputStreamReader);
//                String line = "";
//                StringBuffer sb = new StringBuffer();
//                while ((line = br.readLine()) != null) {
//                    sb.append(line);
//                    sb.append("\n");
//                    listLine.add(line);
//                }
//                for (int i = 0; i < listLine.size(); i++) {
//                    if (listLine.get(i).substring(0, 1).toCharArray()[0] >= 'A' &&
//                            listLine.get(i).substring(0, 1).toCharArray()[0] <= 'Z') {
////                        Log.e("123","大写字母的下标i = "+i);//0  9  17  29
//                        this.y = i;
//                        listy.add(y);
//                        kymcList.add(listLine.get(i));//空域名字
//                        kymingdList.add(listLine.get(i + 1));//最小高度
//                        kymaxgdList.add(listLine.get(i + 2));//最大高度
//                        kydsList.add(listLine.get(i + 3));//某一条空域包含的点数
//                    }
//                }
//                for (int i = 0; i < listy.size(); i++) {
//                    if (i < listy.size() - 1)
//                        listJWLine = listLine.subList(listy.get(i) + 4, listy.get(i + 1));
//                    if (i == listy.size() - 1)
//                        listJWLine = listLine.subList(listy.get(i) + 4, listLine.size());
//                    for (int j = 0; j < listJWLine.size(); j++) {
//
//                        StringBuffer s = new StringBuffer(listJWLine.get(j));
//                        int dxb = listJWLine.get(j).indexOf(".");
//                        int kxb = listJWLine.get(j).indexOf(" ", dxb);
//                        Log.e("xxx", "kxb =" + kxb);
//                        str = s.replace(kxb, kxb + 1, ",").toString();
//
//                        String str_w = str.split("[,]")[0].replaceAll("\\s{1,}", "");
//                        String str_j = str.split("[,]")[1].replaceAll("\\s{1,}", "");
//
//                        int xb_w = str_w.indexOf(".");//xb_w = 5
////                        Log.e("x5x6x", "xb_j =" + xb_j);
//                        double du_w = Double.parseDouble(String.valueOf(Double.parseDouble(str_w) / 100).split("[.]")[0]);//du_j = 109
//                        Log.e("x5x6x", "du_w =" + du_w);
//                        double fen_w = Double.parseDouble(str_w.substring(xb_w - 2, xb_w)) / 60;//fen_j
//                        Log.e("x5x6x", "fen_w =" + fen_w);
//                        StringBuffer sb_w = new StringBuffer(str_w.split("[.]")[1]);
//                        Log.e("x5x6x", "sb_w =" + sb_w.toString());
//                        double miao_w;
//                        if (sb_w.toString().length() < 2) {
//                            miao_w = Double.parseDouble(sb_w.toString()) / 3600;
//                        } else {
//                            miao_w = Double.parseDouble(sb_w.insert(2, '.').toString()) / 3600;//miao_j
//                        }
//                        Log.e("x5x6x", "miao_w =" + miao_w);
//                        double wd = Double.parseDouble(new BigDecimal(du_w + fen_w + miao_w).setScale(8, BigDecimal.ROUND_HALF_UP).toString());
//                        Log.e("x5x6x", "wd =" + wd);
//
//
//                        int xb_j = str_j.indexOf(".");//xb_j = 5
////                        Log.e("x5x6x", "xb_j =" + xb_j);
//                        double du_j = Double.parseDouble(String.valueOf(Double.parseDouble(str_j) / 100).split("[.]")[0]);//du_j = 109
//                        Log.e("x5x6x", "du_j =" + du_j);
//                        double fen_j = Double.parseDouble(str_j.substring(xb_j - 2, xb_j)) / 60;//fen_j
//                        Log.e("x5x6x", "fen_j =" + fen_j);
//                        StringBuffer sb_j = new StringBuffer(str_j.split("[.]")[1]);
//                        Log.e("x5x6x", "sb_j =" + sb_j.toString());
//                        double miao_j = 0;
//                        if (sb_j.toString().length() < 2) {
//                            miao_j = Double.parseDouble(sb_j.toString()) / 3600;
//                        } else {
//                            miao_j = Double.parseDouble(sb_j.insert(2, '.').toString()) / 3600;//miao_j
//                        }
//                        Log.e("x5x6x", "miao_j =" + miao_j);
//                        double jd = Double.parseDouble(new BigDecimal(du_j + fen_j + miao_j).setScale(8, BigDecimal.ROUND_HALF_UP).toString());
//                        Log.e("x5x6x", "jd =" + jd);
//
//                        listqwd.add(String.valueOf(wd));
//                        listqjd.add(String.valueOf(jd));
//
//                    }
//                }
//                for (int i = 0; i < kymcList.size(); i++) {
//                    KongYuBean bean = new KongYuBean();
//                    bean.setKymc(kymcList.get(i));
//                    bean.setKymingd(Integer.parseInt(kymingdList.get(i)));
//                    bean.setKymaxgd(Integer.parseInt(kymaxgdList.get(i)));
//                    bean.setKyds(Integer.parseInt(kydsList.get(i)));
//                    if (i >= 0 && i < kymcList.size()) {
//                        b = a + Integer.parseInt(kydsList.get(i));
//                        kywdList.add(listqwd.subList(a, b));
//                        kyjdList.add(listqjd.subList(a, b));
//                        a = b;
//                    }
//                    bean.setKywds(kywdList.get(i));
//                    bean.setKyjds(kyjdList.get(i));
//                    data_ky.add(bean);
//                }
//                kyAdapter.setKYData(data_ky);
//                kyAdapter.notifyDataSetChanged();
//
//                inputStreamReader.close();
//                fileInputStream.close();
//                br.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Toast.makeText(context, "预读空域文件不存在", Toast.LENGTH_SHORT).show();
////            return;
//        }
//        return data_ky;
//    }


    //原始没有计算的读空域方法
//    public List<KongYuBean> readky() {
//        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "amap" + File.separator + "kongyu" + File.separator + "ky.txt";
//        File file = new File(path);
//        if (file.exists()) {
//            InputStreamReader inputStreamReader = null;
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file);
//                inputStreamReader = new InputStreamReader(fileInputStream, "GB2312");
//                BufferedReader br = new BufferedReader(inputStreamReader);
//                String line = "";
//                StringBuffer sb = new StringBuffer();
//                while ((line = br.readLine()) != null) {
//                    sb.append(line);
//                    sb.append("\n");
////                    Log.e("wj", "line=====" + line);
//                    listLine.add(line);
//                }
//                for (int i = 0; i < listLine.size(); i++) {
//                    if (listLine.get(i).substring(0, 1).toCharArray()[0] >= 'A' &&
//                            listLine.get(i).substring(0, 1).toCharArray()[0] <= 'Z') {//判断首字母为大写字母
////                        Log.e("123","大写字母的下标i = "+i);//0  9  17  29
//                        this.y = i;
//                        listy.add(y);
//                        kymcList.add(listLine.get(i));//空域名字
//                        kymingdList.add(listLine.get(i + 1));//最小高度
//                        kymaxgdList.add(listLine.get(i + 2));//最大高度
//                        kydsList.add(listLine.get(i + 3));//某一条空域包含的点数
//                    }
//                }
//                for (int i = 0; i < listy.size(); i++) {
//                    if (i < listy.size() - 1)
//                        listJWLine = listLine.subList(listy.get(i) + 4, listy.get(i + 1));
//                    if (i == listy.size() - 1)
//                        listJWLine = listLine.subList(listy.get(i) + 4, listLine.size());
//                    for (int j = 0; j < listJWLine.size(); j++) {
////                    Log.e("shu", "listJWLine的第"+i+"个值 = " + listJWLine.get(i));//每一行的经纬度
//                        str = listJWLine.get(j).replaceAll("\\s{1,}", "");//每一行的经纬度
////                        wd = Double.parseDouble(str.substring(0 , 8)) / 100;//每一行的纬度
////                        jd = Double.parseDouble(str.substring(8 , str.length())) / 100;//每一行的经度
//                        listqwd.add(String.valueOf(Double.parseDouble(str.substring(0 , 8)) /100));
//                        listqjd.add(String.valueOf(Double.parseDouble(str.substring(8 , str.length())) /100));
//                    }
//                }
//                for (int i = 0; i < kymcList.size(); i++) {
//                    KongYuBean bean = new KongYuBean();
//                    bean.setKymc(kymcList.get(i));
//                    bean.setKymingd(Integer.parseInt(kymingdList.get(i)));
//                    bean.setKymaxgd(Integer.parseInt(kymaxgdList.get(i)));
//                    bean.setKyds(Integer.parseInt(kydsList.get(i)));
////                    if (i==0){
////                        kywdList.add(listqwd.subList(i,Integer.parseInt(kydsList.get(i))));//(0,5)
////                        kyjdList.add(listqjd.subList(i,Integer.parseInt(kydsList.get(i))));//(0,5)
////                    }
////                    if (i==1){
////                        kywdList.add(listqwd.subList(5,9));
////                        kyjdList.add(listqjd.subList(5,9));
////                    }
////                    if (i==2){
////                        kywdList.add(listqwd.subList(9,17));
////                        kyjdList.add(listqjd.subList(9,17));
////                    }
////                    if (i==kymcList.size()-1){
////                        kywdList.add(listqwd.subList(listqwd.size()-Integer.parseInt(kydsList.get(i)),listqwd.size()));//(17,listqwd.size())
////                        kyjdList.add(listqjd.subList(listqjd.size()-Integer.parseInt(kydsList.get(i)),listqjd.size()));//(17,listqwd.size())
////                    }
//                    if (i >= 0 && i < kymcList.size()) {
//                        b = a + Integer.parseInt(kydsList.get(i));
//                        kywdList.add(listqwd.subList(a, b));
//                        kyjdList.add(listqjd.subList(a, b));
////                        Log.e("123","(a,b) = "+"("+a+" , "+b+")" );
////                        Log.e("123","当 i = "+i+"时,"+"kywdList.get("+i+") = "+ kywdList.get(i));
////                        Log.e("123","当 i = "+i+"时,"+"kyjdList.get("+i+") = "+ kyjdList.get(i));
//                        a = b;
//                    }
//                    bean.setKywds(kywdList.get(i));
//                    bean.setKyjds(kyjdList.get(i));
//                    data_ky.add(bean);
//                }
//                kyAdapter.setKYData(data_ky);
//                kyAdapter.notifyDataSetChanged();
//
//                inputStreamReader.close();
//                fileInputStream.close();
//                br.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Toast.makeText(context, "预读空域文件不存在", Toast.LENGTH_SHORT).show();
////            return;
//        }
//        return data_ky;
//    }

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
