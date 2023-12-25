package com.ihealth.demo.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

/**
 * <li>BaseFragment</li>
 * <li>All Fragment Basic</li>
 *
 * Created by wj on 2018/11/20
 */
public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    private Context mContext;

    // 导入布局文件
    public abstract int contentViewID();

    // 初始化控件
    public abstract void initView();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentViewID() != 0) {
            mRootView = LayoutInflater.from(mContext).inflate(contentViewID(), null);
            init();
        }
        return mRootView;
    }

    // 初始化UI
    private void init() {
        // ButterKnife bind
        ButterKnife.bind(this, mRootView);
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
