package com.informationUpload.fragments;

import com.informationUpload.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * 
 * @author chentao
 *
 */
public class MyRecordFragment extends BaseFragment {

    /**
     * 主view	
     */
	private View view;
	/**
	 * 我的名字
	 */
	private TextView mTvName;
	/**
	 * 我的记录个数
	 */
	private TextView mMyrecordNumTv;
	/**
	 * 返回按钮
	 */
	private TextView mMyrecordNumBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.fragment_my_record,null);
		//初始化界面
		init();
		//添加监听器
		addListeners();
		return view;
	}
    //初始化界面
	private void init() {
		mTvName=(TextView)view.findViewById(R.id.myrecord_num_tv_name);
		mMyrecordNumTv=(TextView)view.findViewById(R.id.myrecord_num_tv);
		mMyrecordNumBack=(TextView)view.findViewById(R.id.myrecord_num_back);
		
		
		
		
	}
    //添加监听器
	private void addListeners() {
		//返回
		mMyrecordNumBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFragmentManager.back();
				
			}
		});
		
	}
}
