package com.ihealth.demo.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ihealth.demo.business.CertificationFragment;
import com.ihealth.demo.business.DevicesFragment;
import com.ihealth.demo.business.LogFragment;
import com.ihealth.demo.business.ScanFragment;

/**
 * <li>BaseFragmentActivity</li>
 * <li>MainActivity Inherited from here</li>
 *
 * Created by wj on 2018/11/20
 */
public abstract class BaseFragmentActivity extends BaseActivity {
    /** Fragment Status*/
    protected static final int CODE_SHOW_FRAGMENT_SUCCESS = 0;
    protected static final int CODE_SHOW_FRAGMENT_NOT_FOUND = -1;
    protected static final int CODE_SHOW_FRAGMENT_HAD_SHOWED = -2;

    protected FragmentManager mFragmentManager;
    protected Fragment mCurrFragment; // 前台的Fragment

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
    }

    public void replaceFragment(int framelayoutResId, Fragment fragment, String tag) {
        if (fragment != null && framelayoutResId != 0) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(framelayoutResId, fragment,
                    (tag == null)?fragment.getClass().getSimpleName():tag);
            ft.addToBackStack((tag == null)?fragment.getClass().getSimpleName():tag);
            ft.commitAllowingStateLoss();

            mCurrFragment = fragment;
        }
    }

    public void addFragment(int framelayoutResId, Fragment fragment, String tag) {
        if (fragment != null && framelayoutResId != 0) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (mCurrFragment != null) {
                ft.hide(mCurrFragment);
            }

            if (!fragment.isAdded()) {
                ft.add(framelayoutResId, fragment,
                        (tag == null)?fragment.getClass().getSimpleName():tag);
            } else {
                ft.show(fragment);
            }
            ft.commitAllowingStateLoss();

            mCurrFragment = fragment;
        }
    }

    public void hideFragment(Fragment fragment) {
        if (fragment != null && !fragment.isHidden()) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.hide(fragment);
        }
    }

    public void removeFragment(String tag) {
        Fragment fragment = findFragmentByTag(tag);
        removeFragment(fragment);
    }

    public void removeFragment(Fragment fragment) {
        if (fragment != null && fragment.isAdded()) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 根据TAG找已经创建的Fragment
     * Find fragments that have been created based on TAG
     * @param tag
     * @return
     */
    public Fragment findFragmentByTag(String tag) {
        if (tag == null) {
            return null;
        }
        return mFragmentManager.findFragmentByTag(tag);
    }

    /**
     * 查找是有有TAG对应的Fragment并显示出来
     * @param tag
     * @return
     */
    public boolean showFragment(String tag, OnShowFragmentListener listener) {
        boolean result = false;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = findFragmentByTag(tag);
        if (fragment != null) {
            if (fragment.isHidden()) {
                if (mCurrFragment != null) {
                    ft.hide(mCurrFragment);
                }
                ft.show(fragment);
                mCurrFragment = fragment;
                ft.commitAllowingStateLoss();
                result = true;
                listener.onShowFragment(CODE_SHOW_FRAGMENT_SUCCESS, tag);
            } else {
                listener.onShowFragment(CODE_SHOW_FRAGMENT_HAD_SHOWED, tag);
            }
        } else {
            listener.onShowFragment(CODE_SHOW_FRAGMENT_NOT_FOUND, tag);
        }
        return result;
    }

    /**
     * 清除已经创建的Fragments
     * Clear Fragments that have been created
     */
    public void clearFragments() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = findFragmentByTag(CertificationFragment.class.getSimpleName());
        if (fragment != null && fragment.isAdded()) {
            ft.remove(fragment);
        }
        fragment = findFragmentByTag(DevicesFragment.class.getSimpleName());
        if (fragment != null && fragment.isAdded()) {
            ft.remove(fragment);
        }
        fragment = findFragmentByTag(ScanFragment.class.getSimpleName());
        if (fragment != null && fragment.isAdded()) {
            ft.remove(fragment);
        }
        fragment = findFragmentByTag(LogFragment.class.getSimpleName());
        if (fragment != null && fragment.isAdded()) {
            ft.remove(fragment);
        }

        ft.commitAllowingStateLoss();
    }

    public interface OnShowFragmentListener {
        void onShowFragment(int code, String tag);
    }
}
