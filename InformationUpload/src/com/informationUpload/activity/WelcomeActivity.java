package com.informationUpload.activity;

import com.informationUpload.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;

public class WelcomeActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomepage);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				WelcomeActivity.this.finish();
			}
		}, 2000);

	}
}
