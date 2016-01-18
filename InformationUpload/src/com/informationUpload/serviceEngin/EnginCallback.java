package com.informationUpload.serviceEngin;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


public class EnginCallback extends RequestCallBack {

	private ProgressDialog pd;
	private Context context;
	

	/**
	 * 构造函数,为公共进度条重写此回调
	 */
	public EnginCallback(Context context) {
		this.context = context;
	}

	@Override
	public void onFailure(HttpException arg0, String arg1) {
		// TODO Auto-generated method stub
		canclDialog();
		Log.e("失败异常", "HttpException:" + arg0 + "返回值" + arg1);
		if(!"网络连接不可用".equals(arg1))
			Toast.makeText(context, "无法连接服务器!!", Toast.LENGTH_SHORT).show();
		// ((Activity) context).finish();
	}

	@Override
	public void onSuccess(ResponseInfo arg0) {
		canclDialog();

	}

	/**
	 * 取消请求对话框
	 */
	public void canclDialog() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (pd == null && context != null) {
			pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("正在请求服务器...");
			pd.setCancelable(false);
			pd.show();
		}
	}

}
