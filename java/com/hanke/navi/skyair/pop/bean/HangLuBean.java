package com.hanke.navi.skyair.pop.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HangLuBean implements Parcelable {

    public String hanglu;//航路名称
    public double weidu;
    public double jingdu;//经度
    public double gaodu;//高度

    public HangLuBean(String hanglu) {
        this.hanglu = hanglu;
    }

    public String getHanglu() {
        return hanglu;
    }

    public void setHanglu(String hanglu) {
        this.hanglu = hanglu;
    }

    public double getWeidu() {
        return weidu;
    }

    public void setWeidu(double weidu) {
        this.weidu = weidu;
    }

    public double getJingdu() {
        return jingdu;
    }

    public void setJingdu(double jingdu) {
        this.jingdu = jingdu;
    }

    public double getGaodu() {
        return gaodu;
    }

    public void setGaodu(double gaodu) {
        this.gaodu = gaodu;
    }

    @Override
    public String toString() {
        return "HangLuBean{" +
                "hanglu='" + hanglu + '\'' +
                ", weidu=" + weidu +
                ", jingdu=" + jingdu +
                ", gaodu=" + gaodu +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hanglu);
        dest.writeDouble(this.weidu);
        dest.writeDouble(this.jingdu);
        dest.writeDouble(this.gaodu);
    }

    public HangLuBean() {
    }

    protected HangLuBean(Parcel in) {
        this.hanglu = in.readString();
        this.weidu = in.readDouble();
        this.jingdu = in.readDouble();
        this.gaodu = in.readDouble();
    }

    public static final Creator<HangLuBean> CREATOR = new Creator<HangLuBean>() {
        @Override
        public HangLuBean createFromParcel(Parcel source) {
            return new HangLuBean(source);
        }

        @Override
        public HangLuBean[] newArray(int size) {
            return new HangLuBean[size];
        }
    };
}
