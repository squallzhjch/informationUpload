package com.informationUpload.activity;

import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: BaseActivity
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class BaseActivity extends FragmentActivity  implements ActivityInstanceStateListener {

    private volatile boolean mOnSaveInstanceStateInvoked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnSaveInstanceStateInvoked = false;
    }

    @Override
    public boolean isInstanceStateSaved() {
        return mOnSaveInstanceStateInvoked;
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        mOnSaveInstanceStateInvoked = true;
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onResume() {
        mOnSaveInstanceStateInvoked = false;
        MobclickAgent.onResume(this);
        super.onResume();
    }
  
}
