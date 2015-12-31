package com.informationUpload.fragments;

import java.util.HashMap;

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
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.informationUpload.R;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.MapManager;
import com.informationUpload.serviceengin.EnginCallback;
import com.informationUpload.serviceengin.ServiceEngin;
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

		//		map.put("latitude",(mLocationManager.getCurrentPoint().getLat()+"").substring(0,(mLocationManager.getCurrentPoint().getLat()+"").lastIndexOf(".")+5));
		//		map.put("longitude",(mLocationManager.getCurrentPoint().getLat()+"").substring(0,(mLocationManager.getCurrentPoint().getLon()+"").lastIndexOf(".")+5));
		map.put("latitude","40.01392");
		map.put("longitude","116.47836");
		ServiceEngin.Request(getActivity(), map, "inforquery" ,new EnginCallback(getActivity()){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				Log.e("请求成功",arg0.result.toString());
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				Log.e("请求失败",arg1);
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
//		WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
//		lp.alpha=0.5f;
//
//		getActivity().getWindow().setAttributes(lp);

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
				//在dismiss中恢复透明度
//				WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
//				lp.alpha=1f;
//
//
//				getActivity().getWindow().setAttributes(lp);



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
