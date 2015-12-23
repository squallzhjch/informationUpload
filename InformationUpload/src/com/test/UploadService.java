package com.test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fm.data.been.GeoBeenUtil;
import com.fm.mapapi.map.FMap;
import com.mobile.model.ProjectInfo;
import com.mobile.net.HttpUtil.ProgressListener;
import com.mobile.util.FileUtils;
import com.mobile.util.MD5Checksum;
import com.mobile.util.MyApplication;
import com.mobile.util.MyLog;
import com.mobile.util.NetError;
import com.mobile.util.Separator;
import com.mobile.util.StringUtil;
import com.mobile.util.SystemConstant;
import com.mobile.util.SystemDateTime;
import com.mobile.util.ZipUtil;

public class UploadService {

	private GeoBeenUtil mGbu;
	private FMap mFMap;
	private String mProjectId;
	private ProjectInfo mProjectInfo;
	private int mThreadState = 0;
	private Handler mHandler;
	private MyApplication mApp;
	private final int THREAD_STATE_STOP = 0;
	private final int THREAD_STATE_RUNNING = 1;
	private final String TAG = "UploadService";
	private Context mContext;
	private boolean mbCanback = true;
	private boolean mbDeleteData = false; // 上传后是否删除作业数据，如果删除了作业数据，就会触发自动下载
	// 有两种方式会导致作业数据删除：1是调用者指定，2是错误数据导致。
	private RecoverIncrementalThread riThread;

	private String infoDirPath;
	private String poiDirPath;
	private String lastFileDate;


	// 上传监听Timer
	//	private Timer uploadDataTimer;
	// 上次监测到的进度
	//	private long dataUploadChange;

	public UploadService(Context con, FMap fmap, Handler handler,
						 MyApplication app,ProjectInfo mProjectInfo) {

		mFMap = fmap;
		if (fmap != null)
			mGbu = new GeoBeenUtil(fmap);
		mApp = app;
		mContext = con;
		mHandler = handler;
		this.mProjectInfo=mProjectInfo;
		mThreadState = THREAD_STATE_RUNNING;

	}


	public UploadService(Context con, FMap fmap, Handler handler,
						 MyApplication app) {

		mFMap = fmap;
		if (fmap != null)
			mGbu = new GeoBeenUtil(fmap);
		mApp = app;
		mContext = con;
		mHandler = handler;
		mThreadState = THREAD_STATE_RUNNING;

	}

	/**
	 * 取消上传
	 *
	 * @return true 成功取消，否则失败
	 */
	public boolean cancelUpload() {

		MyLog.writeLogtoFile("上传履历", TAG,
				"取消上传履历time:" + SystemDateTime.getDataTime());
		mThreadState = THREAD_STATE_STOP;
//		if (uploadDataTimer != null) {
//			uploadDataTimer.cancel();
//			uploadDataTimer = null;
//		}
		return mbCanback;
	}


	public boolean restartUpload() {

		mThreadState = THREAD_STATE_RUNNING;

		return mbCanback;
	}

