package com.alguojian.rxjavademo;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;

/**
 * 获得Retrofit的实例，retrofit已包含okhttp，不用额外添加依赖
 *
 * @author ALguojian
 * @date 2018/1/8
 */


public class OkHttpUtils {

    private static final String ENDPOINT = "http://fy.iciba.com/";

    //得到retrofir对象
    public static Retrofit newInstance() {

        OkHttpClient.Builder mBuilder = new OkHttpClient().newBuilder();
        mBuilder.readTimeout(10, TimeUnit.SECONDS);
        mBuilder.connectTimeout(9, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mBuilder.addInterceptor(interceptor);

        return new Retrofit.Builder()
                //baseUrl推荐baseUrl使用目录形式，path使用相对路径：path="path";baseUrl="http://192.168.1.122:8080/"
                .baseUrl(ENDPOINT)
                .client(mBuilder.build())
                //支持protobuf
                .addConverterFactory(ProtoConverterFactory.create())
                //支持Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                //支持RxJava
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        /** ExecutorCallAdapterFactory（默认）、
         GuavaCallAdapterFactory、
         Java8CallAdapterFactory、
         RxJavaCallAdapterFactory
         **/
    }


}
