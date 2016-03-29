package com.informationUpload.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.informationUpload.R;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.LocationManager;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.widget.SelectPointView;
import com.informationUpload.widget.TitleView;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SelectPointFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SelectPointFragment extends BaseFragment implements BaiduMap.OnMapStatusChangeListener {


	private SelectPointView mSelectView;
	private String mAddress;
	private String mAdminCode;
	private GeoPoint mPoint;
	private MapManager mMapManager;
	private ImageView pointIcon;
	/**
	 * 选点背景图片
	 */
	private BitmapDescriptor bd;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMapManager = MapManager.getInstance();
		mLocationManager = LocationManager.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.fragment_select_point, null);

		TitleView title = (TitleView) view.findViewById(R.id.title_view);
		title.setOnLeftAreaClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});

		mMapManager.setOnMapStatusChangeListener(this);
		mSelectView = (SelectPointView) view.findViewById(R.id.point_view);
		mSelectView.setOnSubmitListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 double dis = mMapManager.getDistance(mLocationManager.getCurrentPoint(),lastPoint);
				 Log.i("info","dis:"+dis);
				 if(dis>2000){
					 Toast.makeText(getActivity(),"所选位置与定位点相差大于2000米，不符合规定，请重新选点！", Toast.LENGTH_SHORT).show();
				 }else{
						mContentsManager.notifyContentUpdateSuccess(SystemConfig.FRAGMENT_UPDATE_SELECT_POINT_ADDRESS, mAddress, mAdminCode, mPoint);
						mFragmentManager.back(); 
				 }
			
			}
		});

		pointIcon = (ImageView) view.findViewById(R.id.point_icon);

		mPoint = new GeoPoint();
		mPoint.setLat(mLocationManager.getCurrentPoint().getLat());
		mPoint.setLon(mLocationManager.getCurrentPoint().getLon());

		mMapManager.searchAddress(mPoint, new OnSearchAddressListener() {

			@Override
			public void OnSuccess(String address, String adminCode) {

				mAdminCode = adminCode;
				mAddress = address;
				mSelectView.setAddressText(address);
			}

			@Override
			public void onFailure() {
				mSelectView.setAddressText("");
			}
		});
		mMapManager.setCenter();
		return view;
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDetach() {
		super.onDetach();
		mMapManager.removeOnMapTouchListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (bd != null) {
			bd.recycle();
		}
	}

	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {
		pointIcon.setBackgroundResource(R.drawable.drag_point);
	}

	@Override
	public void onMapStatusChange(MapStatus mapStatus) {

	}

	GeoPoint lastPoint = null;
	LatLng lastLatLng = null;

	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		pointIcon.setBackgroundResource(R.drawable.default_point);
		GeoPoint point = new GeoPoint(mapStatus.target.latitude, mapStatus.target.longitude);
		if(lastPoint != null){
			double dis1= mMapManager.getDistance(lastPoint, point);

			double dis2 = DistanceUtil.getDistance(lastLatLng, mapStatus.target);

		   
		}
		lastPoint = point;
		lastLatLng = mapStatus.target;
		mMapManager.searchAddress(point, new OnSearchAddressListener() {
			@Override
			public void OnSuccess(String address, String adminCode) {
				mAdminCode = adminCode;
				mAddress = address;
				mSelectView.setAddressText(address);
			}

			@Override
			public void onFailure() {
				mSelectView.setAddressText("");
			}
		});
	}

	@Override
	public void onDataChange(Bundle bundle) {

	}
}
