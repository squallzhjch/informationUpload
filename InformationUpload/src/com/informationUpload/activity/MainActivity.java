package com.informationUpload.activity;

import android.os.Bundle;


import com.baidu.mapapi.map.MapView;
import com.informationUpload.fragments.LoginFragment;
import com.informationUpload.fragments.MainFragment;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;

import com.informationUpload.map.MapManager;
import com.informationUpload.R;
import com.informationUpload.system.LoginHelper;

public class MainActivity extends BaseActivity implements ActivityInstanceStateListener {

    private MapManager mMapManager;
    private MyFragmentManager myFragmentManager;
    private volatile boolean mOnSaveInstanceStateInvoked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnSaveInstanceStateInvoked = false;
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main);
            MapView mapView = (MapView) findViewById(R.id.mapView);

            mMapManager = MapManager.getInstance();
            mMapManager.init(this, mapView);
            myFragmentManager = MyFragmentManager.getInstance();
            myFragmentManager.init(getApplicationContext(), getSupportFragmentManager(), this);
            myFragmentManager.switchFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));

            LoginHelper.checkLogin(this, new LoginHelper.OnCheckLoginListener() {
                @Override
                public void onAgree() {
                    myFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(LoginFragment.class, null));
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapManager != null) {
            mMapManager.onDestroy();
        }
        if(myFragmentManager != null) {
            myFragmentManager.onDestroy();
            myFragmentManager = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mMapManager != null) {
            mMapManager.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMapManager != null) {
            mMapManager.onResume();
        }
        mOnSaveInstanceStateInvoked = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (mMapManager != null) {
            mMapManager.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        mOnSaveInstanceStateInvoked = true;
        super.onSaveInstanceState(bundle);
    }


    @Override
    public void onBackPressed() {
        myFragmentManager.onBackPressed();
    }
    @Override
    public boolean isInstanceStateSaved() {
        return mOnSaveInstanceStateInvoked;
    }
}