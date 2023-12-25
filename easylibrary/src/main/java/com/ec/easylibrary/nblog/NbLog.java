package com.ec.easylibrary.nblog;

import android.util.Log;

/**
 * Created by zbiao on 2016/10/10 0010.
 */

public class NbLog {
    private static final int LEVEL = 0; // 这里定义了要显示哪一个级别的Log信息
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int ASSERT = 6;

    public static void v(String tag, String info) {
        if (VERBOSE >= LEVEL) {
            Log.v(tag, info);
        }
    }

    public static void d(String tag, String info) {
        if (DEBUG >= LEVEL) {
            Log.d(tag, info);
        }
    }

    public static void i(String tag, String info) {
        if (INFO >= LEVEL) {
            Log.i(tag, info);
        }
    }

    public static void w(String tag, String info) {
        if (WARN >= LEVEL) {
            Log.w(tag, info);
        }
    }

    public static void e(String tag, String info) {
        if (ERROR >= LEVEL) {
            Log.e(tag, info);
        }
    }
}
