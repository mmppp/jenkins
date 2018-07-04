
package com.hanke.navi.skyair.util;

import java.math.BigDecimal;

import static java.lang.Integer.parseInt;

/**
 * Created by Che on 2017/10/11.
 */
public class RadixConversionUtil {
    //二进制字符串转换为十进制数据
    public static int binaryStringToDecString(String str) {
        int a = parseInt(str, 2);
        return a;
    }

    public static String encodeHex(int integer) {
        StringBuffer buf = new StringBuffer(2);
        if (((int) integer & 0xff) < 0x10) {
            buf.append("");
        }
        buf.append(Long.toString((int) integer & 0xff, 16));
        return buf.toString();
    }

    public static String binaryStringToDecString_ersiwei(String str) {
        int a = parseInt(str, 2);
        if (a > 9) {
            String b = encodeHex(a);
            return b;
        }
        return a + "";
    }

    public static String binaryStringToDecString_jwd_new(String str) {
        BigDecimal b = null;
        int a = Integer.valueOf(str, 2);
        double LSB = 180 / Math.pow(2, 25);
        b = new BigDecimal("" + a).multiply(new BigDecimal(String.valueOf(LSB))).setScale(8, BigDecimal.ROUND_HALF_UP);
        return b.toString();
    }

    public static String binaryStringToDecString_pa(String str) {
        BigDecimal b = null;
        int a = parseInt(str, 2);
        double LSB = 6.25 * 0.3048;
        b = new BigDecimal("" + a).multiply(new BigDecimal(String.valueOf(LSB))).setScale(6, BigDecimal.ROUND_HALF_UP);
        double result = b.doubleValue();
        return b.toString();
    }

    public static String binaryStringToDecString_ve(String str) {
        BigDecimal b = null;
        int a = parseInt(str, 2);
        double LSB = 6.25 * 0.3048 / 60;//1英尺＝0.3048米    1英尺/min = 0.00508m/s       升降速度的单位为米/秒
        b = new BigDecimal("" + a).multiply(new BigDecimal(String.valueOf(LSB))).setScale(6, BigDecimal.ROUND_HALF_UP);
        return b.toString();
    }

    public static String binaryStringToDecString_gs(String str) {
        BigDecimal b = null;
        int a = parseInt(str, 2);
        double LSB = 1 / Math.pow(2, 14) * 1.852 * 3600;//1海里 = 1.852千米（中国标准）          地速的单位为公里/小时
        b = new BigDecimal("" + a).multiply(new BigDecimal(String.valueOf(LSB))).setScale(6, BigDecimal.ROUND_HALF_UP);
        double round = Math.round(b.doubleValue());
        return round + "";
    }

    public static String binaryStringToDecString_fa(String str) {
        BigDecimal b = null;
        int a = parseInt(str, 2);
        double LSB = 2 * Math.PI / Math.pow(2, 16) * 180 / Math.PI;
        b = new BigDecimal("" + a).multiply(new BigDecimal(String.valueOf(LSB))).setScale(6, BigDecimal.ROUND_HALF_UP);
        return b.toString();
    }

    //字节数组转十六进制字符串
    public static String bytesToHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        String m = "";
        for (int i = 0; i < b.length; ++i) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                m = "0" + s;
            } else {
                m = s;
            }
            buffer.append(m);
        }
        return buffer.toString();
    }

    //十六进制字符串转二进制字符串
    public static String hexStringToBinaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4) + "";
        }
        return bString;
    }
}
