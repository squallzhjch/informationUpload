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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindPasswordActivity  extends BaseActivity{
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
		 * 下一步
		 */
		private TextView mNext;
		/**
		 * 返回
		 */
		private RelativeLayout mBack;
		/**
		 * 改变密码状态
		 */
		private CheckBox mChangeStatePassword;
		/**
		 * 输入密码edittext
		 */
		private EditText mPassword;
		/**
		 * 输入的密码
		 */
		private String password;
		@Override
        protected void onCreate(Bundle savedInstanceState) {
        	// TODO Auto-generated method stub
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.find_password);
        	//初始化
        	init();
        	//添加监听器
        	addListeners();
        }                
        //初始化
		private void init() {
			 mBack = (RelativeLayout)findViewById(R.id.back);
			 mPassword=(EditText)findViewById(R.id.password_et);
			 mChangeStatePassword=(CheckBox)findViewById(R.id.change_state_password);
			mTelephoneNum=(EditText)findViewById(R.id.telephonenum_et);
			mIdentifyingCode=(EditText)findViewById(R.id.identifying_code_et);
			mGetIdentifyingCode=(TextView)findViewById(R.id.get_identifying_code_tv);
			mNext=(TextView)findViewById(R.id.next_tv);
			
		}
        //添加监听器
		private void addListeners() {
			//返回
			mBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					FindPasswordActivity.this.finish();
					
				}
			});
			//获取验证码
			mGetIdentifyingCode.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			//下一步
			mNext.setOnClickListener(new OnClickListener() {
				
				

				@Override
				public void onClick(View arg0) {
					password=mPassword.getText().toString();
					
					
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
	
}
