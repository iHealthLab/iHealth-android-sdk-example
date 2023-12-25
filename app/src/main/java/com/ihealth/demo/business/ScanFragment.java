package com.ihealth.demo.business;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ec.easylibrary.dialog.loadingdialog.LoadingDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.control.BtmControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.adapter.ListScanDeviceAdapter;
import com.ihealth.demo.base.BaseFragment;
import com.ihealth.demo.business.device.model.DeviceCharacteristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ihealth.demo.business.MainActivity.HANDLER_CONNECTED;
import static com.ihealth.demo.business.MainActivity.HANDLER_CONNECT_FAIL;
import static com.ihealth.demo.business.MainActivity.HANDLER_DISCONNECT;
import static com.ihealth.demo.business.MainActivity.HANDLER_RECONNECT;
import static com.ihealth.demo.business.MainActivity.HANDLER_SCAN;
import static com.ihealth.demo.business.MainActivity.HANDLER_USER_STATUE;

/**
 * 搜索主页面
 * 这个页面主要完成的是 设备的搜索和连接功能
 * <li>ScanFragment </li>
 * <li>The main function of this page is to search and connect devices. </li>
 */
public class ScanFragment extends BaseFragment {

    private static final String TAG = ScanFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.tvListTitle)
    TextView mTvListTitle;
    @BindView(R.id.NbList)
    ListView mNbListView;
    @BindView(R.id.tvLogMessage)
    TextView mTvLogMessage;
    @BindView(R.id.ScrollViewLog)
    ScrollView mScrollViewLog;
    @BindView(R.id.logLayout)
    LinearLayout mLogLayout;

    private String mDeviceName;
    private String mParam2;

    private Context mContext;
    MainActivity mMainActivity;
    private int callbackId;
    private List<DeviceCharacteristic> list_ScanDevices = new ArrayList<>();
    private ListScanDeviceAdapter mAdapter;
    private LoadingDialog mLoadingDialog;

    /**
     * public ScanFragment() {
     * <p>
     * }
     * <p>
     * /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevicesFragment.
     */

    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceName = getArguments().getString(ARG_PARAM1);
            Log.i(TAG, "mDeviceName: " + mDeviceName);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public int contentViewID() {
        return R.layout.fragment_scan;
    }

    @Override
    public void initView() {
        mContext = getActivity();
        mMainActivity = (MainActivity) mContext;
        if (mDeviceName.isEmpty() || mDeviceName == null) {
            return;
        }
        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setCancellable(true);
        /*
         * Register callback to the manager. This method will return a callback Id.
         */

        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        mAdapter = new ListScanDeviceAdapter(mContext, list_ScanDevices);
        mNbListView.setAdapter(mAdapter);
        mNbListView.setOnItemClickListener((parent, view, position, id) -> {
            mLoadingDialog.show();
            DeviceCharacteristic deviceCharacteristic = list_ScanDevices.get(position);
            ConnectDevice(deviceCharacteristic.getDeviceName(), deviceCharacteristic.getDeviceMac(), "");
        });
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufacturerData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceType:" + deviceType + " - rssi:" + rssi + " - manufacturerData:" + manufacturerData);
            if ((manufacturerData != null) && (manufacturerData.get("isBound") != null)) {
                boolean isBound = (boolean) manufacturerData.get("isBound");
                Log.i(TAG,"isBound AM6 - " + isBound);
            }
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            bundle.putInt("rssi", rssi);

            Message msg = new Message();
            msg.what = HANDLER_SCAN;
            msg.setData(bundle);
            myHandler.sendMessage(msg);

            //设备附加信息    无线mac后缀表
            //Device additional information wireless MAC suffix table
            if (manufacturerData != null) {
                Log.d(TAG, "onScanDevice mac suffix = " + manufacturerData.get(HsProfile.SCALE_WIFI_MAC_SUFFIX));
            }
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufacturerData) {
            Log.e(TAG, "mac:" + mac + " deviceType:" + deviceType + " status:" + status + " errorId:" + errorID + " - manufacturerData:" + manufacturerData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                msg.what = HANDLER_CONNECTED;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                msg.what = HANDLER_DISCONNECT;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL) {
                msg.what = HANDLER_CONNECT_FAIL;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_RECONNECTING) {
                msg.what = HANDLER_RECONNECT;
            }
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        /**
         * Callback indicating an error happened during discovery.
         *
         * @param reason A string for the reason why discovery failed.
         */
        @Override
        public void onScanError(String reason, long latency) {
            Log.e(TAG, reason);
            Log.e(TAG, "please wait for " + latency + " ms");
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        }

        @Override
        public void onScanFinish() {
            super.onScanFinish();
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        }

        @Override
        public void onSDKStatus(int statusId, String statusMessage) {
            Log.e(TAG, "statusId: " + statusId);
            Log.e(TAG, "statusMessage: " + statusMessage);
        }
    };

    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SCAN:
                    Bundle bundleScan = msg.getData();
                    String macScan = bundleScan.getString("mac");
                    String typeScan = bundleScan.getString("type");
                    int rssi = bundleScan.getInt("rssi");
                    DeviceCharacteristic device = new DeviceCharacteristic();
                    device.setDeviceName(typeScan);
                    device.setRssi(rssi);
                    device.setDeviceMac(macScan);
                    //ECG devices should deal with multiple USB connection duplicate display
                    if (mDeviceName.equals("ECGUSB") || mDeviceName.equals("ECG3USB")) {
                        for (int x = 0; x < list_ScanDevices.size(); x++) {
                            if (list_ScanDevices.get(x).getDeviceMac().equals(device.getDeviceMac())) {
                                list_ScanDevices.remove(x);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    list_ScanDevices.add(device);
                    mAdapter.setList(list_ScanDevices);
                    mAdapter.notifyDataSetChanged();
                    mLoadingDialog.dismiss();

                    showLog("scan device : type:" + typeScan + " mac:" + macScan + " rssi:" + rssi);
                    break;

                case HANDLER_CONNECTED:
                    Bundle bundleConnect = msg.getData();
                    String macConnect = bundleConnect.getString("mac");
                    String typeConnect = bundleConnect.getString("type");
                    iHealthDevicesManager.getInstance().stopDiscovery();
//                    iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
                    mMainActivity.showFunctionActivity(macConnect, typeConnect);
                    mLoadingDialog.dismiss();
                    ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_connected));
                    showLog("connected device : type:" + typeConnect + " mac:" + macConnect);
                    break;
                case HANDLER_DISCONNECT:
                    mLoadingDialog.dismiss();
                    ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_disconnect));
                    showLog(mContext.getString(R.string.connect_main_tip_disconnect));
                    break;
                case HANDLER_CONNECT_FAIL:
                    mLoadingDialog.dismiss();
                    ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_connect_fail));
                    break;
                case HANDLER_RECONNECT:
                    mLoadingDialog.show();
