package com.informationUpload.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.informationUpload.Activity.ActivityInstanceStateListener;
import com.informationUpload.Activity.MainActivity;
import com.informationUpload.Activity.MyApplication;
import com.informationUpload.contents.AbstractOnContentUpdateListener;
import com.informationUpload.contents.ContentsManager;
import com.informationUpload.contents.OnContentUpdateListener;
import com.informationUpload.fragments.utils.MyFragmentManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
    protected ContentsManager mContentsManager;
    protected MyApplication mApplication;
    protected MainActivity mMainActivity;
    protected boolean mIsActive = true;
    private List<AbstractOnContentUpdateListener> mOnContentUpdateListeners = new ArrayList<AbstractOnContentUpdateListener>();
    private volatile boolean mIsFragmentMarkDisposed = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ActivityInstanceStateListener) {
            mActivityInstanceStateListener = (ActivityInstanceStateListener) activity;
        }
        mMainActivity = (MainActivity) activity;
        mApplication = (MyApplication) mMainActivity.getApplicationContext();
        mFragmentManager = MyFragmentManager.getInstance();
        mContentsManager = ContentsManager.getInstance();
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
        for (OnContentUpdateListener listener : mOnContentUpdateListeners) {
            mContentsManager.unregisterOnContentUpdateListener(listener);
        }
        mOnContentUpdateListeners.clear();
    }

    public void onFragmentActive() {
        mIsActive = true;
        for (AbstractOnContentUpdateListener listener : mOnContentUpdateListeners) {
            if (listener.isUpdateHappened()) {
                listener.onContentUpdated(listener.getCachedObjects());
                listener.clearCache();
            }
        }
    }


    public void onFragmentDeactive() {
        mIsActive = false;
    }

    protected void registerOnContentUpdateListener(AbstractOnContentUpdateListener listener) {
        if (mContentsManager.registerOnContentUpdateListener(listener)) {
            mOnContentUpdateListeners.add(listener);
        }
    }

    public  void markFragmentDisposed() {
        mIsFragmentMarkDisposed = true;
    }
}
