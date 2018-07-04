package com.hanke.navi.skyair.pop.msgpop.msgadapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanke.navi.R;
import com.hanke.navi.skyair.pop.msgpop.msgbean.HangBanGJBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HangBanGJListViewAdapter extends BaseAdapter {

    private Context context;
    public HashMap<String, HangBanGJBean> items_hangbangj;
    public ArrayList<HangBanGJBean> data_hangbangj;

    public HangBanGJListViewAdapter(Context context) {
        this.context = context;
    }

    public HangBanGJListViewAdapter(Context context, HashMap<String, HangBanGJBean> items_hangbangj) {
        this.context = context;
        this.items_hangbangj = items_hangbangj;
    }

    @Override
    public int getCount() {
        Log.i("hahaha", "items_gj" + items_hangbangj.size());
        return items_hangbangj == null ? 0 : items_hangbangj.size();
    }

    @Override
    public Object getItem(int position) {


        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.hangban_two, null);
            //警告信息
            viewHolder.tv_gj_hangbanhao = (TextView) convertView.findViewById(R.id.tv_hangbanhao);
            viewHolder.tv_gj_jinggaoxinxi = (TextView) convertView.findViewById(R.id.tv_xinxi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HangBanGJBean banGJBean = data_hangbangj.get(position);
        if (banGJBean.hangbanhao_gj.contains("homeplane")) {
            viewHolder.tv_gj_hangbanhao.setText("本机");
        } else {
            viewHolder.tv_gj_hangbanhao.setText(banGJBean.hangbanhao_gj);
        }
        viewHolder.tv_gj_jinggaoxinxi.setText(banGJBean.jinggaoxinxi_gj);

        //警告信息
        return convertView;
    }

    private class ViewHolder {
        TextView tv_gj_hangbanhao, tv_gj_jinggaoxinxi;
    }

    public void setHangBanGJData(HashMap<String, HangBanGJBean> items_hangbangj) {
        this.items_hangbangj = items_hangbangj;
        this.data_hangbangj = mapToList(this.items_hangbangj);
    }

    public ArrayList<HangBanGJBean> mapToList(HashMap<String, HangBanGJBean> items_hangbangj) {
        ArrayList<HangBanGJBean> result = new ArrayList<>();
        Set<Map.Entry<String, HangBanGJBean>> entries = items_hangbangj.entrySet();
        Iterator<Map.Entry<String, HangBanGJBean>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HangBanGJBean> next = iterator.next();
            result.add(next.getValue());
        }

        return result;
    }

}
