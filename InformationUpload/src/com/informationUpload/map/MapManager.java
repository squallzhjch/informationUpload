package com.informationUpload.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.informationUpload.R;
import com.informationUpload.map.Overlay.LocationOverlay;
import com.informationUpload.utils.Utils;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.tencentmap.mapsdk.map.MapView;

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
        mLocationManager = LocationManager.getInstance(mContext);
        mLocationManager.startLocation();

        mLocationManager.setOnLocationListener(listener);
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
}
