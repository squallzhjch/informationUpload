package com.informationUpload.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.location.LocationClientOption.*;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: LocationManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class LocationManager  implements BDLocationListener{

    private static volatile GeoPoint mCurrentLocation;
    public LocationClient mLocationClient = null;
    private Context mContext;
//
    private final List<WeakReference<OnLocationListener>> mOnLocationListeners = new ArrayList<WeakReference<OnLocationListener>>();
//
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
        mLocationClient = new LocationClient(context);     //声明LocationClient类
        mLocationClient.registerLocationListener(this);
        initLocation();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(mCurrentLocation == null){
            mCurrentLocation = new GeoPoint();
        }
        
        
        
        
        
        synchronized (mCurrentLocation) {
        	
        	
        	
            mCurrentLocation.setLat(bdLocation.getLatitude());
            mCurrentLocation.setLon(bdLocation.getLongitude());

            for (WeakReference<OnLocationListener> weakRef : mOnLocationListeners) {
                if (weakRef.get() != null) {
                    weakRef.get().onLocationChanged(mCurrentLocation, bdLocation.getAddrStr(),bdLocation.getRadius());
                }
            }
        }
    }
    
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public interface OnLocationListener {
        void onLocationChanged(GeoPoint location, String address,float radius);
    }

    public void addOnLocationListener(OnLocationListener listener) {
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
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    public void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.start();
        }

    }

    public void onDestroy() {
        stopLocation();
        mLocationClient.unRegisterLocationListener(this);
        mOnLocationListeners.clear();
    }

    public GeoPoint getCurrentPoint(){
        if(mCurrentLocation != null){
        }else{
            mCurrentLocation = new GeoPoint();
        }
        return mCurrentLocation;
    }
}
