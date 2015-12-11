package com.informationUpload.activity;

import android.app.Application;

import com.informationUpload.VoiceSpeech.VoiceSpeechManager;
import com.informationUpload.contentproviders.InformationManager;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.LocationManager;
import com.informationUpload.thread.ThreadManager;
import com.informationUpload.utils.PoiRecordPopup;
import com.informationUpload.utils.SystemConfig;

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
//		SDKInitializer.initialize(this);

        VoiceSpeechManager.getInstance().init(this);
        ThreadManager.getInstance().init(this);
        IntentHelper.getInstance().init(this);
        InformationManager.getInstance().init(this);
        LocationManager.getInstance().init(this);
        PoiRecordPopup.getInstance().init(this, SystemConfig.DATA_CHAT_PATH);

        setUserId("10000");
	}
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }
}
