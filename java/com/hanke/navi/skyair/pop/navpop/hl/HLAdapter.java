package com.hanke.navi.skyair.pop.navpop.hl;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.pop.bean.HangLuBean;

import java.util.List;

public class HLAdapter extends BaseAdapter {

    private Context context;
    private List<HangLuBean> item_hl;
    private int  selectHLItem=-1;
    private int pos;

    public HLAdapter(Context context) {
        this.context = context;
    }

    public void setSelectHLItem(int selectHLItem) {
        this.selectHLItem = selectHLItem;
    }

    @Override
    public int getCount() {
        return item_hl==null?0:item_hl.size();
    }

    @Override
    public Object getItem(int position) {
        return item_hl==null?0:item_hl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        pos = position;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder=new ViewHolder();
            convertView = View.inflate(context, R.layout.air_way_item, null);
            viewHolder.tv_air_way_item = (TextView) convertView.findViewById(R.id.tv_air_way_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_air_way_item.setText(item_hl.get(position).getHanglu());

        if (position == selectHLItem) {
            viewHolder.tv_air_way_item.setTextColor(Color.YELLOW);
            convertView.setBackgroundColor(Color.parseColor("#ef6d6a6b"));
        }
        else {
            viewHolder.tv_air_way_item.setTextColor(Color.parseColor("#26cfe9"));
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv_air_way_item;
    }

    public void setHLData(List<HangLuBean> data_hl){
        this.item_hl = data_hl;
    }



}
