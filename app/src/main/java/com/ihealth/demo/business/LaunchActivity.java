package com.ihealth.demo.business;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseActivity;

/**
 * LaunchActivity
 * 启动页
 */
public class LaunchActivity extends BaseActivity {
    private Context mContext;

    @Override
    public int contentViewID() {
        return R.layout.activity_launch;
    }

    @Override
    public void initView() {
      init();
    }

    private void init() {
        mContext = this;
        new Handler().postDelayed(new Runnable(){
            public void run() {
                goCertificationActivity();
            }
        }, 3000);
    }

     /**
     * go to certification activity
     */
    private void goCertificationActivity() {
        Intent intent = new Intent();
        intent.setClass(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
}
