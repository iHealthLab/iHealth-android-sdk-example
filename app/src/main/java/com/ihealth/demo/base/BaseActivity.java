package com.ihealth.demo.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ec.easylibrary.AppManager;
import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.DateUtils;
import com.ihealth.demo.R;

import butterknife.ButterKnife;

/**
 * <li>BaseActivity</li>
 * <li>All Activity Basic</li>
 *
 * Created by wj on 2018/11/20
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public Context mContext;
    private RelativeLayout mRlMain;
    /** Log */
    public TextView mTvLogMessage;
    public LinearLayout mLogLayout;
    public ScrollView mScrollViewLog;
    /** Animation */
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    /** Global Log Information */
    private String mLogInformation = "";
    /** Confirm Dialog */
    private ConfirmDialog mConfirmDialog;
    /** Device Name */
    public String mDeviceName = "";
    /** Device Mac */
    public String mDeviceMac = "";
    /** Global Screen Width Default 1080 px*/
    public int mScreenWidth = 1080;
    /** Global Screen Height Default 1920 px*/
    public int mScreenHeight = 1920;
    /** Handle Message What Code*/
    public static final int HANDLER_MESSAGE = 101;
    /** Importing Layout  Abstraction Method*/
    public abstract int contentViewID();
    /** Init  Abstraction Method*/
    public abstract void initView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        setBaseActivityLayout();
        initBaseActivity();

    }

    public void setBaseActivityLayout() {
        setContentView(R.layout.activity_base);
    }


    public void initBaseActivity() {
        mContext = BaseApplication.instance().getApplicationContext();
        mRlMain = findViewById(R.id.rlMain);
        mTvLogMessage = findViewById(R.id.tvLogMessage);
        mLogLayout = findViewById(R.id.logLayout);
        mScrollViewLog = findViewById(R.id.ScrollViewLog);

        if (contentViewID() != 0) {
            View layout = LayoutInflater.from(mContext).inflate(contentViewID(), null);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mRlMain.addView(layout);
        }

        ButterKnife.bind(this);
        AppManager.instance().addActivity(this);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

        initAnim();
        initView();

    }

    private void initAnim() {
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -2.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                -2.0f);
        mHiddenAction.setDuration(500);
    }

    public void showConfirmDialog(Context context, String title, String messgae, ConfirmDialog.OnClickLisenter lisenter) {
        mConfirmDialog = new ConfirmDialog(context, mScreenWidth - 100, mScreenHeight / 3, title, messgae, lisenter);
        mConfirmDialog.show();
    }



    public void showLogLayout() {
        if (mLogLayout.getVisibility() != View.VISIBLE) {
            mLogLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_view_show));
            mLogLayout.setVisibility(View.VISIBLE);
        }

    }

    public void hideLogLayout() {
        if (mLogLayout.getVisibility() != View.GONE) {
            mLogLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_view_dismiss));
            mLogLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 将信息存储到日志中
     * Add information to the log
     * @param infomation
     */
    public void addLogInfo(String infomation) {
        if (infomation != null && !infomation.isEmpty()) {
            String infor = DateUtils.getNow("yyyy-MM-dd HH:mm:ss.SSS") + ": " + infomation + " \n";
            mLogInformation += infor;
            if (mTvLogMessage != null) {
                mTvLogMessage.append(infor);
                mScrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }
    }

    /**
     * 清空日志
     * Clear log
     */
    public void clearLogInfo() {
        mLogInformation = "";
        if (mTvLogMessage != null) {
            mTvLogMessage.setText("");
        }
    }

    /**
     * 获取日志
     * Get log
     * @return
     */
    public String getLogInformation() {
        return mLogInformation;
    }


    /**
     * 判断日志是否正在显示
     * is the log showing now?
     * @return
     */
    public boolean isShowingLogLayout() {
        return mLogLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * 设置全局设备信息
     * set global device information
     * @param deviceName device show name
     * @param deviceMac device mac
     */
    public void setDeviceInfo(String deviceName, String deviceMac) {
        mDeviceName = deviceName;
        mDeviceMac = deviceMac;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.instance().finishActivity(this);
    }


}
