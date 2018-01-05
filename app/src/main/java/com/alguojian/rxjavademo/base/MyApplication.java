package com.alguojian.rxjavademo.base;

import android.app.Application;

import com.socks.library.KLog;


/**
 * Application初始化类
 *
 * @author ALguojian
 * @date 2018/1/4
 */
public class MyApplication extends Application {

    public static final String TTAG = "asdfghjkl";

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.init(true, TTAG);
    }
}
