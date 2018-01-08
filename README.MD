> 该笔记参考如下两篇博客
[这可能是最好的RxJava 2.x 教程（完结版）](https://www.jianshu.com/p/0cd258eecf60)
[给初学者的RxJava2.0教程(一)](https://www.jianshu.com/p/464fa025229e)

>>[Demo地址，快快点我](https://github.com/ALguojian/RxjavaDemo)


### 有关操作符demo如下：

[使用RxJava的操作符](https://github.com/ALguojian/RxjavaDemo/blob/master/app/src/main/java/com/alguojian/rxjavademo/RxJavaUtils.java)

```

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


```


## rxjava操作符使用如下：

```
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

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);

            }
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

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                stringBuilder.append("哈哈");
                emitter.onNext(1);
                stringBuilder.append("，我是");
                emitter.onNext(2);
                //下面事件可以发送，但是不再接收
                emitter.onComplete();
                stringBuilder.append("小学生");
                emitter.onNext(3);
            }
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
     * zip
     * 操作符使用例子
     * zip 专用于合并事件，该合并不是连接（连接操作符后面会说），
     * 而是两两配对，也就意味着，最终配对出的 Observable 发射事件数目只和少的那个相同。
     * <p>
     * zip 组合事件的过程就是分别从发射器 A 和发射器 B 各取出一个事件来组合，并且一个事件只能被使用一次，
     * 组合的顺序是严格按照事件发送的顺序来进行的，所以上面截图中，可以看到，1 永远是和 A 结合的，2 永远是和 B 结合的
     */
    public static void useZip() {

        Observable.zip(getStringObservable(), getIntegerObservable(), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                KLog.d(TTAG, "新的消息字段是" + s);
            }
        });
    }

    private static Observable<String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    aaa.append("asd");
                    e.onNext("A");
                    aaa.append("asd");
                    e.onNext("B");
                    aaa.append("asd");
                    aaa.append("zxczxc");
                    e.onNext("C");
                }
            }
        });
    }

    private static Observable<Integer> getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
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
            }
        });
    }


    /**
     * 对于单一的把两个发射器连接成一个发射器，虽然 zip 不能完成，
     * 但我们还是可以自力更生，官方提供的 concat 让我们的问题得到了完美解决。
     */
    public static void useConcat() {

        Observable.concat(Observable.just(1, 2, 3, 4), Observable.just(6, 7, 8))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        KLog.d(TTAG, "这是第几个？" + integer);
                    }
                });

    }


    /**
     * FlatMap 是一个很有趣的东西，我坚信你在实际开发中会经常用到。
     * 它可以把一个发射器 Observable 通过某种方法转换为多个 Observables，
     * 然后再把这些分散的 Observables装进一个单一的发射器 Observable。但有个需要注意的是，
     * flatMap 并不能保证事件的顺序，如果需要保证，需要用到我们下面要讲的 ConcatMap。
     */
    public static void useFlatMap() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
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
     * 添加过滤器
     */
    public static void useFilter() {

        Observable.just(1, 20, -20, 57, 43, 80)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 1;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                KLog.d(TTAG, "接收消息是" + integer);
            }
        });
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
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {

                        KLog.d(TTAG, integers);
                    }
                });

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
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        KLog.d(TTAG, TimeUtils.getNowTime() + "");
                    }
                });
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
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        KLog.d(TTAG, aLong + "---" + TimeUtils.getNowTime());
                    }
                });
    }
    //subscribe.dispose();
    //记得activity销毁时取消发送


    /**
     * 让订阅者在接收到数据之前干点有意思的事情
     */
    public static void useDoOnNext() {

        Observable.just(1, 2, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        aaa.append("doOnNext 保存 " + integer);
                        KLog.d(TTAG, aaa.toString());
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                KLog.d(TTAG, "accept--" + aaa.toString());
            }
        });
    }


    /**
     * 其实作用就和字面意思一样，接受一个 long 型参数 count ，代表跳过 count 个数目开始接收
     */
    public static void useSkip() {

        Observable.just(1, 2, 3, 4, 5, 6, 7)
                .skip(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        KLog.d(TTAG, integer);
                    }
                });
    }


    /**
     * 接受一个 long 型参数 count ，代表至多接收 count 个数据。
     */
    public static void useTake() {

        Flowable.fromArray(1, 2, 3, 4, 5, 6, 7, 7)
                .take(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        KLog.d(TTAG, integer);
                    }
                });
    }


    /**
     * 简单的发射器依次调用 onNext() 方法。还调用了 onComplete()。
     */
    public static void useJust() {

        Observable.just("1", "2", "3", "5")
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        KLog.d(TTAG, s);
                    }
                });
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

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                emitter.onNext(1);
                Thread.sleep(300);
                emitter.onNext(2);
                Thread.sleep(500);
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        KLog.d(TTAG, integer);
                    }
                });
    }


    /**
     * 每次订阅都会创建一个新的 Observable，并且如果没有被订阅，就不会产生新的 Observable。
     */
    public static void useDefer() {

        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(1, 2, 3, 4);
            }
        });

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
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        KLog.d(TTAG, integer + "");
                    }
                });
    }


    /**
     * 把多个 Observable 结合起来，接受可变参数，也支持迭代器集合。
     * 注意它和 concat 的区别在于，不用等到 发射器 A 发送完所有的事件再进行发射器 B 的发送。
     */
    public static void useMerge() {

        Observable.merge(Observable.just(1, 2, 3), Observable.just(5, 6, 7, 8))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                        KLog.d(TTAG, "第几个" + integer);
                    }
                });
    }

    ;


    /**
     * 每次用一个方法处理一个值，可以有一个 seed 作为初始值
     */
    public static void useReduce() {

        Observable.just(1, 2, 3, 4)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        KLog.d(TTAG, integer + "");
                        KLog.d(TTAG, integer2 + "");
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

                KLog.d(TTAG, integer + "");
            }
        });
    }


    /**
     * reduce 是个只追求结果的坏人，而 scan 会始终如一地把每一个步骤都输出。
     */
    public static void useScan() {

        Observable.just(1, 2, 3, 4)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        KLog.d(TTAG, integer + "");
                        KLog.d(TTAG, integer2 + "");
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                KLog.d(TTAG, integer + "");
            }
        });
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
                .subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        aaa.append("aa");
                        KLog.d(TTAG, aaa.toString());
                        longObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        aaa.append("bb");
                                        KLog.d(TTAG, aaa.toString());
                                    }
                                });
                    }
                });
    }
```