package com.ihealth.demo.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ec.easylibrary.AppManager;
import com.ihealth.communication.manager.iHealthDevicesManager;


/**
 * <li>全局Application</li>
 * <li>Base Application</li>
 *
 * Created by wj on 2018/11/20
 */

public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    //全局context
    /** global connect */
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        applicationContext = this;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        /*
         * Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
         * and connect these devices through iHealthDevicesManager.
         */
        iHealthDevicesManager.getInstance().init(this,  Log.VERBOSE, Log.VERBOSE);
    }

    /**
     * 获取BaseApplication的实例
     *
     * @return
     */
    public static BaseApplication instance() {
        return mInstance;
    }

    /**
     * <li>退出登录要做的一些操作 如清除一些用户数据等等</li>
     * <li>Some operations to exit login, such as clearing some user data, etc.</li>
     */
    public void logOut() {
        AppManager.instance().finishAllActivity();
    }


}
