package com.informationUpload.activity;


import com.informationUpload.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity {
	
	 /**
     * 电话号码输入框
     */
    private EditText mTelephoneNum;
    /**
     * 验证码输入框
     */
	private EditText mIdentifyingCode;
	/**
	 * 获取验证码
	 */
	private TextView mGetIdentifyingCode;
	/**
	 * 注册
	 */
	private TextView mRegister;
	/**
	 * 返回
	 */
	private TextView register_back;
	@Override
   protected void onCreate(Bundle savedInstanceState) {
   	// TODO Auto-generated method stub
   	super.onCreate(savedInstanceState);
   	setContentView(R.layout.register);
   	//初始化
   	init();
   	//添加监听器
   	addListeners();
   }
   //初始化
	private void init() {
		mTelephoneNum=(EditText)findViewById(R.id.register_telephonenum_et);
		mIdentifyingCode=(EditText)findViewById(R.id.register_identifying_code_et);
		mGetIdentifyingCode=(TextView)findViewById(R.id.register_get_identifying_code_tv);
		mRegister=(TextView)findViewById(R.id.register_tv);
		register_back=(TextView)findViewById(R.id.register_back);
		
	}
   //添加监听器
	private void addListeners() {
		//返回
		register_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				RegisterActivity.this.finish();
				
			}
		});
		//获取验证码
		mGetIdentifyingCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//注册
		mRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
			}
		});
		
		
	}

}
