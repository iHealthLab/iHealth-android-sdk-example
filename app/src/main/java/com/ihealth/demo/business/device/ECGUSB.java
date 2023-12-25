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
import android.widget.ImageView;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.dialog.loadingdialog.LoadingDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ec.easylibrary.utils.Utils;
import com.ihealth.communication.control.ECG3Profile;
import com.ihealth.communication.control.ECG3USBControl;
import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.control.UpgradeProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesIDPS;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseActivity;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ihealth.communication.control.ECG3Profile.ACTION_FORMAT_SDCARD;
import static com.ihealth.communication.control.ECG3Profile.ACTION_GET_FILTER_FILES;
import static com.ihealth.communication.control.ECG3Profile.ACTION_GET_IDPS;
import static com.ihealth.communication.control.ECG3Profile.ACTION_SPLICING_DATA;
import static com.ihealth.communication.control.ECG3Profile.ACTION_SYNC_OFFLINE_DATA;
import static com.ihealth.communication.control.ECG3Profile.FORMAT_RESULT;
import static com.ihealth.communication.control.ECG3Profile.GET_CACHE_DATA;
import static com.ihealth.communication.control.ECG3Profile.GET_SPLICING_DATA;
import static com.ihealth.communication.control.ECG3Profile.OFFLINE_DATAS;
import static com.ihealth.communication.control.ECG3Profile.OFFLINE_DATA_FILE_NAME;
import static com.ihealth.communication.control.ECG3Profile.SPLICING_DATA_FILE_NAME;
import static com.ihealth.communication.control.ECG3Profile.SPLICING_MARK_FILE_NAME;
import static com.ihealth.communication.control.UpgradeProfile.ACTION_DEVICE_UP_INFO;


public class ECGUSB extends FunctionFoldActivity {
    @BindView(R.id.etSpliceFilesName)
    EditText mEtSpliceFilesName;
    @BindView(R.id.btnSpliceData)
    Button mBtnSpliceData;
    @BindView(R.id.btnGetFilterFiles)
    Button mBtnGetFilterFiles;
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
    @BindView(R.id.imgStopUpGrade)
    ImageView mImgStopUpGrade;
    @BindView(R.id.etFilterFileA)
    EditText mEtFilterFileA;
    @BindView(R.id.etFilterFileB)
    EditText mEtFilterFileB;
    private Context mContext;
    private static final String TAG = "ECGUSB";
    private ECG3USBControl mECGUSBControl;
    private int mClientCallbackId;

    private String firmwareVersion = "";
    private String hardwareVersion = "";
    private String bleFirmwareVersion;
    private String modelNumber = "";
    private String firmwareVersionCloud = "";
    private LoadingDialog mLoaddingDialog;

