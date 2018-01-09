package com.alguojian.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alguojian.rxjavademo.OkHttpUtils;
import com.alguojian.rxjavademo.R;
import com.alguojian.rxjavademo.allinterface.RetrofitApi;
import com.alguojian.rxjavademo.entity.LoginResponse;
import com.alguojian.rxjavademo.entity.RegisterRequest;
import com.alguojian.rxjavademo.entity.RegisterResponse;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Main2Activity extends AppCompatActivity {

    private RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        this.retrofitApi = OkHttpUtils.newInstance().create(RetrofitApi.class);

        //表单形式上传文件
        RequestBody name = RequestBody.create(MediaType.parse("name"), "xiaoming");
        RequestBody age = RequestBody.create(MediaType.parse("age"), "24");
        RequestBody file = RequestBody.create(MediaType.parse("file"), "C:/asd");

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "aa.txt", file);
        retrofitApi.testMuilt(new RegisterRequest(), name, filePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RegisterResponse registerResponse) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        retrofitApi.register(new RegisterRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {


                    }
                });


        userPartAndPartMap();
    }


    /**
     * 练习使用使用
     */
    private void userPartAndPartMap() {

        MediaType textType = MediaType.parse("text/plain");
        RequestBody name = RequestBody.create(textType, "Carson");
        RequestBody age = RequestBody.create(textType, "24");
        RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), "这里是模拟文件的内容");

        // @Part
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
        Observable<LoginResponse> call3 = retrofitApi.testPart(name, age, filePart);

        // @PartMap
        // 实现和上面同样的效果
        Map<String, RequestBody> fileUpload2Args = new HashMap<>();
        fileUpload2Args.put("name", name);
        fileUpload2Args.put("age", age);
        //这里并不会被当成文件，因为没有文件名(包含在Content-Disposition请求头中)，但上面的 filePart 有
        //fileUpload2Args.put("file", file);
        Observable<LoginResponse> call4 = retrofitApi.testPartMap(fileUpload2Args, filePart); //单独处理文件


    }
}
