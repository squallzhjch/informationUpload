package com.informationUpload.activity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.tool.StringTool;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
	
	public static LoginActivity mLoginActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mLoginActivity = this;
		if(savedInstanceState == null) {
			setContentView(R.layout.acitivity_user_login);
			final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

			//初始化
			init();
			//注册监听器
			addListeners();

			Bundle extras = getIntent().getExtras();
			boolean bFirst = true;
			if (extras != null) {
				bFirst = extras.getBoolean("bFirst");
			}

			SharedPreferences sp = LoginActivity.this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
			String userName = sp.getString("user_name", null);
			String userTel = sp.getString("user_tel", null);
			String is_login = sp.getString("is_login", "1");//0代表登录 1代表未登录
			if (null == userName && bFirst) {
				String time = df.format(new Date());
				String uuid = UUID.randomUUID().toString().replace("-", "");
				String userid = time + uuid;

				sp.edit().putString("user_name", userid).commit();

				LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
				LoginActivity.this.finish();
			} else {
				if (userTel == null && bFirst) {

					LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
					LoginActivity.this.finish();
				} else {
					if ("1".equals(is_login)) {
						mUserName.setText(userTel);
					} else if ("0".equals(is_login)) {

						LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
						LoginActivity.this.finish();
					}
				}
			}
		}

	}

	@Override
	protected void onDestroy() {
		mLoginActivity = null;
		super.onDestroy();
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
				if(TextUtils.isEmpty(name)){

					Toast.makeText(LoginActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
					return;
				}else if(!StringTool.isTelNum(name)){
					Toast.makeText(LoginActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(password)){
					Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
					return;
				}
//				//登录
                Login(name,password);
				
			}
		});
		//找回密码
		mFindPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				LoginActivity.this.startActivity(new Intent(LoginActivity.this,FindPasswordActivity.class));
				LoginActivity.this.finish();
			}
		});
		//快速注册
		mQuickRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
	
	String errcode =""+	jsonObj.getInteger("errcode");
	String errmsg =	jsonObj.getString("errmsg");
	if(!"".equals(errcode)&&null!=errcode&&"0".equals(errcode)){
		String userid =	jsonObj.getString("data");
		SharedPreferences sp = LoginActivity.this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		
		sp.edit().putString("user_name",userid).commit();
		sp.edit().putString("is_login", "0").commit();
		MainActivity mainActivity = new MainActivity();
		if(mainActivity.mMainActivity != null) {
			mainActivity.mMainActivity.finish();
		}
		LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
		LoginActivity.this.finish();
	}else{
		Toast.makeText(LoginActivity.this,errmsg,Toast.LENGTH_SHORT).show();
	}
	
		
	}

}
