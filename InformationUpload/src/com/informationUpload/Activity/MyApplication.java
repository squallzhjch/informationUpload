package com.informationUpload.Activity;

import android.app.Application;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.informationUpload.VoiceSpeech.VoiceSpeechManager;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.tool.ImageTool;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MyApplication
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MyApplication extends Application{

	private VoiceSpeechManager mVoiceSpeechManager;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		mVoiceSpeechManager = VoiceSpeechManager.getInstance();
		mVoiceSpeechManager.init(this);

		IntentHelper.getInstance().init(this);
	}
}
