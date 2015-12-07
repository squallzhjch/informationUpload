package com.informationUpload.fragments.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.informationUpload.R;
import com.informationUpload.fragments.BaseFragment;

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

    public void init(Context context, FragmentManager fragmentManager){
        mContext = context;
        mFragmentManager = fragmentManager;
    }

    public void showFragment(Intent intent){
        FragmentTransaction transformation = mFragmentManager.beginTransaction();
        String fragmentName = intent.getComponent().getClassName();
        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(fragmentName);

        if(fragment != null){
            transformation.show(fragment).commitAllowingStateLoss();
            return;
        }

        fragment = (BaseFragment) Fragment.instantiate(mContext, fragmentName);
        fragment.setArguments(intent.getExtras());
        transformation.addToBackStack(null);
        transformation.add(R.id.main_fragment_layout, fragment, fragmentName);
        transformation.commitAllowingStateLoss();
    }

    public boolean  back(){
        int fragmentSize = mFragmentManager.getBackStackEntryCount();
        boolean canBack = fragmentSize > 0;
        mFragmentManager.popBackStack();
        return  canBack;
    }

    public void hideFragment(){

    }
}
