package com.james.li.rxjavaexample.utils;

import android.util.Log;

import com.james.li.rxjavaexample.bean.AppInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

/**
 * Created by jyj-lsy on 9/13/16 in zsl-tech.
 */
public class UtilsTestOperator {
    /**
     * 测试from操作符
     *
     */
    public static Observable<Integer> fromTest() {
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(3);
        intList.add(5);
        intList.add(7);
        intList.add(9);

        Observable<Integer> integerObservable = Observable.from(intList);
        return integerObservable;
    }

    /**
     * 测试just操作符
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static Observable<Integer> justTest(int a, int b, int c) {
        Observable<Integer> observable = Observable.just(a,b,c);
        return observable;
    }

    /**
     * 测试repeat操作符
     * @param a
     * @return
     */
    public static Observable<Integer> repeatTest(int a) {
        Observable<Integer> observable = Observable.just(a).repeat(5);
        return observable;
    }

    /**
     * 测试defer操作符
     * @param a
     * @return
     */
    public static Observable<Integer> deferTest(final int a) {
        Observable<Integer> observable = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return deferInt(a);
            }
        });
        return observable;
    }

    private static Observable<Integer> deferInt(final int a) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    Log.d("TAG","未订阅状态");
                    return;
                }

                Log.d("TAG","订阅状态！");
                subscriber.onNext(a);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 测试range操作符
     * @param a
     * @return
     */
    public static Observable<Integer> rangeTest(int a, int b) {
        Observable<Integer> observable = Observable.range(a, b);
        return observable;
    }

    /**
     * 测试interval操作符
     * @param a
     * @return
     */
    public static Observable<Integer> intervalTest(int a) {
        Observable<Integer> observable = Observable.interval(2, TimeUnit.SECONDS).range(10,4);
        return observable;
    }

    /**
     * timer操作符
     * @param a
     * @return
     */
    public static Observable<Long> timerTest(int a) {
        // Observable<Integer> observable = Observable.timer(a,10,TimeUnit.SECONDS);
        // 使用interval(int a, int b,TimeUnit unit)代替
        // 表示延迟指定时间后执行
        Observable<Long> observable = Observable.timer(a, TimeUnit.SECONDS);
        return observable;
    }

    /**
     * 过滤操作符的Base操作
     * @param appInfos
     * @return
     */
    private static Observable<AppInfo> filterBase(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = Observable.from(appInfos);
        return observable;
    }

    /**
     * filter操作符
     * @return
     */
    public static Observable<AppInfo> filterTest(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = filterBase(appInfos).filter(new Func1<AppInfo, Boolean>() {
            @Override
            public Boolean call(AppInfo appInfo) {
                return appInfo.getName().startsWith("C");
            }
        });

        return observable;
    }

    /**
     * take操作符,takeLast操作符同理
     * @return
     */
    public static Observable<AppInfo> takeTest(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = filterBase(appInfos).take(3);
        return observable;
    }

    /**
     * 创建重复列表
     * @param appInfos
     * @return
     */
    private static Observable<AppInfo> createRepeatApps(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = filterBase(appInfos).take(4).repeat(4);
        return observable;
    }

    /**
     * distinct操作符
     * @return
     */
    public static Observable<AppInfo> distinctTest(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = createRepeatApps(appInfos).distinct();
        return observable;
    }

    /**
     * first操作符,last操作符同理
     * @return
     */
    public static Observable<AppInfo> firstTest(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = createRepeatApps(appInfos).first();
        return observable;
    }

    /**
     * skip操作符,skipLast操作符同理，而且对应于take和takeLast
     * @return
     */
    public static Observable<AppInfo> skipTest(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = filterBase(appInfos).skip(4);
        return observable;
    }

    /**
     * elementAt操作符
     * @param appInfos
     * @return
     */
    public static Observable<AppInfo> elementAtTest(List<AppInfo> appInfos) {
        Observable<AppInfo> observable = filterBase(appInfos).elementAt(5);
        return observable;
    }


    /**
     * timeout操作符
     * @return
     */
    public static Observable<Integer> timeOutTest() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 6; i++) {
                    try {
                        Thread.sleep(i * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).timeout(200, TimeUnit.MILLISECONDS);
    }

    /**
     * Debounce操作符, 过滤发射过快的元素
     * @return
     */
    public static Observable<Integer> debounceTest() {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 6; i++) {
                    try {
                        Thread.sleep(i * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).debounce(600, TimeUnit.MILLISECONDS);
    }

    /**
     * groupBy操作符
     * @param appInfoList
     * @return
     */
    public static Observable<GroupedObservable<String,AppInfo>> groupByTest(List<AppInfo> appInfoList) {
        return Observable.from(appInfoList).groupBy(new Func1<AppInfo, String>() {
            @Override
            public String call(AppInfo appInfo) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
                return formatter.format(new Date(appInfo.getLastUpdateTime()));
            }
        });
    }

}
