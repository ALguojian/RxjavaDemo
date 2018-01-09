package com.alguojian.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alguojian.rxjavademo.OkHttpUtils;
import com.alguojian.rxjavademo.R;
import com.alguojian.rxjavademo.allinterface.RetrofitApi;
import com.alguojian.rxjavademo.entity.BookCommentRequest;
import com.alguojian.rxjavademo.entity.BookCommentResponse;
import com.alguojian.rxjavademo.entity.BookInfo;
import com.alguojian.rxjavademo.entity.BookInfoRequest;
import com.alguojian.rxjavademo.entity.BookInfoResponse;
import com.alguojian.rxjavademo.entity.LoginRequest;
import com.alguojian.rxjavademo.entity.LoginResponse;
import com.alguojian.rxjavademo.entity.RegisterRequest;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava测试demo
 */
public class MainActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;
    private RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.retrofitApi = OkHttpUtils.newInstance().create(RetrofitApi.class);

        retrofitApi.login(new LoginRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        compositeDisposable = new CompositeDisposable();
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(LoginResponse loginRreponse) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        registerAndLogin();

        getBookInfo();

    }

    /**
     * 使用zip来是接口合并，
     * 比如一个界面需要展示用户的一些信息, 而这些信息分别要从两个服务器接口中获取,
     * 而只有当两个都获取到了之后才能进行展示, 这个时候就可以用Zip了:
     */
    private void getBookInfo() {

        Observable<BookInfoResponse> observable1
                = retrofitApi.getBookInfo(new BookInfoRequest())
                .subscribeOn(Schedulers.io());

        Observable<BookCommentResponse> observable2 = retrofitApi.getBookComment(new BookCommentRequest())
                .subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<BookInfoResponse, BookCommentResponse, BookInfo>() {
            @Override
            public BookInfo apply(BookInfoResponse bookInfoResponse, BookCommentResponse bookCommentResponse) throws Exception {
                return new BookInfo(bookInfoResponse, bookCommentResponse);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BookInfo>() {
                    @Override
                    public void accept(BookInfo bookInfo) throws Exception {

                        //请求成功，合并接口响应内容
                    }
                });


    }

    /**
     * 注册登录,使用flatMap或者concatMap操作线程，转换observable为另一个observable
     */
    private void registerAndLogin() {
        retrofitApi.register(new RegisterRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(registerResponse -> {

                }).observeOn(Schedulers.io())
                .concatMap(registerResponse -> retrofitApi.login(new LoginRequest()))
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求登录的结果
                .subscribe(
                        loginResponse ->
                                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show(),
                        throwable ->
                                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
