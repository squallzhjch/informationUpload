package com.informationUpload.serviceEngin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.informationUpload.utils.NetUtil;
import com.informationUpload.system.SystemConfig;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.commons.logging.Log;


/**
 * 公共网络请求类
 * 
 * @author chentao
 * 
 */
public class ServiceEngin {
	public final static int ZIP_SUCCESS=0;
	public final static int ZIP_FAILURE=1;

	public final static int UPLOAD_SUCCESS=2;
	public final static int UPLOAD_FAILURE=3;
	private final static HttpUtils httputil = new HttpUtils();
	private ProgressDialog pd;
	private Context mContext;

	private static volatile ServiceEngin mInstance;
	public static ServiceEngin getInstance() {
		if (mInstance == null) {
			synchronized (ServiceEngin.class) {
				if (mInstance == null) {
					mInstance = new ServiceEngin();
				}
			}
		}
		return mInstance;
	}

	public void init(Context context){
		mContext = context;
	}
	/**
	 * 取消请求对话框
	 */
	public boolean canclDialog() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 取消请求对话框
	 */
	public void startDialog() {
		if (pd == null && mContext != null) {
			pd = new ProgressDialog(mContext);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("正在请求服务器...");
		}
		pd.show();
	}

	/**
	 * 异步请求方法,请自行在callback中处理返回结果(callbackde success中自行解析result)
	 * 

	 * @param context
	 *            当前界面上下文
	 * @param callback
	 *            重写的EngineCallBack回调函数
	 */
	public void Request(final Context context,HashMap<String,Object> map,String serviceName,
			EnginCallback callback) {
		if (NetUtil.hasNetWork(context)) {

			// 参数拼接
			RequestParams params = new RequestParams();
			String url = SystemConfig.REQUEST_URL +serviceName + "?";
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				params.addBodyParameter(key,val);
				url += "&"+key+"="+val;
			}
			android.util.Log.e("url", url);
			// 请求超时
			httputil.configTimeout(1000 * 20);
			httputil.configSoTimeout(1000 * 20);
			// 发送请求



			httputil.send(HttpRequest.HttpMethod.POST, SystemConfig.REQUEST_URL +serviceName+"/",
					params, callback);
		} else {
			Toast.makeText(context, "网络连接不可用", Toast.LENGTH_LONG).show();
			callback.onFailure(new HttpException("网络异常"), "网络连接不可用");

		}
	}

	/**
	 * 成功码
	 */
	public static final String SUCCESS = "1";
	/**
	 * 失败码
	 */
	public static final String FAILURE = "0";
	/**
	 *  设置编码
	 */
	private static final String CHARSET = "utf-8";
	/**
	 * 上传超时
	 */
	private static final int TIME_OUT = 10 * 10000000;
	/**
	 * 上传文件
	 * @param file
	 * @return
	 */

	public  String uploadFile(File file, Handler handler) {
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
		// 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(SystemConfig.REQUEST_URL + "inforimp/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			if (file != null) {
				System.out.println("----------" + file);
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				OutputStream outputSteam = conn.getOutputStream();

				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"filenamepath\"; filename=\"" + file.getName() + "\"" + LINE_END);
				System.out.println("----------" + file.getName());
				sb.append("Content-Type: application/octet-stream;charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				// BufferedReader br = new BufferedReader(new
				// InputStreamReader(conn.getInputStream()));
				// JSONObject jsonObject =
				// JSONObject.parseObject(br.toString());
				// String resultMsg = (String)
				// jsonObject.get("ResultMsg");
				// System.out.println("resultMsg*******"+resultMsg);
				System.out.println("res----------------" + res);
				if (res == 200) {
					handler.sendEmptyMessage(UPLOAD_SUCCESS);
					return SUCCESS;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(UPLOAD_FAILURE);
		return FAILURE;
	}

}
