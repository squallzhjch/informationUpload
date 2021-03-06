package com.informationUpload.serviceEngin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.informationUpload.utils.NetUtil;
import com.informationUpload.system.SystemConfig;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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
	public final static int DOWNLOAD_SUCCESS=4;
	public final static int START_UPLOAD=5;
	public final static int START_DOWNLOAD=6;
	public final static int DOWNLOAD_FAIL=7;
	
	public final static int DOWNLOAD_CHAT_SUCCESS=8;
	public final static int DOWNLOAD_PIC_SUCCESS=9;

	private final static HttpUtils httputil = new HttpUtils();
	private ProgressDialog pd;
	private Context mContext;
	/**
	 * 返回的结果
	 */
	private String result;

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
			android.util.Log.i("url", url);
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
	 * 存放头像文件夹的名字
	 * @param picname
	 * 
	 * @return
	 * 
	 */

	public  String uploadFile(File file, Handler handler,String picname) {

		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
		// 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {


			URL url = new URL(SystemConfig.REQUEST_URL + picname+"/");

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

				sb.append("Content-Disposition: form-data; name=\"filenamepath\";  filename=\"" + file.getName() +".jpg"+ "\"" + LINE_END);
				System.out.println("----------" + file.getName());
				//				sb.append("Content-Type: application/octet-stream;charset=" + CHARSET + LINE_END);
				sb.append("Content-Type: application/octet-stream" + LINE_END);
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
				if (res == 200) {
					InputStream input = conn.getInputStream();
					StringBuilder sb1 = new StringBuilder();

					byte[] buffer = new byte[1024];
					int len1;
					while ((len1 = input.read(buffer)) != -1) {
						sb1.append(new String(buffer, 0, len1, "UTF-8"));
					}

					result = sb1.toString();
				} else {
					result = "网络请求返回：" + res;
				}

				String resMsg = conn.getResponseMessage();



				android.util.Log.e("url", url.toString());
				if (res == 200) {
					handler.sendEmptyMessage(UPLOAD_SUCCESS);
					return SUCCESS;
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = "网络请求失败:" + e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络请求失败:" + e.getMessage();
		}
		handler.sendEmptyMessage(UPLOAD_FAILURE);
		return FAILURE;
	}


	/**
	 * 上传文件
	 * @param file
	 * 存放头像文件夹的名字
	 * @param picname
	 * 
	 * @return
	 * 
	 */

	public  String uploadFile_head(File file, Handler handler,String picname) {
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
		// 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(SystemConfig.REQUEST_URL + picname+"/");
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

				sb.append("Content-Disposition: form-data; name=\"headfile\"; filename=\"" + file.getName() +".jpg"+ "\"" + LINE_END);
				System.out.println("----------" + file.getName());
				//				sb.append("Content-Type: application/octet-stream;charset=" + CHARSET + LINE_END);
				sb.append("Content-Type: application/octet-stream" + LINE_END);
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
				String resMsg = conn.getResponseMessage();
				if (res == 200) {
					InputStream input = conn.getInputStream();
					StringBuilder sb1 = new StringBuilder();

					byte[] buffer = new byte[1024];
					int len1;
					while ((len1 = input.read(buffer)) != -1) {
						sb1.append(new String(buffer, 0, len1, "UTF-8"));
					}

					result = sb1.toString();
				} else {
					result = "网络请求返回：" + res;
				}




				android.util.Log.e("url", url.toString());
				if (res == 200) {
					Message msg = Message.obtain();
					msg.what=UPLOAD_SUCCESS;
					msg.obj=result;
					handler.sendMessage(msg);
					return SUCCESS;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = "网络请求失败:" + e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			result = "网络请求失败:" + e.getMessage();
		}
		Message msg = Message.obtain();
		msg.what=UPLOAD_FAILURE;
		msg.obj=result;
		handler.sendMessage(msg);

		return FAILURE;

	}
	/**
	 * 下载图片
	 * Get image from newwork
	 * @param path The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public void getImageStream(String path,final Handler handler) throws Exception{

		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.setRequestMethod("GET");

		if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

			Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
			final Message msg = Message.obtain();
			msg.what=ServiceEngin.DOWNLOAD_SUCCESS;
			msg.obj=bitmap;
			handler.sendMessage(msg);

		}else{
			handler.sendEmptyMessage(ServiceEngin.DOWNLOAD_FAIL);

		}

	}

	/**
	 * 下载文件
	 * Get image from newwork
	 * @param path The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public void getFileStream(String path,final Handler handler) throws Exception{

		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.setRequestMethod("GET");

		if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
			android.util.Log.i("info","lujing:"+path.substring(path.lastIndexOf(".")+1));
			if(path.substring(path.lastIndexOf(".")+1).equals("jpg")){
				Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
				android.util.Log.i("info","piclujing:"+path.substring(path.lastIndexOf("/")+1,path.lastIndexOf(".")));
				saveFileToPath(bitmap,path.substring(path.lastIndexOf("/"),path.lastIndexOf(".")),SystemConfig.DATA_PICTURE_PATH,handler);
			}else if(path.substring(path.lastIndexOf(".")+1).equals("wav")){
				android.util.Log.i("info","chatlujing:"+path.substring(path.lastIndexOf("/")+1,path.lastIndexOf(".")));
				writeSDFromInput(SystemConfig.DATA_CHAT_PATH,path.substring(path.lastIndexOf("/")+1,path.lastIndexOf(".")),conn.getInputStream(),handler);
			}
			


		}else{
			handler.sendEmptyMessage(ServiceEngin.DOWNLOAD_FAIL);

		}

	}
	/**  
	 * 保存bitmap文件 到指定路径
	 * @param bm  
	 * @param fileName  
	 * @throws IOException  
	 */    
	public void saveFileToPath(Bitmap bm, String fileName,String path,Handler handler) throws Exception{


		File dirFile = new File(path);   

		if(!dirFile.exists()){  

			dirFile.mkdir();    
		}    

		File myCaptureFile = new File(path + fileName);   


		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));    
		bm.compress(Bitmap.CompressFormat.JPEG,80, bos);    
		bos.flush();    
		bos.close(); 
		final Message msg = Message.obtain();
		msg.what=ServiceEngin.DOWNLOAD_PIC_SUCCESS;
		msg.obj=path+fileName;
		handler.sendMessage(msg);
	}
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File writeSDFromInput(String path, String fileName,
			InputStream input,Handler handler) {
		File file = null;
		OutputStream output = null;
		try {
			file=new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			file = new File(path,fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			// while ((input.read(buffer)) != -1) {
				// output.write(buffer);
				// }

			while (true) {
				int temp = input.read(buffer, 0, buffer.length);
				if (temp == -1) {
					break;
				}
				output.write(buffer, 0, temp);
			}

			output.flush();
			final Message msg = Message.obtain();
			msg.what=ServiceEngin.DOWNLOAD_CHAT_SUCCESS;
			msg.obj=path+fileName;
			handler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}




