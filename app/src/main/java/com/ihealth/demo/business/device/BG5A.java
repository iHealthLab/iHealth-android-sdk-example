package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Bg5aControl;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.utils.ByteBufferUtil;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import butterknife.OnClick;


public class BG5A extends FunctionFoldActivity {

    private Context mContext;
    private static final String TAG = BG5A.class.getSimpleName();
    private Bg5aControl mBg5aControl;
    private int mClientCallbackId;
    private String deviceTimeString = "";

    private String firmwareVersion = "";
    private String hardwareVersion = "";
    private String bleFirmwareVersion;
    private String modelNumber = "";
    private String firmwareVersionCloud = "";

    private String upgradeFile;

    @Override
    public int contentViewID() {
        return R.layout.activity_bg5a;
    }

    @Override
    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");
        /* register ihealthDevicesCallback id */
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BG5A);
        /* Get bg5s controller */
        mBg5aControl = iHealthDevicesManager.getInstance().getBg5aControl(mDeviceMac);

    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac,
                                                  String deviceType, int status, int errorID) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "status: " + status);
            if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                addLogInfo(mContext.getString(R.string.connect_main_tip_disconnect));
                ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_disconnect));
                finish();
            }
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Log.i(TAG, "username: " + username);
            Log.i(TAG, "userState: " + userStatus);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType,
                                   String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            Message msg = new Message();
            msg.what = HANDLER_MESSAGE;
            msg.obj = message;
            myHandler.sendMessage(msg);
        }
    };


    private final Handler myHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_MESSAGE) {
                addLogInfo((String) msg.obj);
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBg5aControl != null) {
            mBg5aControl.disconnect();
        }
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            //如果当前在认证错误的页面 则直接返回 最开始的页面重新取认证
            if (isShowingLogLayout()) {
                hideLogLayout();
            } else {
                showConfirmDialog(mContext, mContext.getString(R.string.confirm_tip_function_title),
                        mContext.getString(R.string.confirm_tip_function_message, mDeviceName, mDeviceMac), new ConfirmDialog.OnClickLisenter() {
                            @Override
                            public void positiveOnClick() {
                                finish();
                            }

                            @Override
                            public void nagetiveOnClick() {

                            }
                        });
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @OnClick({R.id.btnDisconnect,
            R.id.btnMeasurement,
            R.id.btnMeasurement2,
            R.id.btnMeasurement3,
            R.id.btnGetStatus,
            R.id.btnSetTime,
            R.id.btnSetUnit,
            R.id.btnSetUnit2,
            R.id.btnOfflineMeasureEnable,
            R.id.btnOfflineMeasureDisable,
            R.id.btnDeleteData,
            R.id.btnGetData,
            R.id.btnCloseBluetooth,
            R.id.btnEnableDisplay,
            R.id.btnDisableDisplay})
    public void onViewClicked(View view) {
        if (mBg5aControl == null) {
            addLogInfo("mBg5aControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBg5aControl.disconnect();
                addLogInfo("disconnect()");
                break;

            case R.id.btnGetStatus:
                mBg5aControl.getDeviceInfo();
                addLogInfo("getDeviceInfo()");
                break;

            case R.id.btnMeasurement:
                mBg5aControl.setMeasureType(0);
                addLogInfo("startMeasure() --> set blood mode");
                break;

            case R.id.btnMeasurement2:
                mBg5aControl.setMeasureType(1);
                addLogInfo("startMeasure() --> test ctl mode");
                break;

            case R.id.btnMeasurement3:
                mBg5aControl.setMeasureType(2);
                addLogInfo("startMeasure() --> test with test mode");
                break;

            case R.id.btnSetTime:
                //timeZone
                mBg5aControl.setTime(System.currentTimeMillis(), ByteBufferUtil.getTimeZone());
//                mBg5aControl.setTime(System.currentTimeMillis(), 0);
                addLogInfo("setTime()");
                break;

            case R.id.btnSetUnit:
                mBg5aControl.setMeasureUnit(1);
                addLogInfo("setUnit()--> mmol/L");
                break;

            case R.id.btnSetUnit2:
                mBg5aControl.setMeasureUnit(2);
                addLogInfo("setUnit()--> mg/dL");
                break;

            case R.id.btnOfflineMeasureEnable:
                mBg5aControl.enableOfflineMeasure(true);
                addLogInfo("enableOfflineMeasure()");
                break;

            case R.id.btnOfflineMeasureDisable:
                mBg5aControl.enableOfflineMeasure(false);
                addLogInfo("disableOfflineMeasure()");
                break;

            case R.id.btnDeleteData:
                mBg5aControl.deleteHistoryData();
                addLogInfo("deleteOfflineData()");
                break;

            case R.id.btnGetData:
                mBg5aControl.getHistoryData();
                addLogInfo("getOfflineData()");
                break;

            case R.id.btnCloseBluetooth:
                mBg5aControl.closeBluetooth();
                addLogInfo("closeBluetooth()");
                break;

            case R.id.btnEnableDisplay:
                mBg5aControl.enableDisplayMode(true);
                addLogInfo("enableDisplayMode() true");
                break;

            case R.id.btnDisableDisplay:
                mBg5aControl.enableDisplayMode(false);
                addLogInfo("enableDisplayMode() false");
                break;


        }
    }
}
