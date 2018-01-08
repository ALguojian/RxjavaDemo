package com.alguojian.rxjavademo.rxjava;

import com.alguojian.rxjavademo.TimeUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.alguojian.rxjavademo.base.MyApplication.TTAG;

/**
 * RxJava操作符使用工具类
 *
 * @author ALguojian
 * @date 2018/1/4
 */


public class RxJavaUtils {

    private static StringBuilder aaa = new StringBuilder();

    /**
     * map操作符使用,操作消息int转化为string，
     */
    public static void useMap() {

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {

            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onNext(4);
            emitter.onNext(5);

        }).map(new Function<Integer, String>() {

            /**
             * 用于返回新的消息类型
             * @param integer
             * @return
             * @throws Exception
             */
            @Override
            public String apply(Integer integer) throws Exception {
                return "这是第" + integer + "条消息";
            }
        }).subscribe(new Consumer<String>() {

            /**
             * 用户接收map转换之后的新的消息
             * @param s 新的消息
             * @throws Exception
             */
            @Override
            public void accept(String s) throws Exception {

                KLog.d(TTAG, "开始--打印" + s);
            }
        });
    }

    /**
     * 基本使用流程
     * 1.创建被观察者(上游事件)，俗称发射者
     * 2.创建观察者（下游事件），俗称接收者
     * 3.关联二者使用，subscribe
     */
    public static void commentUser() {

        final StringBuilder stringBuilder = new StringBuilder();

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {

            stringBuilder.append("哈哈");
            emitter.onNext(1);
            stringBuilder.append("，我是");
            emitter.onNext(2);
            //下面事件可以发送，但是不再接收
            emitter.onComplete();
            stringBuilder.append("小学生");
            emitter.onNext(3);
        }).subscribe(new Observer<Integer>() {

            /**
             *接收者回调方法
             * @param d 用于判断是否可以接收事件true-停止接受；false可以正常接收
             */
            @Override
            public void onSubscribe(Disposable d) {
                stringBuilder.append("666");
                KLog.d(TTAG, stringBuilder.toString() + "---Disposable---" + d.isDisposed());
            }

            @Override
            public void onNext(Integer s) {
                stringBuilder.append("666");
                KLog.d(TTAG, stringBuilder.toString() + "---onNext初始化");
                switch (s) {
                    case 1:
                        KLog.d(TTAG, "接收到1---" + stringBuilder.toString());
                        break;
                    case 2:
                        KLog.d(TTAG, "接收到2---" + stringBuilder.toString());
                        break;
                    case 3:
                        KLog.d(TTAG, "接收到3---" + stringBuilder.toString());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                KLog.d(TTAG, "处理失败");
            }

            @Override
            public void onComplete() {
                KLog.d(TTAG, "处理完成所有事件");
            }
        });
    }


    /**
     * zip，可以用于接口合并
     * 操作符使用例子
     * zip 专用于合并事件，该合并不是连接（连接操作符后面会说），
     * 而是两两配对，也就意味着，最终配对出的 Observable 发射事件数目只和少的那个相同。
     * <p>
     * zip 组合事件的过程就是分别从发射器 A 和发射器 B 各取出一个事件来组合，并且一个事件只能被使用一次，
     * 组合的顺序是严格按照事件发送的顺序来进行的，所以上面截图中，可以看到，1 永远是和 A 结合的，2 永远是和 B 结合的
     */
    public static void useZip() {

        Observable.zip(getStringObservable(), getIntegerObservable(),
                (s, integer) -> s + integer).subscribe(s -> KLog.d(TTAG, "新的消息字段是" + s));
    }

    private static Observable<String> getStringObservable() {
        return Observable.create((ObservableOnSubscribe<String>) e -> {
            if (!e.isDisposed()) {
                aaa.append("asd");
                e.onNext("A");
                aaa.append("asd");
                e.onNext("B");
                aaa.append("asd");
                aaa.append("zxczxc");
                e.onNext("C");
            }
        }).subscribeOn(Schedulers.io());
    }

    private static Observable<Integer> getIntegerObservable() {
        return Observable.create((ObservableOnSubscribe<Integer>) e -> {
            if (!e.isDisposed()) {

                e.onNext(1);
                aaa.append("--" + 1);

                e.onNext(2);
                aaa.append("--" + 2);

                e.onNext(3);
                aaa.append("--" + 3);

                e.onNext(4);
                aaa.append("--" + 4);

                e.onNext(5);
                aaa.append("--" + 5);
            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     * 对于单一的把两个发射器连接成一个发射器，虽然 zip 不能完成，
     * 但我们还是可以自力更生，官方提供的 concat 让我们的问题得到了完美解决。
     */
    public static void useConcat() {

        Observable.concat(Observable.just(1, 2, 3, 4), Observable.just(6, 7, 8))
                .subscribe(integer -> KLog.d(TTAG, "这是第几个？" + integer));

    }


    /**
     * FlatMap 是一个很有趣的东西，我坚信你在实际开发中会经常用到。
     * 它可以把一个发射器 Observable 通过某种方法转换为多个 Observables，
     * 然后再把这些分散的 Observables装进一个单一的发射器 Observable。但有个需要注意的是，
     * flatMap 并不能保证事件的顺序，如果需要保证，需要用到我们下面要讲的 ConcatMap。
     */
    public static void useFlatMap() {

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
        }).flatMap(integer -> {

            ArrayList<String> strings = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                strings.add("这是第几个" + i);
            }
            int time = (int) (1 + Math.random() * 10);
            //添加延迟效果
            return Observable.fromIterable(strings).delay(time, TimeUnit.MILLISECONDS);
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> KLog.d(TTAG, s));
    }


    /**
     * concatMap 与 FlatMap 的唯一区别就是 concatMap 保证了顺序
     */
    public static void useConcatMap() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {

                ArrayList<String> strings = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    strings.add("这是第几个" + i);
                }
                int time = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(strings).delay(time, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        KLog.d(TTAG, s);
                    }
                });

    }


    /**
     * 发射器发送消息最多十个
     * 消息去重
     */
    public static void useDistinct() {

        Observable.just(1, 1, 1, 2, 2, 2, 12, 2, 3, 3)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        KLog.d(TTAG, "第几条" + integer);
                    }
                });
    }


    /**
     * 添加过滤器,sample是添加给个两秒去一个事件放到水缸中，也是过滤的一种方式
     * <p>
     * 两种方法归根到底其实就是减少放进水缸的事件的数量, 是以数量取胜, 但是这个方法有个缺点, 就是丢失了大部分的事件.
     * <p>
     * 关于OOM
     * 一是从数量上进行治理, 减少发送进水缸里的事件
     * 二是从速度上进行治理, 减缓事件发送进水缸的速度
     * <p>
     * 1.使用 sample 操作符的确定文中已经提到，会丢失部分事件；
     * 2.使用 sleep 延时的操作也并不完美，下游处理过慢（超过 sleep 时间）时依然后丢失事件。
     */
    public static void useFilter() {

        Observable.just(1, 20, -20, 57, 43, 80)
                .filter(integer -> integer % 2 == 1)
                .sample(2, TimeUnit.SECONDS)
                .subscribe(integer -> KLog.d(TTAG, "接收消息是" + integer));
    }


    /**
     * buffer 操作符接受两个参数，buffer(count,skip)，
     * 作用是将 Observable 中的数据按 skip (步长) 分成最大不超过 count 的 buffer ，然后生成一个  Observable 。
     * <p>
     * 意思就是做多取count个，发射之后，重头开始跳过skip个，在选count个发射，一直到最后一个
     */
    public static void useBuffer() {

        Observable.just(1, 2, 3, 4, 5, 0)
                .buffer(4, 2)
                .subscribe(integers -> KLog.d(TTAG, integers));

    }


    /**
     * 设置延迟发送
     * timer 和 interval 均默认在新线程。
     */
    public static void useTimer() {

        KLog.d(TTAG, TimeUtils.getNowTime());
        aaa.append(TimeUtils.getNowTime());

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                // timer 默认在新线程，所以需要切换回主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> KLog.d(TTAG, TimeUtils.getNowTime() + ""));
    }


    /**
     * 设置interval 操作符用于间隔时间执行某个操作，其接受三个参数，分别是第一次发送延迟，间隔时间，时间单位。
     */
    public static void useInterval() {

        KLog.d(TTAG, TimeUtils.getNowTime());
        aaa.append(TimeUtils.getNowTime());

        Disposable subscribe = Observable.interval(3, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> KLog.d(TTAG, aLong + "---" + TimeUtils.getNowTime()));
    }
    //subscribe.dispose();
    //记得activity销毁时取消发送


    /**
     * 让订阅者在接收到数据之前干点有意思的事情
     */
    public static void useDoOnNext() {

        Observable.just(1, 2, 3, 4)
                .doOnNext(integer -> {
                    aaa.append("doOnNext 保存 " + integer);
                    KLog.d(TTAG, aaa.toString());
                }).subscribe(integer -> KLog.d(TTAG, "accept--" + aaa.toString()));
    }


    /**
     * 其实作用就和字面意思一样，接受一个 long 型参数 count ，代表跳过 count 个数目开始接收
     */
    public static void useSkip() {

        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .skip(3)
                .subscribe(integer -> KLog.d(TTAG, integer));
    }


    /**
     * 接受一个 long 型参数 count ，代表至多接收 count 个数据。
     */
    public static void useTake() {

        Flowable.fromArray(1, 2, 3, 4, 5, 6, 7, 7)
                .take(4)
                .subscribe(integer -> KLog.d(TTAG, integer));
    }


    /**
     * 简单的发射器依次调用 onNext() 方法。还调用了 onComplete()。
     */
    public static void useJust() {

        Observable.just("1", "2", "3", "5")
                .subscribeOn(Schedulers.io())
                .subscribe(s -> KLog.d(TTAG, s));
    }

    /**
     * Single 只会接收一个参数，而 SingleObserver 只会调用 onError() 或者 onSuccess()。
     */
    public static void useSingle() {

        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                        KLog.d(TTAG, integer + "");
                    }

                    @Override
                    public void onError(Throwable e) {

                        KLog.d(TTAG, e.toString() + "---" + e.getMessage());
                    }
                });

    }


    /**
     * 去除发送频率过快的项,timeout为分界线
     */
    public static void useDebonunce() {

        Observable.create((ObservableOnSubscribe<Integer>)
                emitter -> {

                    emitter.onNext(1);
                    Thread.sleep(300);
                    emitter.onNext(2);
                    Thread.sleep(500);
                    emitter.onComplete();
                }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> KLog.d(TTAG, integer));
    }

    /**
     * 每次订阅都会创建一个新的 Observable，并且如果没有被订阅，就不会产生新的 Observable。
     */
    public static void useDefer() {

        Observable<Integer> observable = Observable.defer(() -> Observable.just(1, 2, 3, 4));

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

                KLog.d(TTAG, "第几个" + integer);
            }

            @Override
            public void onError(Throwable e) {

                KLog.d(TTAG, "错误是" + e.getMessage());
            }

            @Override
            public void onComplete() {
                KLog.d(TTAG, "完成处理");
            }
        });
    }


    /**
     * 仅取出可观察到的最后一个值，或者是满足某些条件的最后一项。
     */
    public static void useLast() {

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .last(4)
                .subscribe(integer -> KLog.d(TTAG, integer + ""));
    }


    /**
     * 把多个 Observable 结合起来，接受可变参数，也支持迭代器集合。
     * 注意它和 concat 的区别在于，不用等到 发射器 A 发送完所有的事件再进行发射器 B 的发送。
     */
    public static void useMerge() {

        Observable.merge(Observable.just(1, 2, 3), Observable.just(5, 6, 7, 8))
                .subscribe(integer -> KLog.d(TTAG, "第几个" + integer));
    }

    ;


    /**
     * 每次用一个方法处理一个值，可以有一个 seed 作为初始值
     */
    public static void useReduce() {

        Observable.just(1, 2, 3, 4)
                .reduce((integer, integer2) -> {
                    KLog.d(TTAG, integer + "");
                    KLog.d(TTAG, integer2 + "");
                    return integer + integer2;
                }).subscribe(integer -> KLog.d(TTAG, integer + ""));
    }


    /**
     * reduce 是个只追求结果的坏人，而 scan 会始终如一地把每一个步骤都输出。
     */
    public static void useScan() {

        Observable.just(1, 2, 3, 4)
                .scan((integer, integer2) -> {
                    KLog.d(TTAG, integer + "");
                    KLog.d(TTAG, integer2 + "");
                    return integer + integer2;
                }).subscribe(integer -> KLog.d(TTAG, integer + ""));
    }


    /**
     * 按照实际划分窗口，将数据发送给不同的 Observable
     */
    public static void useWindow() {
        aaa.append("哈哈我是");
        Observable.interval(1, TimeUnit.SECONDS)// 间隔一秒发一次
                .take(15)//最多接收15个
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(longObservable -> {
                    aaa.append("aa");
                    KLog.d(TTAG, aaa.toString());
                    longObservable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                aaa.append("bb");
                                KLog.d(TTAG, aaa.toString());
                            });
                });
    }

}
