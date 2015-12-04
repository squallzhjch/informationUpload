package com.informationUpload.map;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: LocationManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class LocationManager  implements TencentLocationListener {

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

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {

    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
}
