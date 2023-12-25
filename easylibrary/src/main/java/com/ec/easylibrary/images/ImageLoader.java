package com.ec.easylibrary.images;

import android.content.Context;

/**
 * Created by zbiao on 2016/10/14 0014.
 *
 * 图像加载类，这里控制图像的加载。
 */

public class ImageLoader {

    private static ImageLoader mInstance;
    private ImageLoaderStrategy mStrategy;

    public ImageLoader() {
        this.mStrategy = new GlideImageLoaderStrategy();
    }

    /**
     * 获取ImageLoader实例
     *
     *     ImageLoader使用单例模式，因为图片加载可能会涉及线程池、缓存系统和网络请求，很耗资源。
     * 所以不能让它构造多个实例。
     * @return 返回ImageLoader实例
     */
    public static ImageLoader instance() {
        if (mInstance == null) {
            mInstance = new ImageLoader();
        }
        return mInstance;
    }

    /**
     * 设置加载图像策略
     * @param strategy 加载图像策略
     */
    public void setImageLoaderStrategy(ImageLoaderStrategy strategy) {
        mStrategy = strategy;
    }

    /**
     * 开始加载图片
     * @param context 上下文
     * @param imager ImagerAid类，提供了图片下载地址和图片要显示的控件
     */
    public void load(Context context, ImagerAid imager) {
        if (mStrategy != null) {
            mStrategy.load(context, imager);
        }
    }
}
