package com.informationUpload.activity;


import java.util.HashMap;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.serviceengin.EnginCallback;
import com.informationUpload.serviceengin.ServiceEngin;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {

	/**
	 * 电话号码输入框
	 */
	private EditText mTelephoneNum;
	/**
	 * 密码输入框
	 */
	private EditText mRegisterPassword;

	/**
	 * 注册
	 */
	private TextView mRegister;
	/**
	 * 返回
	 */
	private RelativeLayout register_back;
	/**
	 * 改变密码状态
	 */
	private CheckBox mChangeStatePassword;
	/**
	 * 存取用户信息的
	 */
	private SharedPreferences sp;
	/**
	 * 注册电话号码
	 */
	private String telNum;
	/**
	 * 注册密码
	 */
	private String rgpassword;
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
		mRegisterPassword=(EditText)findViewById(R.id.register_password);
		mChangeStatePassword=(CheckBox)findViewById(R.id.change_state_password);
		mRegister=(TextView)findViewById(R.id.register_tv);
		register_back=(RelativeLayout)findViewById(R.id.register_back);

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

		//注册
		mRegister.setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View arg0) {
				telNum = mTelephoneNum.getText().toString();
				rgpassword = mRegisterPassword.getText().toString();
				//注册	
				register(telNum,rgpassword);

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
					mRegisterPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					mRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

					mRegisterPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					// inputPassword
					// .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

					mRegisterPassword.setSelection(mRegisterPassword.getText()
							.toString().trim().length());
					mRegisterPassword.setTextColor(Color.parseColor("#666666"));
				} else {

					//
					mRegisterPassword
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					mRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

					mRegisterPassword
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					//
					mRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

					mRegisterPassword.setSelection(mRegisterPassword.getText()
							.toString().trim().length());
					mRegisterPassword.setTextColor(Color.parseColor("#666666"));
				}
			}
		});


	}
	//注册
	protected void register(final String telNum,final String telpassword) {
		sp = RegisterActivity.this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		String userName = sp.getString("user_name",null);
		Log.i("chentao","user_name:"+userName);
		HashMap<String,Object> map=new HashMap<String, Object>();
		map.put("tel", telNum);
		map.put("pwd", telpassword);
		map.put("uuid",userName);
		ServiceEngin.Request(RegisterActivity.this, map, "inforegist" ,new EnginCallback(RegisterActivity.this){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				Log.e("请求成功",arg0.result.toString());
				//进行json解析
				parseJson(arg0.result.toString());
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				Log.e("请求失败",arg1);
			}
		});

	}
	//进行json解析
	protected void parseJson(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		String errcode=""+jsonObj.getInteger("errcode");
		Log.i("chentao", "errcode:"+errcode);
		String errmsg=jsonObj.getString("errmsg");
		if(!"".equals(errcode)&&null!=errcode&&"0".equals(errcode)){
			Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
			sp.edit().putString("user_tel",telNum).commit();
			sp.edit().putString("is_login","0").commit();
			startActivity(new Intent(RegisterActivity.this,MainActivity.class));
			RegisterActivity.this.finish();

		}else{
			if(errmsg.equals("用户已经存在")){
				Toast.makeText(RegisterActivity.this,errmsg, Toast.LENGTH_SHORT).show();
				sp.edit().putString("user_tel",telNum).commit();
				sp.edit().putString("is_login","1").commit();
				startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
			}else{
				Toast.makeText(RegisterActivity.this,errmsg, Toast.LENGTH_SHORT).show();
				
			}
			
		}

	}

}
