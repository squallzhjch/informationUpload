package com.informationUpload.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.informationUpload.R;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.LocationManager;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.SelectPointView;
import com.informationUpload.widget.TitleView;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SelectPointFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SelectPointFragment extends BaseFragment implements BaiduMap.OnMapClickListener {


	private SelectPointView mSelectView;
	private String mAddress;
	private String mAdminCode;
	private GeoPoint point;
	private MapManager mMapManager;
	/**
	 * 选点背景图片
	 */
	private BitmapDescriptor bd;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMapManager = MapManager.getInstance();
		mLocationManager=LocationManager.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View  view =inflater.inflate(R.layout.fragment_select_point,null);


		TitleView title = (TitleView)view.findViewById(R.id.title_view);
		title.setOnLeftAreaClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});

		mMapManager.setOnMapClickListener(this);
		mSelectView = (SelectPointView)view.findViewById(R.id.point_view);
		mSelectView.setOnSubmitListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mContentsManager.notifyContentUpdateSuccess(SystemConfig.FRAGMENT_UPDATE_SELECT_POINT_ADDRESS, mAddress,mAdminCode,point);
				mFragmentManager.back();
			}
		});
		point = new GeoPoint();

		point.setLat(mLocationManager.getCurrentPoint().getLat());
		point.setLon(mLocationManager.getCurrentPoint().getLon());
		mMapManager.searchAddress(point,new OnSearchAddressListener() {
		

			@Override
			public void OnSuccess(String address,String adminCode) {
	
				mAdminCode=adminCode;
				mAddress = address;
				mSelectView.setAddressText(address);
			}

			@Override
			public void onFailure() {
				mSelectView.setAddressText("");
			}
		});
		return view;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mMapManager.removeOnMapClickListener(this);
	}

	@Override
	public void onMapClick(LatLng latLng) {
		mMapManager.getMap().clear();
		 bd = BitmapDescriptorFactory
				.fromResource(R.drawable.type_bus);

		LatLng ll_Point = new LatLng(latLng.latitude,latLng.longitude);
		MarkerOptions ooA = new MarkerOptions().position(ll_Point).icon(bd)
				.zIndex(9).draggable(true);
		Marker mMarker = (Marker) (mMapManager.getMap().addOverlay(ooA));
		point = new GeoPoint();

		point.setLat(latLng.latitude);
		point.setLon(latLng.longitude);
		mMapManager.searchAddress(point,new OnSearchAddressListener() {
			@Override
			public void OnSuccess(String address,String adminCode) {
				mMapManager.centerpoint(point);
				mAdminCode=adminCode;
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
	public boolean onMapPoiClick(MapPoi mapPoi) {
		return false;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapManager.getMap().clear();
		if(bd!=null){
		bd.recycle();
		}
	}

}
