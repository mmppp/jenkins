package com.hanke.navi.skyair.util;

import java.text.DecimalFormat;

/**
 * Created by Che on 2017/8/25.
 */
public class DecimalUtil {

    public static String remainDecimal(double number,int remainNum){

        String result = "";
        String temp = "###.";
        for(int x=0;x<remainNum;x++){
            temp = temp+"0";
        }
        DecimalFormat df = new DecimalFormat(temp);
        result = df.format(number);

        return result;
    }

}
