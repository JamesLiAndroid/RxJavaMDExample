package com.james.li.rxjavaexample.utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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
}
