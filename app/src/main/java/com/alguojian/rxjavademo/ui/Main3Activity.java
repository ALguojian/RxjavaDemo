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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
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

        useDo();
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