    @Override
    public int contentViewID() {
        return R.layout.activity_ecg3_usb;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_ECG3_USB);
        /* Get ecgusb controller */
        mECGUSBControl = iHealthDevicesManager.getInstance().getECG3USBControl(mDeviceMac);
        mLoaddingDialog = new LoadingDialog(mContext);
        mLoaddingDialog.setCancellable(true);
        mECGUSBControl.getIdps();
        //ECGUSB is not support stop upgrade
        mBtnStopUpgrade.setVisibility(View.GONE);
        mImgStopUpGrade.setVisibility(View.GONE);
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
            Log.i(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);
            if (action.equals(ACTION_GET_IDPS)) {
                try {
                    JSONObject object = new JSONObject(message);
                    String macRelease = object.getString(iHealthDevicesIDPS.SERIALNUMBER);
                    Message msgFormat = new Message();
                    msgFormat.what = 2;
                    msgFormat.obj = mDeviceName + " " + macRelease ;
                    myHandler.sendMessage(msgFormat);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(ACTION_DEVICE_UP_INFO)) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    modelNumber = jsonObject.getString(UpgradeProfile.DEVICE_MODE);
                    hardwareVersion = jsonObject.getString(UpgradeProfile.DEVICE_HARDWARE_VERSION);
                    firmwareVersion = jsonObject.getString(UpgradeProfile.DEVICE_FIRMWARE_VERSION);
                    mBtnCheckCloud.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(ACTION_FORMAT_SDCARD)) {
                try {
                    JSONObject object = new JSONObject(message);
                    boolean rusult = object.getBoolean(FORMAT_RESULT);
                    Message msgFormat = new Message();
                    msgFormat.what = HANDLER_MESSAGE;
                    msgFormat.obj = "format SDCard result:" + rusult;
                    myHandler.sendMessage(msgFormat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(ACTION_SYNC_OFFLINE_DATA) || action.equals(ECG3Profile.ACTION_GET_CACHE_DATA)) {
                myHandler.sendEmptyMessage(5);
                try {
                    JSONObject object = new JSONObject(message);
                    JSONArray dataArray = new JSONArray();
                    if (action.equals(ACTION_SYNC_OFFLINE_DATA)) {
                        dataArray = object.getJSONArray(OFFLINE_DATAS);
                    } else if (action.equals(ECG3Profile.ACTION_GET_CACHE_DATA)) {
                        dataArray = object.getJSONArray(GET_CACHE_DATA);
                    }
                    if (dataArray.length() > 0) {
                        for (int x = 0; x < dataArray.length(); x++) {
                            JSONObject object1 = dataArray.getJSONObject(x);
                            mEtSpliceFilesName.append(object1.getString(OFFLINE_DATA_FILE_NAME) + ",");
                        }
                        mBtnSpliceData.setEnabled(true);
                    } else {
                        mBtnSpliceData.setEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (action.equals(ACTION_SPLICING_DATA)) {
                myHandler.sendEmptyMessage(5);
                try {
                    JSONObject object = new JSONObject(message);
                    JSONObject data = object.getJSONObject(GET_SPLICING_DATA);
                    String fileNameA = data.getString(SPLICING_DATA_FILE_NAME);
                    String fileNameB = data.getString(SPLICING_MARK_FILE_NAME);

                    Message msg3 = new Message();
                    msg3.what = 3;
                    msg3.obj = fileNameA;
                    myHandler.sendMessage(msg3);

                    Message msg4 = new Message();
                    msg4.what = 4;
                    msg4.obj = fileNameB;
                    myHandler.sendMessage(msg4);
                    mBtnGetFilterFiles.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (action.equals(ACTION_GET_FILTER_FILES)) {
                myHandler.sendEmptyMessage(5);
            } else if (action.equals(UpgradeProfile.ACTION_DEVICE_CLOUD_FIRMWARE_VERSION)) {
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
            } else if (UpgradeProfile.ACTION_DEVICE_UP_DOWNLOAD_COMPLETED.equals(action)) {
                Log.i(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "download success";
                myHandler.sendMessage(msg);
                mBtnUpgrade.setEnabled(true);
            }
            Message msg = new Message();
            msg.what = HANDLER_MESSAGE;
            msg.obj = action + " " + message;
            myHandler.sendMessage(msg);


        }
    };


    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    addLogInfo((String) msg.obj);
                    break;
                case 2:
                    mToolbarLayout.setTitle((String) msg.obj);
                    break;
                case 3:
                    mEtFilterFileA.setText((String) msg.obj);
                    break;
                case 4:
                    mEtFilterFileB.setText((String) msg.obj);
                    break;
                case 5:
                    mLoaddingDialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        if(mECGUSBControl!=null){
            mECGUSBControl.disconnect();
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

    @OnClick({R.id.btnIDPS, R.id.btnFormatSDCard, R.id.btnSyncData, R.id.btnGetCacheData, R.id.btnSpliceData, R.id.btnGetFilterFiles, R.id.btnDeleteAllData, R.id.btnDeleteCacheData, R.id.btnCheckDevice, R.id.btnCheckCloud, R.id.btnDownload, R.id.btnUpgrade, R.id.btnStopUpgrade})
    public void onViewClicked(View view) {
        if (mECGUSBControl == null) {
            addLogInfo("mECGUSBControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnIDPS:
                mECGUSBControl.getIdps();
                addLogInfo("getIdps()");
                break;
            case R.id.btnFormatSDCard:
                mECGUSBControl.formatSDCard();
                addLogInfo("formatSDCard()");
                break;
            case R.id.btnSyncData:
                mLoaddingDialog.show();
                mECGUSBControl.syncData();
                mEtSpliceFilesName.setText("");
                addLogInfo("syncData()");
                break;
            case R.id.btnGetCacheData:
                mLoaddingDialog.show();
                mEtSpliceFilesName.setText("");
                mECGUSBControl.getCacheData();
                addLogInfo("getCacheData()");
                break;
            case R.id.btnSpliceData:
                mLoaddingDialog.show();
                String fileName = mEtSpliceFilesName.getText().toString().trim();
                if (fileName.endsWith(",")) {
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                mECGUSBControl.spliceWithFileNames(fileName.split(","));
                addLogInfo("spliceWithFileNames() --");
                break;
            case R.id.btnGetFilterFiles:
                mLoaddingDialog.show();
                String fileNameA = mEtFilterFileA.getText().toString().trim();
                String fileNameB = mEtFilterFileB.getText().toString().trim();
                mECGUSBControl.getFilterDataByFileName(fileNameA, fileNameB);
                addLogInfo("getFilterDataByFileName() --> fileDataName:" + fileNameA + " fileMarkName");
                break;
            case R.id.btnDeleteAllData:
                mECGUSBControl.deleteAll();
                addLogInfo("deleteAll() --");
                break;
            case R.id.btnDeleteCacheData:
                mECGUSBControl.deleteCacheData();
                addLogInfo("deleteCacheData() --");
                break;
            case R.id.btnCheckDevice:
                UpgradeControl.getInstance().queryDeviceFirmwareInfo(mDeviceMac,mDeviceName);
                addLogInfo("queryDeviceFirmwareInfo() -->DeviceMac:" + mDeviceMac
                        + " DeviceName:" + mDeviceName );


                break;
            case R.id.btnCheckCloud:
                UpgradeControl.getInstance().queryDeviceCloudInfo(iHealthDevicesManager.TYPE_ECG3_USB, modelNumber, hardwareVersion, firmwareVersion);
                addLogInfo("queryDeviceCloudInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;
            case R.id.btnDownload:
                UpgradeControl.getInstance().downloadFirmwareFile(iHealthDevicesManager.TYPE_ECG3_USB, modelNumber, hardwareVersion, firmwareVersionCloud);
                addLogInfo("downloadFirmwareFile() -->firmwareVersionCloud:" + firmwareVersionCloud);
                break;
            case R.id.btnUpgrade:
                UpgradeControl.getInstance().startUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_ECG3_USB, modelNumber, hardwareVersion,
                        firmwareVersionCloud, modelNumber + hardwareVersion + firmwareVersionCloud);
                addLogInfo("startUpgrade() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber + " firmwareVersionCloud:" + firmwareVersionCloud);
                mBtnStopUpgrade.setEnabled(true);
                break;
//            case R.id.btnStopUpgrade:
//                UpgradeControl.getInstance().stopUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_ECG3_USB);
//                addLogInfo("stopUpgrade() ");
//                break;
        }
    }

}