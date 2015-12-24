package com.informationUpload.fragments;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.informationUpload.R;
import com.informationUpload.activity.OffLineMapActivity;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.utils.FileUtils;

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
	private RelativeLayout mMyRecordLayout;
	/**
	 * 离线地图
	 */
	private RelativeLayout mOfflineMapLayout;
	/**
	 * 新闻中心
	 */
	private RelativeLayout mNewsCenterLayout;
	/**
	 * 清除缓存
	 */
	private RelativeLayout mClearCacheLayout;
	/**
	 * 意见反馈
	 */
	private RelativeLayout 	mFeedBackLayout;
	/**
	 * 关于
	 */
	private RelativeLayout mAboutLayout;


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
		mMyRecordLayout = (RelativeLayout) view.findViewById(R.id.my_record);

		mOfflineMapLayout = (RelativeLayout) view.findViewById(R.id.offline_map);

		mNewsCenterLayout = (RelativeLayout) view.findViewById(R.id.news_center);

		mClearCacheLayout = (RelativeLayout) view.findViewById(R.id.clear_cache);

		mFeedBackLayout = (RelativeLayout) view.findViewById(R.id.feedback);

		mAboutLayout = (RelativeLayout) view.findViewById(R.id.about);





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
		//我的战绩
		mMyRecordLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		//离线地图
		mOfflineMapLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().startActivity(new Intent(getActivity(),OffLineMapActivity.class));

			}
		});
		//新闻中心
		mNewsCenterLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		//清除缓存
		mClearCacheLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					File file=new File(Environment.getExternalStorageDirectory() + "/MyPicture/");
					FileUtils.deleteFile(file);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
				}
				
				Toast.makeText(getActivity(),"清除缓存成功！",Toast.LENGTH_SHORT).show();
			}
		});
		//意见反馈
		mFeedBackLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		// 关于
		mAboutLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	}


}
