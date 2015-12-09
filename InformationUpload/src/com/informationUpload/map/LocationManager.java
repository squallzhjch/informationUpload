package com.informationUpload.map;

import android.content.Context;
import android.util.Log;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: LocationManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class LocationManager implements TencentLocationListener {

    private TencentLocation mCurrentLocation;
    private TencentLocationManager mLocationManager;
    private Context mContext;

    private final List<WeakReference<OnLocationListener>> mOnLocationListeners = new ArrayList<WeakReference<OnLocationListener>>();

    private static volatile LocationManager mInstance;

    public static LocationManager getInstance() {
        if (mInstance == null) {
            synchronized (LocationManager.class) {
                if (mInstance == null) {
                    mInstance = new LocationManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext = context;
        mLocationManager = TencentLocationManager.getInstance(mContext);
    }

    public interface OnLocationListener {
        void onLocationChanged(TencentLocation location, int error, String s);
//        void onStatusUpdate(String s, int i, String s1);
    }


    public void setOnLocationListener(OnLocationListener listener) {
        synchronized (mOnLocationListeners) {
            for (WeakReference<OnLocationListener> weakRef : mOnLocationListeners) {
                if (weakRef != null
                        && weakRef.get() == listener) {
                    return;
                }
            }
            mOnLocationListeners.add(new WeakReference<OnLocationListener>(listener));
        }
    }

    public void removeOnLocationListener(OnLocationListener listener) {
        synchronized (mOnLocationListeners) {
            WeakReference<OnLocationListener> weakRef;
            for (int idx = 0; idx < mOnLocationListeners.size(); idx++) {
                if ((weakRef = mOnLocationListeners.get(idx)) != null) {
                    if (weakRef.get() == listener) {
                        mOnLocationListeners.remove(idx);
                        return;
                    }
                } else {
                    mOnLocationListeners.remove(idx);
                    idx--;
                }
            }
        }
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String s) {

        if (error == TencentLocation.ERROR_OK) {
            mCurrentLocation = location;
            for (WeakReference<OnLocationListener> weakRef : mOnLocationListeners) {
                if (weakRef.get() != null) {
                    weakRef.get().onLocationChanged(location, error, s);
                }
            }
//            // 定位成功
//            StringBuilder sb = new StringBuilder();
//            sb.append("定位参数=").append(mRequestParams).append("\n");
//            sb.append("(纬度=").append(location.getLatitude()).append(",经度=")
//                    .append(location.getLongitude()).append(",精度=")
//                    .append(location.getAccuracy()).append("), 来源=")
//                    .append(location.getProvider()).append(", 地址=")
//                    .append(location.getAddress());
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    public void stopLocation() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    public void startLocation() {
        if (mLocationManager != null) {
            TencentLocationRequest request = TencentLocationRequest.create();
            request.setInterval(5000);
            int error = mLocationManager.requestLocationUpdates(request, this);
            error ++;
        }

//        mRequestParams = request.toString() + ", 坐标系="
//                + DemoUtils.toString(mLocationManager.getCoordinateType());
    }

    public void onDestroy() {
        stopLocation();
        mOnLocationListeners.clear();
    }

    public GeoPoint getCurrentPoint(){
        GeoPoint point = new GeoPoint();
        if(mCurrentLocation != null){
            point.setLat(mCurrentLocation.getLatitude());
            point.setLon(mCurrentLocation.getLongitude());
        }
        return point;
    }
}
