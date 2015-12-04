package com.informationUpload.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.informationUpload.R;
import com.informationUpload.map.Overlay.LocationOverlay;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.tencentmap.mapsdk.map.MapView;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MapManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MapManager {

    private TencentLocation mCurrentLocation;
    private LocationOverlay mLocationOverlay;
    private TencentLocationManager mLocationManager;


    private MapView mMapView;
    private Context mContext;

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
        mMapView.getMap().setZoom(13);
        initLocation();
    }

    private void initLocation(){
        mLocationManager = TencentLocationManager.getInstance(mContext);
        Bitmap bmpMarker = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.mark_location);
        mLocationOverlay = new LocationOverlay(bmpMarker);
        mMapView.addOverlay(mLocationOverlay);
    }



    public void onDestroy(){
        if(mMapView != null){
            mMapView.onDestroy();
            mMapView = null;
        }
    }

    public void onPause(){
        if(mMapView != null){
            mMapView.onPause();
        }
//        stopLocation();
    }

//    private void stopLocation() {
//        if(mLocationManager != null) {
//            mLocationManager.removeUpdates(this);
//        }
//    }
}
