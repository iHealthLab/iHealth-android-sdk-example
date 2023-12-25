package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.BtmProfile;
import com.ihealth.communication.control.TS28BControl;
import com.ihealth.communication.control.TS28BProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ihealth.communication.control.BtmControl.FUNCTION_TARGET_OFFLINE;
import static com.ihealth.communication.control.BtmControl.MEASURING_TARGET_BODY;
import static com.ihealth.communication.control.BtmControl.MEASURING_TARGET_OBJECT;
import static com.ihealth.communication.control.BtmControl.TEMPERATURE_UNIT_C;
import static com.ihealth.communication.control.BtmControl.TEMPERATURE_UNIT_F;


public class TS28B extends FunctionFoldActivity {
    private Context mContext;
    private static final String TAG = "TS28B";
    private TS28BControl mTS28BControl;
    private int mClientCallbackId;
    private SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

    @Override
    public int contentViewID() {
        return R.layout.activity_ts28b;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_TS28B);
        /* Get ts28b controller */
        mTS28BControl = iHealthDevicesManager.getInstance().getTS28BControl(mDeviceMac);

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
            super.onDeviceNotify(mac, deviceType, action, message);
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            Log.i(TAG, "mac:" + mac + " action:" + action + " message" + message);

            if (TS28BProfile.ACTION_BATTERY.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = message;
                myHandler.sendMessage(msg);
            } else if (TS28BProfile.ACTION_MEASUREMENT_RESULT.equals(action)) {
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
        if(mTS28BControl!=null){
            mTS28BControl.disconnect();
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


    @OnClick({R.id.btnDisconnect, R.id.btnGetData})
    public void onViewClicked(View view) {
        if (mTS28BControl == null) {
            addLogInfo("mTS28BControl == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mTS28BControl.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnGetData:
                mTS28BControl.getMeasurement();
                addLogInfo("getMeasurement()");
                break;
        }
    }

}
