package com.informationUpload.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
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
    private MapView mMapView;
    private Context mContext;
    private Boolean bFirst = true;
    private LocationManager mLocationManager;
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

    public void init(Context context, MapView mapView){
        if(mapView == null || context == null)
            return;
        mContext = context;
        mMapView = mapView;
        mMapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomTo(15f));
        mLocationManager = LocationManager.getInstance();
        mLocationManager.startLocation();

        mLocationManager.addOnLocationListener(listener);

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
    }

    private  OnLocationListener listener = new OnLocationListener() {
        @Override
        public void onLocationChanged(GeoPoint location, String address) {
            // 更新 location 图层
            mMapView.getMap().setMyLocationConfigeration(new MyLocationConfiguration(
                    LocationMode.NORMAL, true, null));
            bFirst = false;
            mMapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(location.getLat(), location.getLon())));
        }
    };


    public void onDestroy(){
        if(mMapView != null){
            mMapView.onDestroy();
            mMapView = null;
        }
        if(mLocationManager != null){
            mLocationManager.onDestroy();
        }

        if(mOnMapClickListeners != null){
            mOnMapClickListeners.clear();
        }
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

    public interface OnSearchAddressListener{
        void OnSuccess(String address);
        void onFailure();
    }

    public void searchAddress(final GeoPoint latLng, final OnSearchAddressListener listener){
        if(latLng == null)
            return;
//        TencentSearch api = new TencentSearch(mContext);
//        Geo2AddressParam param = new Geo2AddressParam().location(new Location()
//                .lat((float) latLng.getLat()).lng((float) latLng.getLon()));
//        api.geo2address(param, new HttpResponseListener() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, BaseObject object) {
//                // TODO Auto-generated method stub
//                String result = "";
//                if(object != null){
//                    Geo2AddressResultObject oj = (Geo2AddressResultObject)object;
//                    if(oj.result != null){
//                        result = oj.result.address;
//                    }
//                }
//                if(listener != null){
//                    listener.OnSuccess(result);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers,
//                                  String responseString, Throwable throwable) {
//                if(listener != null){
//                    listener.onFailure();
//                }
//            }
//        });
    }
}
