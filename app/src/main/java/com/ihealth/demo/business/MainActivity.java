package com.ihealth.demo.business;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseApplication;
import com.ihealth.demo.base.BaseFragmentActivity;
import com.ihealth.demo.business.device.AM3;
import com.ihealth.demo.business.device.AM3S;
import com.ihealth.demo.business.device.AM4;
import com.ihealth.demo.business.device.AM5;
import com.ihealth.demo.business.device.AM6;
import com.ihealth.demo.business.device.BG1;
import com.ihealth.demo.business.device.BG1A;
import com.ihealth.demo.business.device.BG1S;
import com.ihealth.demo.business.device.BG5;
import com.ihealth.demo.business.device.BG5A;
import com.ihealth.demo.business.device.BG5S;
import com.ihealth.demo.business.device.BP3L;
import com.ihealth.demo.business.device.BP5;
import com.ihealth.demo.business.device.BP550BT;
import com.ihealth.demo.business.device.BP5S;
import com.ihealth.demo.business.device.BP7S;
import com.ihealth.demo.business.device.BTM;
import com.ihealth.demo.business.device.ECG3;
import com.ihealth.demo.business.device.ECGUSB;
import com.ihealth.demo.business.device.HS2;
import com.ihealth.demo.business.device.HS2S;
import com.ihealth.demo.business.device.HS2SPRO;
import com.ihealth.demo.business.device.HS3;
import com.ihealth.demo.business.device.HS4;
import com.ihealth.demo.business.device.KD723;
import com.ihealth.demo.business.device.KD926;
import com.ihealth.demo.business.device.NT13B;
import com.ihealth.demo.business.device.PO1;
import com.ihealth.demo.business.device.PO3;
import com.ihealth.demo.business.device.PT3SBT;
import com.ihealth.demo.business.device.TS28B;
import com.ihealth.demo.business.device.model.DeviceCharacteristic;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;


/**
 * MainActivity
 * Containers for all fragment
 */
public class MainActivity extends BaseFragmentActivity {

