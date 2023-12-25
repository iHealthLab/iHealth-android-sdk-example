package com.ihealth.demo.base;

/**
 * 通用结果返回
 * Created by Administrator on 2018/4/28.
 */

public interface ICommonResult {
    //积极的结果 正确 正面的
    void onPositiveEvent();

    //消极的结果 错误 负面的
    void onNagetiveEvent();
}
