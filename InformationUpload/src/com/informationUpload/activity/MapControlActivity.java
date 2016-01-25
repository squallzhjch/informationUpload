package com.informationUpload.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.informationUpload.R;
import com.umeng.analytics.MobclickAgent;


/**
 * 演示地图缩放，旋转，视角控制
 */
public class MapControlActivity extends Activity {

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	/**
	 * 当前地点击点
	 */
	private LatLng currentPt;
	private String touchType;

	/**
	 * 用于显示地图状态的面板
	 */
	private TextView mStateBar;
	private TextView cityName;
	private Button back_btn;
	private MapControlActivity mContext;
	private final String mPageName = "MapControlActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapcontrol);
		mContext=this;
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mStateBar = (TextView) findViewById(R.id.state);
		cityName = (TextView) findViewById(R.id.cityName);
		back_btn = (Button) findViewById(R.id.back_btn);

		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		initListener();
	}

	private void initListener() {


		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 当用intent参数时，设置中心点为指定点	
			LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));		
			MapStatus ms = new MapStatus.Builder(new MapStatus.Builder().target(p).build()).build();
			MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
			mBaiduMap.animateMapStatus(u);

		} else {
			//mMapView = new MapView(this, new BaiduMapOptions());
		}

		cityName.setText(b.getString("cityName"));
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		//禁用俯仰、指南针、旋转
		mMapView.getMap().getUiSettings().setOverlookingGesturesEnabled(false);
		mMapView.getMap().getUiSettings().setCompassEnabled(false);
		mMapView.getMap().getUiSettings().setRotateGesturesEnabled(false);


	}




	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
	}
	@Override
	public void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	public void onPause() {

		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);
	}
}
