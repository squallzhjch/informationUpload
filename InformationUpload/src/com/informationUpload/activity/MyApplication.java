package com.informationUpload.activity;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.voiceSpeech.VoiceSpeechManager;
import com.informationUpload.contentProviders.InformationManager;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.LocationManager;
import com.informationUpload.thread.ThreadManager;
import com.informationUpload.utils.PoiRecordPopup;
import com.informationUpload.system.SystemConfig;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MyApplication
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MyApplication extends Application{

    private String mUserId;

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);

        VoiceSpeechManager.getInstance().init(this);
        ThreadManager.getInstance().init(this);
        IntentHelper.getInstance().init(this);
        InformationManager.getInstance().init(this);
        LocationManager.getInstance().init(this);
        PoiRecordPopup.getInstance().init(this, SystemConfig.DATA_CHAT_PATH);
        ConfigManager.getInstance().init(this);

	}
}
