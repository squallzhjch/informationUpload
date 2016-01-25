package com.informationUpload.activity;

import com.informationUpload.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class WelcomeActivity extends Activity{
	private final String mPageName = "WelcomeActivity";
	private WelcomeActivity mContext;
    private ImageView wel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomepage);
		mContext=this;
		MobclickAgent.setDebugMode(true);
		// SDK在统计Fragment时，需要关闭Activity自带的页面统计，
		// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
		MobclickAgent.openActivityDurationTrack(false);
		
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				WelcomeActivity.this.finish();
			}
		}, 2000);
		wel.setBackgroundResource(R.drawable.ic_launcher);

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
