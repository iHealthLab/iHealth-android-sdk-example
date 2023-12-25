/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ec.easylibrary.dialog.loadingdialog;

import android.content.Context;

class Helper {

    private static float scale;

    public static int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

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
}