//                    ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_reconnect));
                    break;
                case HANDLER_USER_STATUE:
                    break;

                default:
                    break;
            }
        }
    };

    private void ConnectDevice(String type, String mac, String userName) {
        boolean req;
        Log.v(TAG, "ConnectDevice: " + type + " -mac: " + mac);
        if (type.equals(iHealthDevicesManager.TYPE_FDIR_V3)) {
            req = iHealthDevicesManager.getInstance().connectTherm(userName, mac, type,
                    BtmControl.TEMPERATURE_UNIT_C, BtmControl.MEASURING_TARGET_BODY,
                    BtmControl.FUNCTION_TARGET_OFFLINE, 0, 1, 0);
        } else {
            req = iHealthDevicesManager.getInstance().connectDevice(mac, type);
        }

        if (!req) {
            ToastUtils.showToast(mContext, mContext.getString(R.string.authorization_fail));
            showLog(mContext.getString(R.string.authorization_fail));
        }
    }

    private DiscoveryTypeEnum getDiscoveryTypeEnum(String deviceName) {
        for (DiscoveryTypeEnum type : DiscoveryTypeEnum.values()) {
            if (deviceName.equals(type.name())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
//        if (hidden) {
//            iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
    }

    @OnClick(R.id.btnDiscovery)
    public void onViewClicked() {
        mLoadingDialog.show();
        mTvListTitle.setVisibility(View.VISIBLE);
        list_ScanDevices.clear();
        mAdapter.notifyDataSetChanged();
        if (mDeviceName.equals("KN550BT")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("BP550BT"));

        } else if (mDeviceName.equals("FDIR-V3")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("FDIR_V3"));

        } else if (mDeviceName.equals("ECGUSB")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("ECG3USB"));

        } else if (mDeviceName.contains("PO3")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("PO3"));

        } else if (mDeviceName.contains("KD-723")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("KD723"));

        } else if (mDeviceName.contains("KD-926")) {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum("KD926"));

        } else if (mDeviceName.contains("HS2S Pro")) {
            iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.HS2SPRO);

        } else if (mDeviceName.contains("BG1A")) {
            iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.BG1A);

        } else if (mDeviceName.contains("AM6")) {
            iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.AM6);

        }  else if (mDeviceName.contains("BG5A")) {
            iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.BG5A);

        } else {
            iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum(mDeviceName));
//            iHealthDevicesManager.getInstance().startDiscovery(DiscoveryTypeEnum.All);
        }
    }

    public void showLog(String log) {
        mMainActivity.addLogInfo(log);
        mTvLogMessage.setText(mMainActivity.getLogInformation());
        mScrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
