package com.hanke.navi.skyair.pop.msgpop.msgbean;


import android.os.Parcel;
import android.os.Parcelable;

public class HangBanGJBean implements Parcelable {
    public String hangbanhao_gj;//告警信息航班号
    public String jinggaoxinxi_gj;//警告信息

    public String getHangbanhao_gj() {
        return hangbanhao_gj;
    }

    public void setHangbanhao_gj(String hangbanhao_gj) {
        this.hangbanhao_gj = hangbanhao_gj;
    }

    public String getJinggaoxinxi_gj() {
        return jinggaoxinxi_gj;
    }

    public void setJinggaoxinxi_gj(String jinggaoxinxi_gj) {
        this.jinggaoxinxi_gj = jinggaoxinxi_gj;
    }

    @Override
    public String toString() {
        return "HangBanGJBean{" +
                "hangbanhao_gj='" + hangbanhao_gj + '\'' +
                ", jinggaoxinxi_gj='" + jinggaoxinxi_gj + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hangbanhao_gj);
        dest.writeString(this.jinggaoxinxi_gj);
    }

    public HangBanGJBean() {
    }

    protected HangBanGJBean(Parcel in) {
        this.hangbanhao_gj = in.readString();
        this.jinggaoxinxi_gj = in.readString();
    }

    public static final Parcelable.Creator<HangBanGJBean> CREATOR = new Parcelable.Creator<HangBanGJBean>() {
        @Override
        public HangBanGJBean createFromParcel(Parcel source) {
            return new HangBanGJBean(source);
        }

        @Override
        public HangBanGJBean[] newArray(int size) {
            return new HangBanGJBean[size];
        }
    };
}
