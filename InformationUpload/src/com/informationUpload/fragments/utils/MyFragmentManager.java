package com.informationUpload.fragments.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.informationUpload.activity.ActivityInstanceStateListener;
import com.informationUpload.R;
import com.informationUpload.fragments.BaseFragment;
import com.informationUpload.utils.SystemConfig;

import java.util.List;
import java.util.Stack;

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
    private final Stack<BaseFragment> mFragmentStack = new Stack<BaseFragment>();
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

    public void switchFragment(Intent intent){
        if (intent == null
                || mActivityInstanceStateListener.isInstanceStateSaved()) {
            return ;
        }



        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments != null) {
            if(fragments.size() > 0){
//                mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment != null) {
                    if (!isFragmentAllowedSwitch(fragment)) {
                        mFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                        removeAllStacks(fragment.getClass().getName());
                    } else {
                        mFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
                    }
                }
            }
        }

        Fragment visibleFragment = null;
            String tagName = getTag(intent.getComponent().getClassName());
            BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(tagName);
            BaseFragment fragmentS =  getFragment(tagName);
            if (fragment == null || fragmentS == null) {
                showFragment(intent);
            } else {
                mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
                visibleFragment = fragment;
                fragment.onFragmentActive();
                mFragmentStack.remove(fragment);
                mFragmentStack.push(fragment);
            }


        if (visibleFragment != null) {
            setFragmentPauseState(visibleFragment);
        }
    }

    private String getTag(String componentName) {
        for (int idx = 0; idx < mFragmentStack.size(); idx++) {
            if (TextUtils.equals(mFragmentStack.get(idx).getClass().getName(), componentName)) {
                return mFragmentStack.get(idx).getClass().getName();
            }
        }
        return null;
    }

    private void removeAllStacks(String componentName) {
        for (int idx = 0; idx < mFragmentStack.size(); idx++) {
            if (mFragmentStack.get(idx).getClass().getName().startsWith(componentName)) {
                 mFragmentStack.remove(idx);
                idx--;
            }
        }
    }

    private boolean isFragmentAllowedSwitch(Fragment fragment) {
        String componentName = fragment.getClass().getName();
        for (int idx = 0; idx < mFragmentStack.size(); idx++) {
            if (TextUtils.equals(mFragmentStack.get(idx).getClass().getName(), componentName)) {
                return  ((BaseFragment) fragment).isFragmentAllowedSwitch();
            }
        }
        return false;
    }

    public void showFragment(Intent intent){
        FragmentTransaction transformation = mFragmentManager.beginTransaction();
        String fragmentName = intent.getComponent().getClassName();
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(fragmentName);
        BaseFragment fragmentS =  getFragment(fragmentName);

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


        if(fragment != null && fragmentS != null){
            int index = mFragmentStack.search(fragment);
            if(index > 0 ){
                mFragmentStack.remove(fragment);
                mFragmentStack.push(fragment);
            }
            transformation.show(fragment).commitAllowingStateLoss();
            fragment.onDataChange(intent.getExtras());
            return;
        }
        setFragmentPauseState(fragment);
        fragment = (BaseFragment) Fragment.instantiate(mContext, fragmentName);
        fragment.setArguments(intent.getExtras());
        transformation.addToBackStack(null);
        transformation.add(R.id.main_fragment_layout, fragment, fragmentName);
        transformation.commitAllowingStateLoss();
        mFragmentStack.push(fragment);
    }


    private void setFragmentPauseState(Fragment visibleFragment) {
        if (mFragmentStack != null) {
            for (BaseFragment fragment:mFragmentStack) {
                if (fragment != null
                        && fragment != visibleFragment && fragment.isVisible()) {
                    fragment.onFragmentDeactive();
                }
            }
        }
    }

    public void removeFragment(Class<?> fragment){
        if(fragment == null)
            return;
        for(BaseFragment fragment1:mFragmentStack){
            if(fragment1.getClass().getName().equals(fragment.getName())){
                mFragmentStack.remove(fragment1);
                FragmentTransaction transformation = mFragmentManager.beginTransaction();
                transformation.remove(fragment1);
                return;
            }
        }
    }

    public boolean  back(){
        int fragmentSize = mFragmentStack.size();
        boolean canBack = fragmentSize > 1;
        if(canBack) {
            BaseFragment currentFragment = mFragmentStack.peek();
            if(currentFragment.getClass().getName().equals("MainFragment.class"))
                return false;
            if(mActivityInstanceStateListener.isInstanceStateSaved()) {
                if (currentFragment != null) {
                    currentFragment.markFragmentDisposed();
                }
                return false;
            }

            mFragmentManager.popBackStack();

            FragmentTransaction transformation = mFragmentManager.beginTransaction();
            transformation.remove(currentFragment);
            mFragmentStack.pop();

            BaseFragment fragment = mFragmentStack.peek();
            if (currentFragment != null){
                fragment.onFragmentDeactive();
            }

            transformation.show(fragment).commitAllowingStateLoss();
            fragment.onFragmentActive();
        }
        return  canBack;
    }

    public BaseFragment getFragment(String fragmentName){
        if(mFragmentStack != null){
            for(BaseFragment fragment:mFragmentStack){
                if(TextUtils.equals(fragment.getClass().getName(), fragmentName)){
                    return fragment;
                }
            }
        }
        return null;
    }

    public void hideFragment(){

    }

    public void onDestroy(){
        mFragmentStack.clear();
        mInstance = null;
    }

    public boolean onBackPressed() {
        if (mFragmentStack != null) {
            for (BaseFragment fragment:mFragmentStack) {
                if (fragment != null && fragment.isVisible()) {
                    return fragment.onBackPressed();
                }
            }
        }
        return  false;
    }
}
