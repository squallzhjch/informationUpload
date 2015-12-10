package com.informationUpload.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.informationUpload.R;
import com.informationUpload.fragments.utils.IntentHelper;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: CenterFragment
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class UserCenterFragment extends BaseFragment{
	/**
	 * 主view
	 */
	private View view;
	/**
	 * 上报记录
	 */
	private RelativeLayout mRecordLayout;
	/**
	 * 我的战绩
	 */
    private RelativeLayout my_recordLayout;
    /**
     * 离线地图
     */
	private RelativeLayout offline_mapLayout;
	/**
	 * 新闻中心
	 */
	private RelativeLayout news_centerLayout;
	/**
	 * 清除缓存
	 */
	private RelativeLayout clear_cacheLayout;
	private RelativeLayout feedbackLayout;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		view = inflater.inflate(R.layout.fragment_user_center, null, true);
		//初始化
		init();
		//添加监听器
		addListeners();
		return view;
	}


	/**
	 * 初始化
	 */
	private void init() {
		mRecordLayout = (RelativeLayout) view.findViewById(R.id.report_record);
		my_recordLayout = (RelativeLayout) view.findViewById(R.id.my_record);

		offline_mapLayout = (RelativeLayout) view.findViewById(R.id.offline_map);

		news_centerLayout = (RelativeLayout) view.findViewById(R.id.news_center);

		clear_cacheLayout = (RelativeLayout) view.findViewById(R.id.clear_cache);

		feedbackLayout = (RelativeLayout) view.findViewById(R.id.feedback);

		mRecordLayout = (RelativeLayout) view.findViewById(R.id.report_record);

		mRecordLayout = (RelativeLayout) view.findViewById(R.id.report_record);



	}
	/**
	 * 注册监听器
	 */
	private void addListeners() {
		//上报记录
		mRecordLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(ReportRecordFragment.class, null));
			}
		});

	}


}
