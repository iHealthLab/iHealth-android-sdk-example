package com.ec.easylibrary.images;

import android.widget.ImageView;

/**
 * Created by zbiao on 2016/10/14 0014.
 *
 * 图像加载辅助类
 */

public class ImagerAid {
    private String mUrl;
    private ImageView mImageView;
    private int mResidPlaceHolder; // 等待时显示的图片资源ID
    private int mResidError; // 加载错误时显示的图片资源ID

    public ImagerAid() {
    }

    /**
     * 设置下载地址
     * @param url 图像源地址
     */
    public void setUrl(String url) {
        mUrl = url;
    }

    /**
     * 获取下载地址源
     * @return 返回下载地址
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 获取下载图片要显示的控件
     * @return 对应图像的控件
     */
    public ImageView getView() {
        return mImageView;
    }

    /**
     * 设置要显示图像的控件
     * @param view 要显示图像的控件
     */
    public void setView(ImageView view) {
        mImageView = view;
    }

    /**
     * 设置等待加载时的图片
     * @param resId 等待图片加载时显示的图片的Resource ID
     */
    public void setPlaceHolder(int resId) {
        mResidPlaceHolder = resId;
    }

    /**
     * 获取等待加载时的图片ID
     * @return 返回等待图片加载时显示的图片的Resource ID
     */
    public int getPlaceHolderResourceId() {
        return mResidPlaceHolder;
    }

    /**
     * 设置加载出错时要显示的图片的Resource ID
     * @param resId 加载出错时要显示的图片的Resource ID
     */
    public void setError(int resId) {
        mResidError = resId;
    }

    /**
     * 获取加载出错时要显示的图片的Resource ID
     * @return 返回加载出错时要显示的图片的Resource ID
     */
    public int getErrorResourceId() {
        return mResidError;
    }

    /**
     * Builder类 创建ImagerAid对象
     */
    public static class Builder {
        private ImagerAid mImager;

        public Builder() {
            mImager = new ImagerAid();
        }

        public Builder setUrl(String url) {
            mImager.setUrl(url);
            return this;
        }

        public Builder setView(ImageView imageView) {
            mImager.setView(imageView);
            return this;
        }

        public Builder setPlaceHolder(int resId) {
            mImager.setPlaceHolder(resId);
            return this;
        }

        public Builder setError(int resId) {
            mImager.setError(resId);
            return this;
        }

        public ImagerAid create() {
            return mImager;
        }
    }
}
