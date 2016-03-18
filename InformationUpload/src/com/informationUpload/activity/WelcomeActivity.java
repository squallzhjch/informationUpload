package com.informationUpload.activity;

import com.informationUpload.R;

import com.umeng.analytics.MobclickAgent;

import com.umeng.update.UmengDialogButtonListener;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class WelcomeActivity extends Activity{
	private final String mPageName = "WelcomeActivity";
	private WelcomeActivity mContext;
	/**
	 * 版本号
	 */
	private TextView mTvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomepage);
		mTvVersion=(TextView)findViewById(R.id.tv_version);
		mContext=this;
		mTvVersion.setText("版本号:"+getVersionName(mContext));
		
		if(!isOPen(mContext)){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("请打开GPS");
			dialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							// 转到手机设置界面，用户设置GPS
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

						}
					});
			dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
	                Toast.makeText(mContext,"对不起！您没有打开gps不能使用该应用，请重新进入并打开gps！", Toast.LENGTH_SHORT).show();
	                mContext.finish();
				}

			
			} );
			dialog.show();
		}else{
	    	MobclickAgent.setDebugMode(true);
			// SDK在统计Fragment时，需要关闭Activity自带的页面统计，
			// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
			MobclickAgent.openActivityDurationTrack(false);
			UmengUpdateAgent.setUpdateOnlyWifi(false);
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				@Override
				public void onUpdateReturned(int i, UpdateResponse updateResponse) {
					switch (i) {
					case UpdateStatus.Yes:
						UmengUpdateAgent.showUpdateDialog(WelcomeActivity.this, updateResponse);
						break;
					default:
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
								WelcomeActivity.this.finish();
							}
						}, 1000);
						break;
					}
				}

			});

			UmengUpdateAgent.update(mContext);
			UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
				@Override
				public void onClick(int i) {
					switch (i) {
					case UpdateStatus.Update:
						break;
					default:
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
								WelcomeActivity.this.finish();
							}
						}, 1000);
						break;
					}
				}
			});;
		}
		
		
	
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);

	}
	//版本名
	public static String getVersionName(Context context) {
		return getPackageInfo(context).versionName;
	}

	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;

		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);

			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pi;
	}
	
	  /** 
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的 
     * @param context 
     * @return true 表示开启 
     */  
    public static final boolean isOPen(final Context context) {  
        LocationManager locationManager   
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
//        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        if (gps) {  
            return true;  
        }  
  
        return false;  
    }  
    
    /** 
     * 强制帮用户打开GPS 
     * @param context 
     */  
    public static final void openGPS(Context context) {  
        Intent GPSIntent = new Intent();  
        GPSIntent.setClassName("com.android.settings",  
                "com.android.settings.widget.SettingsAppWidgetProvider");  
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");  
        GPSIntent.setData(Uri.parse("custom:3"));  
        try {  
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();  
        } catch (CanceledException e) {  
            e.printStackTrace();  
        }  
    }  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(!isOPen(mContext)){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("请打开GPS");
			dialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							// 转到手机设置界面，用户设置GPS
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

						}
					});
			dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
	                Toast.makeText(mContext,"对不起！您没有打开gps不能使用该应用，请重新进入并打开gps！", Toast.LENGTH_SHORT).show();
	                mContext.finish();
				}

			
			} );
			dialog.show();
    	}else{
    	  	MobclickAgent.setDebugMode(true);
    		// SDK在统计Fragment时，需要关闭Activity自带的页面统计，
    		// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
    		MobclickAgent.openActivityDurationTrack(false);
    		UmengUpdateAgent.setUpdateOnlyWifi(false);
    		UmengUpdateAgent.setUpdateAutoPopup(false);
    		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
    			@Override
    			public void onUpdateReturned(int i, UpdateResponse updateResponse) {
    				switch (i) {
    				case UpdateStatus.Yes:
    					UmengUpdateAgent.showUpdateDialog(WelcomeActivity.this, updateResponse);
    					break;
    				default:
    					new Handler().postDelayed(new Runnable() {

    						@Override
    						public void run() {
    							startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
    							WelcomeActivity.this.finish();
    						}
    					}, 1000);
    					break;
    				}
    			}

    		});

    		UmengUpdateAgent.update(mContext);
    		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
    			@Override
    			public void onClick(int i) {
    				switch (i) {
    				case UpdateStatus.Update:
    					break;
    				default:
    					new Handler().postDelayed(new Runnable() {

    						@Override
    						public void run() {
    							startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
    							WelcomeActivity.this.finish();
    						}
    					}, 1000);
    					break;
    				}
    			}
    		});;	
    	}
  
    }


}
