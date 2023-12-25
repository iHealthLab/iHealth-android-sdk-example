package com.ec.easylibrary.images;

import android.content.Context;

/**
 * Created by zbiao on 2016/10/14 0014.
 *
 * 加载图片接口
 */

public interface ImageLoaderStrategy {

    // 加载图片
    void load(Context context, ImagerAid imagerAid);
}
