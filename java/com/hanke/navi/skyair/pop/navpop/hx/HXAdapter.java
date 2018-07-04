package com.hanke.navi.skyair.pop.navpop.hx;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.HangXianBean;
import com.hanke.navi.skyair.ui.MainActivity;

import java.util.List;

public class HXAdapter extends BaseAdapter {

    private Context context;
    private List<HangXianBean> item_hx;
    private int selectHXItem = -1;

    public HXAdapter(Context context) {
        this.context = context;
    }

    public HXAdapter(Context context, List<HangXianBean> item_hx) {
        this.context = context;
        this.item_hx = item_hx;
    }

    public void setSelectHXItem(int selectHXItem) {
        this.selectHXItem = selectHXItem;
    }

    @Override
    public int getCount() {
        return item_hx == null ? 0 : item_hx.size();
    }

    @Override
    public Object getItem(int position) {
        return item_hx == null ? 0 : item_hx.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.air_way_item, null);
            viewHolder.tv_air_way_item = (TextView) convertView.findViewById(R.id.tv_air_way_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //正在执行飞行计划
        if (!MainActivity.instence.flag_zx) {
            if (item_hx.get(position).getHangxian().equals(MyApplication.getMyApplication().flyingPlanName)) {
                viewHolder.tv_air_way_item.setTextColor(Color.YELLOW);
            }
        }
        viewHolder.tv_air_way_item.setText(item_hx.get(position).hangxian);
        return convertView;
    }

    private class ViewHolder {
        TextView tv_air_way_item;
    }

    public void setHXData(List<HangXianBean> data_hx) {
        this.item_hx = data_hx;
    }
}
