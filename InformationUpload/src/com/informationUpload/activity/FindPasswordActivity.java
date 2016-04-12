package com.informationUpload.activity;

import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;

import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.tool.StringTool;
import com.informationUpload.utils.StringUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
		/**
		 * 发送验证码的url地址
		 */
		private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
		/**
		 * 验证码发送成功
		 */
		private final static int INFORMATION_SUBMIT_SUCCESS=1;
		/**
		 * 存储找回密码验证码的 偏好 
		 */
		private SharedPreferences mSharePre;
		/**
		 * 计时器
		 */
		private MyCount mc;
		/**
		 * 偏好编辑器
		 */
		private Editor editor;
		/**
		 * 电话号码
		 */
		private String telNum;
		Handler handler=new Handler(){
			
			

			public void handleMessage(android.os.Message msg) {
				
				switch (msg.what) {
				case INFORMATION_SUBMIT_SUCCESS:
				mc = new MyCount(120000, 1000);
				mc.start();
				break;
				
				}
				}
			};
		
		
		@Override
        protected void onCreate(Bundle savedInstanceState) {
        	// TODO Auto-generated method stub
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.find_password);
        	mSharePre = FindPasswordActivity.this.getSharedPreferences(SystemConfig.VERIFICATION_CODE,
    				Context.MODE_PRIVATE);
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
					telNum=mTelephoneNum.getText().toString();
					if (TextUtils.isEmpty(telNum)) {

						Toast.makeText(FindPasswordActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
						return;
					} else if (!StringTool.isTelNum(telNum)) {
						Toast.makeText(FindPasswordActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
						return;
					}
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							//获取验证码
							sendSmsMessage();
							
						}
					}).start();
					
				}
			});
			//下一步
			mNext.setOnClickListener(new OnClickListener() {
				
				

				@Override
				public void onClick(View arg0) {
					password=mPassword.getText().toString().trim();
					telNum=mTelephoneNum.getText().toString().trim();
					
//					if (TextUtils.isEmpty(telNum)) {
//
//						Toast.makeText(FindPasswordActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
//						return;
//					} else if (!StringTool.isTelNum(telNum)) {
//						Toast.makeText(FindPasswordActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
//						return;
//					}
//					if(!mIdentifyingCode.getText().toString().trim().equals("")&&!mIdentifyingCode.getText().toString().trim().equals(mSharePre.getString(SystemConfig.FINDPASSWORD_VERIFICATION_CODE,""))){
//						Toast.makeText(FindPasswordActivity.this, "验证码不正确！！！", Toast.LENGTH_SHORT).show();
//						return;
//					}
//					if (TextUtils.isEmpty(password)) {
//						Toast.makeText(FindPasswordActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
//						return;
//					}
//					if(password.trim().length()<6){
//						Toast.makeText(FindPasswordActivity.this, "密码不能小于6位",Toast.LENGTH_SHORT).show();
//						return;
//					}
					//修改密码
					modify(password);
					
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
	    public void sendSmsMessage(){ 
	    	HttpClient client = new HttpClient(); 
			PostMethod method = new PostMethod(Url); 
				
			//client.getParams().setContentCharset("GBK");		
			client.getParams().setContentCharset("UTF-8");
			method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

			
			int mobile_code = (int)((Math.random()*9+1)*100000);

			Log.i("info","mobile_code:"+mobile_code);
			
		    String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。"); 

			NameValuePair[] data = {//提交短信
				    new NameValuePair("account", "cf_navinfo"), 
				    new NameValuePair("password", StringUtil.MD5Encode("biohazard3")), //密码可以使用明文密码或使用32位MD5加密
				    //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
				    new NameValuePair("mobile",mTelephoneNum.getText().toString()), 
				    new NameValuePair("content", content),
			};
			
			method.setRequestBody(data);		
			
			
			try {
				client.executeMethod(method);	
				
				String SubmitResult =method.getResponseBodyAsString();
						
				//System.out.println(SubmitResult);

				Document doc = DocumentHelper.parseText(SubmitResult); 
				Element root = doc.getRootElement();


				String code = root.elementText("code");	
				String msg = root.elementText("msg");	
				String smsid = root.elementText("smsid");	
				
				
				Log.i("info","code:"+code);
				Log.i("info","msg:"+msg);
				Log.i("info","smsid:"+smsid);
				
							
				 if("2".equals(code)){
					 
					 
						editor = mSharePre.edit();
						editor.putString(SystemConfig.FINDPASSWORD_VERIFICATION_CODE,mobile_code+"").commit();
						handler.sendEmptyMessage(INFORMATION_SUBMIT_SUCCESS);
					
					Log.i("info","短信提交成功！！！");
				}else{
					Toast.makeText(FindPasswordActivity.this,msg,Toast.LENGTH_LONG).show();
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
				
	    }
		//11.情报用户信息修改接口
	    public void modify(String password){
	    	HashMap<String,Object> map=new HashMap<String, Object>();
	    	map.put("userPhone",telNum);
	  
    	    map.put("pwd",password);
//	    	map.put("nickname","fchen");
	    	ServiceEngin.getInstance().Request(FindPasswordActivity.this, map,"informodifyuserinfo",new EnginCallback(FindPasswordActivity.this){
	    		@Override
	    		public void onSuccess(ResponseInfo arg0) {
	    			// TODO Auto-generated method stub
	    			super.onSuccess(arg0);
	    			String result;
	    			result=arg0.result.toString();
	    			
	    		Log.e("请求成功",result);
	    		parseModifyJson(result);
	    		}
	    		@Override
	    		public void onFailure(HttpException arg0, String arg1) {
	    			// TODO Auto-generated method stub
	    			super.onFailure(arg0, arg1);
	    		}
	    	});
	    }
	    /**
	     *对修改密码返回的json进行解析
	     * @param result
	     */
		protected void parseModifyJson(String result) {
		      JSONObject jsonObj = JSON.parseObject(result);
		      String errcode = jsonObj.getString("errcode");
		      String errmsg = jsonObj.getString("errmsg");
		      if(null!=errcode&&!"".equals(errcode)&&errcode.equals("0")){
		    	  Toast.makeText(FindPasswordActivity.this,"密码修改成功！！！",Toast.LENGTH_SHORT).show();
		    	  finish();
		      }else{
		    	  Toast.makeText(FindPasswordActivity.this,errmsg,Toast.LENGTH_SHORT).show();
		      }
		}
		/**
		 * 倒计时类
		 * 
		 * @author chentao
		 * 
		 */
		private class MyCount extends CountDownTimer {

			public MyCount(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);
				// TODO Auto-generated constructor stub
			}

			@Override
			public void onFinish() {
				mGetIdentifyingCode.setText("获取手机验证码");
				mGetIdentifyingCode.setClickable(true);
				editor.putString(SystemConfig.FINDPASSWORD_VERIFICATION_CODE,"").commit();
//				testGetCode.setBackgroundDrawable(new BitmapDrawable(
//						getResources(), ImageThumbnail.getSoftBitmap(context,
//								R.drawable.f_register_codebutton).get()));

			}

			@Override
			public void onTick(long millisUntilFinished) {
				mGetIdentifyingCode.setText("剩余" + millisUntilFinished / 1000 + "秒");
				mGetIdentifyingCode.setClickable(false);
				// 置灰
//				testGetCode.setBackgroundDrawable(new BitmapDrawable(
//						getResources(), ImageThumbnail.getSoftBitmap(context,
//								R.drawable.f_register_codebutton).get()));

			}

		}
}
