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
import com.ihealth.communication.control.Hs3Control;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.BindView;
import butterknife.OnClick;


public class HS3 extends FunctionFoldActivity {

    private Context mContext;
    private static final String TAG = HS3.class.getSimpleName();

    private Hs3Control mHs3Control;
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
        return R.layout.activity_hs3;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_HS3);
        /* Get hs3 controller */
        mHs3Control = iHealthDevicesManager.getInstance().getHs3Control(mDeviceMac);

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
                        String description = jsonObject.getString(HsProfile.ERROR_DESCRIPTION_HS);
                        Log.d(TAG, "weight:" + err);
                        Message error = new Message();
                        error.what = 1;
                        error.obj = "err:" + err;
                        mHandler.sendMessage(error);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

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
        if(mHs3Control != null){
            mHs3Control.disconnect();
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


    @OnClick({R.id.btn_disconnect,
            R.id.btn_getData })
    public void onViewClicked(View view) {
        if (mHs3Control == null) {
            addLogInfo("mHs4Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btn_disconnect:
                mHs3Control.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btn_getData:
                mHs3Control.getOfflineData();
                addLogInfo("getOfflineData()");
                break;
        }
    }
}
