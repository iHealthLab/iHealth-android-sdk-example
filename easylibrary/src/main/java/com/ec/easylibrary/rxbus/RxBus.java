package com.ec.easylibrary.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by zbiao on 2016/11/10 0010.
 *
 * 类似于EventBus功能，使用RxJava实现。
 */
public class RxBus {
    private static volatile RxBus mInstance; // 单例模式

    private final Subject mBus;

    public RxBus() {
        this.mBus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 获取RxBus实例
     * @return 返回RxBus实例
     */
    public static RxBus instance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送事件
     * @param object 要发送的事件
     */
    public void post(Object object) {
        mBus.onNext(object);
    }

    // 根据传递的eventType类型返回特定的被观察者
    public <T> Observable<T> tObservable (Class<T> eventType) {
        return mBus.ofType(eventType);
    }
}