    @BindView(R.id.flContent)
    FrameLayout mFlContent;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvDeviceInfo)
    TextView mTvDeviceInfo;
    @BindView(R.id.imgStatus)
    ImageView mImgStatus;

    private Context mContext;
    private RxPermissions permissions;

    //handler 中处理的四种状态
    public static final int HANDLER_SCAN = 101;
    public static final int HANDLER_CONNECTED = 102;
    public static final int HANDLER_DISCONNECT = 103;
    public static final int HANDLER_CONNECT_FAIL = 104;
    public static final int HANDLER_RECONNECT = 105;
    public static final int HANDLER_USER_STATUE = 106;

    public static final int FRAGMENT_CERTIFICATION = 0;
    public static final int FRAGMENT_CERTIFICATION_ERROR = 1;
    public static final int FRAGMENT_DEVICE_MAIN = 2;
    public static final int FRAGMENT_SCAN = 3;

    private int mCurrentFragment;

    private long mTimeKeyBackPressed = 0; // Back键按下时的系统时间

    //退出事件的超时时间
    //Setting this time can change the response time when you exit the application.
    private final static long TIMEOUT_EXIT = 2000;

    //Support device list
    public static ArrayList<DeviceCharacteristic> deviceStructList = new ArrayList<>();

    @Override
    public int contentViewID() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        clearFragments();
        checkPermission();
        initDeviceInfo();
        showCertificationFragment("", "");
    }

    /**
     * 初始化所有支持设备信息
     * Initialize all support device information
     */
    private void initDeviceInfo() {
        Field[] fields = iHealthDevicesManager.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("DISCOVERY_")) {
                DeviceCharacteristic struct = new DeviceCharacteristic();
                struct.setDeviceName(fieldName.substring(10));
                try {
                    struct.setDeviceType(field.getLong(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                deviceStructList.add(struct);
            }
        }
    }

    /**
     * 检查权限
     * check Permission
     */
    private void checkPermission() {
        int version = Build.VERSION.SDK_INT;
        if (version > 30) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
        } else if (version > 28) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, 1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO}, 1);
            }
        }
    }


    /**
     * 切换到认证页面
     * Switch to Authentication Fragment
     *
     * @param param1
     * @param param2
     */
    public void showCertificationFragment(String param1, String param2) {
        mCurrentFragment = FRAGMENT_CERTIFICATION;
        Fragment fragment = CertificationFragment.newInstance(param1, param2);
        addFragment(R.id.flContent, fragment, CertificationFragment.class.getSimpleName());
        setTitle(mContext.getString(R.string.main_title_authorization));
        mTvDeviceInfo.setVisibility(View.INVISIBLE);
        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_normal));
    }

    /**
     * 切换到认证失败log展示页面
     * Switch to LogFragment
     *
     * @param param1
     * @param param2
     */
    public void showCertificationLogFragment(String param1, String param2) {
        mCurrentFragment = FRAGMENT_CERTIFICATION_ERROR;
        Fragment fragment = LogFragment.newInstance(param1, param2);
        addFragment(R.id.flContent, fragment, LogFragment.class.getSimpleName());
        setTitle(mContext.getString(R.string.main_title_authorization));
        setDeviceInfo(mContext.getString(R.string.main_tip_author_fail));
        mTvDeviceInfo.setVisibility(View.VISIBLE);
        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_1_error));
    }

    /**
     * 切换到显示所有设备到主页面
     * Switch to DevicesFragment
     *
     * @param param1
     * @param param2
     */
    public void showDevicesFragment(String param1, String param2) {
        mCurrentFragment = FRAGMENT_DEVICE_MAIN;
        Fragment fragment = DevicesFragment.newInstance(param1, param2);
        addFragment(R.id.flContent, fragment, DevicesFragment.class.getSimpleName());
        setTitle(mContext.getString(R.string.main_title_connect));
        mTvDeviceInfo.setVisibility(View.VISIBLE);
        mTvDeviceInfo.setText(mContext.getString(R.string.main_tip_select_device, ""));
        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_1_ok));
    }

    /**
     * 切换到搜索页面
     * Switch to showScanFragment
     *
     * @param param1
     * @param param2
     */
    public void showScanFragment(String param1, String param2) {
        mCurrentFragment = FRAGMENT_SCAN;
        Fragment fragment = ScanFragment.newInstance(param1, param2);
        addFragment(R.id.flContent, fragment, ScanFragment.class.getSimpleName());
        setTitle(mContext.getString(R.string.main_title_connect));
        mTvDeviceInfo.setVisibility(View.VISIBLE);
        mTvDeviceInfo.setText(mContext.getString(R.string.main_tip_select_device, param1));
    }

    /**
     * 跳转到设备功能页
     * Switch to showFunctionActivity
     *
     * @param mac
     * @param type
     */
    public void showFunctionActivity(String mac, String type) {
        Intent intent = new Intent();
        intent.putExtra("mac", mac);
        intent.putExtra("type", type);
        switch (type) {
            case iHealthDevicesManager.TYPE_KD723:
                intent.setClass(MainActivity.this, KD723.class);
                break;

            case iHealthDevicesManager.TYPE_KD926:
                intent.setClass(MainActivity.this, KD926.class);
                break;

            case iHealthDevicesManager.TYPE_BP5:
                intent.setClass(MainActivity.this, BP5.class);
                break;

            case iHealthDevicesManager.TYPE_BP5S:
                intent.setClass(MainActivity.this, BP5S.class);
                break;

            case iHealthDevicesManager.TYPE_550BT:
                intent.setClass(MainActivity.this, BP550BT.class);
                break;

            case iHealthDevicesManager.TYPE_BP7S:
                intent.setClass(MainActivity.this, BP7S.class);
                break;

            case iHealthDevicesManager.TYPE_BP3L:
                intent.setClass(MainActivity.this, BP3L.class);
                break;

            case iHealthDevicesManager.TYPE_BG1:
                intent.setClass(MainActivity.this, BG1.class);
                break;

            case iHealthDevicesManager.TYPE_BG5:
                intent.setClass(MainActivity.this, BG5.class);
                break;

            case iHealthDevicesManager.TYPE_BG5S:
                intent.setClass(MainActivity.this, BG5S.class);
                break;

            case iHealthDevicesManager.TYPE_BG5A:
                intent.setClass(MainActivity.this, BG5A.class);
                break;

            case iHealthDevicesManager.TYPE_BG1S:
                intent.setClass(MainActivity.this, BG1S.class);
                break;

            case iHealthDevicesManager.TYPE_BG1A:
                intent.setClass(MainActivity.this, BG1A.class);
                break;

            case iHealthDevicesManager.TYPE_HS2:
                intent.setClass(MainActivity.this, HS2.class);
                break;

            case iHealthDevicesManager.TYPE_HS2S:
                intent.setClass(MainActivity.this, HS2S.class);
                break;

            case iHealthDevicesManager.TYPE_HS2SPRO:
                intent.setClass(MainActivity.this, HS2SPRO.class);
                break;

            case iHealthDevicesManager.TYPE_HS3:
                intent.setClass(MainActivity.this, HS3.class);
                break;

            case iHealthDevicesManager.TYPE_HS4:
                intent.setClass(MainActivity.this, HS4.class);
                break;

            case iHealthDevicesManager.TYPE_HS6:
                intent.setClass(MainActivity.this, BP5.class);
                break;

            case iHealthDevicesManager.TYPE_AM3:
                intent.setClass(MainActivity.this, AM3.class);
                break;

            case iHealthDevicesManager.TYPE_AM3S:
                intent.setClass(MainActivity.this, AM3S.class);
                break;

            case iHealthDevicesManager.TYPE_AM4:
                intent.setClass(MainActivity.this, AM4.class);
                break;

            case iHealthDevicesManager.TYPE_AM5:
                intent.setClass(MainActivity.this, AM5.class);
                break;

            case iHealthDevicesManager.TYPE_AM6:
                intent.setClass(MainActivity.this, AM6.class);
                break;

            case iHealthDevicesManager.TYPE_TS28B:
                intent.setClass(MainActivity.this, TS28B.class);
                break;

            case iHealthDevicesManager.TYPE_FDIR_V3:
                intent.setClass(MainActivity.this, BTM.class);
                break;

            case iHealthDevicesManager.TYPE_NT13B:
                intent.setClass(MainActivity.this, NT13B.class);
                break;

            case iHealthDevicesManager.TYPE_PO3:
                intent.setClass(MainActivity.this, PO3.class);
                break;

            case iHealthDevicesManager.TYPE_ECG3:
                intent.setClass(MainActivity.this, ECG3.class);
                break;

            case iHealthDevicesManager.TYPE_ECG3_USB:
                intent.setClass(MainActivity.this, ECGUSB.class);
                break;

            case iHealthDevicesManager.TYPE_PT3SBT:
                intent.setClass(MainActivity.this, PT3SBT.class);
                break;

            case iHealthDevicesManager.TYPE_PO1:
                intent.setClass(MainActivity.this, PO1.class);
                break;

        }
        startActivity(intent);
    }

    /**
     * 设置主页面到标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 设置设备连接信息
     *
     * @param deviceInfo
     */
    public void setDeviceInfo(String deviceInfo) {
        mTvDeviceInfo.setText(deviceInfo);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            //如果当前在认证错误的页面 则直接返回 最开始的页面重新取认证
            if (mCurrentFragment == FRAGMENT_CERTIFICATION_ERROR) {
                showCertificationFragment("", "");
            } else if (mCurrentFragment == FRAGMENT_SCAN) {//如果当前在搜索页面 则直接返回 设备主页面
                showDevicesFragment("", "");
            } else {//点击两次返回退出
                long currTime = System.currentTimeMillis();
                if (currTime - mTimeKeyBackPressed > TIMEOUT_EXIT) {
                    ToastUtils.showToast(this, R.string.exit_warning);
                    mTimeKeyBackPressed = currTime;
                } else {
                    ToastUtils.stopToast();
                    finish();
                    System.exit(0);
                }
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.instance().logOut();
    }
}
