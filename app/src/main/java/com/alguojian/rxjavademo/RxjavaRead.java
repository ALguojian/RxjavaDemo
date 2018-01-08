package com.alguojian.rxjavademo;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * rxjava介绍代码
 *
 * @author ALguojian
 * @date 2018/1/5
 */
public class RxjavaRead {


    Disposable mDisposable;
    int i;

    /**
     * 上游可以发送无限个onNext, 下游也可以接收无限个onNext.
     * 当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送,
     * 而下游收到onComplete事件之后将不再继续接收事件.
     * 当上游发送了一个onError后, 上游onError之后的事件将继续发送,
     * 而下游收到onError事件之后将不再继续接收事件.
     * 上游可以不发送onComplete或onError.
     * 最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete,
     * 也不能发多个onError, 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
     */
    public void getSomeThing() {

        //创建上游，被观察者，发射者
        //它可以发出三种类型的事件，通过调用emitter的onNext(T value)、
        // onComplete()和onError(Throwable error)就可以分别发出next事件、complete事件和error事件。

        //注: 关于onComplete和onError唯一并且互斥这一点,
        // 是需要自行在代码中进行控制, 如果你的代码逻辑中违背了这个规则,
        // **并不一定会导致程序崩溃. ** 比如发送多个onComplete是可以正常运行的,
        // 依然是收到第一个onComplete就不再接收了, 但若是发送多个onError,
        // 则收到第二个onError事件会导致程序会崩溃.
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            /**
             *
             * @param emitter 发射器 它可以发出三种类型的事件，通过调用emitter的onNext(T value)、
             *                  onComplete()和onError(Throwable error)就可以分别发出next事件、complete事件和error事件。
             *
             *                ---------------------------------注意如下--------------------------
             *
             *                1.上游可以发送无限个onNext, 下游也可以接收无限个onNext.
             *
             *                2.当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送,
             *                  而下游收到onComplete事件之后将不再继续接收事件.
             *
             *                3.当上游发送了一个onError后, 上游onError之后的事件将继续发送,
             *                  而下游收到onError事件之后将不再继续接收事件.
             *
             *                4.上游可以不发送onComplete或onError.
             *
             *                5.最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete,
             *                  也不能发多个onError, 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
             *
             * @throws Exception
             */
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        });


        //创建下游，观察者，
        Observer<Integer> integerObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(Integer o) {
                //调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.
                if (i == 2) {
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(integerObserver);
    }

    //关联字段subscribe()

    /**
     * public final Disposable subscribe() {}
     * public final Disposable subscribe(Consumer<? super T> onNext) {}
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {}
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {}
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {}
     * public final void subscribe(Observer<? super T> observer) {}
     **/

    /**不带任何参数的subscribe() 表示下游不关心任何事件,你上游尽管发你的数据去吧, 老子可不管你发什么.**/

    /**
     * 带有一个Consumer参数的方法表示下游只关心onNext事件, 其他的事件我假装没看见, 因此我们如果只需要onNext事件可以这么写:
     */
    public void useConsumer() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });

    }

    /**
     * 关于线程的调控，上下游默认是在同一个线程工作
     * <p>
     * 指定上游发送事件的线程，多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
     * .subscribeOn(Schedulers.newThread())
     * <p>
     * 指定下游接收事件所在线程，多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
     * .observeOn(AndroidSchedulers.mainThread())
     * <p>
     * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
     * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
     * Schedulers.newThread() 代表一个常规的新线程
     * AndroidSchedulers.mainThread() 代表Android的主线程
     **/
    //例子如下
    public void aa() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        }).subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });

    }


    /**
     * Flowable在设计的时候采用了一种新的思路也就是响应式拉取的方式来更好的解决上下游流速不均衡的问题.
     * <p>
     * 上游根据下游的处理能力来决定发送多少事件, 就不会造成一窝蜂的发出一堆事件来, 从而导致OOM.
     * <p>
     * 上有事件发送也要做特殊处理
     * <p>
     * <p>
     * 为什么上游发送第一个事件后下游就抛出了MissingBackpressureException异常,
     * 这是因为下游没有调用request, 上游就认为下游没有处理事件的能力, 而这又是一个同步的订阅,
     * 既然下游处理不了, 那上游不可能一直等待吧, 如果是这样, 万一这两根水管工作在主线程里, 界面不就卡死了吗, 因
     * 此只能抛个异常来提醒我们. 那如何解决这种情况呢, 很简单啦, 下游直接调用request(Long.MAX_VALUE)就行了,
     * 或者根据上游发送事件的数量来request就行了, 比如这里request(3)就可以了.
     * <p>
     * 在Flowable里默认有一个大小为128的水缸, 当上下游工作在不同的线程中时,
     * 上游就会先把事件发送到这个水缸中, 因此, 下游虽然没有调用request,
     * 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.
     * <p>
     * 用BackpressureStrategy.ERROR这种方式, 这种方式会在出现上下游流速不均衡的时候直接抛出一个异常,
     * 这个异常就是著名的MissingBackpressureException
     * 以下有多个方式
     * <p>
     * MISSING,
     * ERROR,直接抛出一个异常
     * BUFFER,设置新水缸，没有大小限制, 因此可以存放许许多多的事件.
     * DROP,直接把存不下的事件丢弃
     * LATEST，只保留最新的事件
     */


    public void useFlowable() {

        Flowable<Integer> integerFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR);

        Subscriber<Integer> stream = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {

                //切断水管
                s.cancel();
                //向上又发出消息，这次需要处理到的事件个数
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };

        integerFlowable.subscribe(stream);


    }


    /**
     * 使用Flowable操作符--interval
     * <p>
     * interval操作符发送Long型的事件, 从0开始, 每隔指定的时间就把数字加1并发送出来,
     * 在这个例子里, 我们让它每隔1毫秒就发送一次事件, 在下游延时1秒去接收处理, 不用猜也知道结果是什么:
     * <p>
     * 处理方式--加上背压-- .onBackpressureDrop()
     * <p>
     * 当上下游工作在不同的线程里时，每一个线程里都有一个requested，
     * 而我们调用request（1000）时，实际上改变的是下游主线程中的requested，
     * 而上游中的requested的值是由RxJava内部调用request(n)去设置的，这个调用会在合适的时候自动触发。
     */
    public void useFlowableInterval() {

        Flowable.interval(1, TimeUnit.MICROSECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long aLong) {

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    /**
     * 同步情况下：
     * 当上下游在同一个线程中的时候，在下游调用request(n)就会直接改变上游中的requested的值，
     * 多次调用便会叠加这个值，而上游每发送一个事件之后便会去减少这个值，
     * 当这个值减少至0的时候，继续发送事件便会抛异常了。
     * <p>
     * 异步情况：
     * <p>
     * 上下游工作在不同的线程里时，每一个线程里都有一个requested，
     * 而我们调用request（1000）时，实际上改变的是下游主线程中的requested，
     * 而上游中的requested的值是由RxJava内部调用request(n)去设置的，
     * 这个调用会在合适的时候自动触发。
     * <p>
     * <p>
     * 现在我们就能理解为什么没有调用request，上游中的值是128了，
     * 因为下游在一开始就在内部调用了request(128)去设置了上游中的值，因此即使下游没有调用request()，
     * 上游也能发送128个事件，这也可以解释之前我们为什么说Flowable中默认的水缸大小是128，其实就是这里设置的
     */
    public void setRequestId() {

        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {

                    }
                }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                        s.request(10);
                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
