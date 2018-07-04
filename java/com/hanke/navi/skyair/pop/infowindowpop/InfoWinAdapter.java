package com.hanke.navi.skyair.pop.infowindowpop;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.hanke.navi.R;
import com.hanke.navi.framwork.base.BaseActivity;
import com.hanke.navi.skyair.MyApplication;

public class InfoWinAdapter extends BaseAdapter implements AMap.InfoWindowAdapter {

    private Context context;
    private Marker marker;

    public InfoWinAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.infowin, null);
            viewHolder.ifw_hangbanhao = (TextView) convertView.findViewById(R.id.ifw_hangbanhao);
            viewHolder.ifw_gaodu = (TextView) convertView.findViewById(R.id.ifw_gaodu);
            viewHolder.ifw_sudu = (TextView) convertView.findViewById(R.id.ifw_sudu);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ifw_hangbanhao.setText(marker.getZIndex() + "");
        viewHolder.ifw_gaodu.setText(marker.getTitle());
        viewHolder.ifw_sudu.setText(marker.getSnippet());

        return convertView;

    }

    @Override
    public View getInfoWindow(Marker marker) {//返回的View将用于构造整个info window的窗口
        this.marker = marker;
        View view = getView(0, null, null);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {//返回的View将用于构造info window的显示内容，保留原来的窗口背景和框架
        return null;
    }

    public class ViewHolder {
        TextView ifw_hangbanhao, ifw_gaodu, ifw_sudu;
    }
}