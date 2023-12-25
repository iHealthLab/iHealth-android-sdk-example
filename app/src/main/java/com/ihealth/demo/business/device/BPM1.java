package com.ihealth.demo.business.device;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.dialog.loadingdialog.LoadingDialog;
import com.ihealth.communication.base.wifi.iHealthDeviceBPM1Callback;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.control.Bpm1Control;
import com.ihealth.communication.control.HS6Control;
import com.ihealth.communication.manager.iHealthDeviceHs6Callback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;


public class BPM1 extends FunctionFoldActivity {

    private final String TAG = BPM1.class.getSimpleName();

    /**
     * <p>1:Is connected to the BPM1.</p>
     * <p>2:Connection BPM1 failed.</p>
     * <p>3:Connection BPM1 timeout.</p>
     * <p>4:Not found BPM1.</p>
     * <p>5:Connection has been disconnected.</p>
     * <p>6:Don't switch the channel.</p>
     * <p>7:Switch the channel (at this point, the AP with the mobile phone may lose connection).</p>
     * <p>8:Can't find the router.</p>
     * <p>9:Password error.</p>
     * <p>10:DHCP error.</p>
     * <p>11:Set up the success.</p>
     * <p>12:Connection failed.</p>
     * <p>13:WiFi is connected to the specified.</p>
     * <p>14:List is scanning Wifi.</p>
     * <p>15:Have been scan to BPM1.</p>
     * <p>16:Have been scan to WIFI.</p>
     * <p>17:Is Connecting.</p>
     */

    public static final int BPM1_CONNECTED = 1;
    public static final int BPM1_CONNECT_FAILED = 2;
    public static final int BPM1_CONNECT_TIMEOUT = 3;
    public static final int BPM1_NOT_FOUND = 4;
    public static final int BPM1_DISCONNECTED = 5;
    public static final int BPM1_DONOT_SWITCH_CHANNEL = 6;
    public static final int BPM1_SWITCH_CHANNEL = 7;
    public static final int BPM1_CANNOT_FOUND_ROUTER = 8;
    public static final int BPM1_PASSWORD_ERROR = 9;
    public static final int BPM1_DHCP_ERROR = 10;
    public static final int BPM1_SETUP_SUCCESS = 11;
    public static final int BPM1_CONNECTION_FAILED = 12;
    public static final int BPM1_CONNECT_WIFI_SPECIFIED = 13;//回连到WiFi指定WiFi
    public static final int BPM1_SCANING_WIFI_SPECIFIED = 14;//正在扫描需要回连的WiFi
    public static final int BPM1_WIFI_SCANNED = 15;
    public static final int BPM1_SCANED_WIFI_SPECIFIED = 16;
    public static final int BPM1_IS_CONNECTING = 17;

    @BindView(R.id.mEtSSID)
    EditText mEtSSID;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.btnSetWifi)
    Button mBtnSetWifi;

    private Context mContext;
    private Bpm1Control mBpm1Control;
    private LoadingDialog mLoadingDialog;

    @Override
    public int contentViewID() {
        return R.layout.activity_bpm1;
    }

    @Override
    public void initView() {
        mContext = this;
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setCancellable(true);
        Intent intent = getIntent();
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");

        WifiManager manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String ssid = info.getSSID();
        mEtSSID.setText(ssid);
    }

    iHealthDeviceBPM1Callback mIHealthDeviceBpm1Callback = new iHealthDeviceBPM1Callback() {
        @Override
        public void onNewDataNotify(String type, String action, String message) {
            Log.i(TAG, "message: " + message);
            if (action.equals(BpProfile.ACTION_STATE_BPM1)) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(message);
                    int status = jsonObject.getInt(BpProfile.STATE_NUMBER_BPM1);
                    Message handMessage = new Message();
                    handMessage.what = HANDLER_MESSAGE;
                    handMessage.obj = "Set Wifi Result:" + status;
                    mHandler.sendMessage(handMessage);

                    if (status == 1) {
                        new Handler().postDelayed(() -> mBpm1Control.getIDPS(), 3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (action.equals(BpProfile.ACTION_ROUTERS_BPM1)) {
                Log.i(TAG, "message: " + message);
            }
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mLoadingDialog.show();
                    break;
                case 2:
                    mLoadingDialog.dismiss();
                    break;
                case HANDLER_MESSAGE:
                    addLogInfo((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @OnClick({ R.id.btnSetWifi, })
    public void onViewClicked(View view) {
        showLogLayout();
        switch (view.getId()) {
            case R.id.btnSetWifi:
                mLoadingDialog.show();
                mBpm1Control = new Bpm1Control(this, iHealthDevicesManager.TYPE_BPM1, mIHealthDeviceBpm1Callback);
                mBpm1Control.connectDevice("iHealth-BPM1", 20000);
//                String ssid = mEtSSID.getText().toString().trim();
//                String password = etPassword.getText().toString().trim();
//                RouterBean routerBean = new RouterBean();
//                routerBean.setSSID(ssid);
//                routerBean.setPsd(password);
//
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put(BpProfile.ROUTER_SSID_BPM1,routerBean.getSSID());
//                    jsonObject.put(BpProfile.ROUTER_CHANNEL_BPM1,routerBean.getChannel());
//                    jsonObject.put(BpProfile.ROUTER_SECURITY_BPM1,routerBean.getSecurity());
//                    jsonObject.put(BpProfile.ROUTER_RSSI_BPM1,routerBean.getRSSI());
//                    jsonObject.put(BpProfile.ROUTER_URL_BPM1,routerBean.getUrl());
//                    jsonObject.put(BpProfile.ROUTER_PID_BPM1,routerBean.getPid());
//                    jsonObject.put(BpProfile.ROUTER_PWD_BPM1,routerBean.getPsd());
//                    mBpm1Control.sendRouter(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                String ssid = mEtSsid.getText().toString().trim();
//                String password = mEtPassword.getText().toString().trim();
//                String deviceKey = mEtDeviceKey.getText().toString().trim();


//                if (deviceKey.isEmpty()) {
//                    mBpm1Control.sendRouter();
//                }
//                addLogInfo("HS6 setWifi --> ssid:" + ssid + " password:" + password + " deviceKey:" + deviceKey);
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
