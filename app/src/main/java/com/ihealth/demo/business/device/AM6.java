package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Am6Control;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;
import com.ihealth.sdk.command.am6.Am6Command;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


public class AM6 extends FunctionFoldActivity {

    @BindView(R.id.rv_am6_api)
    public RecyclerView rvAPIs;

    @BindView(R.id.et_type)
    public EditText etType;

    @BindView(R.id.et_status)
    public EditText etStatus;

    @BindView(R.id.et_title)
    public EditText etTitle;

    @BindView(R.id.et_content)
    public EditText etContent;

    private Context mContext;
    private static final String TAG = AM6.class.getSimpleName();

    private int mClientCallbackId;
    private Am6Control mAm6Control;

    @Override
    public int contentViewID() {
        return R.layout.activity_am6;
    }

    @Override
    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");

        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_AM6);
        /* Get am3s controller */
        mAm6Control = iHealthDevicesManager.getInstance().getAm6Control(mDeviceMac);

        List<AM6API> data = new ArrayList<>();
        data.add(new AM6API("multiScanConnect", "Multi Scan&Connect"));
        data.add(new AM6API("multiConnect", "Multi Connect"));

//
        data.add(new AM6API("getDeviceInfoAndSyncTime","Get device info and sync time"));
        data.add(new AM6API("setUser","Set User"));
//        data.add(new AM6API("setPhonePlatform","Set Phone Platform"));
        data.add(new AM6API("notifyMessage","Notify Message"));
        data.add(new AM6API("bindUser", "Bind User"));
        data.add(new AM6API("bindUserSuccess","Bind User Success"));
        data.add(new AM6API("bindUserFail","Bind User Fail"));
        data.add(new AM6API("unbindUser","Unbind User"));
//        data.add(new AM6API("findDevice","Find Device"));
//        data.add(new AM6API("rebootDevice","Reboot Device"));
        data.add(new AM6API("getTime","Get Time"));
//        data.add(new AM6API("setTargetRemind","Set Target Remind"));
//        data.add(new AM6API("getTargetRemind","Get Target Remind"));
//        data.add(new AM6API("setRaiseToLightRemind","Set Raise To Light Remind"));
//        data.add(new AM6API("getRaiseToLightRemind","Get Raise To Light Remind"));
        data.add(new AM6API("setDoNotDisturbMode","Set Do Not Disturb Mode"));
//        data.add(new AM6API("getDoNotDisturbMode","Get Do Not Disturb Mode"));
//        data.add(new AM6API("getHand","Get Wear Hand"));
//        data.add(new AM6API("setHand","Set Wear Hand"));
//        data.add(new AM6API("getSedentaryRemind","Get Sedentary Remind"));
//        data.add(new AM6API("setSedentaryRemind","Set Sedentary Remind"));
//        data.add(new AM6API("getAlarmClockList","Get AlarmClock List"));
//        data.add(new AM6API("setAlarmClockList","Set AlarmClock List"));
//        data.add(new AM6API("readySyncData","Ready Sync Data"));
//        data.add(new AM6API("getDailyData","Get Daily Data"));
//        data.add(new AM6API("getStepData","Get Step Data"));
        data.add(new AM6API("getSleepData","Get Sleep Data"));
//        data.add(new AM6API("getHeartRateData","Get HeartRate Data"));
//        data.add(new AM6API("getBloodOxygenData","Get BloodOxygen Data"));
        data.add(new AM6API("getActivityData","Get ActivityData"));
