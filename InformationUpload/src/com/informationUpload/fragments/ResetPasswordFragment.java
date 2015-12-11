package com.informationUpload.fragments;


import com.informationUpload.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;

public class ResetPasswordFragment extends Fragment{
	/**
	 * 主view
	 */
	
	private View view;
	/**
	 * 返回
	 */
	private TextView mResetpasswordBack;
	/**
	 * 设置按钮
	 */
	private EditText mResetPasswordEt;
	/**
	 * 确认修改
	 */
	private TextView confirm_modify;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_reset_password,null);
		//初始化
		init();
		//添加监听器
		addListeners();
		return view;
	}
    //初始化
	private void init() {
		mResetpasswordBack=(TextView)view.findViewById(R.id.resetpassword_back);
		mResetPasswordEt=(EditText)view.findViewById(R.id.reset_password_et);
		confirm_modify  =  (TextView)view.findViewById(R.id.confirm_modify);
		
	}
    //添加监听器
	private void addListeners() {
		//返回
		mResetpasswordBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().popBackStackImmediate();
				
			}
		});
		//确认修改
		confirm_modify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

}
