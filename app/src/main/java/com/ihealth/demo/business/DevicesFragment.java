package com.ihealth.demo.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ec.easylibrary.dialog.IOSActionSheetDialog;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseFragment;
import com.ihealth.demo.business.device.BG1;
import com.ihealth.demo.business.device.BPM1;
import com.ihealth.demo.business.device.HS6;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * <li>DevicesFragment</li>
 * <li>This Fragment shows all the supported devices,and guide subsequent operations</li>
 * <p>
 * Created by wj on 2018/11/20
 */
public class DevicesFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    MainActivity mMainActivity;

    public DevicesFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DevicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DevicesFragment newInstance(String param1, String param2) {
        DevicesFragment fragment = new DevicesFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public int contentViewID() {
        return R.layout.fragment_devices;
    }

    @Override
    public void initView() {
        mContext = getActivity();
        mMainActivity = (MainActivity) mContext;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @OnClick({
            R.id.llBP5, R.id.llBP5S, R.id.ll550BT, R.id.llBP7S, R.id.llBP3L,R.id.llKD723, R.id.llKD926, R.id.llBPM1,
            R.id.llBG1, R.id.llBG1S, R.id.llBG5,R.id.llBG1A,R.id.llBG5A,
            R.id.llBG5S, R.id.llHS2, R.id.llHS2S, R.id.llHS4, R.id.llHS6, R.id.llHS2SPRO,
            R.id.llAM3, R.id.llAM3S, R.id.llAM4, R.id.llAM5, R.id.llAM6,
            R.id.llTS28B, R.id.llBTM, R.id.ll_nt13b, R.id.ll_pt3sbt,
            R.id.llPO3M, R.id.llPO1,
            R.id.llECG3})
    public void onViewClicked(View view) {
        String deviceName = "";
        switch (view.getId()) {
            case R.id.llBP5:
                deviceName = iHealthDevicesManager.TYPE_BP5;
                break;

            case R.id.llBP5S:
                deviceName = iHealthDevicesManager.TYPE_BP5S;
                break;

            case R.id.ll550BT:
                deviceName = "KN550BT";
                break;

            case R.id.llBP7S:
                deviceName = iHealthDevicesManager.TYPE_BP7S;
                break;

            case R.id.llBP3L:
                deviceName = iHealthDevicesManager.TYPE_BP3L;
                break;

            case R.id.llKD723:
                deviceName = iHealthDevicesManager.TYPE_KD723;
                break;

            case R.id.llKD926:
                deviceName = iHealthDevicesManager.TYPE_KD926;
                break;

            case R.id.llBPM1:
                Intent intent3 = new Intent();
                intent3.putExtra("mac", "");
                intent3.putExtra("type", "BPM1");
                intent3.setClass(mContext, BPM1.class);
                startActivity(intent3);
                break;

            case R.id.llBG1:
                Intent intent = new Intent();
                intent.putExtra("mac", "");
                intent.putExtra("type", "BG1");
                intent.putExtra("username", "test@com.cn");
                intent.setClass(mContext, BG1.class);
                startActivity(intent);
                return;

            case R.id.llBG1S:
                deviceName = iHealthDevicesManager.TYPE_BG1S;
                break;

            case R.id.llBG1A:
                deviceName = iHealthDevicesManager.TYPE_BG1A;
                break;

            case R.id.llBG5A:
                deviceName = iHealthDevicesManager.TYPE_BG5A;
                break;

            case R.id.llBG5:
                deviceName = iHealthDevicesManager.TYPE_BG5;
                break;

            case R.id.llBG5S:
                deviceName = iHealthDevicesManager.TYPE_BG5S;
                break;

            case R.id.llHS2:
                deviceName = iHealthDevicesManager.TYPE_HS2;
                break;

            case R.id.llHS3:
                deviceName = iHealthDevicesManager.TYPE_HS3;
                break;

            case R.id.llHS4:
                deviceName = iHealthDevicesManager.TYPE_HS4;
                break;

            case R.id.llHS2S:
                deviceName = iHealthDevicesManager.TYPE_HS2S;
                break;

            case R.id.llHS2SPRO:
                deviceName = iHealthDevicesManager.TYPE_HS2SPRO;
                break;

            case R.id.llHS6:
                Intent intent2 = new Intent();
                intent2.putExtra("mac", "");
                intent2.putExtra("type", "HS6");
                intent2.setClass(mContext, HS6.class);
                startActivity(intent2);
                return;

            case R.id.llAM3:
                deviceName = iHealthDevicesManager.TYPE_AM3;
                break;

            case R.id.llAM3S:
                deviceName = iHealthDevicesManager.TYPE_AM3S;
                break;

            case R.id.llAM4:
                deviceName = iHealthDevicesManager.TYPE_AM4;
                break;

            case R.id.llAM5:
                deviceName = iHealthDevicesManager.TYPE_AM5;
                break;

            case R.id.llAM6:
                deviceName = iHealthDevicesManager.TYPE_AM6;
                break;

            case R.id.llTS28B:
                deviceName = iHealthDevicesManager.TYPE_TS28B;
                break;

            case R.id.llBTM:
                deviceName = "FDIR-V3";
                break;

            case R.id.ll_nt13b:
                deviceName = iHealthDevicesManager.TYPE_NT13B;
                break;

            case R.id.llPO3M:
                deviceName = "PO3/PO3M";
                break;

            case R.id.llPO1:
                deviceName = "PO1";
                break;

            case R.id.ll_pt3sbt:
                deviceName = "PT3SBT";
                break;

            case R.id.llECG3:
                showECGDialog();
                return;
        }
        mMainActivity.showScanFragment(deviceName, null);
    }

    public void showECGDialog() {
        new IOSActionSheetDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.dialog_title_connect_mode))
                //.setSheetItemList(sheetItemList)//添加item集合
                .addSheetItem(mContext.getString(R.string.function_item_button_connect_mode_ble), "FF0098EF")
                .addSheetItem(mContext.getString(R.string.function_item_button_connect_mode_usb), "FF0098EF")
                .setOnItemClickListener(new IOSActionSheetDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(LinearLayout parent, View view, int position) {
                        //do something
                        if (position == 0) {
                            mMainActivity.showScanFragment(iHealthDevicesManager.TYPE_ECG3, null);
                        } else {
                            mMainActivity.showScanFragment(iHealthDevicesManager.TYPE_ECG3_USB, null);
                        }
                    }
                })
//                .setTitleHeight(57)//可选项
//                .setTitleSize(20)
                .build()
                .show();
    }
}
