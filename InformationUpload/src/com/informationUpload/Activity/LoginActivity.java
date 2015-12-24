package com.informationUpload.activity;


import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.informationUpload.R;
import com.informationUpload.serviceengin.EnginCallback;
import com.informationUpload.serviceengin.ServiceEngin;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 登录页面
 * @author chentao
 *
 */
public class LoginActivity extends BaseActivity{


	/**
	 * 输入用户名edittext
	 */
	private EditText mUserName;
	/**
	 * 输入密码edittext
	 */
	private EditText mPassword;
	/**
	 * 登录textview
	 */
	private TextView mLogin;
	/**
	 * 找回密码textview
	 */
	private TextView mFindPassword;
	/**
	 * 快速注册按
	 */
	private TextView mQuickRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_user_login);
		
		
		
		//初始化
		init();
		//注册监听器
		addListeners();
		HashMap<String,String> map=new HashMap<String, String>();
		map.put("chen","tao");
		String servicePara = JSON.toJSONString(map);
		ServiceEngin.Request(LoginActivity.this,"","", servicePara,new EnginCallback(LoginActivity.this){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				Log.e("请求成功",arg0.result.toString());
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				Log.e("请求失败",arg0+"");
			}
		});
	}
	//初始化
	private void init() {
		mUserName=(EditText)findViewById(R.id.username_et);
		mPassword=(EditText)findViewById(R.id.password_et);
		mLogin=(TextView)findViewById(R.id.login_tv);
		mFindPassword=(TextView)findViewById(R.id.findpassword_tv);
		mQuickRegister=(TextView)findViewById(R.id.quick_register);
	}
	//注册监听器
	private void addListeners() {
        //登录
		mLogin.setOnClickListener(new OnClickListener() {

			private String name;
			private String password;

			@Override
			public void onClick(View arg0) {
				name=mUserName.getText().toString();
				password=mPassword.getText().toString();
				LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
				LoginActivity.this.finish();
			}
		});
		//找回密码
		mFindPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LoginActivity.this.startActivity(new Intent(LoginActivity.this,FindPasswordActivity.class));

			}
		});
		//快速注册
		mQuickRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LoginActivity.this.startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

			}
		});

	}

}
