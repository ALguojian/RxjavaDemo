package com.alguojian.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alguojian.rxjavademo.OkHttpUtils;
import com.alguojian.rxjavademo.R;
import com.alguojian.rxjavademo.allinterface.RetrofitApi;
import com.alguojian.rxjavademo.entity.Translation;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiPredicate;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
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

//        concatMap();

//        buffer();

//        concat();

//        merge();

//        concatArrayDelayErrorTest();

//        combineLatest();

//        collect();

//        startWith();

//        count();

//        zip();

//        useDo();

//        onErrorReturn();

//        onErrorResumeNext();

//        onExceptionResumeNext();

//        retry();

//        retryUntil();

//        retryWhen();

//        repeat();

        repeatWhen();
    }


    /**
     * 有条件地、重复发送 被观察者事件
     * 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））
     * 转换成1个 Object 类型数据传递给1个新被观察者（Observable），以此决定是否重新订阅 & 发送原来的 Observable
     * <p>
     * 返回结果分为两种情况：
     * 1.若新被观察者（Observable）返回1个Complete / Error事件，则不重新订阅 & 发送原来的 Observable
     * 2.若新被观察者（Observable）返回其余事件时，则重新订阅 & 发送原来的 Observable
     */
    private void repeatWhen() {

        Observable.just(1,2,3)
                .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {

                        // 在Function函数中，必须对输入的 Observable<Object>进行处理，这里使用的是flatMap操作符接收上游的数据

                        return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Object o) throws Exception {

                                //情况1：若新被观察者（Observable）返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable

                                // Observable.empty() = 发送Complete事件，但不会回调观察者的onComplete（）

                                // return Observable.error(new Throwable("不再重新订阅事件"));
                                // 返回Error事件 = 回调onError（）事件，并接收传过去的错误信息。

                                // 情况2：若新被观察者（Observable）返回其余事件，则重新订阅 & 发送原来的 Observable
                                 return Observable.just(1);
                                // 仅仅是作为1个触发重新订阅被观察者的通知，发送的是什么数据并不重要，只要不是Complete（） /  Error（）事件
                            }
                        });
                    }
                }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                KLog.d(TTAG, "开始连接");
            }
            @Override
            public void onNext(Integer integer) {
                KLog.d(TTAG, "收到事件：" + integer);
            }
            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "收到错误是："+e.getMessage());
            }
            @Override
            public void onComplete() {
                KLog.d(TTAG, "完成");
            }
        });

    }


    /**
     * 无条件地、重复发送 被观察者事件
     */
    private void repeat() {

        Observable.just(1, 2, 3, 4)
                //设置重复发送次数3次
                .repeat(3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        KLog.d(TTAG, "开始了链接");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        KLog.d(TTAG, "接收事件是：" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.d(TTAG, "收到错误了");
                    }

                    @Override
                    public void onComplete() {
                        KLog.d(TTAG, "完成");
                    }
                });

    }


    /**
     * 遇到错误时，将发生的错误传递给一个新的被观察者（Observable），
     * 并决定是否需要重新订阅原始被观察者（Observable）& 发送事件
     */
    private void retryWhen() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Exception("发送错误"));
                emitter.onNext(6);
                emitter.onNext(7);
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {

                // 参数Observable<Throwable>中的泛型 = 上游操作符抛出的异常，可通过该条件来判断异常的类型
                // 返回Observable<?> = 新的被观察者 Observable（任意类型）
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {

                        // 1. 若返回的Observable发送的事件 = Error事件，则原始的Observable不重新发送事件
                        // 该异常错误信息可在观察者中的onError（）中获得
//                        return Observable.error(new Throwable("终止了"));

                        // 2. 若返回的Observable发送的事件 = Next事件，则原始的Observable重新发送事件（若持续遇到错误，则持续重试）
                        return Observable.just(1);
                    }
                });
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

                KLog.d(TTAG, "收到事件：" + integer);
            }

            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "错误信息是：" + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }


    /**
     * 出现错误后，判断是否需要重新发送数据
     * <p>
     * 若需要重新发送 & 持续遇到错误，则持续重试
     * 作用类似于retry（Predicate predicate）
     * 返回false就一直重试
     * 返回true结束
     */
    private void retryUntil() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onError(new Exception("发生错误了"));
                emitter.onNext(5);
                emitter.onNext(8);
            }
        }).retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {

                //返回false就一直重试
                //返回true结束
                return true;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

                KLog.d(TTAG, "onNext:" + integer);
            }

            @Override
            public void onError(Throwable e) {

                KLog.d(TTAG, "错误是：" + e.getMessage());

            }

            @Override
            public void onComplete() {

            }
        });

    }


    /**
     * 当出现错误时，让被观察者（Observable）重新发射数据
     * Throwable 和 Exception都可拦截
     * <p>
     * 1. retry（）
     * 作用：出现错误时，让被观察者重新发送数据
     * 注：若一直错误，则一直重新发送
     * <p>
     * 2. retry（long time）
     * 作用：出现错误时，让被观察者重新发送数据（具备重试次数限制
     * 参数 = 重试次数
     * <p>
     * 3. retry（Predicate predicate）
     * 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送& 持续遇到错误，则持续重试）
     * 参数 = 判断逻辑
     * <p>
     * 4. retry（new BiPredicate<Integer, Throwable>）
     * 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送 & 持续遇到错误，则持续重试
     * 参数 =  判断逻辑（传入当前重试次数 & 异常错误信息）
     * <p>
     * 5. retry（long time,Predicate predicate）
     * 作用：出现错误后，判断是否需要重新发送数据（具备重试次数限制
     * 参数 = 设置重试次数 & 判断逻辑
     */
    private void retry() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onError(new Throwable("发送错误了"));
                emitter.onNext(5);
                emitter.onNext(6);
            }
            //遇到错误时，让被观察者重新发射数据（若一直错误，则一直重新发送
        }).retry()
                //遇到错误时，重试3次
                .retry(3)
                //拦截错误后，判断是否需要重新发送请求
                .retry(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        KLog.d(TTAG, "错误是：" + throwable.getMessage());

                        //返回false = 不重新重新发送数据 & 调用观察者的onError结束
                        //返回true = 重新发送请求（若持续遇到错误，就持续重新发送）
                        return throwable.getMessage().equals("我是判定错误");
                    }
                    //出现错误后，判断是否需要重新发送数据（若需要重新发送 & 持续遇到错误，则持续重试
                    // 参数 =  判断逻辑（传入当前重试次数 & 异常错误信息）
                }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer integer, Throwable throwable) throws Exception {
                KLog.d(TTAG, "错误是：" + throwable.getMessage());
                KLog.d(TTAG, "重试次数是：" + integer);

                return true;
            }
            // 作用：出现错误后，判断是否需要重新发送数据（具备重试次数限制
            // 参数 = 设置重试次数 & 判断逻辑
        }).retry(3, new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                KLog.d(TTAG, "错误是：" + throwable.getMessage());

                //返回false = 不重新重新发送数据 & 调用观察者的onError（）结束
                //返回true = 重新发送请求（最多重新发送3次）
                return true;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    private void onExceptionResumeNext() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onError(new Exception("发生错误了呢"));
            }
        }).onExceptionResumeNext(new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {

                observer.onNext(11);
                observer.onNext(22);
                observer.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

                KLog.d(TTAG, "接收到事件：" + integer);
            }

            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "失败了");
            }

            @Override
            public void onComplete() {
                KLog.d(TTAG, "结束了");
            }
        });

    }

    /**
     * 方案2
     * 发送新的eObservable
     * 两种方式
     * onErrorResumeNext( )拦截的错误=Throwable；需要拦截Exception使用下面的方式
     * <p>
     * onExceptionResumeNext( )如果拦截的错误=Exception，则会发送新的Observable，不会走onerror（）方法
     * 如果拦截到Throwable错误，会将错误传递给观察者的onError方法，不在发送新的Observable
     */
    private void onErrorResumeNext() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onError(new Throwable("发生错误了呢"));
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {

                KLog.d(TTAG, "onErrorResumeNext:" + throwable.getMessage());

                return Observable.just(7, 3, 6, 8);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

                KLog.d(TTAG, "接收到事件：" + integer);
            }

            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "失败了");
            }

            @Override
            public void onComplete() {
                KLog.d(TTAG, "结束了");
            }
        });
    }


    /**
     * 关于错误的解决方案
     */
    private void onErrorReturn() {


        /**
         * 方案1
         * 发送一个特殊书剑，正常结束
         */
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onComplete();
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onNext(4);
            emitter.onError(new Throwable("发生错误了"));
        }).onErrorReturn(throwable -> {
            KLog.d(TTAG, "在onErrorReturn处理了错误::" + throwable.getMessage());

            //发生错误时发送一个事件，正常结束
            return 666;
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                KLog.d(TTAG, "接收到事件：" + integer);
            }

            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "失败了");
            }

            @Override
            public void onComplete() {
                KLog.d(TTAG, "结束了");
            }
        });
    }


    /**
     * do操作符
     */
    private void useDo() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onError(new Throwable("发送错误"));
            }
            //1. 当Observable每发送1次数据事件就会调用1次
        }).doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> integerNotification) throws Exception {

                KLog.d(TTAG, "doOnEach:" + integerNotification);
            }
            // 2. 执行Next事件前调用
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                KLog.d(TTAG, "doOnNext:" + integer);
            }
            //3.执行Next事件后调用
        }).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                KLog.d(TTAG, "doOnCompleted:");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

                KLog.d(TTAG, "doOnError:" + throwable.getMessage());
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                KLog.d(TTAG, "doOnSubscribe:");
            }
        }).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                KLog.d(TTAG, "doAfterTerminate");
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                KLog.d(TTAG, "doFinally");
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                KLog.d(TTAG, "开始发射了");
            }

            @Override
            public void onNext(Integer integer) {
                KLog.d(TTAG, "接收到事件：" + integer);
            }

            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "发生错误了：" + e.getMessage());
            }

            @Override
            public void onComplete() {
                KLog.d(TTAG, "处理完成了");
            }
        });
    }


    /**
     * 合并数据源
     */
    private void zip() {

        Observable.zip(
                retrofitApi.getCall().subscribeOn(Schedulers.io()),
                retrofitApi.getCall().subscribeOn(Schedulers.io()),
                (translation, translation2) ->
                        translation.toString() + translation2.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    KLog.d(TTAG, "合并的数据源是：" + s.toString());
                }, throwable -> {

                });
    }

    /**
     * 统计被观察者发送事件的数量
     */
    private void count() {

        Observable.just(1, 2, 3, 4)
                .count()
                .subscribe(aLong -> {
                    KLog.d(TTAG, "发送事件数量是：" + aLong);
                });
    }


    /**
     * 在一个被观察者发送事件前，追加发送一些数据
     * 后追加，先调用，组合模式
     */
    private void startWith() {

        Observable.just(2, 3, 4, 5)
                .startWith(0)
                .startWith(Observable.just(7, 8))
                .startWithArray(1)
                .subscribe(integer -> {

                });
    }

    /**
     * 将被观察者Observable发送的数据事件收集到一个数据结构里
     */
    private void collect() {

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .collect((Callable<ArrayList<Integer>>) () ->
                                new ArrayList<>(),
                        (integers, integer) -> {
                            integers.add(integer);
                        }).subscribe(integers ->
                KLog.d(TTAG, integers.toString()));

    }

    /**
     * 当两个Observables中的任何一个发送了数据后，
     * 将先发送了数据的Observables 的最新（最后）一个数据 与
     * 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
     */
    private void combineLatest() {

        Observable.combineLatest(
                Observable.just(1L, 2L, 3L, 4L, 5L),
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                (aLong, aLong2) -> {
                    KLog.d(TTAG, aLong);
                    KLog.d(TTAG, aLong2);
                    return aLong + aLong2;
                }
        ).subscribe(aLong -> {
            KLog.d(TTAG, aLong);
        });

    }


    /**
     * 使用conat以及merge操作符时，如果某个发射者发出error()时间，则会总结整个流程，
     * 我们希望onError（）事件推迟到其他发射者都发送完时间之后后才会触发，
     * 即可使用` concatDelayError()`以及`mergeDelayError()`
     */
    private void concatArrayDelayErrorTest() {

        Observable.concatArrayDelayError(Observable.create(emitter -> {

            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            // 发送Error事件，因为使用了concatDelayError，所以第2个Observable将会发送事件，等发送完毕后，再发送错误事件
            emitter.onError(new NullPointerException());
            emitter.onComplete();

        }), Observable.just(4, 5, 6))
                .subscribe(integer -> {
                });
    }

    /**
     * 合并发射者，按时间线执行
     */

    String resultss = "数据源来自：";

    private void merge() {

//        Observable.merge(
//                //延迟发送操作符
//                //从0开始发送，发送3个数据，第一次发件延迟时间1秒。间隔时间1s
//                //
//                Observable.intervalRange(0,3,1,1,TimeUnit.SECONDS),
//                Observable.intervalRange(2,3,1,1,TimeUnit.SECONDS)
//        ).subscribe(aLong -> {
//
//        });

        Observable.merge(
                Observable.just("网络"),
                Observable.just("本地文件")
        ).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                resultss += s;
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

                KLog.d(TTAG, "接收完成统一处理事件：" + resultss);
            }
        });
    }

    /**
     * 该类型的操作符的作用 = 组合多个被观察者
     * 组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
     * concat()
     * concatArray()
     * <p>
     * 实例：从内存以及磁盘和网络获取缓存
     */

    String memoryCache = null;
    String diskCache = "磁盘缓存数据";

    private void concat() {

        Observable.concat(Observable.just(1, 2)//发射者数量不超过4个
                , Observable.just(3, 4)
                , Observable.just(7, 8))
                .subscribe(integer -> {
                });

        Observable.concatArray(Observable.just(1, 2)//被观察者数量不受限制
                , Observable.just(4, 5)
                , Observable.just(7, 8)
                , Observable.just(3, 6))
                .subscribe(integer -> {

                });

        Observable.concat(
                Observable.create(emitter -> {

                    //判断内存是否含有缓存
                    if (null == memoryCache) {
                        emitter.onComplete();
                    } else {
                        emitter.onNext(memoryCache);
                    }
                }),
                Observable.create(emitter -> {

                    //判断磁盘
                    if (null == diskCache) {
                        emitter.onComplete();
                    } else {
                        emitter.onNext(diskCache);
                    }
                }),
                Observable.create((ObservableOnSubscribe<String>) emitter -> {

                    emitter.onNext("从网络获取缓存数据");
                })
                //通过firstElement()，从串联队列中取出并发送第1个有效事件（Next事件），即依次判断检查memory、disk、network
        ).firstElement()
                // 即本例的逻辑为：
                // a. firstElement()取出第1个事件 = memory，即先判断内存缓存中有无数据缓存；由于memoryCache = null，即内存缓存中无数据，所以发送结束事件（视为无效事件）
                // b. firstElement()继续取出第2个事件 = disk，即判断磁盘缓存中有无数据缓存：由于diskCache ≠ null，即磁盘缓存中有数据，所以发送Next事件（有效事件）
                // c. 即firstElement()已发出第1个有效事件（disk事件），所以停止判断。

                .subscribe(s -> {

                    KLog.d(TTAG, "缓存获得路径是：" + s.toString());
                });
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
