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
import android.widget.Spinner;

import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.Bg5Control;
import com.ihealth.communication.control.Bg5Profile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class BG5 extends FunctionFoldActivity {
    @BindView(R.id.etQRCode)
    EditText mEdQRCode;
    @BindView(R.id.etStripType)
    EditText mEdStripType;
    @BindView(R.id.spMeasureType)
    Spinner mSpMeasureType;
    @BindView(R.id.etStripNum)
    EditText mEdStripNum;
    @BindView(R.id.etOverDate)
    EditText mEdOverDate;
    @BindView(R.id.btnMeasurement)
    Button mBtnMeasurement;
    @BindView(R.id.btnMeasurement2)
    Button mBtnMeasurement2;
    private Context mContext;
    private static final String TAG = "BG5";
    private Bg5Control mBg5Control;
    private int mClientCallbackId;

    @Override
    public int contentViewID() {
        return R.layout.activity_bg5;
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
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BG5);
        /* Get bg5 controller */
        mBg5Control = iHealthDevicesManager.getInstance().getBg5Control(mDeviceMac);

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

            switch (action) {
                case Bg5Profile.ACTION_BATTERY_BG:
                case Bg5Profile.ACTION_SET_TIME:
                case Bg5Profile.ACTION_SET_UNIT:
                case Bg5Profile.ACTION_ERROR_BG:
                case Bg5Profile.ACTION_GET_CODEINFO:
                case Bg5Profile.ACTION_HISTORICAL_DATA_BG:
                case Bg5Profile.ACTION_DELETE_HISTORICAL_DATA:
                case Bg5Profile.ACTION_START_MEASURE:
                case Bg5Profile.ACTION_ONLINE_RESULT_BG:
                case Bg5Profile.ACTION_KEEP_LINK:
                    msg.obj = message;
                    break;
                case Bg5Profile.ACTION_STRIP_IN:
                    msg.obj = "strip in";
                    break;
                case Bg5Profile.ACTION_GET_BLOOD:
                    msg.obj = "get blood";
                    break;
                case Bg5Profile.ACTION_STRIP_OUT:
                    msg.obj = "strip out";
                    break;
                case Bg5Profile.ACTION_GET_BOTTLEID:
                    msg.obj = message;
                    break;
                case Bg5Profile.ACTION_SET_BOTTLE_ID_SUCCESS:
                    msg.obj = "set bottleId success";
                    break;
                case Bg5Profile.ACTION_SET_BOTTLE_MESSAGE_SUCCESS:
                    msg.obj = message;
                    mBtnMeasurement.setEnabled(true);
                    mBtnMeasurement2.setEnabled(true);
                    break;
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
        if(mBg5Control!=null){
            mBg5Control.disconnect();
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

    @OnClick({R.id.btnDisconnect, R.id.btnHoldLink, R.id.btnMeasurement, R.id.btnMeasurement2, R.id.btnBattery,
            R.id.btnSetUnit, R.id.btnSetUnit2, R.id.btnSetTime, R.id.btnSetBottleInfo,
            R.id.btnGetBottleInfo, R.id.btnSetBottleId, R.id.btnGetBottleId,
            R.id.btnGetIdByQR, R.id.btnGetData, R.id.btnDeleteData})
    public void onViewClicked(View view) {
        if (mBg5Control == null) {
            addLogInfo("mBg5Control == null");
            return;
        }
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBg5Control.disconnect();
                addLogInfo("disconnect()");
                break;
            case R.id.btnHoldLink:
                mBg5Control.holdLink();
                addLogInfo("holdLink()");
                break;
            case R.id.btnBattery:
                mBg5Control.getBattery();
                addLogInfo("getBattery()");
                break;
            case R.id.btnSetUnit:
                mBg5Control.setUnit(1);
                addLogInfo("setUnit()--> mmol/L");
                break;
            case R.id.btnSetUnit2:
                mBg5Control.setUnit(2);
                addLogInfo("setUnit()--> mg/dL");
                break;
            case R.id.btnSetTime:
                mBg5Control.setTime();
                addLogInfo("setTime()");
                break;
            case R.id.btnGetIdByQR:
                String QR = mEdQRCode.getText().toString().trim();
                String QRInfo = mBg5Control.getBottleInfoFromQR(QR);
                addLogInfo("getBottleInfoFromQR()-->QR:" + QR + " \nQRInfo:" + QRInfo);
                break;
            case R.id.btnSetBottleInfo:
                String stripType = mEdStripType.getText().toString();
                String measureType = (String)mSpMeasureType.getSelectedItem();
                String stripNum = mEdStripNum.getText().toString();
                String QRCode = mEdQRCode.getText().toString();
                String overDate = mEdOverDate.getText().toString();

                try {
                    //GOD
                    if ("blood".equals(measureType)) {
                        mBg5Control.setBottleMessageWithInfo(Integer.parseInt(stripType), Bg5Profile.MEASURE_BLOOD,
                                QRCode, Integer.parseInt(stripNum), overDate);
                    } else {
                        mBg5Control.setBottleMessageWithInfo(Integer.parseInt(stripType),  Bg5Profile.MEASURE_CTL,
                                QRCode, Integer.parseInt(stripNum), overDate);
                    }
                } catch (NumberFormatException e) {
                    addLogInfo("NumberFormatException:  Please input correct parameters.") ;
                    e.printStackTrace();
                }

                //GDH
//                mBg5Control.setBottleMessageWithInfo(Bg5Profile.STRIP_GDH, Bg5Profile.MEASURE_BLOOD, "", 20, "2020-1-1");
//                addLogInfo("setBottleMessageWithInfo() --> GDH");

                addLogInfo("setBottleMessageWithInfo() --> stripType:" + stripType +
                        " measureType:" + measureType + " stripNum:" + stripNum + " QRCode:" + QRCode + " overDate:" + overDate);
                break;
            case R.id.btnGetBottleInfo:
                mBg5Control.getBottleMessage();
                addLogInfo("getBottleMessage()");
                break;
            case R.id.btnGetBottleId:
                mBg5Control.getBottleId();
                addLogInfo("getBottleId()");
                break;
            case R.id.btnSetBottleId:
                mBg5Control.setBottleId(123456);
                addLogInfo("setBottleId() -->bottleId :" + 123456);
                break;
            case R.id.btnMeasurement:
                mBg5Control.startMeasure(1);
                addLogInfo("startMeasure() --> test with blood");
                break;
            case R.id.btnMeasurement2:
                mBg5Control.startMeasure(2);
                addLogInfo("startMeasure() --> test with control liquid ");
                break;

            case R.id.btnGetData:
                mBg5Control.getOfflineData();
                addLogInfo("getOfflineData()");
                break;
            case R.id.btnDeleteData:
                mBg5Control.deleteOfflineData();
                addLogInfo("deleteOfflineData()");
                break;
        }
    }


}