	/**
	 * 上传履历文件,如果履历文件有错误，会自动恢复，上传，删除数据，并自动下载
	 *
	 * @param userId
	 * @param projectId
	 * @param incrPath
	 *            履历文件名，如果次值不为空，则不再压缩履历，否则进行履历压缩
	 */
	public void uploadData(final String userId, final String projectId,
						   final String incrPath, final String unUploadData,final int workCount,
						   final boolean bDelete, final boolean error) {


		poiDirPath=SystemConstant.PROJECT_USERS_PATH + "/"
				+ mApp.getUserId() + SystemConstant.PROJECT_PATH
				+ "/" + projectId;

		Log.e("upload", "uploadData--------");
		mFMap.setCurrentUser(userId);
		new Thread() {

			int checkResult=-1;
			@Override
			public void run() {

				// TODO Auto-generated method stub
				MyLog.writeLogtoFile("上传履历", TAG,"开始上传 time:" + SystemDateTime.getDataTime());
				mProjectId = projectId;
				mbDeleteData = bDelete;

				Map<String, String> mapParams = new HashMap<String, String>();
				mapParams.put("projectId", projectId);
				mapParams.put("filetype", "incremental");
				// 断点续传偏移量
				Message msg = mHandler.obtainMessage();
				String fileName = incrPath;

				boolean lock = manageLockTask(false);// 临上传之前锁定Task;
				MyLog.writeLogtoFile("上锁状态", TAG, lock + "");
				if (!lock) {// 锁定不成功，退出
					return;
				}

				boolean time = getLastUploadTime(projectId, mApp.getDeviceId(), userId);// 临上传之前锁定Task;
				MyLog.writeLogtoFile("获取上传时间", TAG, time + "");
				if (!time) {// 获取失败退出
					return;
				}
				boolean isRestartUpload = false;
				String lastUploadTime = mGbu.getIncrementalDataTimestamp(mProjectInfo.getProjectID()+"");
				if(lastFileDate!=null&&lastFileDate.length()>0&&lastUploadTime!=null&&lastUploadTime.length()>0){
					if(SystemDateTime.getTimeInfo(lastFileDate)>SystemDateTime.getTimeInfo(lastUploadTime)){
						isRestartUpload = true;
						mGbu.setIncrementalDataTimestamp(
								"" + mProjectInfo.getProjectID(),
								lastFileDate + ".zip");
						Message msg1 = new Message();
						msg1.what = SystemConstant.MSG_PROJECT_SYNDATA_FINISH;
						mHandler.sendMessage(msg1);
					}
				}
				if(!isRestartUpload){
					if (incrPath != null && unUploadData != null
							&& unUploadData.length() > 0) {// 上传外部传入的履历文件
						fileName = incrPath;

						fileName = poiDirPath+ "/" + fileName;
						msg.what = SystemConstant.MSG_PROJECT_SYNDATA_START;
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConstant.MAX_SIZE,
								new File(fileName).length());
						msg.setData(bundle);
						msg.obj = new File(fileName).getName();
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
						Log.e("upload", "uploadData------unUploadData--");

						for (int i = 0; i < unUploadData.length(); i++) {
							String isUpload = unUploadData.substring(i, i + 1);
							if (isUpload.equals("0")) {
								if (i == unUploadData.length() - 1)
									datachunk(new File(fileName), i, true, 0);
								else
									datachunk(new File(fileName), i, false, 0);
							}
						}
						return;
					}else if (incrPath != null) {
						checkResult=check(incrPath, false, 0);
						if(checkResult!=-5){// -5表示服务端未有该文件
							return;
						}
					}
				}

				if(checkResult==-5){//履历存在，直接拼接
					fileName=incrPath+","+workCount;
				}else{//压缩履历
					boolean isOpenProject=mGbu.isProjectOpen(projectId);
					mGbu.closeAllProject();
					mbCanback = false;
					MyLog.writeLogtoFile("上传履历", TAG, "开始压缩履历 time:"
							+ SystemDateTime.getDataTime());
					ArrayList<String> list = mGbu.generateIncrementalPath(projectId);
					if(list==null||list.size()==0){
						msg = mHandler.obtainMessage();
						msg.what = SystemConstant.MSG_PROJECT_SYNDATA_NO_INCREMENTAL;
						msg.obj = "没有新的POI修改履历";
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
						return;
					}else{
						fileName = list.get(0);
						if(list.size()>1){
							String strs[] = fileName.split(",");
							File file = new File(strs[0]);
							//删除该文件重新上传
							if(file.exists())
								file.delete();
							MyLog.writeLogtoFile("拷贝失败", "拷贝失败", "拷贝失败:"+list.toString());
							msg = mHandler.obtainMessage();
							msg.what = SystemConstant.MSG_PROJECT_SYNDATA_COPY_FAILED_INCREMENTAL;
							msg.obj = "压缩履历照片错误，请重新上传";

							if (mThreadState != THREAD_STATE_STOP)
								mHandler.sendMessage(msg);
							return;
						}
					}

					if(isOpenProject){
						mGbu.openMyProject(projectId, Integer.parseInt(mProjectInfo.getType()));
					}

					MyLog.writeLogtoFile("上传履历", TAG, "结束压缩履历 time:"
							+ SystemDateTime.getDataTime());
					mbCanback = true;
					if(error)
						fileName = "2";//测试用
					//String lastError = GeoBeenUtil.GetLastJniError();		
					if (fileName == null || fileName.equals("0")) {
						msg = mHandler.obtainMessage();
						msg.what = SystemConstant.MSG_PROJECT_SYNDATA_NO_INCREMENTAL;
						msg.obj = "没有新的POI修改履历";

						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
						return;
					}

					if (fileName.equals("2") || fileName.equals("1")) {
						msg = mHandler.obtainMessage();
						msg.what = SystemConstant.MSG_PROJECT_SYNDATA_INCREMENTAL_ERROR;
						msg.obj = "履历生成异常，需要恢复！";
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
						return;
					}
				}
				// 分隔开上传的文件和POI数量
				String strs[] = fileName.split(",");
				File file = new File(strs[0]);
				MyLog.writeLogtoFile("md5", "md5", " md5开始=="+strs[0]+"==存在==="+file.exists()+"===size==="+StringUtil.formatByte(file.length()));
				String md5 = MD5Checksum.getFileMD5(file);
				MyLog.writeLogtoFile("md5", "md5", " md5结束==" + md5);

				if (md5 == null) {

					msg = mHandler.obtainMessage();
					msg.arg1 = 0;
					msg.what = SystemConstant.APP_MD5_FAILED;
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					cancelUpload();
					return;
				}

				md5 = StringUtil.paddedCharacter(md5, 32, "0");
				MyLog.writeLogtoFile("md5", "md5", " md5补齐位数==" + md5);
				Log.i("md5", md5 + "===========" + file.getName());
				MyLog.writeLogtoFile("上传文件", TAG, "文件路径：" + fileName
						+ "; 是否存在:" + file.exists());
				mapParams.put("filename", file.getName());
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_START;
				if (strs.length > 1) {

					fileName = strs[0];
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConstant.MAX_SIZE,
							new File(fileName).length());
					msg.setData(bundle);
					msg.arg2 = Integer.parseInt(strs[1]);
					msg.obj = new File(fileName).getName();
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					postSnapshot(file, md5, file.length() + "",
							file.getAbsolutePath(), 0);
				}
			}
		}.start();

	}

	/**
	 * 恢复履历
	 */
	public void genRecoverIncremental(String projectId, String userId, int type) {

		if (riThread != null) {

			riThread.cancel();
			riThread = null;
		}

		riThread = new RecoverIncrementalThread(projectId, userId, type);
		riThread.start();

	}

	/**
	 * 通知服务器要上传的文件大小
	 *
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private String postSnapshot(File string, String md5, String fileLength,
								String filePath, int type) {

		Map<String, String> mapParams = new HashMap<String, String>();
		String res = "";
		Message msg = mHandler.obtainMessage();
		msg.what = SystemConstant.MSG_SNAP_SHOT_START;
		msg.arg1 = type;

		mapParams.put("access_token", mApp.getToken());
		mapParams.put("md5", md5);
		mapParams.put("filelength", fileLength);
		mapParams.put("filename", string.getName());

		try {
			res = HttpUtil.post(
					SystemConstant.URL_TASK_DATA_UPLOADRES_SNAPSHOT, mapParams,
					null, mContext, false, null);

		} catch (IOException e) {

			msg = mHandler.obtainMessage();
			msg.arg1 = type;
			msg.what = SystemConstant.MSG_SNAP_SHOT_EXCEPTION;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			cancelUpload();
			return res;
		}

		msg = mHandler.obtainMessage();
		msg.arg1 = type;
		msg.what = SystemConstant.MSG_SNAP_SHOT_FAILED;

		JSONObject jobj;
		try {
			jobj = new JSONObject(res);
			String val = jobj.getString("errcode");
			if (val.equals("0")) {

				Separator separator = new Separator();
				separator.getFileAttribute(filePath);
				long chunkno = separator.getBlockNum(1024 * 1024);
				// List<File> list = separator.separatorFile(filePath, 1024);
				boolean bIsChunkOk = true;
				if (chunkno > 0) {
					for (int i = 0; i < chunkno; i++) {
						if (i == chunkno - 1)
							bIsChunkOk = datachunk(string, i, true, type) && bIsChunkOk ? true : false;
						else
							bIsChunkOk = datachunk(string, i, false, type) && bIsChunkOk ? true : false;
					}
				}
				if(bIsChunkOk)
					msg.what = SystemConstant.MSG_SNAP_SHOT_SUCCESS;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				return res;
			} else if (val.equals("-4")) {

				msg = mHandler.obtainMessage();
				msg.obj = "文件上传失败！" + jobj.optString("errmsg");
				msg.arg1 = type;
				msg.what = SystemConstant.MSG_SNAP_SHOT_EXCEPTION;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				MyLog.writeLogtoFile("上传失败", TAG,
						"文件上传失败:" + jobj.optString("errmsg"));
				cancelUpload();
				return res;

			} else { // 入库失败
				msg.obj = "文件上传失败！" + jobj.optString("errmsg");
				msg.arg1 = type;
				MyLog.writeLogtoFile("上传失败", TAG,
						"文件上传失败:" + jobj.optString("errmsg"));
			}

		} catch (JSONException e) {
			msg = mHandler.obtainMessage();
			msg.obj = "文件上传失败！";
			msg.arg1 = type;
			msg.what = SystemConstant.MSG_SNAP_SHOT_EXCEPTION;
			cancelUpload();
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			MyLog.writeLogtoFile("上传失败", TAG, "文件上传失败:");

			return res;
		}

		msg.what = SystemConstant.MSG_SNAP_SHOT_FAILED;
		msg.arg1 = type;
		if (mThreadState != THREAD_STATE_STOP)
			mHandler.sendMessage(msg);

		return res;
	}

	/**
	 * 通知服务器需要上传区块序号
	 *
	 * @param file
	 * @param end
	 * @param type
	 *            0：项目，1：情报，2：道路情报
	 * @return
	 * @throws Exception
	 */
	private boolean datachunk(final File file, int chunkno, boolean end,
							  final int type) {

		Log.e("upload", "datachunk--------");
		Map<String, String> mapParams = new HashMap<String, String>();
		String res = "";
		Message msg = mHandler.obtainMessage();
		msg.what = SystemConstant.MSG_DATA_TRUNK_FAILED;
		msg.arg2 = type;

		if (!file.exists()) {
			mHandler.sendMessage(msg);
			return false;
		}
		if (mThreadState == THREAD_STATE_STOP) {
			return false;
		}

		mapParams.put("access_token", mApp.getToken());
		mapParams.put("chunkno", chunkno + "");
		mapParams.put("filename", file.getName());
		Map<String, File> map = new HashMap<String, File>();
		map.put("file", file);

		long offset = chunkno;
		final long progBase = offset * 1024 * 1024;

		try {
			res = HttpUtil.post(
					SystemConstant.URL_TASK_DATA_UPLOADRES_DATA_TRUNK,
					mapParams, file, mContext, true,
					new UpLoadProgressListener() {
						@Override
						public boolean upLoadSize(long size) {

							Log.e("upload", "datachunk--------size：" + size);

							Message msg = mHandler.obtainMessage();
							msg.what = SystemConstant.MSG_PROJECT_DATA_CHUNK_SYNDATA;
							msg.arg2 = type;
							long len = (long) (size + progBase);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConstant.CHANGE_SIZE, len);
							bundle.putLong(SystemConstant.MAX_SIZE,
									file.length());
							msg.setData(bundle);
							if (mThreadState != THREAD_STATE_STOP) {
								mHandler.sendMessage(msg);
							}
							return false;
						}
					}, chunkno);

		} catch (IOException e) {

			e.printStackTrace();
			msg = mHandler.obtainMessage();
			msg.arg2 = type;
			msg.what = SystemConstant.MSG_DATA_TRUNK_EXCEPTION;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			cancelUpload();

			return false;
		}

		if (end) {

			if(check(file.getName(), true, type)!=0)
				return false;
		}

		msg = mHandler.obtainMessage();
		msg.arg2 = type;
		msg.what = SystemConstant.MSG_DATA_TRUNK_FAILED;

		JSONObject jobj;
		String val = null;
		try {
			jobj = new JSONObject(res);
			val = jobj.getString("errcode");

			if (val.equals("0")) {

				msg.arg2 = type;
				msg.what = SystemConstant.MSG_DATA_TRUNK_SUCCESS;
				MyLog.writeLogtoFile("上传区块成功", TAG, "成功上传区块 time:"
						+ SystemDateTime.getDataTime());
				return true;

			} else { // 入库失败
				msg.obj = "文件上传区块！" + jobj.optString("errmsg");
				msg.arg1 = chunkno;
				msg.arg2 = type;
				MyLog.writeLogtoFile("上传区块失败", TAG,
						"文件上传区块失败:" + jobj.optString("errmsg"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg = mHandler.obtainMessage();
			msg.arg2 = type;
			msg.what = SystemConstant.MSG_DATA_TRUNK_EXCEPTION;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			cancelUpload();
			return false;
		}

		msg.what = SystemConstant.MSG_DATA_TRUNK_FAILED;
		if (mThreadState != THREAD_STATE_STOP)
			mHandler.sendMessage(msg);

		return false;
	}

	/**
	 * 校验是否成功上传
	 *
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private int check(String fileName, boolean isUpload, int type) {

		Log.e("upload", "check--------");

		Map<String, String> mapParams = new HashMap<String, String>();
		String res = "";
		Message msg = mHandler.obtainMessage();
		msg.arg2 = type;
		msg.what = SystemConstant.MSG_CHECK_FILE_START;

		if (mThreadState == THREAD_STATE_STOP) {
			return -1;
		} else {
			mHandler.sendMessage(msg);
		}

		mapParams.put("access_token", mApp.getToken());
		mapParams.put("filename", fileName);
		try {
			res = HttpUtil.post(SystemConstant.URL_TASK_DATA_UPLOADRES_CHECK,
					mapParams, null, mContext, true, null);
			MyLog.writeLogtoFile("checkName", "checkName",
					"res==" + res.toString()+"==文件名称==="+fileName);
			msg = mHandler.obtainMessage();
			msg.arg2 = type;
			msg.what = SystemConstant.MSG_CHECK_FILE_FAILED;

			JSONObject jobj = new JSONObject(res);
			String val = jobj.optString("errcode");

			if (val.equals("0")) {

				MyLog.writeLogtoFile("校验履历" + type, TAG, "成功校验履历 time:"
						+ SystemDateTime.getDataTime());

				if (type == 0) {/*

					boolean unlock = manageLockTask(true);// 解锁
					MyLog.writeLogtoFile("解锁状态", TAG, unlock + "");
				*/}

				msg = mHandler.obtainMessage();
				msg.arg2 = type;
				msg.what = SystemConstant.MSG_CHECK_SUCCESS;
				if (mThreadState != THREAD_STATE_STOP){
					mHandler.sendMessage(msg);
					if(postIncremental(fileName, mApp.getUserId(), type, mApp.getDeviceId())){
						return 0;
					}else{
						return -1;
					}
				} else {
					return -1;
				}

			} else if (val.equals("-1")) {

				Log.e("upload", "check-----1----");

				msg.what = SystemConstant.MSG_CHECK_FILE_FAILED;
				msg.obj = "校验上传失败！" + jobj.optString("errmsg");
				msg.arg2 = type;
				MyLog.writeLogtoFile("校验上传失败" + type, TAG,
						"校验上传失败:" + jobj.optString("errmsg"));
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				return -1;

			} else if (val.equals("-2")) {

				Log.e("upload", "check-----2----");

				MyLog.writeLogtoFile("校验上传失败" + type, TAG,"校验上传失败:" + jobj.optString("errmsg"));
				MyLog.writeLogtoFile("check" + type, "check","check==" + jobj.optString("data"));
				res = jobj.optString("data");
				msg = mHandler.obtainMessage();
				msg.obj = res;

				if (isUpload) {
					msg.arg2 = type;
					msg.what = SystemConstant.MSG_CHECK_NO_ALL_SUCCESS;
				} else {
					msg.arg2 = type;
					msg.what = SystemConstant.MSG_CHECK_NO_ALL_SUCCESS_DIALOG;
				}
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);

				return -2;

			} else if (val.equals("-3")) {

				Log.e("upload", "check-----3----");

				msg.what = SystemConstant.MSG_CHECK_FILE_FAILED;

				MyLog.writeLogtoFile("校验上传失败" + type, TAG,
						"校验上传失败:" + jobj.optString("errmsg"));
				msg.obj = jobj.optString("errmsg");
				msg.arg2 = type;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);

				return -3;

			} else if (val.equals("-4")) {

				Log.e("upload", "check-----4----");

				msg.what = SystemConstant.MSG_CHECK_EXCEPTION;

				MyLog.writeLogtoFile("校验网络连接失败" + type, TAG,
						"网络网络连接失败:" + jobj.optString("errmsg"));
				msg.obj = jobj.optString("errmsg");
				msg.arg2 = type;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				cancelUpload();

				return -4;

			} else if (val.equals("-5")) {//未有上传的文件

				Log.e("upload", "check-----5----");

				msg.what = SystemConstant.MSG_CHECK_FILE_NO_FILE;

				MyLog.writeLogtoFile("未发现校验文件" + type, TAG,
						"未发现校验文件:" + jobj.optString("errmsg"));
				msg.obj = jobj.optString("errmsg");
				msg.arg2 = type;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				return -5;
			}
		} catch (Exception e) {

			Log.e("upload", "check----Exception----");

			msg.what = SystemConstant.MSG_CHECK_EXCEPTION;
			msg.arg2 = type;
			MyLog.writeLogtoFile("校验上传异常" + type, TAG, "校验上传异常:" + res);
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			cancelUpload();
			return -1;
		}

		Log.e("upload", "check----end----");
		msg.what = SystemConstant.MSG_CHECK_FILE_FAILED;
		msg.arg2 = type;
		if (mThreadState != THREAD_STATE_STOP)
			mHandler.sendMessage(msg);
		return -1;
	}

	/**
	 * 通知服务器上传文件结束
	 *
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private boolean postIncremental(String filePath, String userId, int postType, String deviceId) {

		Log.e("upload", "postIncremental--------");

		Map<String, String> mapParams = new HashMap<String, String>();
		String res = "";
		Message msg = mHandler.obtainMessage();
		msg.arg1 = postType;
		msg.what = SystemConstant.MSG_PROJECT_SYNDATA_FILE_START;

		if (mThreadState == THREAD_STATE_STOP) {
			return false;
		} else {
			mHandler.sendMessage(msg);
		}

		String url = null;
		if (postType == 0) {

			mapParams.put("projectId", mProjectId);
			mapParams.put("deviceId", deviceId);
			mapParams.put("data", "multipart/form-data");

			mapParams.put("filename", new File(filePath).getName());
/*			mapParams.put("filename", new File(filePath).getName()
					+ SystemConstant.PROJECT_INCREMTENTAL_FILE_EXT);*/

			url = SystemConstant.URL_TASK_DATA_INCREMENT + mApp.getToken();

		} else {

			mapParams.put("data", "multipart/form-data");
			mapParams.put("filename", new File(filePath).getName());
			url = SystemConstant.URL_INFORMATION_DATA_INCREMENT
					+ mApp.getToken();
		}

		try {

			res = HttpUtil.post(url, mapParams, null, mContext, true, null);

		} catch (IOException e) {

			e.printStackTrace();
			msg = mHandler.obtainMessage();
			msg.arg1 = postType;
			msg.what = SystemConstant.MSG_PROJECT_SYNDATA_EXCEPTION;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);

			cancelUpload();
			return false;
		}

		msg = mHandler.obtainMessage();
		msg.arg1 = postType;
		msg.what = SystemConstant.MSG_PROJECT_SYNDATA_FAILED;

		try {

			JSONObject jobj = new JSONObject(res);
			String val = jobj.getString("errcode");

			if (val.equals("0")) {

				msg.obj = jobj.optString("errmsg");
				String data = jobj.optString("data");
				if (data != null && !data.equals("")) {
					jobj = new JSONObject(data);
					if (jobj != null) {
						if (jobj.optInt("failed") > 0) {
							Message msgData = new Message();
							msgData.obj = jobj.toString();
							msg.arg1 = postType;
							msgData.what = SystemConstant.MSG_PROJECT_DATA_FAILED;
							if (mThreadState != THREAD_STATE_STOP)
								mHandler.sendMessage(msgData);
						}
					}
				}

				msg = mHandler.obtainMessage();
				msg.arg1 = postType;
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_DONE;

				if (postType == 2) {// 道路情报
					String str = "";
					str = SystemConstant.PROJECT_USERS_PATH + "/"
							+ mApp.getUserId()
							+ SystemConstant.INFORMATION_PATH;


					// 删除zip文件
					String name = str + "/" + filePath;
					boolean result = new File(name).delete();
					if(!result)
						MyLog.writeLogtoFile("上传道路情报成功", TAG, "删除文件失败:" + result+name);

					MyLog.writeLogtoFile("上传道路情报成功", TAG, "道路情报上传成功:" + msg.obj);
					MyLog.writeLogtoFile("上传道路情报履历", TAG, "道路成功上传情报履历 time:"
							+ SystemDateTime.getDataTime());
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);

					return true;
				} else if (postType == 1) {
					// 清空本地情报照片
					if (mbDeleteData) {// 删除所有作业数据

						MyLog.writeLogtoFile("删除情报数据", TAG,
								"情报履历恢复，并上传成功，开始删除情报数据文件");
						mGbu.closeProject(mProjectId);
						FileUtils.deleteUserDatas(infoDirPath, false, true);
						boolean result = new File(infoDirPath).delete();
						if(!result)
							MyLog.writeLogtoFile("删除情报数据", TAG, "删除情报数据文件失败:" + result+infoDirPath);
					}

					// 删除zip文件
					String name = infoDirPath + "/" + filePath;
					new File(name).delete();
					mApp.setLastUpInfoFile(null);

					MyLog.writeLogtoFile("上传情报成功", TAG, "情报上传成功:" + msg.obj);
					//mGbu.setInforDataTimestamp(new File(filePath).getName());
					MyLog.writeLogtoFile("上传情报履历", TAG, "成功上传情报履历 time:"
							+ SystemDateTime.getDataTime());
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);

					return true;

				} else {

					// 清空本地的照片			
					if (mbDeleteData) {// 删除所有作业数据
						MyLog.writeLogtoFile("删除数据", TAG,
								"履历恢复，并上传成功，开始删除数据文件:" + mProjectId);
						mGbu.closeProject(mProjectId);
						// project
						FileUtils.deleteUserDatas(poiDirPath, false, false);
						new File(poiDirPath).delete();
						// bin
						String str = SystemConstant.PROJECT_USERS_PATH + "/"
								+ mApp.getUserId()
								+ SystemConstant.PROJECT_BIN_PATH;
						FileUtils.deleteUserDatas(str, false, true);
						new File(str).delete();
					}
					// 删除zip文件
					String name = poiDirPath + "/" + filePath;
					boolean result = new File(name).delete();
					if(!result)
						MyLog.writeLogtoFile("上传同步成功", TAG, "文件删除失败:" + result+name);
					mApp.setLastUpFile(null);

					MyLog.writeLogtoFile("上传同步成功", TAG, "文件同步成功:" + msg.obj);
					mGbu.setIncrementalDataTimestamp(mProjectId, new File(
							filePath).getName());

					MyLog.writeLogtoFile("上传履历", TAG, "成功同步履历 time:"
							+ SystemDateTime.getDataTime());
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return true;

				}
			} else if (val.equals("-4")) {

				msg = mHandler.obtainMessage();
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_EXCEPTION;
				msg.obj = "文件同步失败！" + jobj.optString("errmsg");
				msg.arg1 = postType;
				MyLog.writeLogtoFile("同步失败", TAG,
						"文件同步失败:" + jobj.optString("errmsg"));
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				cancelUpload();
				return false;

			} else {//入库失败
				msg = mHandler.obtainMessage();
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_FAILED;
				msg.obj = "文件同步失败！" + jobj.optString("errmsg");
				msg.arg1 = postType;
				MyLog.writeLogtoFile("同步失败", TAG,
						"文件同步失败:" + jobj.optString("errmsg"));
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				cancelUpload();
				return false;
			}
		} catch (Exception e) {

			e.printStackTrace();
			msg = mHandler.obtainMessage();
			msg.arg1 = postType;
			msg.what = SystemConstant.MSG_PROJECT_SYNDATA_EXCEPTION;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			cancelUpload();
			return false;
		}
	}

	/**
	 * 自动上传轨迹
	 */
	public void autoUploadTracks() {

		new Thread() {
			@Override
			public void run() {
				uploadGPSData();
			}
		}.start();

	}

	/**
	 * 上传日志文件
	 */
	public void autoUploadLogs() {

		new Thread() {
			@Override
			public void run() {

				String time = SystemDateTime.getTime();
				List<String> arrFiles = new ArrayList<String>(); // 实际需要上传的文件名
				File f = new File(SystemConstant.SD_PATH + "Fastmap/Kernel.log");
				if(f.exists()&&f.length()>0)
					arrFiles.add(f.getAbsolutePath());
				// kernel文件
				f = new File(MyApplication.LOG_PATH + "/Kernel_"
						+ mApp.getUserId() + "_" + mProjectId + ".log");
				if (f.exists() && f.length() > 0) {
					arrFiles.add(f.getAbsolutePath());
				}
				// 崩溃日志
				f = new File(MyApplication.CRASH_PATH);
				String files[] = f.list();
				for (int i = 0; files != null && i < files.length; i++) {
					arrFiles.add(MyApplication.CRASH_PATH + "/" + files[i]);
				}

				// 普通log日志
				f = new File(MyApplication.LOG_PATH);
				files = f.list();
				for (int i = 0; files != null && i < files.length; i++) {
					if(files[i].startsWith("Kernel_"))
						continue;
					arrFiles.add(MyApplication.LOG_PATH + "/" + files[i]);
				}
				Message msg = mHandler.obtainMessage();

				if (arrFiles.size() == 0) {
					msg = mHandler.obtainMessage();
					msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_FAILED;
					msg.obj = "没有需要上传的日志文件!";
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return;
				}

				String uName = mApp.getUserName();
				String fileName = uName + "_" + mApp.getUserId() + "_log_"
						+ time + ".zip";
				final String logZipFile = SystemConstant.PROJECT_USERS_PATH
						+ "/" + fileName;

				try {
					ZipUtil.zip(arrFiles, logZipFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					msg = mHandler.obtainMessage();
					msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_FAILED;
					msg.obj = "日志压缩失败！！";
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					MyLog.writeLogtoFile("上传日志", TAG,
							"日志文件压缩失败:" + e1.getMessage());
					new File(logZipFile).delete();
					return;
				}
				if (mThreadState == THREAD_STATE_STOP)
					return;
				msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_START;
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConstant.MAX_SIZE,
						new File(logZipFile).length());
				msg.setData(bundle);
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);

				try {

					Map<String, String> params = new HashMap<String, String>();
					params.put("filetype", "android_log");
					params.put("filename", new File(logZipFile).getName());
					String res = HttpUtil.httpUpload(
							params,
							logZipFile,
							SystemConstant.URL_TASK_DATA_UPLOADRES
									+ mApp.getToken(), false,
							new ProgressListener() {

								@Override
								public void transferred(long num) {
									// TODO Auto-generated method stub
									Message msg = mHandler.obtainMessage();
									Bundle bundle = new Bundle();
									bundle.putLong(SystemConstant.CHANGE_SIZE,
											num);
									bundle.putLong(SystemConstant.MAX_SIZE,
											new File(logZipFile).length());
									msg.setData(bundle);
									if (mThreadState != THREAD_STATE_STOP)
										msg.what = SystemConstant.MSG_USER_UPLOAD_LOG;

									if (mThreadState == THREAD_STATE_STOP) {
										try {
											throw new Exception("上传取消!");
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										mHandler.sendMessage(msg);
									}
								}
							});
					if (res == null) {

						msg = mHandler.obtainMessage();
						msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_FAILED;
						msg.obj = "上传日志出错: 服务器连接错误!";
						MyLog.writeLogtoFile("上传日志", TAG, "服务器连接错误!");
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
						new File(logZipFile).delete();

						return;
					}
					JSONObject jobj = new JSONObject(res);
					if (!"0".equals(jobj.optString("errcode"))) {
						msg = mHandler.obtainMessage();
						msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_FAILED;
						msg.obj = jobj.optString("errmsg");
						MyLog.writeLogtoFile("上传日志", TAG, "服务器连接错误!");
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
						new File(logZipFile).delete();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					msg = mHandler.obtainMessage();
					msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_FAILED;
					msg.obj = "上传日志出错:" + e.getMessage();
					MyLog.writeLogtoFile("上传日志", TAG,
							"日志文件压缩失败:" + e.getMessage());
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					new File(logZipFile).delete();
					return;
				}

				msg = mHandler.obtainMessage();
				msg.what = SystemConstant.MSG_USER_UPLOAD_LOG_SUCCESS;
				// 删除已经上传的文件
				for (int i = 0; i < arrFiles.size(); i++)
					new File(arrFiles.get(i)).delete();
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				new File(logZipFile).delete();
			}
		}.start();

	}

	/**
	 * 上传轨迹文件
	 */
	private void uploadGPSData() {

		Message msg = mHandler.obtainMessage();
		List<String> results = mGbu.generateTrackData();

		// 判断是否有轨迹文件
		if (results != null && results.size() > 0) {

			String result = results.get(0);
			if (result.equals("")) {

				MyLog.writeLogtoFile("轨迹同步", "轨迹", "同步轨迹失败！没有生成轨迹文件！");
				msg = new Message();
				msg.obj = "未生成轨迹文件，无需同步！";
				msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_FAILED;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				return;
			}

			String path = result;
			final File file = new File(path);

			if (!file.exists()) {

				MyLog.writeLogtoFile("轨迹同步", "轨迹", "同步轨迹失败！没有生成轨迹文件！");
				msg = new Message();
				msg.obj = "未生成轨迹文件，无需同步！";
				msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_FAILED;
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				return;
			}
			// String name =
			// URLEncoder.encode(file.getAbsolutePath().toString(),"utf-8");
			Map<String, String> params = new HashMap<String, String>();
			params.put("filetype", "track");
			params.put("filename", new File(path).getName());
			try {
				msg = new Message();
				msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_START;
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConstant.MAX_SIZE, file.length());
				msg.setData(bundle);
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				Log.e("maxSize----", msg.arg1 + "");

				String res = "";
				res = HttpUtil.httpUpload(
						params,
						path,
						SystemConstant.URL_TASK_DATA_UPLOADRES
								+ mApp.getToken(), false,
						new ProgressListener() {

							@Override
							public void transferred(long size) {

								// TODO Auto-generated method stub
								Message msg = mHandler.obtainMessage();
								msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK;
								Bundle bundle = new Bundle();
								bundle.putLong(SystemConstant.CHANGE_SIZE, size);
								bundle.putLong(SystemConstant.MAX_SIZE,
										file.length());
								msg.setData(bundle);
								if (mThreadState == THREAD_STATE_STOP) {
									try {
										throw new Exception("上传取消!");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									if (mThreadState != THREAD_STATE_STOP)
										mHandler.sendMessage(msg);
								}

							}

						});

				if (res == null) {// 上传失败，网络错误
					msg = mHandler.obtainMessage();
					msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_FAILED;
					msg.obj = "GPS上传失败,请检查网络";
					MyLog.writeLogtoFile("上传失败", TAG, "文件上传失败,请检查网络!");
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return;

				} else {

					Log.e("gpsRes", res);
					JSONObject jobj = new JSONObject(res);
					String val = jobj.getString("errcode");

					if (val.equals("0")) {// 上传文件成功

						msg = mHandler.obtainMessage();
						msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_SUCCESS;
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);

						for (int i = 0; i < results.size(); i++) {
							Log.e("all_gps_results", results.get(i));
						}

						String timestamp = results.get(0);
						// 除去轨迹压缩文件
						results.remove(0);
						Collections.sort(results);
						// 除去当天
						results.remove(results.size() - 1);
						if (results.size() > 2) {

							for (int i = 0; i < results.size() - 2; i++) {
								boolean deleteResult = mGbu
										.deleteTrackData(results.get(i));
								Log.e("deleteTrackData", deleteResult + "");
							}
						}

						if (results.size() > 0) {

							for (int i = 0; i < results.size(); i++) {
								Log.e("gps_results", results.get(i));
							}
							mGbu.setTrackDataTimestamp(
									(ArrayList<String>) results, timestamp);
						}

						if (file.exists()) {

							if (file.isFile()) {
								file.delete();
							}
						}
						return;

					} else {
						msg.obj = "文件上传失败！" + jobj.optString("errmsg");
						MyLog.writeLogtoFile("上传失败", TAG,
								"文件上传失败:" + jobj.optString("errmsg"));
						msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_FAILED;
						if (mThreadState != THREAD_STATE_STOP)
							mHandler.sendMessage(msg);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg = new Message();
				msg.obj = e.getMessage();
				msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_FAILED;
				MyLog.writeLogtoFile("上传失败", TAG,
						"GPS文件上传失败:异常" + e.getMessage());
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
			}
		} else {

			MyLog.writeLogtoFile("轨迹同步", "轨迹", "同步轨迹失败！没有生成轨迹文件！");
			msg = new Message();
			msg.obj = "未生成轨迹文件，无需同步！";
			msg.what = SystemConstant.MSG_USER_UPLOAD_TRACK_FAILED;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
		}
		MyLog.writeLogtoFile("GPS数据上传结束", "GPSDetail", "Over");
	}

	/**
	 * 任务开锁解锁
	 *
	 * @param toUnlock
	 * @return
	 */
	public boolean manageLockTask(boolean toUnlock) {

		Message msg = mHandler.obtainMessage();
		if (!toUnlock) {
			msg.what = SystemConstant.MSG_TASKS_START_LOCK_DATA;

		} else {
			msg.what = SystemConstant.MSG_TASKS_START_UNLOCK_DATA;
		}
		mHandler.sendMessage(msg);

		msg = mHandler.obtainMessage();
		msg.arg2 = 0;
		if (mThreadState == THREAD_STATE_STOP) {
			return false;
		}

		String urlStr = null;
		if (toUnlock) {
			urlStr = SystemConstant.URL_TASK_UPDATE_UNLOCK_STATUS
					+ "lockStatus=1" + "&access_token=" + mApp.getToken()
					+ "&projectId=" + mProjectId;
		} else {
			urlStr = SystemConstant.URL_TASK_UPDATE_LOCK_STATUS
					+ "access_token=" + mApp.getToken() + "&projectId="
					+ mProjectId + "&lockStatus=" + 1;
		}
		URL url = null;
		URI uri = null;
		try {

			url = new URL(urlStr);
			uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
					url.getPort(), url.getPath(), url.getQuery(), null);
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 10000);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setParams(httpParams);
			HttpGet httpRequest = new HttpGet(uri);
			MyLog.d(TAG, "锁任务：" + uri);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String res = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
				JSONObject jsonObjTemp = new JSONObject(res);
				String errcode = jsonObjTemp.getString("errcode");
				if (errcode.equals("0")) {
					if (toUnlock) {
						msg.what = SystemConstant.MSG_TASKS_UPDATE_UNLOCK_STATUS_SUCCESS;

					} else {
						msg.what = SystemConstant.MSG_TASKS_UPDATE_LOCK_STATUS_SUCCESS;
					}
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return true;

				} else {
					if (toUnlock) {
						msg.what = SystemConstant.MSG_TASKS_UPDATE_UNLOCK_STATUS_FAILED;
					} else {
						msg.what = SystemConstant.MSG_TASKS_UPDATE_LOCK_STATUS_FAILED;
					}
					msg.obj = jsonObjTemp.optString("errmsg");
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return false;
				}
			} else {

				if (toUnlock) {
					msg.what = SystemConstant.MSG_TASKS_UPDATE_UNLOCK_STATUS_FAILED;
				} else {
					msg.what = SystemConstant.MSG_TASKS_UPDATE_LOCK_STATUS_FAILED;
				}
				msg.obj = NetError.setErrorType(httpResponse.getStatusLine()
						.getStatusCode());
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);

				return false;
			}
		} catch (Exception e) {

			e.printStackTrace();
			msg = mHandler.obtainMessage();
			if (toUnlock) {
				msg.what = SystemConstant.MSG_TASKS_UPDATE_UNLOCK_STATUS_FAILED;
			} else {
				msg.what = SystemConstant.MSG_TASKS_UPDATE_LOCK_STATUS_FAILED;
			}
			msg.obj = NetError.setErrorType(NetError.ERROR_TYPE_UNCONNECT);
			msg.arg2 = 0;
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);

			return false;
		}
	}

	/**
	 * 获取上次上传的时间信息
	 *
	 */
	public boolean getLastUploadTime(String projectId, String deviceId, String userId) {

		Map<String, String> mapParams = new HashMap<String, String>();
		String res = "";
		Message msg = mHandler.obtainMessage();
		msg.what = SystemConstant.MSG_LAST_FILE_TIME_START;

		if (mThreadState == THREAD_STATE_STOP) {
			return false;
		} else {
			mHandler.sendMessage(msg);
		}

		mapParams.put("access_token", mApp.getToken());
		mapParams.put("projectId", projectId);
		mapParams.put("deviceId", deviceId);

		try {
			res = HttpUtil.post(SystemConstant.URL_TASK_DATA_UPLOAD_TIME,
					mapParams, null, mContext, true, null);

			msg = mHandler.obtainMessage();
			msg.what = SystemConstant.MSG_LAST_FILE_TIME_FAILED;

			JSONObject jobj = new JSONObject(res);
			String val = jobj.optString("errcode");

			if (val.equals("0")) {


				msg = mHandler.obtainMessage();
				msg.what = SystemConstant.MSG_LAST_FILE_TIME_SUCCESS;
				msg.obj = jobj.optString("data");
				lastFileDate = jobj.optString("data");
				if (mThreadState != THREAD_STATE_STOP){
					mHandler.sendMessage(msg);
					return true;
				}

			} else if (val.equals("-1")) {

				msg.what = SystemConstant.MSG_LAST_FILE_TIME_FAILED;
				msg.obj = "获取上传时间失败！" + jobj.optString("errmsg");
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
				return false;
			}
		} catch (Exception e) {

			Log.e("upload", "check----Exception----");

			msg.what = SystemConstant.MSG_LAST_FILE_TIME_EXCEPTION;
			MyLog.writeLogtoFile("获取上传文件时间异常", TAG, "获取上传文件时间异常:" + res);
			if (mThreadState != THREAD_STATE_STOP)
				mHandler.sendMessage(msg);
			cancelUpload();
			return false;
		}

		Log.e("upload", "check----end----");
		msg.what = SystemConstant.MSG_LAST_FILE_TIME_FAILED;
		if (mThreadState != THREAD_STATE_STOP)
			mHandler.sendMessage(msg);
		return false;
	}

	/**
	 * 数据恢复线程
	 *
	 * @author stone
	 */
	class RecoverIncrementalThread extends Thread {

		public boolean bCancel = false;
		public String projectId;
		public String userId;
		public int type;

		public void cancel() {
			bCancel = true;
		}

		public RecoverIncrementalThread(String projectId, String userId,
										int type) {

			this.projectId = projectId;
			this.userId = userId;
			this.type = type;

		}

		@Override
		public void run() {

			boolean isOpenProject=mGbu.isProjectOpen(projectId);
			mGbu.closeAllProject();
			mFMap.setCurrentUser(userId);
			Message msg = mHandler.obtainMessage();
			try {

				String path = mGbu.generateIncrementalDataFromLog(projectId).get(0);


				if(isOpenProject){

					mGbu.openMyProject(projectId, Integer.parseInt(mProjectInfo.getType()));

				}
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_NO_INCREMENTAL;
				msg.arg1 = type;

				mApp.setERROR_DATA(0);// 恢复状态
				// path = "1";//测试用
				if (path.equals("0")) {

					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_NO_INCREMENTAL;
					msg.obj = "没有生成履历";

				} else if (path.equals("1")) {

					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_INCREMENTAL_ERROR;
					msg.obj = "生成履历失败";

				}else if (path.equals("2")) {

					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_NO_INCREMENTAL;
					msg.obj = "无法生成要上传的履历";

				}else if (path.length() > 2) {

					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_RECOVER_SUCCESS;
					String strs[] = path.split(",");
					msg.arg2 = Integer.parseInt(strs[1]);//数量
					msg.obj = strs[0];//文件路径
				}
			}catch(Exception e) {
				e.printStackTrace();
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_INCREMENTAL_ERROR;
			}
			if (!bCancel) {
				if (mThreadState != THREAD_STATE_STOP)
					mHandler.sendMessage(msg);
			}
		}
	}

	/**
	 * 上传情报文件,如果情报文件有错误，会自动恢复，上传，删除数据，并自动下载
	 *
	 * @param userId
	 * @param incrPath履历文件名
	 *            ，如果次值不为空，则不再压缩履历，否则进行履历压缩
	 * @param inforType
	 *            情报类型，1是点情报，2是道路情报
	 */
	public void uploadInforData(final String userId, final String incrPath,
								final String unUploadData, final boolean bDelete,
								final int inforType) {

		if(inforType==1){

			infoDirPath= SystemConstant.PROJECT_USERS_PATH + "/"
					+ userId+ SystemConstant.PROJECT_PATH
					+ "/" + mProjectInfo.getProjectID()
					+SystemConstant.INFORMATION_PATH ;

		}

		new Thread() {

			@Override
			public void run() {
				int type = inforType;
				mFMap.setCurrentUser(userId);
				MyLog.writeLogtoFile("上传情报", TAG,
						"开始上传 time:" + SystemDateTime.getDataTime());
				mbDeleteData = bDelete;

				// 断点续传偏移量
				Message msg = mHandler.obtainMessage();
				String fileName = incrPath;

				if (incrPath != null && unUploadData != null
						&& unUploadData.length() > 0) {// 上传外部传入的情报履历文件
					fileName = incrPath;

					fileName =infoDirPath+ "/" + fileName;
					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_START;
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConstant.MAX_SIZE,new File(fileName).length());
					msg.setData(bundle);
					msg.obj = new File(fileName).getName();
					msg.arg1 = type;
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);

					for (int i = 0; i < unUploadData.length(); i++) {
						String isUpload = unUploadData.substring(i, i + 1);
						if (isUpload.equals("0")) {
							if (i == unUploadData.length() - 1)
								datachunk(new File(fileName), i, true, type);
							else
								datachunk(new File(fileName), i, false, type);
						}
					}
					return;
				} else if (incrPath != null) {
					check(incrPath, false, 1);
					return;
				}

				mbCanback = false;
				MyLog.writeLogtoFile("上传情报履历", TAG, "开始压缩情报履历 time:"
						+ SystemDateTime.getDataTime());
				if (type == 1) {
					fileName = mGbu.generateInforData(mProjectId).get(0);
				} else if (type == 2) {//压缩道路情报
					fileName = mGbu.generateInforRoadData(mProjectId).get(0);
				}
				//分隔开上传的文件和Info数量
				MyLog.writeLogtoFile("上传情报履历", TAG, "结束压缩情报履历 time:"
						+SystemDateTime.getDataTime());
				mbCanback = true;

				if (fileName == null || fileName.equals("0")
						|| fileName.equals("2")) {

					msg = mHandler.obtainMessage();
					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_NO_INCREMENTAL;
					msg.obj = "没有新的情报修改履历";
					msg.arg1 = 1;

					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return;
				}

				if (fileName.equals("1")) {
					msg = mHandler.obtainMessage();
					msg.what = SystemConstant.MSG_PROJECT_SYNDATA_INCREMENTAL_ERROR;
					msg.obj = "情报履历生成异常！需要恢复！";
					msg.arg1 = 1;
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					return;
				}

				// 分隔开上传的文件和POI数量
				String strs[] = fileName.split(",");
				File file = new File(strs[0]);
				MyLog.writeLogtoFile("md5", "md5", " 情报md5开始");
				String md5 = MD5Checksum.getFileMD5(file);
				MyLog.writeLogtoFile("md5", "md5", " 情报md5结束==" + md5);

				if (md5 == null) {

					msg = mHandler.obtainMessage();
					msg.arg1 = 1;
					msg.what = SystemConstant.APP_MD5_FAILED;
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);

					return;
				}

				md5 = StringUtil.paddedCharacter(md5, 32, "0");
				MyLog.writeLogtoFile("md5", "md5", " 情报md5补齐位数==" + md5);

				Log.i("md5", md5 + "===========" + file.getName());
				MyLog.writeLogtoFile("情报上传文件", TAG, "情报文件路径：" + fileName
						+ "; 是否存在:" + file.exists());
				msg.what = SystemConstant.MSG_PROJECT_SYNDATA_START;
				if (strs.length > 1) {

					fileName = strs[0];
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConstant.MAX_SIZE,
							new File(fileName).length());
					msg.setData(bundle);
					msg.arg2 = Integer.parseInt(strs[1]);
					msg.obj = new File(fileName).getName();
					msg.arg1 = type;
					if (mThreadState != THREAD_STATE_STOP)
						mHandler.sendMessage(msg);
					postSnapshot(file, md5, file.length() + "",
							file.getAbsolutePath(), type);
				}
			}
		}.start();
	}

}
