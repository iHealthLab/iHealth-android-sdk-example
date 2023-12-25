package com.ec.easylibrary;

import android.app.Activity;

import java.util.Stack;

/**
 * App管理类。
 * 管理Activity和Fragment.
 * 现在只实现了Activity的管理.该类采用单例模式
 * Created by wj
 */

public class AppManager {
    private static Stack<Activity> mActivityStack;

    private static AppManager mInstance;

    public AppManager() {
        if (mActivityStack == null) {
            mActivityStack = new Stack<>();
        }
    }

    public static AppManager instance() {
        if (mInstance == null) {
            synchronized (AppManager.class) {
                if (mInstance == null) {
                    mInstance = new AppManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 向栈中加入当前Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        mActivityStack.push(activity);
    }

    /**
     * 获取当前执行的Activity
     *
     * @return
     */
    public Activity currentActivity() {
        if (mActivityStack == null) {
            return null;
        } else {
            return mActivityStack.lastElement();
        }
    }

    /**
     * 结束掉栈顶的Activity.
     */
    public void finishActivity() {
        Activity activity = mActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity 指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.remove(activity);
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                activity.finish();
            }
            activity = null;
        }
    }

    /**
     * 将指定的类名的Activity类结束掉
     *
     * @param cls 类名
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有的Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = mActivityStack.size(); i < size; i++) {
            if (null != mActivityStack.get(i)) {
                mActivityStack.get(i).finish();
            }
        }
        mActivityStack.clear();
    }

    /**
     * 回退到某一个Activity。
     * 如果栈中没有对应的Activity,则会退出应用
     *
     * @param cls
     */
    public void backActivity(Class<?> cls) {
        while (!mActivityStack.isEmpty()) {
            Activity activity = mActivityStack.peek();
            if (activity.getClass().equals(cls)) {
                break;
            } else {
                finishActivity(activity);
            }
        }
    }
}
