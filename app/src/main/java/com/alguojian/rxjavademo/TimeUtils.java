package com.alguojian.rxjavademo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ${DESCRIPTION}
 *
 * @author ALguojian
 * @date 2018/1/4
 */


public class TimeUtils {


    public static String getNowTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new Date();
        return  sdf.format(date);
    }

}
