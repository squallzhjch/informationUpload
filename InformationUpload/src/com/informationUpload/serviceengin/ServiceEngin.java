package com.informationUpload.serviceengin;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.utils.NetUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest;


/**
 * 公共网络请求类
 * 
 * @author chentao
 * 
 */
public class ServiceEngin {

	private static HttpUtils httputil = new HttpUtils();
	// private static String LOCAL_HOST = Constants.PROTOCOL + Constants.HOST
	// + ":" + Constants.PORT + "/";
	// /** 回归环境 **/
	// private static String url = LOCAL_HOST
	// + "regressionmobileservice/ServiceEngin.do";
	// /** 生产环境 **/
	// private static String url = LOCAL_HOST
	// + "ncihmobileservice/ServiceEngin.do";
	// private static String
	// url="http://10.8.51.59:9090/ncihmobileservice/ServiceEngin.do";
	private static String RESULT;

    private static String URL="http://220.181.111.188:80";
	/**
	 * 异步请求方法,请自行在callback中处理返回结果(callbackde success中自行解析result)
	 * 
	 * @param bizId
	 *            业务场景编码
	 * @param serviceName
	 *            方法名
	 * @param servicePara
	 *            参数集合
	 * @param context
	 *            当前界面上下文
	 * @param callback
	 *            重写的EngineCallBack回调函数
	 */
	public static void Request(final Context context, String bizId,
			String serviceName, String servicePara, EnginCallback callback) {
		if (NetUtil.hasNetWork(context)) {
		
			// 参数拼接
			RequestParams params = new RequestParams();
			params.addBodyParameter("keyVer", "v1");
			params.addBodyParameter("appID", "1");
			EnginBean eb = new EnginBean();
			eb.setBizId(bizId);
			String deviceId = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			eb.setDeviceId(deviceId);
			eb.setClientOS("1");
		
			eb.setServiceName(serviceName);
			eb.setServicePara(servicePara);
			String para = JSON.toJSON(eb) + "";
			try {
				para = Des3.encode(para);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				RESULT = "请求加密失败";
			}
			params.addBodyParameter("para", para);
			// 请求超时
			httputil.configTimeout(1000 * 60);
			httputil.configSoTimeout(1000 * 60);
			// 发送请求
			httputil.send(HttpRequest.HttpMethod.POST, URL,
					params, callback);
		} else {
			Toast.makeText(context, "网络连接不可用", Toast.LENGTH_LONG).show();
			callback.onFailure(new HttpException("网络异常"), "网络连接不可用");

		}
	}

