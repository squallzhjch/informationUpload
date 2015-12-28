package com.informationUpload.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;





import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonToken;
import android.util.Log;

public class ZipUtil {
    
	private static final String TAG = "ZipUtil";
	private static final int BUFFER = 2048;

	public static void zip(List<String> files, String dest) throws IOException {
		// 提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try {

			File outFile = new File(dest);// 源文件或者目录
			out = new ZipOutputStream(new FileOutputStream(outFile));

			for (int i = 0; i < files.size(); i++) {
				// 递归压缩，更新curPaths
				zipFileOrDirectory(out, new File(files.get(i)), "");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭输出流
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static void zip(String src, String dest) throws IOException {
		// 提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try {

			File outFile = new File(dest);// 源文件或者目录
			File fileOrDirectory = new File(src);// 压缩文件路径
			out = new ZipOutputStream(new FileOutputStream(outFile));
			// 如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				// 返回一个文件或空阵列。
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], "");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// 关闭输出流
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
   
	private static void zipFileOrDirectory(ZipOutputStream out,
			File fileOrDirectory, String curPath) throws IOException {

		// 从文件中读取字节的输入流
		FileInputStream in = null;
		try {
			// 如果此文件是一个目录，否则返回false。
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);
				// 实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				// 条目的信息写入底层流
				out.putNextEntry(entry);
				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static boolean unzip(String zipFileName, String outputDirectory,
			Handler handler) throws IOException {

		ZipFile zipFile = null;
		long zipSize = 0;
		long currentSize = 0;
		long tempSize = 100;
		Log.i(TAG, new File(zipFileName).length() + "==文件大小");
		Message msg = handler.obtainMessage();

		try {
			zipFile = new ZipFile(zipFileName);
			Enumeration e = zipFile.entries();
			Enumeration eCurrent = zipFile.entries();
			ZipEntry zipEntry = null;
			File destFile = new File(outputDirectory);
			destFile.mkdirs();
			// 获取zip解压后的大小
			while (e.hasMoreElements()) {
				try {
					zipEntry = (ZipEntry) e.nextElement();

					zipSize += zipEntry.getSize();
				} catch (Exception ex) {

					msg.what = SystemConfig.MSG_TASK_GET_DATA_UNZIP_FAILED;
					msg.obj = "解压失败";
					handler.sendMessage(msg);
					ex.printStackTrace();
					return false;
				}
			}
			Log.i(TAG, "解压文件的大小===" + zipSize);
			msg.what = SystemConfig.MSG_TASK_GET_DATA_UNZIP_START;
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.MAX_SIZE,
					new File(zipFileName).length());
			msg.setData(bundle);
			handler.sendMessage(msg);
			Log.i(TAG, "begin unzip file");
			int count = 0;
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			// 开始解压
			while (eCurrent.hasMoreElements()) {

				count++;
				zipEntry = (ZipEntry) eCurrent.nextElement();
				String entryName = zipEntry.getName();

				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdirs();
				} else {
					int index = entryName.lastIndexOf("\\");
					if (index != -1) {
						File df = new File(outputDirectory + File.separator
								+ entryName.substring(0, index));
						df.mkdirs();
					}
					index = entryName.lastIndexOf("/");
					if (index != -1) {
						File df = new File(outputDirectory + File.separator
								+ entryName.substring(0, index));
						df.mkdirs();
					}
					File f = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					// f.createNewFile();
					is = new BufferedInputStream(
							zipFile.getInputStream(zipEntry));
					FileOutputStream out = new FileOutputStream(f);
					dest = new BufferedOutputStream(out, BUFFER);
					int c = 0;
					byte[] by = new byte[BUFFER];
					while ((c = is.read(by, 0, BUFFER)) != -1) {
						dest.write(by, 0, c);
					}
					currentSize += zipEntry.getSize();
					tempSize--;
					msg = handler.obtainMessage();
					msg.what = SystemConfig.MSG_TASK_GET_DATA_UNZIPPING;
					Bundle bundleChange = new Bundle();
					bundleChange.putLong(SystemConfig.CHANGE_SIZE,
							currentSize);
					bundleChange.putLong(SystemConfig.MAX_SIZE, new File(
							zipFileName).length());
					msg.setData(bundleChange);
					Log.i(TAG,
							"bundle=="
									+ bundle.getLong(SystemConfig.CHANGE_SIZE));
					Log.d(TAG, "正在解压的文件大小" + (long) currentSize);
					Log.i(TAG, "第" + count + "文件解压完成时======"
							+ (long) currentSize);
					handler.sendMessage(msg);

					dest.flush();
					if (dest != null)
						dest.close();
					if (is != null)
						is.close();
				}
			}
			zipFile.close();
			msg = handler.obtainMessage();
			msg.what = SystemConfig.MSG_TASK_GET_DATA_UNZIP_SUCCESS;
			msg.arg1 = (int) currentSize;
			Log.i(TAG, "文件解压完成时======" + (int) currentSize);
			handler.sendMessage(msg);
			Log.i(TAG, " unzip file over!");
			return true;

		} catch (IOException ex) {

			ex.printStackTrace();
			msg = handler.obtainMessage();
			msg.what = SystemConfig.MSG_TASK_GET_DATA_UNZIP_FAILED;
			msg.obj = "解压失败";
			handler.sendMessage(msg);
			return false;

		} finally {

			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
					msg = new Message();
					msg.what = SystemConfig.MSG_TASK_GET_DATA_FAILED;
					msg.obj = ex.getMessage();
					handler.sendMessage(msg);
					return false;
				}

			}
		}
	}

	public static void unZip(Context context, String assetName,
			String outputDirectory, boolean isReWrite) throws IOException {
		// 创建解压目标目录
		File file = new File(outputDirectory);
		// 如果目标目录不存在，则创建
		if (!file.exists()) {
			file.mkdirs();
		}
		// 打开压缩文件
		InputStream inputStream = context.getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		// 读取一个进入点
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		// 使用1Mbuffer
		byte[] buffer = new byte[1024 * 1024];
		// 解压时字节计数
		int count = 0;
		// 如果进入点为空说明已经遍历完所有压缩包中文件和目录
		while (zipEntry != null) {
			// 如果是一个目录
			if (zipEntry.isDirectory()) {
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				// 文件需要覆盖或者是文件不存在
				if (isReWrite || !file.exists()) {
					file.mkdir();
				}
			} else {
				// 如果是文件
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				// 文件需要覆盖或者文件不存在，则解压文件
				if (isReWrite || !file.exists()) {
					file.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(
							file);
					while ((count = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, count);
					}
					fileOutputStream.close();
				}
			}
			// 定位到下一个文件入口
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();

	}

	public static String zipString(String str) {
		try {
			ByteArrayOutputStream bos = null;
			GZIPOutputStream os = null;
			byte[] bs = null;
			try {
				bos = new ByteArrayOutputStream();
				os = new GZIPOutputStream(bos);
				os.write(str.getBytes());
				os.close();
				bos.close();
				bs = bos.toByteArray();
				return new String(bs, "iso-8859-1");
			} finally {
				bs = null;
				bos = null;
				os = null;
			}
		} catch (Exception ex) {
			return str;
		}
	}

	public static String unzipString(String str) {
		//return str;
		
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		GZIPInputStream is = null;
		byte[] buf = null;
		try {
			bis = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			bos = new ByteArrayOutputStream();
			is = new GZIPInputStream(bis);
			buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			is.close();
			bis.close();
			bos.close();
			return new String(bos.toByteArray());
		} catch (Exception ex) {
			return str;
		} finally {
			bis = null;
			bos = null;
			is = null;
			buf = null;
		}
	}
}
