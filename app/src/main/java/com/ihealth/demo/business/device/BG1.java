package com.ihealth.demo.business.device;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.ihealth.communication.control.Bg1Control;
import com.ihealth.communication.control.Bg1Profile;
import com.ihealth.demo.R;
import com.ihealth.demo.business.FunctionFoldActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BG1 extends FunctionFoldActivity {
    private Context mContext;

    @BindView(R.id.tvStartBG1)
    TextView mTvStartBG1;

    @BindView(R.id.spMeasureType)
    Spinner mSpMeasureType;

    private static final String TAG = "BG1";
    public Bg1Control mBg1Control;

    private boolean isGetStripInBg1 = false;
    private boolean isGetResultBg1 = false;
    private boolean isGetBloodBg1 = false;
    @Override
    public int contentViewID() {
        return R.layout.activity_bg1;
    }

    @Override
    public void initView() {
        mContext = this;
        registerBroadcast();
        Intent intent = getIntent();
        String userName = intent.getExtras().getString("userName");
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");
        mBg1Control = Bg1Control.getInstance();
        mBg1Control.init(BG1.this, userName, 0x00FF1304, true);
    }
    public String QRCode = "02554064554014322D1200A05542D3BACE1446CE9A961901222F00A70B46";
//    public String QRCode = "02ABCDE67C284BA29ACDFEE6E60A2FE43EDF0C";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS--");

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        filter.addAction(Bg1Profile.ACTION_BG1_DEVICE_READY);
        filter.addAction(Bg1Profile.ACTION_BG1_IDPS);
        filter.addAction(Bg1Profile.ACTION_BG1_CONNECT_RESULT);
        filter.addAction(Bg1Profile.ACTION_BG1_SENDCODE_RESULT);

        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_ERROR);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STRIP_IN);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STRIP_OUT);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_GET_BLOOD);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_RESULT);
        filter.addAction(Bg1Profile.ACTION_BG1_MEASURE_STANDBY);
        this.registerReceiver(mBroadcastReceiver, filter);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {

                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        mBg1Control.disconnect();
                        addLogInfo("headset out");
                        mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_1));
                    }
                    if (intent.getIntExtra("state", 0) == 1) {
                        showLogLayout();
                        addLogInfo(  "headset in");
                        String QRInfo = mBg1Control.getBottleInfoFromQR(QRCode);
                        addLogInfo("QRInfo =" + QRInfo);
                        mBg1Control.connect();
                    }
                }
            }

            //1305
            else if (action.equals(Bg1Profile.ACTION_BG1_DEVICE_READY)) {
                addLogInfo( "device handshake");
            } else if (action.equals(Bg1Profile.ACTION_BG1_IDPS)) {
                String idps = intent.getStringExtra(Bg1Profile.BG1_IDPS);
                addLogInfo( "idps ="+idps);
            } else if (action.equals(Bg1Profile.ACTION_BG1_CONNECT_RESULT)) {
                int flag = intent.getIntExtra(Bg1Profile.BG1_CONNECT_RESULT, -1);
                addLogInfo( "conect flag ="+flag);
                if (flag == 0) {
                    addLogInfo( "connect success, please send code");
                    mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_2));
                    if ((mSpMeasureType.getSelectedItem()).equals("blood")) {
                        mBg1Control.sendCode(QRCode, Bg1Profile.CODE_GOD, Bg1Profile.MEASURE_BLOOD);
                    } else {
                        mBg1Control.sendCode(QRCode, Bg1Profile.CODE_GOD, Bg1Profile.MEASURE_CTL);
                    }

                } else {
                    addLogInfo( "connect failed");
                    mBg1Control.disconnect();
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_SENDCODE_RESULT)) {
                int flag = intent.getIntExtra(Bg1Profile.BG1_SENDCODE_RESULT, -1);
                addLogInfo( "sendCode flag = "+flag);
                if (flag == 0) {
                    addLogInfo( "sendCode success,ready to  measure");
                } else {
                    addLogInfo( "sendCode failed");
                    mBg1Control.disconnect();
                }
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_ERROR)) {
                int errorNum = intent.getIntExtra(Bg1Profile.BG1_MEASURE_ERROR, -1);
                String error = intent.getStringExtra(Bg1Profile.BG1_MEASURE_ERROR_DESCRIPTION);
                addLogInfo( "msgError = "+errorNum);
                addLogInfo( "error information = "+error);
                //resend code to fix error 4
                if (errorNum == 4) {
                    mBg1Control.sendCode(QRCode, Bg1Profile.CODE_GOD, Bg1Profile.MEASURE_BLOOD);
                }
                mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_5));
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STRIP_IN)) {
                if (!isGetStripInBg1) {
                    isGetStripInBg1 = true;
                    addLogInfo( "Strip In");
                    mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_3));
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetStripInBg1 = false;
                    }
                }.start();
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_GET_BLOOD)) {
                if (!isGetBloodBg1) {
                    isGetBloodBg1 = true;
                    addLogInfo( "Get Blood");
                    mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_4));
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetBloodBg1 = false;
                    }
                }.start();
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_RESULT)) {
                if (!isGetResultBg1) {
                    isGetResultBg1 = true;
                    int measureResult = intent.getIntExtra(Bg1Profile.BG1_MEASURE_RESULT, -1);
                    String dataId = intent.getStringExtra(Bg1Profile.DATA_ID);
                    addLogInfo( "dataId = "+dataId);
                    addLogInfo( "msgResult = "+measureResult);
                }
                mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_5));
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetResultBg1 = false;
                    }
                }.start();

            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STRIP_OUT)) {
                addLogInfo( "Strip Out");
                mTvStartBG1.setText(mContext.getString(R.string.confirm_tip_bg1_2));
            } else if (action.equals(Bg1Profile.ACTION_BG1_MEASURE_STANDBY)) {
                mBg1Control.disconnect();

                if (!isGetResultBg1) {
                    isGetResultBg1 = true;
                    addLogInfo( "Stand By");
                }
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(3000);
                        isGetResultBg1 = false;
                    }
                }.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(mBg1Control!=null){
            mBg1Control.disconnect();
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
