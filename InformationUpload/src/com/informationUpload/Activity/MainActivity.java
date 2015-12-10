package com.informationUpload.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.informationUpload.fragments.BusReportFragment;
import com.informationUpload.fragments.MainFragment;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;


import com.informationUpload.map.MapManager;
import com.informationUpload.R;
import com.tencent.tencentmap.mapsdk.map.MapView;

public class MainActivity extends BaseActivity implements OnClickListener, ActivityInstanceStateListener {

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
            myFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapManager != null) {
            mMapManager.onDestroy();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn: {
                myFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(BusReportFragment.class, null));
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!myFragmentManager.back()) {
            finish();
            System.exit(0);
        }
    }
    @Override
    public boolean isInstanceStateSaved() {
        return mOnSaveInstanceStateInvoked;
    }
}