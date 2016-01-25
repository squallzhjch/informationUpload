package com.informationUpload.activity;

import com.informationUpload.R;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomepage);

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
}
