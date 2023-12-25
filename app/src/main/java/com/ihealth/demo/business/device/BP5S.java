package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Bp5sControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;


public class BP5S extends FunctionFoldActivity {
    @BindView(R.id.btnOfflineMeasureEnable)
    Button mBtnOfflineMeasureEnable;
    @BindView(R.id.btnOfflineMeasureDisable)
    Button mBtnOfflineMeasureDisable;

    private Context mContext;
    private static final String TAG = "BP5S";
    private Bp5sControl mBp5sControl;
    private int mClientCallbackId;


    @Override
    public int contentViewID() {
        return R.layout.activity_bp5s;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForAddress(mClientCallbackId, mDeviceMac);
        /* Get bp5 controller */
        mBp5sControl = iHealthDevicesManager.getInstance().getBp5sControl(mDeviceMac);
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


            } else if (BpProfile.ACTION_DISENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "disable operation is success");
                addLogInfo("disable operation is success");

            } else if (BpProfile.ACTION_ENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "enable operation is success");
                addLogInfo("enable operation is success");
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
//                            String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
//                            String hsd = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n";
//                                    + "ahr:" + ahr + "\n"
//                                    + "hsd:" + hsd + "\n";

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

            } else if (BpProfile.ACTION_IS_ENABLE_OFFLINE.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    boolean isEnableoffline = info.getBoolean(BpProfile.IS_ENABLE_OFFLINE);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "isEnableoffline: " + isEnableoffline;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PRESSURE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure: " + pressure;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PULSEWAVE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure:" + pressure + "\n"
                            + "wave: " + wave + "\n"
                            + " - heartbeat:" + heartbeat;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_RESULT_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String ahr = info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "highPressure: " + highPressure
                            + "lowPressure: " + lowPressure
                            + "ahr: " + ahr
                            + "pulse: " + pulse;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ZOREING_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "zoreing";
                myHandler.sendMessage(msg);

            } else if (BpProfile.ACTION_ZOREOVER_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "zoreover";
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
        if(mBp5sControl!=null){
            mBp5sControl.disconnect();
        }
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        super.onDestroy();

    }

    @OnClick({R.id.btnDisconnect,
            R.id.btnMeasurement,
            R.id.btnStopMeasurement,
            R.id.btnBattery,
            R.id.btnFunction,
            R.id.btnOfflineMeasureEnable,
            R.id.btnOfflineMeasureDisable,
            R.id.btnDataNum,
            R.id.btnGetData,
            R.id.btnSetUnit})
    public void onViewClicked(View view) {
        if (mBp5sControl == null) {
            addLogInfo("mBp5Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBp5sControl.disconnect();
                addLogInfo("disconnect()");
                break;

            case R.id.btnMeasurement:
                mBp5sControl.startMeasure(0, 5, 0, 10);
                addLogInfo("startMeasure()");
                break;

            case R.id.btnStopMeasurement:
                mBp5sControl.interruptMeasure();
                addLogInfo("interruptMeasure()");
                break;

            case R.id.btnBattery:
                mBp5sControl.getBattery();
                addLogInfo("getBattery()");
                break;

            case R.id.btnFunction:
                mBp5sControl.getFunctionInfo();
                addLogInfo("isEnableOffline()");
                break;

            case R.id.btnOfflineMeasureEnable:
                mBp5sControl.setMode(BpProfile.STATUS_MODE_TO_C);
                addLogInfo("enbleOffline()");
                break;

            case R.id.btnOfflineMeasureDisable:
                mBp5sControl.setMode(BpProfile.STATUS_MODE_TO_B);
                addLogInfo("disableOffline()");
                break;

            case R.id.btnDataNum:
                mBp5sControl.getOfflineDataNum();
                addLogInfo("getOfflineNum()");
                break;

            case R.id.btnGetData:
                mBp5sControl.getOfflineData();
                addLogInfo("getOfflineData()");
                break;

            case R.id.btnSetUnit:
                mBp5sControl.setUnitDisplay(1);
                addLogInfo("setUnitDisplay()");
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
