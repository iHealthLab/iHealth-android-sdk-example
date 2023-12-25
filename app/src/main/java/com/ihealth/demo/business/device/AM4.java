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
import android.widget.EditText;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ec.easylibrary.utils.Utils;
import com.ihealth.communication.control.Am4Control;
import com.ihealth.communication.control.UpgradeControl;
import com.ihealth.communication.control.UpgradeProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesIDPS;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseActivity;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AM4 extends FunctionFoldActivity {
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
    @BindView(R.id.etSetUserId)
    EditText mEtSetUserId;
    @BindView(R.id.etResetId)
    EditText mEtResetId;
    @BindView(R.id.etAge)
    EditText mEtAge;
    @BindView(R.id.etHeight)
    EditText mEtHeight;
    @BindView(R.id.etWeight)
    EditText mEtWeight;
    @BindView(R.id.etGender)
    EditText mEtGender;
    @BindView(R.id.etUnit)
    EditText mEtUnit;
    @BindView(R.id.etTarget)
    EditText mEtTarget;
    @BindView(R.id.etActivityLevel)
    EditText mEtActivityLevel;
    @BindView(R.id.etSwimTargetTime)
    EditText mEtSwimTargetTime;
    @BindView(R.id.etAlarmId)
    EditText mEtAlarmId;
    @BindView(R.id.etAlarmHour)
    EditText mEtAlarmHour;
    @BindView(R.id.etAlarmMinute)
    EditText mEtAlarmMinute;
    @BindView(R.id.etAlarmRepeat)
    EditText mEtAlarmRepeat;
    @BindView(R.id.etAlarmDay)
    EditText mEtAlarmDay;
    @BindView(R.id.etAlarmOn)
    EditText mEtAlarmOn;
    @BindView(R.id.etDeleteAlarmId)
    EditText mEtDeleteAlarmId;
    @BindView(R.id.etRemandHour)
    EditText mEtRemandHour;
    @BindView(R.id.etRemandMinute)
    EditText mEtRemandMinute;
    @BindView(R.id.etRemandOn)
    EditText mEtRemandOn;
    @BindView(R.id.etTimeMode)
    EditText mEtTimeMode;
    @BindView(R.id.etMetaBolic)
    EditText mEtMetabolic;
    @BindView(R.id.etPoolLength)
    EditText mEtPoolLength;
    @BindView(R.id.etSwimHour)
    EditText mEtSwimHour;
    @BindView(R.id.etSwimMinute)
    EditText mEtSwimMinute;
    @BindView(R.id.etSwimUnit)
    EditText mEtSwimUnit;
    @BindView(R.id.etSwimOpen)
    EditText mEtSwimOpen;
    private Context mContext;
    private static final String TAG = "AM3S";
    private Am4Control mAm4Control;
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
        return R.layout.activity_am4;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_AM4);
        /* Get am4 controller */
        mAm4Control = iHealthDevicesManager.getInstance().getAm4Control(mDeviceMac);

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

            if (UpgradeProfile.ACTION_DEVICE_CLOUD_FIRMWARE_VERSION.equals(action)) {
                msg.obj = "result: " + message;
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
        if(mAm4Control!=null){
            mAm4Control.disconnect();
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

    @OnClick({R.id.btnDisconnect, R.id.btnIDPS, R.id.btnReset, R.id.btnSetUserId, R.id.btnGetUserId,
            R.id.btnSetUserInfo, R.id.btnGetUserInfo, R.id.btnGetAlarmNum, R.id.btnGetAlarmDetail,
            R.id.btnSetAlarm, R.id.btnDeleteAlarm, R.id.btnSetActivity, R.id.btnGetActivity,
            R.id.btnGetStatus, R.id.btnSyncTime, R.id.btnSetTimeMode, R.id.btnGetTimeMode,
            R.id.btnSetMetabolic, R.id.btnSyncReport, R.id.btnSetSwim, R.id.btnGetSwim,
            R.id.btnSyncData, R.id.btnSyncSleep, R.id.btnSyncActivity, R.id.btnCheckDevice,
            R.id.btnCheckCloud, R.id.btnDownload, R.id.btnUpgrade, R.id.btnStopUpgrade, R.id.btnSendRandom})
    public void onViewClicked(View view) {
        if (mAm4Control == null) {
            addLogInfo("mAm4Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mAm4Control.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnIDPS:
                mAm4Control.getIdps();
                addLogInfo("getIdps() -->" + mAm4Control.getIdps());
                break;
            case R.id.btnReset:
                long resetId = Long.parseLong(mEtResetId.getText().toString().trim());
                mAm4Control.reset(resetId);
                addLogInfo("reset() --> reset id:" + resetId);
                break;
            case R.id.btnSetUserId:
                int userId = Integer.parseInt(mEtSetUserId.getText().toString().trim());
                mAm4Control.setUserId(userId);
                addLogInfo("setUserId() --> set user id:" + userId);
                break;
            case R.id.btnGetUserId:
                mAm4Control.getUserId();
                addLogInfo("getUserId()");
                break;
            case R.id.btnSetUserInfo:
                String age = mEtAge.getText().toString().trim();
                String height = mEtHeight.getText().toString().trim();
                String weight = mEtWeight.getText().toString().trim();
                String gender = mEtGender.getText().toString().trim();
                String unit = mEtUnit.getText().toString().trim();
                String target = mEtTarget.getText().toString().trim();
                String activityLevel = mEtActivityLevel.getText().toString().trim();
                String swimTargetTime = mEtSwimTargetTime.getText().toString().trim();
                mAm4Control.setUserInfo(Integer.parseInt(age), Integer.parseInt(height), Float.parseFloat(weight),
                        Integer.parseInt(gender), Integer.parseInt(unit), Integer.parseInt(target), Integer.parseInt(activityLevel), Integer.parseInt(swimTargetTime));
                addLogInfo("setUserInfo()--> age:" + age + " height:" + height + " weight:" + weight + "" +
                        " gender:" + gender + " unit:" + unit + " target:" + target + " activityLevel:" + activityLevel);
                break;
            case R.id.btnGetUserInfo:
                mAm4Control.getUserInfo();
                addLogInfo("getUserInfo()");
                break;
            case R.id.btnGetAlarmNum:
                mAm4Control.getAlarmClockNum();
                addLogInfo("getAlarmClockNum()");
                break;
            case R.id.btnGetAlarmDetail: {
                int alarmId = Integer.parseInt(mEtDeleteAlarmId.getText().toString().trim());
                mAm4Control.getAlarmClockDetail(alarmId);
                addLogInfo("getAlarmClockDetail()-->alarmId:" + alarmId);
                break;
            }

            case R.id.btnSetAlarm: {
                String alarmId = mEtAlarmId.getText().toString().trim();
                String hour = mEtAlarmHour.getText().toString().trim();
                String minute = mEtAlarmMinute.getText().toString().trim();
                String strRepeat = mEtAlarmRepeat.getText().toString().trim();
                String days = mEtAlarmDay.getText().toString().trim();
                String strOn = mEtAlarmOn.getText().toString().trim();

                String[] alarmDays = days.split(",");
                int[] intDays = new int[alarmDays.length];
                for (int x = 0; x < alarmDays.length; x++) {
                    intDays[x] = Integer.parseInt(alarmDays[x]);
                }
                boolean isRepeat = strRepeat.equals("1") ? true : false;
                boolean isOn = strOn.equals("1") ? true : false;

                mAm4Control.setAlarmClock(Integer.parseInt(alarmId), Integer.parseInt(hour), Integer.parseInt(minute), isRepeat, intDays, isOn);
                addLogInfo("setAlarmClock()--> alarmId:" + alarmId + " hour:" + hour + " minute:" + minute + "" +
                        " isRepeat:" + isRepeat + " alarmDays:" + alarmDays + " isOn:" + isOn);
                break;
            }

            case R.id.btnDeleteAlarm:
                int deleteAlarmId = Integer.parseInt(mEtDeleteAlarmId.getText().toString().trim());
                mAm4Control.deleteAlarmClock(deleteAlarmId);
                addLogInfo("deleteAlarmClock()--> deleteAlarmId:" + deleteAlarmId);
                break;
            case R.id.btnSetActivity: {
                String hour = mEtRemandHour.getText().toString().trim();
                String minute = mEtRemandMinute.getText().toString().trim();
                String strOn = mEtRemandOn.getText().toString().trim();
                boolean isOn = strOn.equals("1") ? true : false;
                mAm4Control.setActivityRemind(Integer.parseInt(hour), Integer.parseInt(minute), isOn);
                addLogInfo("setActivityRemind() -->hour:" + hour + " minute:" + minute + " strOn:" + strOn);
                break;
            }

            case R.id.btnGetActivity:
                mAm4Control.getActivityRemind();
                addLogInfo("getActivityRemind()");
                break;
            case R.id.btnGetStatus:
                mAm4Control.queryAMState();
                addLogInfo("queryAMState()");
                break;
            case R.id.btnSyncTime:
                mAm4Control.syncRealTime();
                addLogInfo("syncRealTime()");
                break;
            case R.id.btnSetTimeMode:
                int mode = Integer.parseInt(mEtTimeMode.getText().toString().trim());
                mAm4Control.setHourMode(mode);
                addLogInfo("setHourMode()--> mode:" + mode);
                break;
            case R.id.btnGetTimeMode:
                mAm4Control.getHourMode();
                addLogInfo("getHourMode()");
                break;
            case R.id.btnSetSwim: {
                String poolLength = mEtPoolLength.getText().toString().trim();
                String hour = mEtSwimHour.getText().toString().trim();
                String minute = mEtSwimMinute.getText().toString().trim();
                String swimUnit = mEtSwimUnit.getText().toString().trim();
                String strOn = mEtSwimOpen.getText().toString().trim();
                boolean isOn = strOn.equals("1") ? true : false;
                mAm4Control.setSwimPara(isOn, Integer.parseInt(poolLength), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(swimUnit));
                addLogInfo("setSwimPara()--> isOn:" + isOn + " poolLength:" + poolLength + " hour:" + hour + "" +
                        " minute:" + minute + " swimUnit:" + swimUnit);
                break;
            }
            case R.id.btnGetSwim:
                mAm4Control.checkSwimPara();
                addLogInfo("checkSwimPara()");
                break;
            case R.id.btnSetMetabolic:
                int bmr = Integer.parseInt(mEtMetabolic.getText().toString().trim());
                mAm4Control.setUserBmr(bmr);
                addLogInfo("setUserBmr()--> bmr:" + bmr);
                break;
            case R.id.btnSendRandom:
                mAm4Control.sendRandom();
                addLogInfo("sendRandom()");
                break;
            case R.id.btnSyncReport:
                mAm4Control.syncStageReprotData();
                addLogInfo("syncStageReprotData()");
                break;
            case R.id.btnSyncData:
                mAm4Control.syncRealData();
                addLogInfo("syncRealData()");
                break;
            case R.id.btnSyncSleep:
                mAm4Control.syncSleepData();
                addLogInfo("syncSleepData()");
                break;
            case R.id.btnSyncActivity:
                mAm4Control.syncActivityData();
                addLogInfo("syncActivityData()");
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
                UpgradeControl.getInstance().queryDeviceCloudInfo(iHealthDevicesManager.TYPE_AM4, modelNumber, hardwareVersion, firmwareVersion);
                addLogInfo("queryDeviceCloudInfo() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber);
                break;
            case R.id.btnDownload:
                UpgradeControl.getInstance().downloadFirmwareFile(iHealthDevicesManager.TYPE_AM4, modelNumber, hardwareVersion, firmwareVersionCloud);
                addLogInfo("downloadFirmwareFile() -->firmwareVersionCloud:" + firmwareVersionCloud);
                break;
            case R.id.btnUpgrade:
                UpgradeControl.getInstance().startUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_AM4, modelNumber, hardwareVersion,
                        firmwareVersionCloud, modelNumber + hardwareVersion + firmwareVersionCloud);
                addLogInfo("startUpgrade() -->firmwareVersion:" + firmwareVersion
                        + " hardwareVersion:" + hardwareVersion + " modelNumber:" + modelNumber + " firmwareVersionCloud:" + firmwareVersionCloud);
                mBtnStopUpgrade.setEnabled(true);
                break;
            case R.id.btnStopUpgrade:
                UpgradeControl.getInstance().stopUpgrade(mDeviceMac, iHealthDevicesManager.TYPE_AM4);
                addLogInfo("stopUpgrade() ");
                break;
        }
    }

}
