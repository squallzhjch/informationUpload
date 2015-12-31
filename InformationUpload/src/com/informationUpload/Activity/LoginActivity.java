package com.informationUpload.activity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.informationUpload.R;
import com.informationUpload.entity.attachmentsMessage;
import com.informationUpload.entity.locationMessage;
import com.informationUpload.serviceengin.EnginCallback;
import com.informationUpload.serviceengin.ServiceEngin;
import com.informationUpload.utils.ZipUtil;
import com.lidroid.xutils.db.annotation.Check;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
	/**
	 * 改变密码状态
	 */
	private CheckBox mChangeStatePassword;
	



	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acitivity_user_login);
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");



		//初始化
		init();
		//注册监听器
		addListeners();


//		SharedPreferences sp = LoginActivity.this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
//		String userName = sp.getString("user_name",null);
//		String userTel = sp.getString("user_tel", null);
//		String is_login = sp.getString("is_login","1");//0代表登录 1代表未登录
//		if(null==userName){
//			String time=df.format(new Date());
//			String uuid = UUID.randomUUID().toString().replace("-", "");
//			String userid = (time+uuid).replace("-","").replace(" ","").replace(":","");
//		
//			sp.edit().putString("user_name",userid).commit();
//			LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
//		}else{
//			if(userTel==null){
//				LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
//			}else{
//				if("1".equals(is_login)){
//					mUserName.setText(userTel);
//				}else if("0".equals(is_login)){
//					LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
//				}
//			}
//		}

	





	}
	//初始化
	private void init() {
		mUserName=(EditText)findViewById(R.id.username_et);
		mPassword=(EditText)findViewById(R.id.password_et);
		mLogin=(TextView)findViewById(R.id.login_tv);
		mFindPassword=(TextView)findViewById(R.id.findpassword_tv);
		mQuickRegister=(TextView)findViewById(R.id.quick_register);
		mChangeStatePassword=(CheckBox)findViewById(R.id.change_state_password);

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
//				//登录
//                Login(name,password);
				
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

		// 密码显示隐藏
		mChangeStatePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked == true) {

					//
					mPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					mPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

					mPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					// inputPassword
					// .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

					mPassword.setSelection(mPassword.getText()
							.toString().trim().length());
					mPassword.setTextColor(Color.parseColor("#666666"));
				} else {

					//
					mPassword
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					mPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

					mPassword
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					//
					mPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

					mPassword.setSelection(mPassword.getText()
							.toString().trim().length());
					mPassword.setTextColor(Color.parseColor("#666666"));
				}
			}
		});

	}
	//登录
	protected void Login(String name,String password) {
		HashMap<String,Object> map=new HashMap<String, Object>();
		map.put("tel",name);
		map.put("pwd",password);
		ServiceEngin.Request(LoginActivity.this, map, "inforlogin",new EnginCallback(LoginActivity.this){
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
				Log.e("请求失败",arg1);
			}
		});
		
	}

}
