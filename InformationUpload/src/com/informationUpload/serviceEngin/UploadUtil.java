package com.informationUpload.serviceEngin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class UploadUtil {
	private static final int TIME_OUT = 30 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码

	public static String uploadFiles(ArrayList<String> files, String RequestURL) {
		String result = null;

		try {
			String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
			String PREFIX = "--";
			String LINE_END = "\r\n";
			String CONTENT_TYPE = "multipart/form-data;charset=UTF-8"; // 内容类型

			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
			int len = files.size();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++) {
				String fname = files.get(i);
				File file = new File(fname);
				if (!file.exists() ||file.isDirectory()) {
					fname = "";
				} else {
					fname = file.getName();
				}
				sb = new StringBuilder();
				sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
				String name = "picfile" + String.valueOf(i + 1);
				String str = String
						.format("Content-Disposition: form-data; name=\"headfile\"; filename=\"%s",
								name, fname);
				sb.append(str + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream" + LINE_END);
				// sb.append("Content-Type: image/jpeg");
				sb.append(LINE_END);
				out.write(sb.toString().getBytes());
				DataInputStream in = null;
				if (file.exists()&&!file.isDirectory()) {
					in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];

					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
				}

				out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
				if (in != null) {
					in.close();
				}
			}

			out.write(end_data);
			out.flush();
			out.close();

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
		} catch (Exception ex) {
			result = "网络请求失败:" + ex.getMessage();
		}
		return result;
	}
}
