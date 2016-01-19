package com.informationUpload.map;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.informationUpload.map.LocationManager.OnLocationListener;



import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MapManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MapManager {
	/**
	 * marker的list 
	 */
	public  ArrayList<MarkerOptions> list_markers_opt=new ArrayList<MarkerOptions>();
	public  ArrayList<Marker> list_markers=new ArrayList<Marker>();
	boolean isFirstLoc = true;// 是否首次定位
	
	private MapView mMapView;
	private BaiduMap map;
	private Context mContext;
	private Boolean bFirst = true;
	private LocationManager mLocationManager;
	private final List<WeakReference<OnMapStatusChangeListener>> mOnMapStatusChangeListeners = new ArrayList<WeakReference<OnMapStatusChangeListener>>();
	private final List<WeakReference<OnMapClickListener>> mOnMapClickListeners = new ArrayList<WeakReference<OnMapClickListener>>();
	private static volatile MapManager mInstance;
	public static MapManager getInstance() {
		if (mInstance == null) {
			synchronized (MapManager.class) {
				if (mInstance == null) {
					mInstance = new MapManager();
				}
			}
		}
		return mInstance;
	}
    //获得地图操作类
	public BaiduMap getMap(){
		return map;
	}

	public void init(final Context context, final MapView mapView){
		if(mapView == null || context == null)
			return;
		mContext = context;
		mMapView = mapView;
		map = mMapView.getMap();
	
		mMapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomTo(17f));
		mapView.getMap().setMyLocationEnabled(true);
		mapView.showZoomControls(false);

		mLocationManager = LocationManager.getInstance();
		mLocationManager.startLocation();
		mLocationManager.addOnLocationListener(listener);
		
		View child = mMapView.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
			child.setVisibility(View.INVISIBLE);
		}

		mMapView.getMap().setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				for (WeakReference<OnMapClickListener> weakRef : mOnMapClickListeners) {
					if (weakRef.get() != null) {
						weakRef.get().onMapClick(latLng);
					}
				}
			}

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				return false;
			}
		});


		mMapView.getMap().setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChangeStart(MapStatus mapStatus) {
				for (WeakReference<OnMapStatusChangeListener> weakRef : mOnMapStatusChangeListeners) {
					if (weakRef.get() != null) {
						weakRef.get().onMapStatusChangeStart(mapStatus);
					}
				}
			}

			@Override
			public void onMapStatusChange(MapStatus mapStatus) {
				for (WeakReference<OnMapStatusChangeListener> weakRef : mOnMapStatusChangeListeners) {
					if (weakRef.get() != null) {
						weakRef.get().onMapStatusChange(mapStatus);
					}
				}
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus mapStatus) {
				for (WeakReference<OnMapStatusChangeListener> weakRef : mOnMapStatusChangeListeners) {
					if (weakRef.get() != null) {
						weakRef.get().onMapStatusChangeFinish(mapStatus);
					}
				}
			}
		});

		mMapView.getMap().setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				int width = wm.getDefaultDisplay().getWidth();
				mapView.getMap().getUiSettings().setCompassPosition(new Point(width - 80, 80));
			}
		});

	}

	private  OnLocationListener listener = new OnLocationListener() {

		@Override
		public void onLocationChanged(GeoPoint location,String address,float radius) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(radius)
			// 此处设置开发者获取到的方向信息，顺时针0-360
			.direction(100).latitude( location.getLat())
			.longitude(location.getLon()).build();
			mMapView.getMap().setMyLocationData(locData);
			mMapView.getMap()
			.setMyLocationConfigeration(new MyLocationConfiguration(
					LocationMode.NORMAL, true, null));

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLat(),
						location.getLon());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mMapView.getMap().animateMapStatus(u);
			}
			//            // 更新 location 图层
			//            mMapView.getMap().setMyLocationConfigeration(new MyLocationConfiguration(
			//                    LocationMode.NORMAL, true, null));
			//            bFirst = false;
			//            mMapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(location.getLat(), location.getLon())));
		}
	};
    //使某点居中显示
    public void setCenter(GeoPoint point){
    	LatLng ll = new LatLng(point.getLat(),
    			point.getLon());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mMapView.getMap().animateMapStatus(u);
    }
    
  

	public GeoPoint getCenter(){
		final LatLng point = mMapView.getMap().getMapStatus().target;
		GeoPoint point1 = new GeoPoint(point.latitude, point.longitude);
		return point1;
	}

	public void onDestroy(){
		if(mMapView != null){
			mMapView.onDestroy();
			mMapView = null;
		}
		if(mLocationManager != null){
			mLocationManager.onDestroy();
		}

		if(mOnMapStatusChangeListeners != null){
			mOnMapStatusChangeListeners.clear();
		}

		if(mOnMapClickListeners != null){
			mOnMapClickListeners.clear();
		}
		mInstance = null;
	}

	public void onPause(){
		if(mMapView != null){
			mMapView.onPause();
		}
		if(mLocationManager != null){
			mLocationManager.stopLocation();
		}
	}

	public void onResume() {
		if(mMapView != null){
			mMapView.onResume();
		}
		if(mLocationManager != null){
			mLocationManager.startLocation();
		}
	}

	public void onStop() {
		if(mMapView != null){
			
		}
	}
	public void setOnMapClickListener(OnMapClickListener listener){
		synchronized (mOnMapClickListeners) {
			for (WeakReference<OnMapClickListener> weakRef : mOnMapClickListeners) {
				if (weakRef != null
						&& weakRef.get() == listener) {
					return;
				}
			}
			mOnMapClickListeners.add(new WeakReference<OnMapClickListener>(listener));
		}
	}

	public void removeOnMapClickListener(OnMapClickListener listener) {
		synchronized (mOnMapClickListeners) {
			WeakReference<OnMapClickListener> weakRef;
			for (int idx = 0; idx < mOnMapClickListeners.size(); idx++) {
				if ((weakRef = mOnMapClickListeners.get(idx)) != null) {
					if (weakRef.get() == listener) {
						mOnMapClickListeners.remove(idx);
						return;
					}
				} else {
					mOnMapClickListeners.remove(idx);
					idx--;
				}
			}
		}
	}

	public void setOnMapStatusChangeListener(OnMapStatusChangeListener listener){
		synchronized (mOnMapStatusChangeListeners) {
			for (WeakReference<OnMapStatusChangeListener> weakRef : mOnMapStatusChangeListeners) {
				if (weakRef != null
						&& weakRef.get() == listener) {
					return;
				}
			}
			mOnMapStatusChangeListeners.add(new WeakReference<OnMapStatusChangeListener>(listener));
		}
	}

	public void removeOnMapTouchListener(OnMapStatusChangeListener listener) {
		synchronized (mOnMapStatusChangeListeners) {
			WeakReference<OnMapStatusChangeListener> weakRef;
			for (int idx = 0; idx < mOnMapStatusChangeListeners.size(); idx++) {
				if ((weakRef = mOnMapStatusChangeListeners.get(idx)) != null) {
					if (weakRef.get() == listener) {
						mOnMapStatusChangeListeners.remove(idx);
						return;
					}
				} else {
					mOnMapStatusChangeListeners.remove(idx);
					idx--;
				}
			}
		}
	}
	/**
	 * 重新定位
	 */
	 public void FixPositionAgain(){
		LatLng ll = new LatLng(mLocationManager.getCurrentPoint().getLat(),
				mLocationManager.getCurrentPoint().getLon());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mMapView.getMap().animateMapStatus(u);
	}
	/**
	 * 放大
	 */
	 public void zoomIn(){
		mMapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomIn());

	}
	/**
	 * 缩小
	 */
	 public void zoomOut(){
		mMapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomOut());
	 }
	 /**
	  * 将marker添加到list
	  * @param marker
	  */
	 public void setMarker(MarkerOptions markeropt,Marker marker){
		 list_markers_opt.add(markeropt);
		 list_markers.add(marker);
		
	 }
	 /**
	  * 获取markeroption的list
	  * @return
	  */
	 public ArrayList<MarkerOptions> getMarkersOpt(){
		 return list_markers_opt;
	 }
	 /**
	  * 获取marke的list
	  * @return
	  */
	 public ArrayList<Marker> getMarkers(){
		 return list_markers;
	 }

	 public interface OnSearchAddressListener{
		 void OnSuccess(String address,String admincode);
		 void onFailure();
	 }

	 public void searchAddress(final GeoPoint latLng, final OnSearchAddressListener listener){
		 if(latLng == null)
			 return;

		 GeoCoder gc = GeoCoder.newInstance();
		 gc.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

			 @Override
			 public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				 Log.i("chentao","onGetReverseGeoCodeResult:"+arg0.getAddress());

				 if(listener!=null  && arg0  != null && arg0.getAddressDetail() != null){
					 String province=arg0.getAddressDetail().province;
					 String city = arg0.getAddressDetail().city;
					 String district=arg0.getAddressDetail().district;
					 String street=arg0.getAddressDetail().street;
					 String streetNumber=arg0.getAddressDetail().streetNumber;
                     Log.i("chentao","province:"+province+",city:"+city+",district:"+district+",street:"+street+",streetNumber:"+streetNumber);
				     String admincode = province+city+district;
					 listener.OnSuccess(arg0.getAddress(),admincode);
				 }
			 }

			 @Override
			 public void onGetGeoCodeResult(GeoCodeResult arg0) {
				 Log.i("chentao","onGetGeoCodeResult:"+arg0.getAddress());

			 }
		 });

		 gc.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latLng.getLat(),latLng.getLon())));

	 }
	 
}
