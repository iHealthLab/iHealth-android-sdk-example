package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ido.ble.protocol.model.NewMessageInfo;
import com.ido.ble.protocol.model.QuickSportMode;
import com.ihealth.communication.control.Am5Control;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.model.AM5Alarm;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;


public class AM5 extends FunctionFoldActivity {

    private Context mContext;
    private static final String TAG = "AM5";
    private Am5Control mAM5Control;
    private int mClientCallbackId;

    @Override
    public int contentViewID() {
        return R.layout.activity_am5;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_AM5);
        /* Get AM5 controller */
        mAM5Control = iHealthDevicesManager.getInstance().getAm5Control(mDeviceMac);

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
            msg.obj = action + "      " + message;
            mHandler.sendMessage(msg);
        }
    };


    Handler mHandler = new Handler() {
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
        if(mAM5Control!=null){
            mAM5Control.disconnect();
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


    @OnClick({R.id.btnDisconnect, R.id.btnBindUser, R.id.btnUnBindUser, R.id.btnGetBindStatus,
            R.id.btnGetDeviceInfo, R.id.btnFunction, R.id.btnGetMac, R.id.btnSetTime, R.id.btnSetAlarm,
            R.id.btnSedentaryReminder, R.id.btnSetTarget, R.id.btnSetUserInfo, R.id.btnSetUnit,
            R.id.btnSetWearMode, R.id.btnSetHeartInterval, R.id.btnSetMeasureMode,
            R.id.btnSetNoDisturb, R.id.btnSportMode, R.id.btnNoticeComingCall, R.id.btnStopComingCall,
            R.id.btnNoticeMessage, R.id.btnRestore, R.id.btnSyncConfigPending,
            R.id.btnSyncConfig, R.id.btnSyncConfigNo, R.id.btnGetLiveData, R.id.btnGetActivityCount,
            R.id.btnSyncHealthData, R.id.btnSyncHealthData_no, R.id.btnSyncActivityData, R.id.btnSyncActivityDataNo})
    public void onViewClicked(View view) {
        if (mAM5Control == null) {
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mAM5Control.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnBindUser:
                mAM5Control.bindDevice();
                addLogInfo("bindDevice()");
                break;
            case R.id.btnUnBindUser:
                mAM5Control.unBindDevice();
                addLogInfo("unBindDevice()");
                break;
            case R.id.btnGetBindStatus:
                boolean isBind = mAM5Control.isBind();
                addLogInfo("isBind()  status: "+isBind);
                break;
            case R.id.btnGetDeviceInfo:
                mAM5Control.getBasicInfo();
                addLogInfo("getBasicInfo()");
                break;
            case R.id.btnFunction:
                mAM5Control.getFunctionInfo();
                addLogInfo("getFunctionInfo()");
                break;
            case R.id.btnGetMac:
                mAM5Control.getMacAddress();
                addLogInfo("getMacAddress()");
                break;
            case R.id.btnSetTime:
//                mAM5Control.setTime("2019", "1", "1", "12", "0", "0", "2");
                mAM5Control.setTime();
                addLogInfo("setTime()");
                break;
            case R.id.btnSetAlarm:
                List<AM5Alarm> alarmList = new ArrayList<>();
                AM5Alarm AM5Alarm = new AM5Alarm();
                AM5Alarm.setAlarmId(1);
                AM5Alarm.setAlarmHour(16);
                AM5Alarm.setAlarmMinute(38);
                AM5Alarm.setAlarmType(AM5Alarm.TYPE_GETUP);
                AM5Alarm.setOn_off(true);
                AM5Alarm.setWeekRepeat(new boolean[]{true, true, true, true, true, true, true});

                AM5Alarm AM5Alarm2 = new AM5Alarm();
                AM5Alarm.setAlarmId(2);
                AM5Alarm2.setAlarmHour(16);
                AM5Alarm2.setAlarmMinute(50);
                AM5Alarm2.setAlarmType(AM5Alarm.TYPE_MEETING);
                AM5Alarm2.setOn_off(true);
                AM5Alarm2.setWeekRepeat(new boolean[]{true, true, true, true, true, true, true});

                alarmList.add(AM5Alarm);
                alarmList.add(AM5Alarm2);
                mAM5Control.setAlarm(alarmList);
                addLogInfo("setAlarm()");
                break;
            case R.id.btnSedentaryReminder:
                mAM5Control.setLongSit(8,30,17,30,60,true,new boolean[]{false,true,true,true,true,true,false});
                addLogInfo("setLongSit()");
                break;
            case R.id.btnSetTarget:
                mAM5Control.setGoal("10000");
                addLogInfo("setGoal()");
                break;
            case R.id.btnSetUserInfo:
                mAM5Control.setUserInfo(1991, 4, 4, 90, 180, 1);
                addLogInfo("setUserInfo()");
                break;
            case R.id.btnSetUnit:
                mAM5Control.setUnit(0, 1);
                addLogInfo("setUnit()");
                break;
            case R.id.btnSetWearMode:
                mAM5Control.setHandWearMode(0);
                addLogInfo("setHandWearMode()");
                break;
            case R.id.btnSetHeartInterval:
                mAM5Control.setHeartRateInterval(120, 130, 140, 90);
                addLogInfo("setHeartRateInterval()");
                break;
            case R.id.btnSetMeasureMode:
                mAM5Control.setHeartRateMeasureMode(1, 0, 12, 0, 20, 0);
                addLogInfo("setHeartRateMeasureMode()");
                break;
            case R.id.btnSetNoDisturb:
                mAM5Control.setNotDisturb(true, 10, 30, 12, 0);
                addLogInfo("setNotDisturb()");
                break;
            case R.id.btnSportMode:
                QuickSportMode quickSportMode = new QuickSportMode();
                quickSportMode.sport_type0_walk = true;
                quickSportMode.sport_type0_run = true;
                quickSportMode.sport_type0_by_bike = true;
                quickSportMode.sport_type0_on_foot = true;
                quickSportMode.sport_type0_mountain_climbing = true;
                quickSportMode.sport_type0_badminton = true;
                quickSportMode.sport_type1_fitness = true;
                quickSportMode.sport_type1_spinning = true;
                quickSportMode.sport_type1_treadmill = true;
                quickSportMode.sport_type2_yoga = true;
                quickSportMode.sport_type2_basketball = true;
                quickSportMode.sport_type2_footballl = true;
                quickSportMode.sport_type2_tennis = true;
                quickSportMode.sport_type3_dance = true;

                mAM5Control.setSportMode(quickSportMode);
                addLogInfo("setSportMode()");
                break;
            case R.id.btnNoticeComingCall:
                mAM5Control.setIncomingCallInfo("wj", "13588886666");
                addLogInfo("setIncomingCallInfo()");
                break;
            case R.id.btnStopComingCall:
                mAM5Control.setStopInComingCall();
                addLogInfo("setStopInComingCall()");
                break;
            case R.id.btnNoticeMessage:
                mAM5Control.setNewMessageDetailInfo(NewMessageInfo.TYPE_SMS, "wj", "13588886666", "ihealth");
                addLogInfo("setNewMessageDetailInfo()");
                break;
            case R.id.btnRestore:
                mAM5Control.reboot();
                addLogInfo("reboot()");
                break;
            case R.id.btnSyncConfigPending:
                mAM5Control.setUserInfoPending(1991, 4, 4, 90, 180, 1);
                addLogInfo("setUserInfoPending()");
                break;
            case R.id.btnSyncConfig:
                mAM5Control.syncConfigData();
                addLogInfo("syncConfigData()");
                break;
            case R.id.btnSyncConfigNo:
                mAM5Control.stopSyncConfigData();
                addLogInfo("stopSyncConfigData()");
                break;
            case R.id.btnGetLiveData:
                mAM5Control.getLiveData();
                addLogInfo("getLiveData()");
                break;
            case R.id.btnGetActivityCount:
                mAM5Control.getActivityCount();
                addLogInfo("getActivityCount()");
                break;
            case R.id.btnSyncHealthData:
                mAM5Control.syncHealthData();
                addLogInfo("syncHealthData()");
                break;
            case R.id.btnSyncHealthData_no:
                mAM5Control.stopSyncHealthData();
                addLogInfo("stopSyncHealthData()");
                break;
            case R.id.btnSyncActivityData:
                mAM5Control.syncActivityData();
                addLogInfo("syncActivityData()");
                break;
            case R.id.btnSyncActivityDataNo:
                mAM5Control.stopSyncActivityData();
                addLogInfo("stopSyncActivityData()");
                break;
        }
    }
}

