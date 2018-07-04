package com.hanke.navi.skyair.pop.msgpop.msgbean;


import android.os.Parcel;
import android.os.Parcelable;

public class HangBanJTBean implements Parcelable {
    public String hangbanhao_jt;//交通信息航班号
    public String juli_jt;//距离

    public String getHangbanhao_jt() {
        return hangbanhao_jt;
    }

    public void setHangbanhao_jt(String hangbanhao_jt) {
        this.hangbanhao_jt = hangbanhao_jt;
    }

    public String getJuli_jt() {
        return juli_jt;
    }

    public void setJuli_jt(String juli_jt) {
        this.juli_jt = juli_jt;
    }

    @Override
    public String toString() {
        return "HangBanJTBean{" +
                "hangbanhao_jt='" + hangbanhao_jt + '\'' +
                ", juli_jt='" + juli_jt + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hangbanhao_jt);
        dest.writeString(this.juli_jt);
    }

    public HangBanJTBean() {
    }

    protected HangBanJTBean(Parcel in) {
        this.hangbanhao_jt = in.readString();
        this.juli_jt = in.readString();
    }

    public static final Parcelable.Creator<HangBanJTBean> CREATOR = new Parcelable.Creator<HangBanJTBean>() {
        @Override
        public HangBanJTBean createFromParcel(Parcel source) {
            return new HangBanJTBean(source);
        }

        @Override
        public HangBanJTBean[] newArray(int size) {
            return new HangBanJTBean[size];
        }
    };
}
