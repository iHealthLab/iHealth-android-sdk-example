package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ec.easylibrary.utils.Utils;
import com.ihealth.communication.control.Hs4Control;
import com.ihealth.communication.control.HsProfile;
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
import butterknife.OnClick;


public class HS4 extends FunctionFoldActivity {
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
    @BindView(R.id.etUnitType)
    EditText mEtUnitType;
    @BindView(R.id.etUserId)
    EditText mEtUserId;


    private Context mContext;
    private static final String TAG = "HS4";
    private Hs4Control mHs4Control;
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
        return R.layout.activity_hs4;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_HS4);
        /* Get hs2 controller */
        mHs4Control = iHealthDevicesManager.getInstance().getHs4Control(mDeviceMac);

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
                case HsProfile.ACTION_BATTERY_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        int battery = jsonObject.getInt(HsProfile.BATTERY_HS);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "battery:" + battery;
                        mHandler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_HISTORICAL_DATA_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        JSONArray jsonArray = object.getJSONArray(HsProfile.HISTORDATA_HS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String dateString = jsonObject.getString(HsProfile.MEASUREMENT_DATE_HS);
                            float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                            String dataId = jsonObject.getString(HsProfile.DATAID);
                            Log.d(TAG, "dataId:" + dataId + "--date:" + dateString + "-weight:" + weight);
                        }
                        Message history = new Message();
                        history.what = 1;
                        history.obj = message;
                        mHandler.sendMessage(history);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_HISTORICAL_DATA_COMPLETE_HS:
                    Message complete = new Message();
                    complete.what = 1;
                    complete.obj = "history data complete";
                    mHandler.sendMessage(complete);
                    break;
                case HsProfile.ACTION_LIVEDATA_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonObject.getDouble(HsProfile.LIVEDATA_HS);
                        Log.d(TAG, "weight:" + weight);
                        Message value = new Message();
                        value.what = 1;
                        value.obj = "weight:" + weight;
                        mHandler.sendMessage(value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_ONLINE_RESULT_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                        String dataId = jsonObject.getString(HsProfile.DATAID);
                        Log.d(TAG, "dataId:" + dataId + "---weight:" + weight);
                        Message result = new Message();
                        result.what = 1;
                        result.obj = "dataId:" + dataId + "---weight:" + weight;
                        mHandler.sendMessage(result);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_NO_HISTORICALDATA:
                    Message empty = new Message();
                    empty.what = 1;
                    empty.obj = "no history data";
                    mHandler.sendMessage(empty);
                    break;
                case HsProfile.ACTION_ERROR_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        int err = jsonObject.getInt(HsProfile.ERROR_NUM_HS);
                        Log.d(TAG, "weight:" + err);
                        Message error = new Message();
                        error.what = 1;
                        error.obj = "err:" + err;
                        mHandler.sendMessage(error);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UpgradeProfile.ACTION_DEVICE_UP_INFO:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        modelNumber = jsonObject.getString(UpgradeProfile.DEVICE_MODE);
                        hardwareVersion = jsonObject.getString(UpgradeProfile.DEVICE_HARDWARE_VERSION);
                        firmwareVersion = jsonObject.getString(UpgradeProfile.DEVICE_FIRMWARE_VERSION);

                        Message unknown = new Message();
                        unknown.what = 1;
                        unknown.obj = message;
                        mHandler.sendMessage(unknown);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UpgradeProfile.ACTION_DEVICE_CLOUD_FIRMWARE_VERSION:
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        firmwareVersionCloud = jsonObject.getString(UpgradeProfile.DEVICE_CLOUD_FIRMWARE_VERSION);
                        if (Utils.compareVersion(firmwareVersion, firmwareVersionCloud) < 0) {
                            mBtnDownload.setEnabled(true);
                            addLogInfo("Need to upgrade");
                        } else {
                            mBtnDownload.setEnabled(false);
                            mBtnUpgrade.setEnabled(false);
                            addLogInfo("No need to upgrade");
                        }
                        Message unknown = new Message();
                        unknown.what = 1;
                        unknown.obj = message;
                        mHandler.sendMessage(unknown);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UpgradeProfile.ACTION_DEVICE_UP_DOWNLOAD_COMPLETED: {
                    Message unknown = new Message();
                    unknown.what = 1;
                    unknown.obj = "download success";
                    mHandler.sendMessage(unknown);
                    mBtnUpgrade.setEnabled(true);
                    break;
                }
                default: {
                    Message unknown = new Message();
                    unknown.what = 1;
                    unknown.obj = message;
                    mHandler.sendMessage(unknown);
                    break;
                }
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
        if(mHs4Control!=null){
            mHs4Control.disconnect();
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


    @OnClick({R.id.btnDisconnect, R.id.btnMeasurement,
            R.id.btnGetData, R.id.btnCheckDevice, R.id.btnCheckCloud,
            R.id.btnDownload, R.id.btnUpgrade, R.id.btnStopUpgrade})
    public void onViewClicked(View view) {
        if (mHs4Control == null) {
            addLogInfo("mHs4Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mHs4Control.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnMeasurement:
                String unit = mEtUnitType.getText().toString().trim();
                String userid = mEtUserId.getText().toString().trim();
                try {
                    mHs4Control.measureOnline(Integer.parseInt(unit), Integer.parseInt(userid));
                } catch (NumberFormatException e) {
                    addLogInfo("NumberFormatException:  Please input correct parameters.") ;
                    e.printStackTrace();
                }
                addLogInfo("measureOnline() -unit type:1 kg;2 lb;3 st-> unit:" + unit + " userid:" + userid);
                break;
            case R.id.btnGetData:
                mHs4Control.getOfflineData();
                addLogInfo("getOfflineData()");
                break;
            case R.id.btnCheckDevice:
//                UpgradeControl.getInstance().queryDeviceFirmwareInfo(mDeviceMac, iHealthDevicesManager.TYPE_HS4);
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
                UpgradeControl.getInstance().queryDeviceCloudInfo(iHealthDevicesManager.TYPE_HS4, modelNumber, hardwareVersion, firmwareVersion);
                addLogInfo("queryDeviceCloudInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;
            case R.id.btnDownload:
                UpgradeControl.getInstance().downloadFirmwareFile(iHealthDevicesManager.TYPE_HS4, modelNumber, hardwareVersion, firmwareVersionCloud);
                addLogInfo("downloadFirmwareFile() -->firmwareVersionCloud:" + firmwareVersionCloud);
                break;
            case R.id.btnUpgrade:
                UpgradeControl.getInstance().startUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_HS4, modelNumber, hardwareVersion,
                        firmwareVersionCloud, modelNumber + hardwareVersion + firmwareVersionCloud);
                addLogInfo("startUpgrade() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber + " firmwareVersionCloud:" + firmwareVersionCloud);
                mBtnStopUpgrade.setEnabled(true);
                break;
            case R.id.btnStopUpgrade:
                UpgradeControl.getInstance().stopUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_HS4);
                addLogInfo("stopUpgrade() ");
                break;
        }
    }
}
