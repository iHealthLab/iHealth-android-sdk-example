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
import com.ihealth.communication.control.Po3Control;
import com.ihealth.communication.control.PoProfile;
import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.control.UpgradeProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesIDPS;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PO3 extends FunctionFoldActivity {
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
    @BindView(R.id.btnMeasurement)
    Button mBtnMeasurement;
    @BindView(R.id.btnBattery)
    Button mBtnBattery;
    @BindView(R.id.btnGetData)
    Button mBtnGetData;


    private Context mContext;
    private static final String TAG = "PO3";
    private Po3Control mPo3Control;
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
        return R.layout.activity_po3;
    }

    @Override
    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = "PO3/PO3M";
        /* register ihealthDevicesCallback id */
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_PO3);
        /* Get po3 controller */
        mPo3Control = iHealthDevicesManager.getInstance().getPo3Control(mDeviceMac);

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
            Log.d(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);
            JSONTokener jsonTokener = new JSONTokener(message);
            switch (action) {
                case PoProfile.ACTION_OFFLINEDATA_PO:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        JSONArray jsonArray = object.getJSONArray(PoProfile.OFFLINEDATA_PO);
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                            String dataId = jsonObject.getString(PoProfile.DATAID);
//                            String dateString = jsonObject.getString(PoProfile.MEASURE_DATE_PO);
//                            int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
//                            int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
////                            JSONArray jsonArray1 = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
//                            int[] wave = new int[jsonArray1.length()];
//                            for (int j = 0; j < jsonArray1.length(); j++) {
//                                wave[j] = jsonArray1.getInt(j);
//                            }
//                            Log.i(TAG, "dataId:" + dataId + "--date:" + dateString + "--oxygen:" + oxygen + "--pulseRate:" + pulseRate
//                                    + "-wave1:"
//                                    + wave[0]
//                                    + "-wave2:" + wave[1] + "--wave3:" + wave[2]);
//                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = message;
                        mHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PoProfile.ACTION_LIVEDA_PO:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                        int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                        float PI = (float) jsonObject.getDouble(PoProfile.PI_PO);
                        JSONArray jsonArray = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                        int[] wave = new int[3];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            wave[i] = jsonArray.getInt(i);
                        }
                        Log.i(TAG, "oxygen:" + oxygen + "--pulseRate:" + pulseRate + "--Pi:" + PI + "-wave1:" + wave[0]
                                + "-wave2:" + wave[1] + "--wave3:" + wave[2]);
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = message;
                        mHandler.sendMessage(message3);
                        mBtnMeasurement.setEnabled(false);
                        mBtnBattery.setEnabled(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PoProfile.ACTION_RESULTDATA_PO:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        String dataId = jsonObject.getString(PoProfile.DATAID);
                        int oxygen = jsonObject.getInt(PoProfile.BLOOD_OXYGEN_PO);
                        int pulseRate = jsonObject.getInt(PoProfile.PULSE_RATE_PO);
                        float PI = (float) jsonObject.getDouble(PoProfile.PI_PO);
                        JSONArray jsonArray = jsonObject.getJSONArray(PoProfile.PULSE_WAVE_PO);
                        int[] wave = new int[3];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            wave[i] = jsonArray.getInt(i);
                        }
                        Log.i(TAG, "dataId:" + dataId + "--oxygen:" + oxygen + "--pulseRate:" + pulseRate + "--Pi:" + PI + "-wave1:" + wave[0]
                                + "-wave2:" + wave[1] + "--wave3:" + wave[2]);
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = message;
                        mHandler.sendMessage(message3);

                        mBtnMeasurement.setEnabled(true);
                        mBtnBattery.setEnabled(true);
                        mBtnGetData.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PoProfile.ACTION_NO_OFFLINEDATA_PO:
                    Message message2 = new Message();
                    message2.what = 1;
                    message2.obj = "no history data";
                    mHandler.sendMessage(message2);
                    break;
                case PoProfile.ACTION_BATTERY_PO:
                    JSONObject jsonobject;
                    try {
                        jsonobject = (JSONObject) jsonTokener.nextValue();
                        int battery = jsonobject.getInt(PoProfile.BATTERY_PO);
                        Log.d(TAG, "battery:" + battery);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message message3 = new Message();
                    message3.what = 1;
                    message3.obj = message;
                    mHandler.sendMessage(message3);

                    break;

                case UpgradeProfile.ACTION_DEVICE_CLOUD_FIRMWARE_VERSION: {
                    Message message4 = new Message();
                    message4.what = 1;
                    message4.obj = "result: " + message4;
                    mHandler.sendMessage(message4);
                    try {
                        JSONObject object = new JSONObject(message);
                        firmwareVersionCloud = object.optString(UpgradeProfile.DEVICE_CLOUD_FIRMWARE_VERSION);
                        if (Utils.compareVersion(firmwareVersion, firmwareVersionCloud) < 0) {
                            mBtnDownload.setEnabled(true);
                            addLogInfo("Need to upgrade");
                        } else {
                            mBtnDownload.setEnabled(false);
                            mBtnUpgrade.setEnabled(false);
                            addLogInfo("No need to upgrade");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case UpgradeProfile.ACTION_DEVICE_UP_DOWNLOAD_COMPLETED: {
                    Message message5 = new Message();
                    message5.what = 1;
                    message5.obj = "download success";
                    mHandler.sendMessage(message5);
                    mBtnUpgrade.setEnabled(true);
                    break;
                }
                default:
                    Message message5 = new Message();
                    message5.what = 1;
                    message5.obj = "notify() action =  " + action + ", message = " + message;
                    mHandler.sendMessage(message5);
                    break;
            }

        }
    };


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    addLogInfo((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        if(mPo3Control!=null){
            mPo3Control.disconnect();
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
            R.id.btnSetTime,
            R.id.btnGetTime,
            R.id.btnBattery,
            R.id.btnGetData,
            R.id.btnCheckDevice,
            R.id.btnCheckCloud,
            R.id.btnDownload,
            R.id.btnUpgrade,
            R.id.btnStopUpgrade})
    public void onViewClicked(View view) {
        if (mPo3Control == null) {
            addLogInfo("mPo3Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mPo3Control.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnMeasurement:
                mPo3Control.startMeasure();
                addLogInfo("startMeasure()");
                break;
            case R.id.btnSetTime:
//                mPo3Control.setTime();
                addLogInfo("startMeasure()");
                break;
            case R.id.btnGetTime:
//                mPo3Control.getTime();
                addLogInfo("startMeasure()");
                break;
            case R.id.btnGetData:
                mPo3Control.getHistoryData();
                addLogInfo("getHistoryData()");
                break;
            case R.id.btnBattery:
                mPo3Control.getBattery();
                addLogInfo("getBattery()");
                break;
            case R.id.btnCheckDevice:
//                UpgradeControl.getInstance().queryDeviceFirmwareInfo(mDeviceMac, iHealthDevicesManager.TYPE_PO3);
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
                UpgradeControl.getInstance().queryDeviceCloudInfo(iHealthDevicesManager.TYPE_PO3, modelNumber, hardwareVersion, firmwareVersion);
                addLogInfo("queryDeviceCloudInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;
            case R.id.btnDownload:
                UpgradeControl.getInstance().downloadFirmwareFile(iHealthDevicesManager.TYPE_PO3, modelNumber, hardwareVersion, firmwareVersionCloud);
                addLogInfo("downloadFirmwareFile() -->firmwareVersionCloud:" + firmwareVersionCloud);
                break;
            case R.id.btnUpgrade:
                UpgradeControl.getInstance().startUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_PO3, modelNumber, hardwareVersion,
                        firmwareVersionCloud, modelNumber + hardwareVersion + firmwareVersionCloud);
                addLogInfo("startUpgrade() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber + " firmwareVersionCloud:" + firmwareVersionCloud);
                mBtnStopUpgrade.setEnabled(true);
                break;
            case R.id.btnStopUpgrade:
                UpgradeControl.getInstance().stopUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_PO3);
                addLogInfo("stopUpgrade() ");
                break;
        }
    }

}
