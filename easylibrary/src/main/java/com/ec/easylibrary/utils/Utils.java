package com.ec.easylibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by zbiao on 2016/10/11 0011.
 */

public class Utils {
    /**
     * 根据屏幕分辨率从dp转为px
     *
     * @param dp 屏幕dp值
     * @return 像素值
     */
    public static float dp2px(Context context, float dp) {
        if (context == null) {
            return dp;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (dp * scale + 0.5f);
    }

    /**
     * 根据屏幕分辨率从像素转化成dp单位
     *
     * @param px 像素值
     * @return dp值
     */
    public static float px2dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (px / scale + 0.5f);
    }

    /**
     * 获取当前APP版本号
     *
     * @param context 上下文
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return ((pi != null) ? pi.versionCode : 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前APP版本号
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return ((pi != null) ? pi.versionName : null);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int compareVersion(String version1, String version2) {

        if (version1 == null || version2 == null) {
            Log.i("compareVersion","error compareVersion version1 == null || version2 == null");
            return 0;
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        for(int i = 0 ; i<versionArray1.length ; i++){ //如果位数只有一位则自动补零（防止出现一个是04，一个是5 直接以长度比较）
            if(versionArray1[i].length() == 1){
                versionArray1[i] = "0" + versionArray1[i];
            }
        }
        String[] versionArray2 = version2.split("\\.");
        for(int i = 0 ; i<versionArray2.length ; i++){//如果位数只有一位则自动补零
            if(versionArray2[i].length() == 1){
                versionArray2[i] = "0" + versionArray2[i];
            }
        }
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }
}
