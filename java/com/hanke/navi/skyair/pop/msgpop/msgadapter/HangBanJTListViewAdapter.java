package com.hanke.navi.skyair.pop.msgpop.msgadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.hanke.navi.R;
import com.hanke.navi.skyair.MyApplication;
import com.hanke.navi.skyair.pop.bean.PlaneInfoBean;
import com.hanke.navi.skyair.util.DecimalUtil;
import com.hanke.navi.skyair.util.DistanceUtil;

import java.util.ArrayList;

public class HangBanJTListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PlaneInfoBean> transData;

    public HangBanJTListViewAdapter(Context context) {
        this.context = context;
    }

    public HangBanJTListViewAdapter(Context context, ArrayList<PlaneInfoBean> data) {
        this.context = context;
        this.transData = data;
    }

    @Override
    public int getCount() {
        return transData == null ? 0 : transData.size();
    }

    @Override
    public Object getItem(int position) {
        return transData == null ? 0 : transData.get(position);
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
            convertView = View.inflate(context, R.layout.hangban, null);
            //交通信息
            viewHolder.tv_jt_hangbanhao = (TextView) convertView.findViewById(R.id.tv_hangbanhao);
            viewHolder.tv_jt_juli = (TextView) convertView.findViewById(R.id.tv_xinxi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PlaneInfoBean bean = transData.get(position);

        //交通信息
        viewHolder.tv_jt_hangbanhao.setText(bean.planeNum);
        if (MyApplication.getMyApplication().homePlaneLatlng != null) {
            double distance = DistanceUtil.getInstance().getDistance(bean.latLng, MyApplication.getMyApplication().homePlaneLatlng);
            distance = distance / 1000;
            String s = DecimalUtil.remainDecimal(distance, 1);
            viewHolder.tv_jt_juli.setText(s);
        } else {
            viewHolder.tv_jt_juli.setText("0");
        }
        return convertView;
    }

    public int getDistance(LatLng latlng1, LatLng latlng2) {
        int distance = 0;

        return distance;
    }

    private class ViewHolder {
        TextView tv_jt_hangbanhao, tv_jt_juli;
    }

    public void setHangBanJTData(ArrayList<PlaneInfoBean> data_hangbanjt) {
        this.transData = data_hangbanjt;
        //在这里需要对数据进行一个排序
    }
}
