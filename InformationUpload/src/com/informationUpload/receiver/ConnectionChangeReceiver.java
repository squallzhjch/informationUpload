package com.informationUpload.receiver;





import com.informationUpload.system.SystemConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	
	
	public ConChangeCallBack conChangeCallBack;

	protected SharedPreferences mSharePre;
	private Editor editor;
     
	public ConChangeCallBack getConChangeCallBack() {
		return conChangeCallBack;
	}

	public void setConChangeCallBack(ConChangeCallBack conChangeCallBack) {
		this.conChangeCallBack = conChangeCallBack;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		mSharePre = context.getSharedPreferences(SystemConfig.NET_WORK_STATE,
				Context.MODE_PRIVATE);
		editor = mSharePre.edit();
		
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (activeNetInfo != null) {
			//MyToast.showToast(context,"网络类型 : " + activeNetInfo.getTypeName(),Toast.LENGTH_SHORT);
			//Toast.makeText(context,"网络类型 : " + activeNetInfo.getTypeName(),Toast.LENGTH_SHORT).show();
			
			editor.putInt("netStatus",0);
			editor.commit();
			
			if(conChangeCallBack!=null){
				conChangeCallBack.connectionStatus(0);
			}
		}
		Log.i("ConnectionChangeReceiver", intent.getAction()+"====");
		if (mobNetInfo != null) {
/*			Toast.makeText(context,
					"Mobile Network Type : " + mobNetInfo.getTypeName(),
					Toast.LENGTH_SHORT).show();*/
		}
		
		if(activeNetInfo==null||mobNetInfo==null){
			Toast.makeText(context,"没有可用网络！", Toast.LENGTH_SHORT).show();
		
			if(conChangeCallBack!=null){
				conChangeCallBack.connectionStatus(-1);
			}
			
			editor.putInt("netStatus",-1);
			editor.commit();
			
		}
	}
	
	
	public interface ConChangeCallBack{
		
		public abstract void connectionStatus(int status);
		
		}
}