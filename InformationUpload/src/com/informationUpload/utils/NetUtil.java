package com.informationUpload.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络管理工具
 * 
 * @author user
 * 
 */
public class NetUtil {

	public static boolean hasNetWork(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnectivity != null) {
			NetworkInfo info = mConnectivity.getActiveNetworkInfo();
			if(info != null){
				return info.isAvailable();
			}
		}
		return false;
	}
	
    public static boolean isWiFiActive(Context inContext) {   
        Context context = inContext.getApplicationContext();   
        ConnectivityManager connectivity = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (connectivity != null) {   
            NetworkInfo[] info = connectivity.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    }  

}
