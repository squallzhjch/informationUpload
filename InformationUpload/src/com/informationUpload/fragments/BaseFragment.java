package com.informationUpload.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.informationUpload.Activity.ActivityInstanceStateListener;
import com.informationUpload.Activity.MainActivity;
import com.informationUpload.Activity.MyApplication;
import com.informationUpload.fragments.utils.MyFragmentManager;

import java.lang.ref.WeakReference;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: BaseFragment
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public abstract class BaseFragment extends Fragment {
    protected ActivityInstanceStateListener mActivityInstanceStateListener;

    protected MyFragmentManager mFragmentManager;
    protected MyApplication mApplication;
    protected MainActivity mMainActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ActivityInstanceStateListener) {
            mActivityInstanceStateListener = (ActivityInstanceStateListener) activity;
        }
        mMainActivity = (MainActivity) activity;
        mApplication = (MyApplication) mMainActivity.getApplicationContext();
        mFragmentManager = MyFragmentManager.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFragmentActive();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onDetach() {
        super.onDetach();
//        for (OnContentUpdateListener listener : mOnContentUpdateListeners) {
//            mContentsManager.unregisterOnContentUpdateListener(listener);
//        }
//        mOnContentUpdateListeners.clear();
    }

    public void onFragmentActive() {

//        if (mMapControl != null
//                && !isFragmentAllowedSwitch()) {
//            mMapControl.pause();
//        }
//        mIsActive = true;
//        mLog.d(LOG_TAG, "onResume.called, this: %s, mIsActive: %b, mOnContentUpdateListeners.size: %d", this, mIsActive, mOnContentUpdateListeners.size());
//        for (AbstractOnContentUpdateListener listener : mOnContentUpdateListeners) {
//            if (listener.isUpdateHappened()) {
//                listener.onContentUpdated(listener.getCachedObjects());
//                listener.clearCache();
//            }
//        }
//
//        for (WeakReference<OnViewVisualChangedListener> viewVisualChangedListenerRef : mOnViewVisualChangedListeners) {
//            if (viewVisualChangedListenerRef != null && viewVisualChangedListenerRef.get() != null) {
//                viewVisualChangedListenerRef.get().setViewVisualState(OnViewVisualChangedListener.ViewVisualState.FOREGROUND);
//            }
//        }
    }
}
