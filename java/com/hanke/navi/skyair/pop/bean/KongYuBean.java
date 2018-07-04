package com.hanke.navi.skyair.pop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class KongYuBean implements Parcelable {

    public String kymc;//空域名称
    public int kymingd;//最小空域高度
    public int kymaxgd;//最大空域高度
    public int kyds;//空域点数
    public List<String> kywds;//空域纬度
    public List<String> kyjds;//空域经度

    public boolean isSelect;//是否选中

//    public KongYuBean() {
//    }

    public String getKymc() {
        return kymc;
    }

    public void setKymc(String kymc) {
        this.kymc = kymc;
    }

    public int getKymingd() {
        return kymingd;
    }

    public void setKymingd(int kymingd) {
        this.kymingd = kymingd;
    }

    public int getKymaxgd() {
        return kymaxgd;
    }

    public void setKymaxgd(int kymaxgd) {
        this.kymaxgd = kymaxgd;
    }

    public int getKyds() {
        return kyds;
    }

    public void setKyds(int kyds) {
        this.kyds = kyds;
    }

    public List<String> getKywds() {
        return kywds;
    }

    public void setKywds(List<String> kywds) {
        this.kywds = kywds;
    }

    public List<String> getKyjds() {
        return kyjds;
    }

    public void setKyjds(List<String> kyjds) {
        this.kyjds = kyjds;
    }

    @Override
    public String toString() {
        return "KongYuBean{" +
                "kymc='" + kymc + '\'' +
                ", kymingd=" + kymingd +
                ", kymaxgd=" + kymaxgd +
                ", kyds=" + kyds +
                ", kywds=" + kywds +
                ", kyjds=" + kyjds +
                ", isSelect=" + isSelect +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kymc);
        dest.writeInt(this.kymingd);
        dest.writeInt(this.kymaxgd);
        dest.writeInt(this.kyds);
        dest.writeList(this.kywds);
        dest.writeList(this.kyjds);
    }

    public KongYuBean() {
    }

    protected KongYuBean(Parcel in) {
        this.kymc = in.readString();
        this.kymingd = in.readInt();
        this.kymaxgd = in.readInt();
        this.kyds = in.readInt();
        this.kywds = new ArrayList<String>();
        in.readList(this.kywds, Double.class.getClassLoader());
        this.kyjds = new ArrayList<String>();
        in.readList(this.kyjds, Double.class.getClassLoader());
    }

    public static final Creator<KongYuBean> CREATOR = new Creator<KongYuBean>() {
        @Override
        public KongYuBean createFromParcel(Parcel source) {
            return new KongYuBean(source);
        }

        @Override
        public KongYuBean[] newArray(int size) {
            return new KongYuBean[size];
        }
    };
}
