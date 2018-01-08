package com.alguojian.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alguojian.rxjavademo.allinterface.RetrefitApi;
import com.alguojian.rxjavademo.entity.BookCommentRequest;
import com.alguojian.rxjavademo.entity.BookCommentResponse;
import com.alguojian.rxjavademo.entity.BookInfo;
import com.alguojian.rxjavademo.entity.BookInfoRequest;
import com.alguojian.rxjavademo.entity.BookInfoResponse;
import com.alguojian.rxjavademo.entity.LoginRequest;
import com.alguojian.rxjavademo.entity.LoginResponse;
import com.alguojian.rxjavademo.entity.RegisterRequest;
import com.alguojian.rxjavademo.entity.RegisterResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava测试demo
 */
public class MainActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;
    private RetrefitApi retrefitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.retrefitApi = OkHttpUtils.cerat().create(RetrefitApi.class);

        retrefitApi.login(new LoginRequest())
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
                = retrefitApi.getBookInfo(new BookInfoRequest())
                .subscribeOn(Schedulers.io());

        Observable<BookCommentResponse> observable2 = retrefitApi.getBookComment(new BookCommentRequest())
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
        retrefitApi.register(new RegisterRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {

                    }
                }).observeOn(Schedulers.io())
                .concatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        return retrefitApi.login(new LoginRequest());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求登录的结果
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
