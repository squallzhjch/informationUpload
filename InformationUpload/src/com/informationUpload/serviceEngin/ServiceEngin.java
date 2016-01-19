package com.informationUpload.serviceEngin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.widget.Toast;

import com.informationUpload.utils.NetUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;


/**
 * 公共网络请求类
 * 
 * @author chentao
 * 
 */
public class ServiceEngin {

	private static HttpUtils httputil = new HttpUtils();

	private static String RESULT;
	

//	private static String URL="http://172.23.44.11:8081/infor/information/";
	private static String URL="http://fs.navinfo.com/infor/information/";

	/**
	 * 异步请求方法,请自行在callback中处理返回结果(callbackde success中自行解析result)
	 * 

	 * @param context
	 *            当前界面上下文
	 * @param callback
	 *            重写的EngineCallBack回调函数
	 */
	public static void Request(final Context context,HashMap<String,Object> map,String serviceName,
			EnginCallback callback) {
		if (NetUtil.hasNetWork(context)) {

			// 参数拼接
			RequestParams params = new RequestParams();

			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				params.addBodyParameter(key,val);
			}
			//			
			// 请求超时
			httputil.configTimeout(1000 * 60);
			httputil.configSoTimeout(1000 * 60);
			// 发送请求
			httputil.send(HttpRequest.HttpMethod.POST, URL+serviceName+"/",
					params, callback);
		} else {
			Toast.makeText(context, "网络连接不可用", Toast.LENGTH_LONG).show();
			callback.onFailure(new HttpException("网络异常"), "网络连接不可用");

		}
	}
	


}
