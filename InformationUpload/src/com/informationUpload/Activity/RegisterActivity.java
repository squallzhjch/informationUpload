package com.informationUpload.activity;


import com.informationUpload.R;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

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
	private TextView register_back;
	/**
	 * 改变密码状态
	 */
	private CheckBox mChangeStatePassword;
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
	
		//注册
		mRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {


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

}