	/**
	 * 同步请求方法,自动返回解密后的json类型数据(result解密后自动解析)
	 * 
	 * @param bizId
	 *            业务场景编码
	 * @param serviceName
	 *            方法名
	 * @param servicePara
	 *            参数集合
	 * @param context
	 *            当前界面上下文
	 * 
	 * @return 已解密json类型数据
	 */
	public static String Request(final Context context, String bizId,
			String serviceName, String servicePara) {

		
		String result = "";
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		// 参数拼接
		RequestParams params = new RequestParams();
		params.addBodyParameter("keyVer", "v1");
		params.addBodyParameter("appID", "1");
		EnginBean eb = new EnginBean();
		eb.setBizId(bizId);
		String deviceId = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		// eb.setDeviceId(deviceId);
		eb.setDeviceId(deviceId);
		eb.setClientOS("1");
		
		eb.setServiceName(serviceName);
		eb.setServicePara(servicePara);
		String para = JSON.toJSON(eb) + "";
		try {
			para = Des3.encode(para);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			RESULT = "请求加密失败";
		}
		params.addBodyParameter("para", para);

		// 非xutils请求
		// try {
		// // 封装请求参数
		// List<NameValuePair> pair = new ArrayList<NameValuePair>();
		// pair.add(new BasicNameValuePair("keyVer", "v1"));
		// pair.add(new BasicNameValuePair("appID", "1"));
//		 pair.add(new BasicNameValuePair("para", para));
		// // 把请求参数变成请求体部分
		// UrlEncodedFormEntity uee = new UrlEncodedFormEntity(pair, "utf-8");
		// // 使用HttpPost对象设置发送的URL路径
		// HttpPost post = new HttpPost(url);
		// post.setHeader("Accept-Encoding", "identity");
		// // 发送请求体
		// post.setEntity(uee);
		// // 创建一个浏览器对象，以把POST对象向服务器发送，并返回响应消息
		// DefaultHttpClient dhc = new DefaultHttpClient();
		// HttpResponse response = dhc.execute(post);
		// if (response.getStatusLine().getStatusCode() == 200) {
		// HttpEntity entity = response.getEntity();
		// String content = EntityUtils.toString(entity, "gbk");
		// if (content != null) {
		// result = Des3.decode(content);
		// }
		// }
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// // 请求超时
		httputil.configTimeout(1000 * 60);
		httputil.configSoTimeout(1000 * 60);
		BufferedReader in = null;
		try {
			ResponseStream receiveStream = httputil.sendSync(
					HttpRequest.HttpMethod.POST, URL, params);
			in = new BufferedReader(new InputStreamReader(receiveStream));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			result = Des3.decode(result);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		// 发送请求
		return result;
	}

	/**
	 * 同步请求方法,自动返回解密后的json类型数据(result解密后自动解析)
	 * 
	 * @param bizId
	 *            业务场景编码
	 * @param serviceName
	 *            方法名
	 * @param servicePara
	 *            参数集合
	 * @param context
	 *            当前界面上下文
	 * @param URL
	 *            请求地址
	 * @return 已解密json类型数据
	 */
	public static String Request(final FragmentActivity context, String bizId,
			String serviceName, String servicePara, String URL) {

		
		String result = "";
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		// 参数拼接
		RequestParams params = new RequestParams();
		params.addBodyParameter("keyVer", "v1");
		params.addBodyParameter("appID", "1");
		EnginBean eb = new EnginBean();
		eb.setBizId(bizId);
		String deviceId = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		eb.setDeviceId(deviceId);
		eb.setClientOS("1");
		
		eb.setServiceName(serviceName);
		eb.setServicePara(servicePara);
		String para = JSON.toJSON(eb) + "";
		try {
			para = Des3.encode(para);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			RESULT = "请求加密失败";
		}
		params.addBodyParameter("para", para);
		// 请求超时
		httputil.configTimeout(1000 * 60);
		httputil.configSoTimeout(1000 * 60);
		BufferedReader in = null;
		try {
			ResponseStream receiveStream = httputil.sendSync(
					HttpRequest.HttpMethod.POST, URL, params);
			in = new BufferedReader(new InputStreamReader(receiveStream));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			result = Des3.decode(result);
			JSONObject jo = JSONObject.parseObject(result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		// 发送请求
		return result;
	}

	/**
	 * 异步请求方法,请自行在callback中处理返回结果(callbackde success中自行解析result)
	 * 
	 * @param bizId
	 *            业务场景编码
	 * @param serviceName
	 *            方法名
	 * @param servicePara
	 *            参数集合
	 * @param context
	 *            当前界面上下文
	 * @param callback
	 *            重写的EngineCallBack回调函数
	 * @param URL
	 *            请求的地址
	 */
	public static void Request(final Context context, String bizId,
			String serviceName, String servicePara, EnginCallback callback,
			String URL) {
		if (NetUtil.hasNetWork(context)) {
			
			// 参数拼接
			RequestParams params = new RequestParams();
			params.addBodyParameter("keyVer", "v1");
			params.addBodyParameter("appID", "1");
			EnginBean eb = new EnginBean();
			eb.setBizId(bizId);
			String deviceId = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			eb.setDeviceId(deviceId);
			eb.setClientOS("1");
		
			eb.setServiceName(serviceName);
			eb.setServicePara(servicePara);
			String para = JSON.toJSON(eb) + "";
			try {
				para = Des3.encode(para);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				RESULT = "请求加密失败";
			}
			params.addBodyParameter("para", para);
			// 请求超时
			httputil.configTimeout(1000 * 60);
			httputil.configSoTimeout(1000 * 60);
			// 发送请求
			httputil.send(HttpRequest.HttpMethod.POST, URL, params, callback);
		} else {
			Toast.makeText(context, "网络连接不可用", Toast.LENGTH_LONG).show();

		}
	}
}
