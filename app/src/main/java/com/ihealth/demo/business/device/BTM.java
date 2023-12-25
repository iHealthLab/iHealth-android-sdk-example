package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Bg5Profile;
import com.ihealth.communication.control.BtmControl;
import com.ihealth.communication.control.BtmProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseActivity;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ihealth.communication.control.BtmControl.FUNCTION_TARGET_OFFLINE;
import static com.ihealth.communication.control.BtmControl.FUNCTION_TARGET_ONLINE;
import static com.ihealth.communication.control.BtmControl.MEASURING_TARGET_BODY;
import static com.ihealth.communication.control.BtmControl.MEASURING_TARGET_OBJECT;
import static com.ihealth.communication.control.BtmControl.TEMPERATURE_UNIT_C;
import static com.ihealth.communication.control.BtmControl.TEMPERATURE_UNIT_F;


public class BTM extends FunctionFoldActivity {
    @BindView(R.id.etStandbyHour)
    EditText mEtStandbyHour;
    @BindView(R.id.etStandbyMinute)
    EditText mEtStandbyMinute;
    @BindView(R.id.etStandbySecond)
    EditText mEtStandbySecond;
    private Context mContext;
    private static final String TAG = "BTM";
    private BtmControl mBTMControl;
    private int mClientCallbackId;

    @Override
    public int contentViewID() {
        return R.layout.activity_btm;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_FDIR_V3);
        /* Get btm controller */
        mBTMControl = iHealthDevicesManager.getInstance().getBtmControl(mDeviceMac);

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

            Log.i(TAG, "mac:" + mac + " action:" + action + " message" + message);
            if (BtmProfile.ACTION_BTM_BATTERY.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(BtmProfile.BTM_BATTERY);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (BtmProfile.ACTION_BTM_MEMORY.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = message;
                myHandler.sendMessage(msg);
            } else if (BtmProfile.ACTION_BTM_MEASURE.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = message;
                myHandler.sendMessage(msg);
            } else if (BtmProfile.ACTION_BTM_CALLBACK.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = message;
                myHandler.sendMessage(msg);

            }
        }
    };


    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    addLogInfo((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        if(mBTMControl!=null){
            mBTMControl.disconnect();
        }
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        super.onDestroy();

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


    @OnClick({R.id.btnDisconnect, R.id.btnGetBattery, R.id.btnSetStandbyTime, R.id.btnSetUnit,
            R.id.btnSetUnit2, R.id.btnSetTarget, R.id.btnSetTarget2,
            R.id.btnOnLine, R.id.btnOffLine, R.id.btnGetData})
    public void onViewClicked(View view) {
        if (mBTMControl == null) {
            addLogInfo("mBTMControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBTMControl.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnGetBattery:
                mBTMControl.getBattery();
                addLogInfo("getBattery()");
                break;
            case R.id.btnSetStandbyTime:
                String standbyHour = mEtStandbyHour.getText().toString();
                String standbyMinute = mEtStandbyMinute.getText().toString();
                String standbySecond = mEtStandbySecond.getText().toString();
                mBTMControl.setStandbyTime(Integer.parseInt(standbyHour), Integer.parseInt(standbyMinute), Integer.parseInt(standbySecond));
                addLogInfo("setStandbyTime() -->standbyHour:" + standbyHour + " standbyMinute:" + standbyMinute + " standbySecond:" + standbySecond);
                break;
            case R.id.btnSetUnit:
                mBTMControl.setTemperatureUnit(TEMPERATURE_UNIT_C);
                addLogInfo("btnSetUnit()--> TEMPERATURE_UNIT_C");
                break;
            case R.id.btnSetUnit2:
                mBTMControl.setTemperatureUnit(TEMPERATURE_UNIT_F);
                addLogInfo("btnSetUnit()--> TEMPERATURE_UNIT_F");
                break;
            case R.id.btnOnLine:
                mBTMControl.setOfflineTarget(FUNCTION_TARGET_ONLINE);
                addLogInfo("setOfflineTarget()--> FUNCTION_TARGET_ONLINE");
                break;
            case R.id.btnOffLine:
                mBTMControl.setOfflineTarget(FUNCTION_TARGET_OFFLINE);
                addLogInfo("setOfflineTarget()--> FUNCTION_TARGET_OFFLINE");
                break;
            case R.id.btnSetTarget:
                mBTMControl.setMeasuringTarget(MEASURING_TARGET_BODY);
                addLogInfo("setMeasuringTarget()--> MEASURING_TARGET_BODY");
                break;
            case R.id.btnSetTarget2:
                mBTMControl.setMeasuringTarget(MEASURING_TARGET_OBJECT);
                addLogInfo("setMeasuringTarget()--> MEASURING_TARGET_OBJECT");
                break;
            case R.id.btnGetData:
                mBTMControl.getMemoryData();
                addLogInfo("getMemoryData()");
                break;
        }
    }

}