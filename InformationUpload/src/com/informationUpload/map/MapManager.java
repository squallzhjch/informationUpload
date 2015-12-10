package com.informationUpload.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.informationUpload.R;
import com.informationUpload.map.Overlay.LocationOverlay;
import com.informationUpload.utils.Utils;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapClickListener;

import org.apache.http.Header;

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
    private LocationOverlay mLocationOverlay;
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
        mMapView.getMap().setZoom(15);
        addLocationOverlay();
        mLocationManager = LocationManager.getInstance();
        mLocationManager.startLocation();

        mLocationManager.setOnLocationListener(listener);

        mMapView.getMap().setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for (WeakReference<OnMapClickListener> weakRef : mOnMapClickListeners) {
                    if (weakRef.get() != null) {
                        weakRef.get().onMapClick(latLng);
                    }
                }
            }
        });
    }

    private  LocationManager.OnLocationListener listener = new LocationManager.OnLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation location, int error, String s) {
            // 更新 location 图层
            mLocationOverlay.setAccuracy(location.getAccuracy());
            mLocationOverlay.setGeoCoords(Utils.of(location));
            if(bFirst == true){
                mMapView.getMap().animateTo(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            mMapView.invalidate();
            bFirst = false;
        }
    };

    private void addLocationOverlay(){
        Bitmap bmpMarker = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.mark_location);
        if(bmpMarker != null) {
            mLocationOverlay = new LocationOverlay(bmpMarker);
            mMapView.addOverlay(mLocationOverlay);
        }
    }

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
            mMapView.onStop();
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
        TencentSearch api = new TencentSearch(mContext);
        Geo2AddressParam param = new Geo2AddressParam().location(new Location()
                .lat((float) latLng.getLat()).lng((float) latLng.getLon()));
        api.geo2address(param, new HttpResponseListener() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, BaseObject object) {
                // TODO Auto-generated method stub
                String result = "";
                if(object != null){
                    Geo2AddressResultObject oj = (Geo2AddressResultObject)object;
                    if(oj.result != null){
                        result = oj.result.address;
                    }
                }
                if(listener != null){
                    listener.OnSuccess(result);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if(listener != null){
                    listener.onFailure();
                }
            }
        });
    }
}
