package com.ec.easylibrary.images;

import android.content.Context;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * Created by zbiao on 2016/10/14 0014.
 */

public class GlideImageLoaderStrategy implements ImageLoaderStrategy {

    @Override
    public void load(Context context, ImagerAid imagerAid) {
        loadNormal(context, imagerAid);
    }

    /**
     * 正常加载图片
     *
     * @param context   上下文
     * @param imagerAid ImagerAid对象
     */
    public void loadNormal(Context context, final ImagerAid imagerAid) {
        // 圆形ImageView库或者其他的一些自定义的圆形ImageView，并且又刚好设置了占位，第一次加载的是展位图，取消加载动画OK
        // 标准的ImageView 在列表加载时会忽大忽小，去掉动画OK
        DrawableRequestBuilder builder = Glide.with(context).load(imagerAid.getUrl());
        if (imagerAid.getPlaceHolderResourceId() != 0) {
            builder.placeholder(imagerAid.getPlaceHolderResourceId());
        }
        if (imagerAid.getErrorResourceId() != 0) {
            builder.error(imagerAid.getErrorResourceId());
        }
        builder.dontAnimate();
        builder.into(imagerAid.getView());
//                .crossFade()
//                .into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                       imagerAid.getView().setImageDrawable(resource);
//                    }
//                });
    }

    /**
     * 加载缓存中的图片
     *
     * @param context   上下文
     * @param imagerAid ImagerAid对象
     */
    public void loadCache(Context context, ImagerAid imagerAid) {
        Glide.with(context).using(new StreamModelLoader<String>() {
            @Override
            public DataFetcher<InputStream> getResourceFetcher(final String model, int width, int height) {
                return new DataFetcher<InputStream>() {
                    @Override
                    public InputStream loadData(Priority priority) throws Exception {
                        return null;
                    }

                    @Override
                    public void cleanup() {

                    }

                    @Override
                    public String getId() {
                        return model;
                    }

                    @Override
                    public void cancel() {


                    }
                };
            }
        }).load(imagerAid
                .getUrl())
                .placeholder(imagerAid.getPlaceHolderResourceId())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagerAid.getView());
    }
}