//        data.add(new AM6API("deleteData","Delete Data"));
        Am6Adapter adapter = new Am6Adapter(data);
        rvAPIs.setAdapter(adapter);
        rvAPIs.setLayoutManager(new LinearLayoutManager(this));
        rvAPIs.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(id -> {
            if ("multiScanConnect".equals(id)) {
                multiScanConnect();

            } else if ("multiConnect".equals(id)) {
                multiConnect();

            } else if ("bindUser".equals(id)) {
                mAm6Control.startBind();
                Log.i("", "Am6Control.parseQrCode(\"\"): " + Am6Control.parseQrCode(""));

            } else if ("bindUserSuccess".equals(id)) {
                String userId = "1234567890abcdef";
                byte[] byteUserId = new byte[16];
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] inputBytes = userId.getBytes();
                    byteUserId = md.digest(inputBytes);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                mAm6Control.bindUserSuccess(byteUserId);

            } else if ("bindUserFail".equals(id)) {
                mAm6Control.bindUserFail();

            } else if ("unbindUser".equals(id)) {
                String userId = "1234567890abcde";
                byte[] byteUserId = new byte[16];
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] inputBytes = userId.getBytes();
                    byteUserId = md.digest(inputBytes);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                mAm6Control.unBindUser(byteUserId);

            } else if ("getDeviceInfoAndSyncTime".equals(id)) {
                mAm6Control.getDeviceInfoAndSyncTime(false);

            } else if ("setPhonePlatform".equals(id)) {
                mAm6Control.setPhonePlatform();

            } else if ("notifyMessage".equals(id)) {
                int status = Integer.parseInt(etStatus.getText().toString());
                int type = Integer.parseInt(etType.getText().toString());
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                mAm6Control.notifyMessage(System.currentTimeMillis(), status, type, title.getBytes(StandardCharsets.UTF_8), content.getBytes(StandardCharsets.UTF_8));

            } else if ("setUser".equals(id)) {
                String userId = "1234567890abcdef";
                byte[] byteUserId = new byte[16];
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] inputBytes = userId.getBytes();
                    byteUserId = md.digest(inputBytes);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                mAm6Control.setUserInfo(byteUserId, 1, 30, 176, 60);

            } else if ("setTargetRemind".equals(id)) {
                mAm6Control.setTargetRemind(true, 1000, 1000);

            } else if ("findDevice".equals(id)) {
                mAm6Control.findDevice(0);

            } else if ("rebootDevice".equals(id)) {
                mAm6Control.rebootDevice();

            } else if ("getTime".equals(id)) {
                mAm6Control.getTime();

            } else if ("getTargetRemind".equals(id)) {
                mAm6Control.getTargetRemind();

            } else if ("setRaiseToLightRemind".equals(id)) {
                mAm6Control.setRaiseToLightRemind(1, 840, 1200);

            } else if ("getRaiseToLightRemind".equals(id)) {
                mAm6Control.getRaiseToLightRemind();

            } else if ("setDoNotDisturbMode".equals(id)) {
                mAm6Control.setDoNotDisturbMode(1, 840, 1200);

            } else if ("getDoNotDisturbMode".equals(id)) {
                mAm6Control.getDoNotDisturbMode();

            } else if ("setHand".equals(id)) {
                mAm6Control.setWearHand(1);

            } else if ("getHand".equals(id)) {
                mAm6Control.getWearHand();

            } else if ("setSedentaryRemind".equals(id)) {
                mAm6Control.setSedentaryRemind(1, 840, 1200);

            } else if ("getSedentaryRemind".equals(id)) {
                mAm6Control.getSedentaryRemind();

            } else if ("setAlarmClockList".equals(id)) {
                Am6Command.AlarmClockInfo info = new Am6Command.AlarmClockInfo();
                info.ts = 1440;
                info.isOpen = true;
                info.week = new boolean[]{true, true, true, true, true, true, true};
                mAm6Control.setAlarmClockList(info);
//                mAm6Control.setAlarmClockList(null);

            } else if ("getAlarmClockList".equals(id)) {
                mAm6Control.getAlarmClockList();

            } else if ("readySyncData".equals(id)) {
                mAm6Control.readySyncData();

            } else if ("getDailyData".equals(id)) {
                mAm6Control.getDailyData();

            } else if ("getStepData".equals(id)) {
                mAm6Control.getStepData();

            } else if ("getSleepData".equals(id)) {
                mAm6Control.getSleepData();

            } else if ("getHeartRateData".equals(id)) {
                for (int i = 0; i < 10; i++) {
                    mAm6Control.getActivityData();
                    mAm6Control.getDailyData();
                    mAm6Control.getStepData();
                    mAm6Control.getSleepData();
                    mAm6Control.getHeartRateData();
                    mAm6Control.getBloodOxygenData();
                }

            } else if ("getBloodOxygenData".equals(id)) {
                mAm6Control.getBloodOxygenData();

            } else if ("getActivityData".equals(id)) {
                mAm6Control.getActivityData();

            } else if ("deleteData".equals(id)) {
                mAm6Control.deleteData(1);

            }
        });

        showLogLayout();
    }

    private int multiTime = 0;
    private int multiSuccessTime = 0;
    private int multiFailTime = 0;
    private boolean isMultiScanConnect = false;
    private boolean isMultiConnect = false;
    private void multiScanConnect() {
        multiTime = 0;
        multiSuccessTime = 0;
        multiFailTime = 0;
        isMultiScanConnect = true;
        mAm6Control.disconnect();
    }

    private void multiConnect() {
        multiTime = 0;
        multiSuccessTime = 0;
        multiFailTime = 0;
        isMultiConnect = true;
        mAm6Control.disconnect();
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "status: " + status);

            if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                if (!isMultiScanConnect && !isMultiConnect) {
                    runOnUiThread(() -> {
                        addLogInfo(mContext.getString(R.string.connect_main_tip_disconnect));
                        ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_disconnect));
                        finish();
                    });
                }
                Log.e(TAG, "连接1: " + mac);
                if (isMultiScanConnect) {
                    multiTime += 1;
                    iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.AM6);
                }
                Log.e(TAG, "连接2: " + mac + " " + isMultiConnect);
                if (isMultiConnect) {
                    multiTime += 1;
                    Log.e(TAG, "直接连接3: " + mac);
                    SystemClock.sleep(5000);
                    iHealthDevicesManager.getInstance().connectDevice(mDeviceMac, iHealthDevicesManager.TYPE_AM6);
                }
            }

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                if (isMultiScanConnect || isMultiConnect) {
                    multiSuccessTime += 1;
                    Log.e(TAG, "multi time: " + multiTime);
                    Log.e(TAG, "multi success time: " + multiSuccessTime);
                    Log.e(TAG, "multi fail time: " + multiFailTime);
                    if (multiTime > 100) {
                        isMultiScanConnect = false;
                        isMultiConnect = false;
                    } else {
                        Log.e(TAG, "断开: " + mac);
                        iHealthDevicesManager.getInstance().getAm6Control(mac).disconnect();
                    }
                }
            }

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL) {
                multiFailTime += 1;
                Log.e("TAG", "multi time: " + multiTime);
                Log.e("TAG", "multi success time: " + multiSuccessTime);
                Log.e("TAG", "multi fail time: " + multiFailTime);
                multiTime += 1;
                if (multiTime > 100) {
                    isMultiScanConnect = false;
                    isMultiConnect = false;
                    return;
                }
                if (isMultiScanConnect) {
                    iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.AM6);
                }

                if (isMultiConnect) {
                    iHealthDevicesManager.getInstance().connectDevice(mDeviceMac, iHealthDevicesManager.TYPE_AM6);
                }
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

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map<String, Object> manufacturerData) {
            if (mac.equals(mDeviceMac) && isMultiScanConnect) {
                iHealthDevicesManager.getInstance().connectDevice(mac, deviceType);
            }
        }

        @Override
        public void onScanFinish() {
            if (isMultiScanConnect) {
                iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.AM6);
            }
        }
    };

    @Override
    protected void onDestroy() {
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

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_MESSAGE) {
                addLogInfo((String) msg.obj);

            }
            super.handleMessage(msg);
        }
    };

}
