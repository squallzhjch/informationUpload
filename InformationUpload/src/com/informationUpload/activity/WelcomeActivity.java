package com.informationUpload.activity;

import com.informationUpload.R;

import com.umeng.analytics.MobclickAgent;

import com.umeng.update.UmengDialogButtonListener;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;


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






		UmengUpdateAgent.update(this);
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
		});
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


}
