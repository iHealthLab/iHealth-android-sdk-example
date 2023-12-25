package com.ihealth.demo.business;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.utils.Log;
import com.ihealth.demo.R;
import com.ihealth.demo.base.BaseFragment;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * <li>CertificationFragment</li>
 * <li>Authentication</li>
 *
 * <li> Please apply for authorization on the server and download the. PEM file,
   then put it in the assets folder and modify the corresponding name to call the following method </li>
   <li>When ispass replays true, it indicates that the authentication has passed</li>
 * Created by wj on 2018/11/20
 */
public class CertificationFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;

    @BindView(R.id.btnCertification)
    Button mBtnCertification;
    MainActivity mMainActivity;

    @Override
    public int contentViewID() {
        return R.layout.fragment_certification;
    }

    @Override
    public void initView() {
        mContext = getActivity();
        mMainActivity = (MainActivity) mContext;
    }

    public CertificationFragment() { }

    public static CertificationFragment newInstance(String param1, String param2) {
        CertificationFragment fragment = new CertificationFragment();
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

    @OnClick(R.id.btnCertification)
    public void onViewClicked() {
        try {
            //Please apply for authorization on the server and download the. PEM file,
            //then put it in the assets folder and modify the corresponding name to call the following method
            //When ispass replays true, it indicates that the authentication has passed
            //refer to https://chenxuewei-ihealth.github.io/ihealthlabs-sdk-docs/docs/android/quickstart
            InputStream is = mContext.getAssets().open("your authorization PEM file");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            boolean isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            Log.i(TAG, "isPass: " + isPass);
            mMainActivity.addLogInfo("sdkAuthWithLicense(buffer)");
            mMainActivity.addLogInfo("isPass:" + isPass);
            if (isPass) {
                mMainActivity.showDevicesFragment("", "");
                mMainActivity.clearLogInfo();
            } else {
                mMainActivity.showCertificationLogFragment("", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
