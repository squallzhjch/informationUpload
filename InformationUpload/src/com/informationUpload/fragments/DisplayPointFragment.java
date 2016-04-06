package com.informationUpload.fragments;

import java.io.Serializable;
import java.util.ArrayList;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.informationUpload.R;
import com.informationUpload.entity.AroundInformation;
import com.informationUpload.entity.SubmitInformation;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.utils.ChangePointUtil;
import com.informationUpload.widget.TitleView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DisplayPointFragment extends BaseFragment{
	/**
	 * 主布局的view
	 */
	private View view;
	/**
	 * submitInformation
	 */
	private SubmitInformation subinfo;
	/**
	 * 
	 */
	private BitmapDescriptor bd;
	/**
	 * 
	 */
	private String text;
	/**
	 * 地图管理类
	 */
	private MapManager mMapManager;
	private TitleView title_view;

	@Override
	public void onDataChange(Bundle bundle) {
		if(bundle!=null){
			subinfo=(SubmitInformation) bundle.getSerializable(SystemConfig.BUNDLE_DATA_LIST_POSITION);
			mMapManager=MapManager.getInstance();


			if(subinfo.getInfo_type()==0){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_bus);
				text="公交";
			}else if(subinfo.getInfo_type()==1){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_establishment);
				text="设施";
			}else if(subinfo.getInfo_type()==2){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_road);
				text="道路";
			}else if(subinfo.getInfo_type()==3){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_other);
				text="周边变化";
			}
			LatLng ll_Point = new LatLng(subinfo.getLat(),subinfo.getLon());
			MarkerOptions ooA = new MarkerOptions().position(ll_Point).icon(bd)
					.zIndex(9).draggable(true);

			String title =  subinfo.getAddress();
			Log.i("info","lat:"+subinfo.getLat()+",lon:"+subinfo.getLon());
			mMapManager.setMarker(ooA, title);
			mMapManager.setCenter(new GeoPoint(subinfo.getLat(),subinfo.getLon()));
			Log.i("info","subinfo:"+subinfo);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		view =inflater.inflate(R.layout.fragment_display_point,null);
	
		title_view=(TitleView)view.findViewById(R.id.title_view);
		title_view.setOnLeftAreaClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});
		return view;
	}

}
