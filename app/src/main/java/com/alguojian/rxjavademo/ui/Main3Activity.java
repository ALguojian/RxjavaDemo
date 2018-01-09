package com.alguojian.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alguojian.rxjavademo.OkHttpUtils;
import com.alguojian.rxjavademo.R;
import com.alguojian.rxjavademo.allinterface.RetrofitApi;
import com.alguojian.rxjavademo.entity.Translation;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.alguojian.rxjavademo.base.MyApplication.TTAG;

public class Main3Activity extends AppCompatActivity {

    private RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        this.retrofitApi = OkHttpUtils.newInstance().create(RetrofitApi.class);

//        init();

//        map();

//        flatMap();

        concatMap();

//        buffer();

    }

    /**
     * 接口合并，实例，注册登录
     */
    private void concatMap() {

        retrofitApi.getCall().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(translation -> {
                    translation.show();
                }).observeOn(Schedulers.io())//注册线程结束，作为新的观察者，切换到io此线程（理应为设置subscribeOn(Schedulers.io())）
                //作为观察者，下面又有新的观察者，他就作为老的观察者，也就是新的被观察者，所以调控线程用observeOn(Schedulers.io())
                .concatMap(translation ->
                        //添加注册失败是的判断返回空对象
                        null != translation ? retrofitApi.getCall() : Observable.empty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translation -> {
                    translation.show();
                }, throwable -> {
                    KLog.d(TTAG, throwable.getMessage());
                });
    }

    /**
     * buffer 操作符接受两个参数，buffer(count,skip)，
     * 作用是将 Observable 中的数据按 skip (步长) 分成最大不超过 count 的 buffer ，然后生成一个  Observable 。
     * <p>
     * 意思就是取count个，发射之后，重头开始跳过skip个，在选count个发射，一直到最后一个
     */
    private void buffer() {

        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .buffer(3, 1)//设置缓存区大小==每次从被观察者中获取的事件数量
                //步长：每次获取新事件数量
                .subscribe(integers -> {

                    KLog.d(TTAG, "缓存区数量" + integers.size());
                    for (Integer integer : integers) {
                        KLog.d(TTAG, "事件" + integer);
                    }
                });
    }


    /**
     *
     */
    private void flatMap() {

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
        }).concatMap(integer -> {
            final ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                strings.add("我是事件" + integer + "拆分后的子事件" + i);
            }
            return Observable.fromIterable(strings);
        }).subscribe(s -> {

            KLog.d(TTAG, s);
        });

    }

    private void map() {

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {

            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
        }).map(integer ->
                "这是发送的第" + integer + "条消息")
                .subscribe(s ->
                        KLog.d(TTAG, "接收事件：：" + s));

    }

    /**
     * 轮询查询接口-使用操作符interval
     * 此处主要展示无限次轮询，若要实现有限次轮询，仅需将interval（）改成intervalRange（）即可
     */
    private void init() {
        /**
         * 参数说明：
         * 参数1==第一次延迟时间，1秒后发送查询请求
         * 参数2==间隔时间
         * 参数3==实践单位
         * 该例子发送的事件特点：延迟2s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
         */
        Observable.interval(2, 1, TimeUnit.SECONDS)
                /**
                 * 步骤2：每次发送数字前发送1次网络请求（doOnNext（）在执行Next事件前调用）
                 * 即每隔1秒产生1个数字前，就发送1次网络请求，从而实现轮询需求
                 */
                .doOnNext(aLong -> {
                    KLog.d(TTAG, "第" + aLong + "次查询");

                    retrofitApi.getCall()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Translation>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    //切断
                                    d.dispose();
                                }

                                @Override
                                public void onNext(Translation translation) {
                                    translation.show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    KLog.d(TTAG, "请求失败了：失败原因是：" + e.getMessage());
                                }

                                @Override
                                public void onComplete() {
                                    KLog.d(TTAG, "本次请求结束了");
                                }
                            });

                }).subscribe(aLong -> {

            KLog.d(TTAG, "接收到请求，这是第" + aLong + "次");

        });

    }
}
