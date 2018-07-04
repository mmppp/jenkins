package com.hanke.navi.skyair.pop.tcpop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.pop.bean.KongYuBean;

import java.util.List;

public class KYAdapter extends BaseAdapter {

    private Context context;
    private List<KongYuBean> item_ky;
    private int selectPos = 0;//选中item位置
    private int  selectKYItem=-1;
    public int pos;

    public KYAdapter(Context context) {
        this.context = context;
    }

    public void setKYData(List<KongYuBean> data_ky){
        this.item_ky = data_ky;
    }
    public void setKYData(List<KongYuBean> data_ky, int selectPos){
        this.item_ky = data_ky;
        this.selectPos = selectPos;//
    }

    public List<KongYuBean> getKYData() {
        return item_ky;
    }

    public void setSelectKYItem(int selectKYItem) {
        this.selectKYItem = selectKYItem;
    }

    @Override
    public int getCount() {
        return item_ky==null?0:item_ky.size();
    }

    @Override
    public Object getItem(int position) {
        return item_ky==null?0:item_ky.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        this.pos = position;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder=new ViewHolder();
            convertView = View.inflate(context, R.layout.kongyu_item, null);
            viewHolder.cb_kongyu_item = (TextView) convertView.findViewById(R.id.cb_kongyu_item);
            viewHolder.img_gou = (ImageView) convertView.findViewById(R.id.img_gou);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        KongYuBean bean = item_ky.get(position);
        viewHolder.cb_kongyu_item.setText(item_ky.get(position).kymc);

        if (item_ky.get(position).isSelect){
//            Log.e("asd","isSelect111 = "+item_ky.get(position).isSelect);//true
            viewHolder.cb_kongyu_item.setTextColor(Color.BLACK);
            viewHolder.img_gou.setVisibility(View.VISIBLE);
            viewHolder.img_gou.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }else{
//            Log.e("asd","isSelect222 = "+item_ky.get(position).isSelect);//false
            viewHolder.cb_kongyu_item.setTextColor(Color.parseColor("#999999"));
            viewHolder.img_gou.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder {
        TextView cb_kongyu_item;
        ImageView img_gou;
    }


}