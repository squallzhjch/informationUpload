package com.informationUpload.serviceEngin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


public class EnginCallback extends RequestCallBack {


	private Context context;
	private boolean bLoading = true;

	/**
	 * 构造函数,为公共进度条重写此回调
	 */
	public EnginCallback(Context context, boolean bLoading) {
		this.context = context;
		this.bLoading = bLoading;
	}

	public EnginCallback(Context context) {
		this.context = context;
	}
	@Override
	public void onFailure(HttpException arg0, String arg1) {
		// TODO Auto-generated method stub
		ServiceEngin.getInstance().canclDialog();
		Log.e("失败异常", "HttpException:" + arg0 + "返回值" + arg1);
		if(!"网络连接不可用".equals(arg1))
			Toast.makeText(context, "无法连接服务器!!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSuccess(ResponseInfo arg0) {
		ServiceEngin.getInstance().canclDialog();
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(bLoading) {
			ServiceEngin.getInstance().startDialog();
		}
	}

}
