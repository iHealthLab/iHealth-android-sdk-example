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
import com.ihealth.communication.control.Bg5sProfile;
import com.ihealth.communication.control.BgProfile;
import com.ihealth.communication.control.Bp7sControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;


public class BP7S extends FunctionFoldActivity {
    private Context mContext;
    private static final String TAG = "BP7S";
    private Bp7sControl mBp7sControl;
    private int mClientCallbackId;

    @Override
    public int contentViewID() {
        return R.layout.activity_bp7s;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BP7S);
        /* Get bp7s controller */
        mBp7sControl = iHealthDevicesManager.getInstance().getBp7sControl(mDeviceMac);
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
            Log.i(TAG, "message: " + message);

            if (BpProfile.ACTION_BATTERY_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(BpProfile.BATTERY_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (BpProfile.ACTION_ERROR_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.ERROR_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "error num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_DATA_BP.equals(action)) {
                String str = "{}";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave = obj.getString(BpProfile.PULSE_BP);
                            String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n"
                                    + "hsd:" + hsd + "\n";
                            Message msg = new Message();
                            msg.what = HANDLER_MESSAGE;
                            msg.obj = str;
                            myHandler.sendMessage(msg);
                        }
                    }else {
                        Message msg = new Message();
                        msg.what = HANDLER_MESSAGE;
                        msg.obj = str;
                        myHandler.sendMessage(msg);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_NUM_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (BpProfile.ACTION_SET_UNIT_SUCCESS_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "set unit success";
                myHandler.sendMessage(msg);
            } else if (BpProfile.ACTION_SET_ANGLE_SUCCESS_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "set angle success";
                myHandler.sendMessage(msg);
            } else if (BpProfile.ACTION_FUNCTION_INFORMATION_BP.equals(action)) {
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
        if(mBp7sControl!=null){
            mBp7sControl.disconnect();
        }
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        clearLogInfo();
        super.onDestroy();

    }

    @OnClick({R.id.btnDisconnect, R.id.btnIDPS, R.id.btnBattery, R.id.btnFunction,
            R.id.btnAngle, R.id.btnSetUnit, R.id.btnSetUnit2,
            R.id.btnDataNum, R.id.btnGetData})
    public void onViewClicked(View view) {
        if (mBp7sControl == null) {
            addLogInfo("mBp7sControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBp7sControl.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnIDPS:
                mBp7sControl.getIdps();
                addLogInfo("getIdps() -->" + mBp7sControl.getIdps());
                break;
            case R.id.btnBattery:
                mBp7sControl.getBattery();
                addLogInfo("getBattery()");
                break;
            case R.id.btnFunction:
                mBp7sControl.getFunctionInfo();
                addLogInfo("getFunctionInfo()");
                break;
            case R.id.btnAngle:
                mBp7sControl.angleSet((byte) 90, (byte) 60, (byte) 90, (byte) 60);
                addLogInfo("angleSet()");
                break;
            case R.id.btnSetUnit:
                mBp7sControl.setUnit(0);
                addLogInfo("setUnit()--> mmHg");
                break;
            case R.id.btnSetUnit2:
                mBp7sControl.setUnit(1);
                addLogInfo("setUnit()--> kPa");
                break;
            case R.id.btnDataNum:
                mBp7sControl.getOfflineNum();
                addLogInfo("getOfflineNum()");
                break;
            case R.id.btnGetData:
                mBp7sControl.getOfflineData();
                addLogInfo("getOfflineData()");
                break;
        }
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

}
