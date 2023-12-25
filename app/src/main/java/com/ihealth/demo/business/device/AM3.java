package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Am3Control;
import com.ihealth.communication.control.AmProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;


public class AM3 extends FunctionFoldActivity {

    @BindView(R.id.etResetId)
    EditText mEtResetId;

    private Context mContext;
    private static final String TAG = AM3.class.getSimpleName();
    private Am3Control mAm3Control;
    private int mClientCallbackId;

    @Override
    public int contentViewID() {
        return R.layout.activity_am3;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_AM3);
        /* Get am3s controller */
        mAm3Control = iHealthDevicesManager.getInstance().getAm3Control(mDeviceMac);
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
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            if (AmProfile.ACTION_USERID_AM.equals(action)) {
                try {
                    JSONObject obj = new JSONObject(message);
                    String userid = obj.getString(AmProfile.USERID_AM);
                    mEtResetId.setText(userid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Message msg = new Message();
            msg.what = HANDLER_MESSAGE;
            msg.obj = "notify() action =  " + action + ", message = " + message;
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
        if(mAm3Control !=null){
            mAm3Control.disconnect();
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

    @OnClick({ R.id.btnDisconnect,
            R.id.btnReset,
            R.id.btnGetUserId })
    public void onViewClicked(View view) {
        if (mAm3Control == null) {
            addLogInfo("mAm3Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mAm3Control.disconnect();
                addLogInfo("disconnect()");
                break;

            case R.id.btnReset:
                String userId = mEtResetId.getText().toString().trim();
                if (TextUtils.isEmpty(userId)) {
                    long resetId = Long.parseLong(userId);
                    mAm3Control.reset(resetId);
                    addLogInfo("reset() --> reset id:" + resetId);
                } else {
                    Toast.makeText(this, "Please get user id, before reset the AM3 device", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btnGetUserId:
                mAm3Control.getUserId();
                addLogInfo("getUserId()");
                break;
        }
    }

}
