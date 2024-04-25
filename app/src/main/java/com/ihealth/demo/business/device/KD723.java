package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Bp723Control;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.utils.Log;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;


public class KD723 extends FunctionFoldActivity {
    private Context mContext;
    private static final String TAG = KD723.class.getSimpleName();
    private Bp723Control mBp723Control;
    private int mClientCallbackId;

    @Override
    public int contentViewID() {
        return R.layout.activity_kd723;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_KD723);
        /* Get bp550bt controller */
        mBp723Control = iHealthDevicesManager.getInstance().getBp723Control(mDeviceMac);
//        setDeviceInfo(mDeviceName, mDeviceMac);
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
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
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.json(TAG, "message: " + message);

            if (BpProfile.ACTION_BATTERY_CBP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    int battery = info.getInt(BpProfile.BATTERY_CBP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (BpProfile.ACTION_HISTORY_DATA_CBP.equals(action)) {
                String str = "{}";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORY_DATA_CBP)) {
//                        JSONObject obj = info.getJSONObject(BpProfile.HISTORY_DATA_CBP);
//                        double sys = obj.getDouble(BpProfile.CBPINFO_SYS);
//                        double dia = obj.getDouble(BpProfile.CBPINFO_DIA);
//                        str = "highPressure:" + sys + "\n"
//                                + "lowPressure:" + dia + "\n";
//                        Message msg = new Message();
//                        msg.what = HANDLER_MESSAGE;
//                        msg.obj = str;
//                        myHandler.sendMessage(msg);
                        JSONArray array = info.getJSONArray(BpProfile.HISTORY_DATA_CBP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave = obj.getString(BpProfile.PULSE_BP);
                            String ahr = obj.getString(BpProfile.IRREGULAR);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n";
                            Message msg = new Message();
                            msg.what = HANDLER_MESSAGE;
                            msg.obj = str;
                            myHandler.sendMessage(msg);
                        }

                    } else {
                        Message msg = new Message();
                        msg.what = HANDLER_MESSAGE;
                        msg.obj = str;
                        myHandler.sendMessage(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "message: " + message;
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
//        if (mBp723Control != null) {
//            mBp723Control.disconnect();
//        }
        iHealthDevicesManager.getInstance().disconnectDevice(mDeviceMac, iHealthDevicesManager.TYPE_KD723);
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        clearLogInfo();
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

    @OnClick({R.id.btnDisconnect, R.id.btn_getFunctionInfo, R.id.btnGetData, R.id.btnSetTime, R.id.btnGetDataCount, R.id.btnDeleteData})
    public void onViewClicked(View view) {
        if (mBp723Control == null) {
            addLogInfo("mBp926Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                iHealthDevicesManager.getInstance().disconnectDevice(mDeviceMac, iHealthDevicesManager.TYPE_KD723);
                addLogInfo("disconnect()");
                break;

            case R.id.btn_getFunctionInfo:
                mBp723Control.getFunctionInfo();
                addLogInfo("getFunctionInformation()");
                break;

            case R.id.btnSetTime:
                mBp723Control.setTime(DateFormat.is24HourFormat(mContext));
                addLogInfo("setTime()");
                break;

            case R.id.btnGetDataCount:
                mBp723Control.getMemoryCount();
                addLogInfo("getMemoryCountWithUserID()");
                break;

            case R.id.btnGetData:
//                mBp723Control.commandSetUser(1);
//                mBp723Control.getData();

                mBp723Control.getMemoryData();
                addLogInfo("getData()");
                break;

            case R.id.btnDeleteData:
                mBp723Control.deleteMemoryData();
                addLogInfo("deleteHistoryData()");
                break;
        }
    }
}
