package com.informationUpload.fragments.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.informationUpload.activity.ActivityInstanceStateListener;
import com.informationUpload.R;
import com.informationUpload.fragments.BaseFragment;
import com.informationUpload.utils.SystemConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: FragmentManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MyFragmentManager {

    private FragmentManager mFragmentManager;
    private static volatile MyFragmentManager mInstance;
    private Context mContext;
    private final List<BaseFragment> mFragmentList = new ArrayList<BaseFragment>();
    private ActivityInstanceStateListener mActivityInstanceStateListener;
    public static MyFragmentManager getInstance() {

        if (mInstance == null) {
            synchronized (MyFragmentManager.class) {
                if (mInstance == null) {
                    mInstance = new MyFragmentManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context, FragmentManager fragmentManager, ActivityInstanceStateListener activityInstanceStateListener){
        mContext = context;
        mFragmentManager = fragmentManager;
        mActivityInstanceStateListener = activityInstanceStateListener;
    }

    public void showFragment(Intent intent){
        FragmentTransaction transformation = mFragmentManager.beginTransaction();
        String fragmentName = intent.getComponent().getClassName();
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(fragmentName);


        boolean hideAllOtherFragment = false;
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                hideAllOtherFragment = bundle.getBoolean(SystemConfig.HIDE_OTHER_FRAGMENT, true);
            }
        }

        if (hideAllOtherFragment) {
            List<Fragment> fragments = mFragmentManager.getFragments();
            if (fragments != null) {
                for (int i = 0; i < fragments.size(); i++) {
                    Fragment fragmentTag = fragments.get(i);
                    if (fragmentTag != null) {
                        mFragmentManager.beginTransaction().hide(fragmentTag).commitAllowingStateLoss();
                    }
                }
            }
        }


        if(fragment != null){
            transformation.show(fragment).commitAllowingStateLoss();
            return;
        }
        setFragmentPauseState(fragment);
        fragment = (BaseFragment) Fragment.instantiate(mContext, fragmentName);
        fragment.setArguments(intent.getExtras());
        transformation.addToBackStack(null);
        transformation.add(R.id.main_fragment_layout, fragment, fragmentName);
        transformation.commitAllowingStateLoss();
        mFragmentList.add(fragment);
    }


    private void setFragmentPauseState(Fragment visibleFragment) {
        if (mFragmentList != null) {
            for (int i = 0; i < mFragmentList.size(); i++) {
                BaseFragment curFragment = mFragmentList.get(i);
                if (curFragment != null
                        && curFragment != visibleFragment && curFragment.isVisible()) {
                     curFragment.onFragmentDeactive();
                }
            }
        }
    }

    public boolean  back(){
        int fragmentSize =mFragmentList.size();
        boolean canBack = fragmentSize > 1;
        if(canBack) {
            BaseFragment currentFragment = mFragmentList.get(mFragmentList.size() - 1);
            BaseFragment fragment = mFragmentList.get(mFragmentList.size() - 2);

            if (currentFragment != null){
                fragment.onFragmentDeactive();
            }
            if(mActivityInstanceStateListener.isInstanceStateSaved()) {
                if (currentFragment != null) {
                    currentFragment.markFragmentDisposed();
                }
                return false;
            }


            mFragmentManager.popBackStack();

            FragmentTransaction transformation = mFragmentManager.beginTransaction();
            transformation.remove(currentFragment);
            mFragmentList.remove(currentFragment);
            transformation.show(fragment).commitAllowingStateLoss();
            fragment.onFragmentActive();
        }
        return  canBack;
    }

    public void hideFragment(){

    }

    public void onDestroy(){
        mFragmentList.clear();
        mInstance = null;
    }
}
