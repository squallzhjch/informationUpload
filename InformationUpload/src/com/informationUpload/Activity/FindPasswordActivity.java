package com.informationUpload.activity;

import com.informationUpload.R;
import com.informationUpload.fragments.ResetPasswordFragment;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
		private TextView mBack;
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
			 mBack = (TextView)findViewById(R.id.back);
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
					FindPasswordActivity.this.getSupportFragmentManager().beginTransaction()
					.replace(R.id.all_ll,new ResetPasswordFragment()).addToBackStack(null).commit();
					
				}
			});
			
			
		}
	
}
