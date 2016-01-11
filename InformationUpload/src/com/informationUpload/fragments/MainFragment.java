package com.informationUpload.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.informationUpload.R;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.entity.AroundInformation;
import com.informationUpload.fragments.utils.IntentHelper;

import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.serviceengin.EnginCallback;
import com.informationUpload.serviceengin.ServiceEngin;
import com.informationUpload.utils.ChangePointUtil;
import com.informationUpload.utils.SystemConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MainFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MainFragment extends BaseFragment {
	/**
	 * 发现情况按钮
	 */
	private Button mDiscoverySituationBtn;
	/**
	 * 个人中心按钮
	 */
	private RelativeLayout mCenterBtn;
	/**
	 * popwindow弹出的view
	 */
	private View popview;
	/**
	 * 弹出窗
	 */
	private PopupWindow popupWindow;
	/**
	 * 主view
	 */
	private View view;
	/**
	 * popview公交按钮
	 */
	private View mBusButton;
	/**
	 * popview设施按钮
	 */
	private View mEstablishmentButton;
	/**
	 * popview道路按钮
	 */
	private View mRoadButton;
	/**
	 * popview周边改变按钮
	 */
	private View mChangeNearButton;
	/**
	 * 刷新地图码点按钮
	 */
	private RelativeLayout main_refresh;
	/**
	 * 重新定位按钮
	 */
	private RelativeLayout main_fix_position;
	/**
	 * 对地图放大按钮
	 */
	private RelativeLayout main_enlarge_map;
	/**
	 * 对地图缩小按钮
	 */
	private RelativeLayout main_reduce_map;
	/**
	 * 地图管理类
	 */
	private MapManager mapManager;
	/**
	 * 地图上的图标背景
	 */
	private BitmapDescriptor bd;
	/**
	 * 地图上的点显示信息
	 */
	private String text;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		view = inflater.inflate(R.layout.fragment_main, null, true);
		mapManager=MapManager.getInstance();
		popview = inflater.inflate(R.layout.main_fragment_select_pop, null);
		//初始化popview
		initPopview();
		//添加popwindow监听器
		addPopListeners();
		popview.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
		popview.setFocusableInTouchMode(true);
		popview.setOnKeyListener(backlistener);

		mDiscoverySituationBtn = (Button) view.findViewById(R.id.discovery_situation);
		//发现情况按钮
		mDiscoverySituationBtn.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {

				showPopview();

			}
		});
		//个人中心按钮
		mCenterBtn = (RelativeLayout) view.findViewById(R.id.center_btn);
		mCenterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(UserCenterFragment.class, null));
			}
		});
		//刷新地图麻点按钮
		main_refresh=(RelativeLayout)view.findViewById(R.id.main_refresh);
		main_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//刷新码点
				refreshmap();

			}
		});
		//重新定位按钮
		main_fix_position=(RelativeLayout)view.findViewById(R.id.main_fix_position);
		main_fix_position.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mapManager.FixPositionAgain();

			}
		});
		//对地图放大按钮
		main_enlarge_map=(RelativeLayout)view.findViewById(R.id.main_enlarge_map);
		main_enlarge_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mapManager.zoomIn();

			}
		});
		//对地图缩小按钮
		main_reduce_map=(RelativeLayout)view.findViewById(R.id.main_reduce_map);
		main_reduce_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mapManager.zoomOut();

			}
		});
		return view;
	}
	//刷新码点
	protected void refreshmap() {
		HashMap<String,Object> map=new HashMap<String, Object>();
		double[] ret = ChangePointUtil.baidutoreal(mLocationManager.getCurrentPoint().getLat(),mLocationManager.getCurrentPoint().getLon());
		map.put("latitude",(ret[0]+"").substring(0,(mLocationManager.getCurrentPoint().getLat()+"").lastIndexOf(".")+5));
		map.put("longitude",(ret[1]+"").substring(0,(mLocationManager.getCurrentPoint().getLon()+"").lastIndexOf(".")+5));

		ServiceEngin.Request(getActivity(), map, "inforquery" ,new EnginCallback(getActivity()){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				Log.e("请求成功",arg0.result.toString());
				//进行json解析
				parseJson(arg0.result.toString());
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				Log.e("请求失败",arg1);
			}
		});

	}
	//进行json解析	
	protected void parseJson(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		String errcode = jsonObj.getString("errcode");
		
		String errmsg = jsonObj.getString("errmsg");
		if(!"".equals(errcode)&&null!=errcode&&"0".equals(errcode)){
			JSONArray data = jsonObj.getJSONArray("data");
			ArrayList<AroundInformation> list=new ArrayList<AroundInformation>();
			for(int i=0;i<data.size();i++){
				JSONObject obj = (JSONObject) data.get(i);
				String info_type = obj.getString("info_type");

				String info_intel_id = obj.getString("info_intel_id");
				String address = obj.getString("address");
				JSONObject locationObj = obj.getJSONObject("location");
				String latitude = locationObj.getString("latitude");
				String longitude = locationObj.getString("longitude");
				Log.i("chentao","info_type："+i+":"+info_type);
				Log.i("chentao","address："+i+":"+address);
				GeoPoint gp=new GeoPoint((Double.parseDouble(latitude)),Double.parseDouble(longitude));

				AroundInformation aioformation = new AroundInformation(info_intel_id,info_type,address,gp);
				list.add(aioformation);

			}
			//在地图上添加覆盖物
			initOverlay(list);
		}else{
			Toast.makeText(getActivity(),errmsg,Toast.LENGTH_SHORT).show();
		}


	}
	/**
	 * 子地图上添加覆盖物
	 * @param list 
	 */
	private void initOverlay(ArrayList<AroundInformation> list) {

		// add marker overlay
		for(int i=0;i<list.size();i++){

			if(list.get(i).getInfoType().equals("1")){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_bus);
				text="公交";
			}else if(list.get(i).getInfoType().equals("2")){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_establishment);
				text="设施";
			}else if(list.get(i).getInfoType().equals("3")){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_road);
				text="道路";
			}else if(list.get(i).getInfoType().equals("4")){
				bd = BitmapDescriptorFactory
						.fromResource(R.drawable.type_other);
				text="周边变化";
			}
			double[] ret = ChangePointUtil.realtobaidu(list.get(i).getGp().getLat(),list.get(i).getGp().getLon());
			LatLng ll_Point = new LatLng(ret[0],ret[1]);
			MarkerOptions ooA = new MarkerOptions().position(ll_Point).icon(bd)
					.zIndex(9).draggable(true);
			Marker mMarker = (Marker) (mapManager.getMap().addOverlay(ooA));
			mMarker.setTitle(text+":"+list.get(i).getAddress());




		}
		mapManager.getMap().setOnMarkerClickListener(new OnMarkerClickListener() {
			//弹窗
			private InfoWindow mInfoWindow;

			@Override
			public boolean onMarkerClick(Marker marker) {

				LatLng ll = marker.getPosition();
				String[] title = marker.getTitle().split(":");
				TextView tv=new TextView(getActivity());
				tv.setText("");
				for(int i=0;i<title.length;i++){

					if(i==title.length-1){
						tv.append(title[i]);
					}else{
						tv.append(title[i]+"\n");
					}
				}


				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				tv.setLayoutParams(lp);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundResource(R.drawable.select_point_pop_bg);

				OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mapManager.getMap().hideInfoWindow();

					}
				};


				mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(tv), ll, -100, listener);
				mapManager.getMap().showInfoWindow(mInfoWindow);

				return true;
			}

		});

	}


	//添加popwindow监听器
	private void addPopListeners() {
		//公交按钮
		mBusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_BUS);
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

			}
		});
		//设施按钮
		mEstablishmentButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_ESTABLISHMENT);
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

			}
		});
		//
		// 道路按钮
		mRoadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_ROAD);
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

			}
		});
		//改变周边按钮
		mChangeNearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_AROUND);
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

			}
		});


	}

	//初始化popview
	private void initPopview() {
		mBusButton = popview.findViewById(R.id.bus_button);
		mEstablishmentButton = popview.findViewById(R.id.establishment_button);
		mRoadButton = popview.findViewById(R.id.road_button);
		mChangeNearButton = popview.findViewById(R.id.change_near_button);

	}

	//打开popwindow
	protected void showPopview() {
		//	

		popupWindow = new PopupWindow(popview, 700,700);
		//设置popwindow显示位置
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 200);
		//获取popwindow焦点
		popupWindow.setFocusable(true);
		//		ColorDrawable cd = new ColorDrawable(0x000000);
		//
		//		popupWindow.setBackgroundDrawable(cd);
		//设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {


			}
		});
		popupWindow.update();




	}

	private View.OnKeyListener backlistener = new View.OnKeyListener() {
		@Override
		public boolean onKey(View view, int i, KeyEvent keyEvent) {

			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
				if (i == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作

					popupWindow.dismiss();
					return true;


				}
			}

			return false;
		}
	};
}
