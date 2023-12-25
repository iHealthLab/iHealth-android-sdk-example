package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ec.easylibrary.utils.Utils;
import com.ihealth.communication.control.Bg5sControl;
import com.ihealth.communication.control.Bg5sProfile;
import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.control.UpgradeProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesIDPS;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BG5S extends FunctionFoldActivity {
    @BindView(R.id.btnCheckDevice)
    Button mBtnCheckDevice;
    @BindView(R.id.btnCheckCloud)
    Button mBtnCheckCloud;
    @BindView(R.id.btnDownload)
    Button mBtnDownload;
    @BindView(R.id.btnUpgrade)
    Button mBtnUpgrade;
    @BindView(R.id.btnStopUpgrade)
    Button mBtnStopUpgrade;
    private Context mContext;
    private static final String TAG = "BG5S";
    private Bg5sControl mBg5sControl;
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
        return R.layout.activity_bg5s;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BG5S);
        /* Get bg5s controller */
        mBg5sControl = iHealthDevicesManager.getInstance().getBg5sControl(mDeviceMac);

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
            if (Bg5sProfile.ACTION_GET_STATUS_INFO.equals(action)) {
                msg.obj = "statusInfo: " + message;
                try {
                    JSONObject object = new JSONObject(message);
                    deviceTimeString = object.getString(Bg5sProfile.INFO_TIME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (Bg5sProfile.ACTION_ERROR.equals(action)) {
                msg.obj = "error: " + message;
            } else if (Bg5sProfile.ACTION_SET_TIME.equals(action)) {
                msg.obj = "set time success";
            } else if (Bg5sProfile.ACTION_SET_UNIT.equals(action)) {
                msg.obj = "set unit success";
            } else if (Bg5sProfile.ACTION_SEND_CODE.equals(action)) {
                msg.obj = "send code : " + message;
            } else if (Bg5sProfile.ACTION_DELETE_USED_STRIP.equals(action)) {
                msg.obj = "delete used strip success";
            } else if (Bg5sProfile.ACTION_DELETE_OFFLINE_DATA.equals(action)) {
                msg.obj = "delete offline data success";
            } else if (Bg5sProfile.ACTION_KEEP_LINK.equals(action)) {
                msg.obj = "keep link success";
            } else if (Bg5sProfile.ACTION_STRIP_IN.equals(action)) {
                msg.obj = "strip in";
            } else if (Bg5sProfile.ACTION_GET_BLOOD.equals(action)) {
                msg.obj = "get blood";
            } else if (Bg5sProfile.ACTION_STRIP_OUT.equals(action)) {
                msg.obj = "strip out";
            } else if (Bg5sProfile.ACTION_START_MEASURE.equals(action)) {
                msg.obj = "start measure success";
            } else if (Bg5sProfile.ACTION_GET_OFFLINE_DATA.equals(action)) {
                if (mBg5sControl != null) {
                    msg.obj = "offline data: " + message + "\nAfter adjust:" + mBg5sControl.adjustOfflineData(deviceTimeString, message);
                } else {
                    msg.obj = "offline data: " + message;
                }
            } else if (Bg5sProfile.ACTION_RESULT.equals(action)) {
                msg.obj = "result: " + message;
            } else if (UpgradeProfile.ACTION_DEVICE_CLOUD_FIRMWARE_VERSION.equals(action)) {
                msg.obj = "result: " + message;
                try {
                    JSONObject object = new JSONObject(message);
                    firmwareVersionCloud = object.optString(UpgradeProfile.DEVICE_CLOUD_FIRMWARE_VERSION);
                    if (Utils.compareVersion(firmwareVersion, firmwareVersionCloud) < 0) {
                        mBtnDownload.setEnabled(true);
                        addLogInfo("Need to upgrade");
                    }else {
                        mBtnDownload.setEnabled(false);
                        mBtnUpgrade.setEnabled(false);
                        addLogInfo("No need to upgrade");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (UpgradeProfile.ACTION_DEVICE_UP_DOWNLOAD_COMPLETED.equals(action)) {
                msg.obj = "download success";
                mBtnUpgrade.setEnabled(true);
            } else {
                msg.obj = "notify() action =  " + action + ", message = " + message;
            }
            myHandler.sendMessage(msg);

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
        if(mBg5sControl!=null){
            mBg5sControl.disconnect();
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


    @OnClick({R.id.btnDisconnect,
            R.id.btnMeasurement,
            R.id.btnMeasurement2,
            R.id.btnGetStatus,
            R.id.btnSetUnit,
            R.id.btnSetUnit2,
            R.id.btnSetTime,
            R.id.btnCloseBluetooth,
            R.id.btnSetDisplayMode,
            R.id.btnDeleteBottleInfo,
            R.id.btnGetData,
            R.id.btnSetOfflineMode,
            R.id.btnDeleteData,
            R.id.btnCheckDevice,
            R.id.btnCheckCloud,
            R.id.btnDownload,
            R.id.btnUpgrade,
            R.id.btnStopUpgrade})
    public void onViewClicked(View view) {
        if (mBg5sControl == null) {
            addLogInfo("mBg5sControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBg5sControl.disconnect();
                addLogInfo("disconnect()");
                break;

            case R.id.btnMeasurement:
                mBg5sControl.startMeasure(1);
                addLogInfo("startMeasure() --> test with blood");
                break;

            case R.id.btnMeasurement2:
                mBg5sControl.startMeasure(2);
                addLogInfo("startMeasure() --> test with control liquid ");
                break;

            case R.id.btnGetStatus:
                mBg5sControl.getStatusInfo();
                addLogInfo("getStatusInfo()");
                break;

            case R.id.btnSetUnit:
                mBg5sControl.setUnit(1);
                addLogInfo("setUnit()--> mmol/L");
                break;

            case R.id.btnSetUnit2:
                mBg5sControl.setUnit(2);
                addLogInfo("setUnit()--> mg/dL");
                break;

            case R.id.btnSetTime:
                //timeZone
                mBg5sControl.setTime(new Date(), -7);
                addLogInfo("setTime()");
                break;

            case R.id.btnCloseBluetooth:
                //timeZone
                mBg5sControl.closeBluetooth();
                addLogInfo("closeBluetooth()");
                break;

            case R.id.btnSetDisplayMode:
                mBg5sControl.setDisplayMode(false);
                addLogInfo("setDisplayMode()");
                break;

            case R.id.btnDeleteBottleInfo:
                mBg5sControl.deleteUsedStrip();
                addLogInfo("deleteUsedStrip()");
                break;

            case R.id.btnGetData:
                mBg5sControl.getOfflineData();
                addLogInfo("getOfflineData()");
                break;

            case R.id.btnSetOfflineMode:
                mBg5sControl.setOfflineMeasurementMode(false);
                addLogInfo("setOfflineMeasurementMode()");
                break;

            case R.id.btnDeleteData:
                mBg5sControl.deleteOfflineData();
                addLogInfo("deleteOfflineData()");
                break;

            case R.id.btnCheckDevice:
//                UpgradeControl.getInstance().queryDeviceFirmwareInfo(mDeviceMac, iHealthDevicesManager.TYPE_BG5S);
//                addLogInfo("queryDeviceFirmwareInfo()");
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

                addLogInfo("queryDeviceFirmwareInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                mBtnCheckCloud.setEnabled(true);
                break;
            case R.id.btnCheckCloud:
                UpgradeControl.getInstance().queryDeviceCloudInfo(iHealthDevicesManager.TYPE_BG5S, modelNumber, hardwareVersion, firmwareVersion);
                addLogInfo("queryDeviceCloudInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;

            case R.id.btnDownload:
                UpgradeControl.getInstance().downloadFirmwareFile(iHealthDevicesManager.TYPE_BG5S, modelNumber, hardwareVersion, firmwareVersionCloud);
                addLogInfo("downloadFirmwareFile() -->firmwareVersionCloud:" + firmwareVersionCloud);
                break;

            case R.id.btnUpgrade:
                UpgradeControl.getInstance().startUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_BG5S, modelNumber, hardwareVersion,
                        firmwareVersionCloud, modelNumber + hardwareVersion + firmwareVersionCloud);
                addLogInfo("startUpgrade() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber + " firmwareVersionCloud:" + firmwareVersionCloud);
                mBtnStopUpgrade.setEnabled(true);
                break;

            case R.id.btnStopUpgrade:
                UpgradeControl.getInstance().stopUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_BG5S);
                addLogInfo("stopUpgrade() " );
                break;
        }
    }
}
