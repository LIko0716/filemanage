package com.hzvtc1063.filemanage.utils;

import java.util.Calendar;
import java.util.Random;

/**
 * @author hangzhi1063
 * @date 2020/12/19 21:16
 */
public class TimeUtils {

    public static String getFileName() {
        Random rand = new Random();//生成随机数
        int random = rand.nextInt();
        Calendar calCurrent = Calendar.getInstance();
        int intDay = calCurrent.get(Calendar.DATE);
        int intMonth = calCurrent.get(Calendar.MONTH) + 1;
        int intYear = calCurrent.get(Calendar.YEAR);
        String now = String.valueOf(intYear) + "_" + String.valueOf(intMonth) + "_" +
                String.valueOf(intDay) + "_";
        return now + String.valueOf(random > 0 ? random : (-1) * random);
    }
}
