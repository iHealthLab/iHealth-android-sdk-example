package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ec.easylibrary.utils.Utils;
import com.ihealth.communication.control.Pt3sbtControl;
import com.ihealth.communication.control.Pt3sbtProfile;
import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.control.UpgradeProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesIDPS;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class PT3SBT extends FunctionFoldActivity {

    private Context mContext;
    private static final String TAG = PT3SBT.class.getSimpleName();
    private Pt3sbtControl mPt3sbtControl;
    private int mClientCallbackId;

    private String firmwareVersion = "";
    private String hardwareVersion = "";
    private String bleFirmwareVersion;
    private String modelNumber = "";
    private String firmwareVersionCloud = "";

    @Override
    public int contentViewID() {
        return R.layout.activity_pt3sbt;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_PT3SBT);
        /* Get bg1s controller */
        mPt3sbtControl = iHealthDevicesManager.getInstance().getPt3sbtDevice(mDeviceMac);

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

            if (UpgradeProfile.ACTION_DEVICE_CLOUD_FIRMWARE_VERSION.equals(action)) {

                try {
                    JSONObject object = new JSONObject(message);
                    firmwareVersionCloud = object.optString(UpgradeProfile.DEVICE_CLOUD_FIRMWARE_VERSION);

                    addLogInfo("firmwareVersionCloud: " + firmwareVersionCloud);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (UpgradeProfile.ACTION_DEVICE_UP_DOWNLOAD_COMPLETED.equals(action)) {
                msg.obj = "download success";
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
        super.onDestroy();
        if (mPt3sbtControl != null) {
            mPt3sbtControl.disconnect();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_disconnect,
            R.id.btn_settime,
            R.id.btn_getbattery,
            R.id.btn_setunit,
            R.id.btn_getunit,
            R.id.btn_get_history_count,
            R.id.btn_get_history_data,
            R.id.btn_delete_data,
            R.id.btn_get_factory_data,
            R.id.btn_query_device,
            R.id.btn_query_latest,
            R.id.btn_download_firmware,
            R.id.btn_upgrade})
    public void onViewClicked(View view) {
        if (mPt3sbtControl == null) {
            addLogInfo("mBg1sControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btn_disconnect:
                mPt3sbtControl.disconnect();
                addLogInfo("disconnect()");
                break;

            case R.id.btn_settime:
                mPt3sbtControl.setTime();
                addLogInfo("setTime()");
                break;

            case R.id.btn_getbattery:
                mPt3sbtControl.getBattery();
                addLogInfo("getBattery()");
                break;

            case R.id.btn_setunit:
                mPt3sbtControl.setUnit(Pt3sbtProfile.PT3SBT_UNIT.Centigrade);
                addLogInfo("setUnit()");
                break;

            case R.id.btn_getunit:
                mPt3sbtControl.getUnit();
                addLogInfo("getUnit()");
                break;

            case R.id.btn_get_history_count:
                mPt3sbtControl.getHistoryCount();
                addLogInfo("getHistoryCount()");
                break;

            case R.id.btn_get_history_data:
                mPt3sbtControl.getHistoryData();
                addLogInfo("getHistoryData()");
                break;


            case R.id.btn_delete_data:
                mPt3sbtControl.deleteHistory();
                addLogInfo("deleteHistory()");
                break;

            case R.id.btn_get_factory_data:
                mPt3sbtControl.getFactoryData();
                addLogInfo("getFactoryData()");
                break;

            case R.id.btn_query_device:
                String idps = iHealthDevicesManager.getInstance().getDevicesIDPS(mDeviceMac);

                try {
                    JSONObject idpsObj = new JSONObject(idps);
                    firmwareVersion = idpsObj.getString(iHealthDevicesIDPS.FIRMWAREVERSION);
                    hardwareVersion = idpsObj.getString(iHealthDevicesIDPS.HARDWAREVERSION);
                    bleFirmwareVersion = idpsObj.getString(iHealthDevicesIDPS.BLEFIRMWAREVERSION);
                    modelNumber = idpsObj.getString(iHealthDevicesIDPS.MODENUMBER);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                addLogInfo("queryDeviceFirmwareInfo() -->firmwareVersion:" + firmwareVersion + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;

            case R.id.btn_query_latest:
                UpgradeControl.getInstance().queryDeviceCloudInfo(iHealthDevicesManager.TYPE_PT3SBT, modelNumber, hardwareVersion, firmwareVersion);
                addLogInfo("queryDeviceCloudInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;

            case R.id.btn_download_firmware:
                UpgradeControl.getInstance().downloadFirmwareFile(iHealthDevicesManager.TYPE_PT3SBT, modelNumber, hardwareVersion, firmwareVersionCloud);
                addLogInfo("downloadFirmwareFile() -->firmwareVersionCloud:" + firmwareVersionCloud);
                break;

            case R.id.btn_upgrade:
                UpgradeControl.getInstance().startUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_PT3SBT,
                        modelNumber,
                        hardwareVersion,
                        firmwareVersionCloud,
                        modelNumber + hardwareVersion + firmwareVersionCloud);
                addLogInfo("startOnlineUpgrade()");
                break;

        }
    }



}
