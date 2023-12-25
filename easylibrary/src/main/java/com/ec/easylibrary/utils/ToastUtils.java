package com.ec.easylibrary.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zbiao on 2016/10/28 0028.
 * <p>
 * 该类定义了Toast展示信息的静态方法。通过该类中的方法使信息通过Toast展示出来。
 */

public class ToastUtils {
    public static Toast mToast; // 通知信息

    /**
     * 通过Toast窗显示信息
     *
     * @param context 上下文
     * @param text    要显示的信息
     */
    public static void showToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * 通过Toast窗显示信息
     *
     * @param context  上下文
     * @param text     要显示的信息
     * @param duration Toast窗展示时间
     */
    public static void showToast(Context context, String text, int duration) {
        if (null != mToast) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, text, duration);
        mToast.show();

    }

    /**
     * 通过Toast窗显示信息
     *
     * @param context 上下文
     * @param resId   要展示信息的资源ID
     */
    public static void showToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 通过Toast窗显示信息
     *
     * @param context  上下文
     * @param resId    要展示信息的资源ID
     * @param duration Toast窗展示时间
     */
    public static void showToast(Context context, int resId, int duration) {
        if (null != mToast) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, resId, duration);
        mToast.show();

    }

    public static void stopToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
