package com.informationUpload.activity;

import com.informationUpload.R;

import com.umeng.analytics.MobclickAgent;

import com.umeng.update.UmengDialogButtonListener;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;


public class WelcomeActivity extends Activity{
	private final String mPageName = "WelcomeActivity";
	private WelcomeActivity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomepage);

		mContext=this;
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


}
