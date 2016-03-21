package com.informationUpload.fragments;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.informationUpload.R;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.system.LoginHelper;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.tool.StringTool;
import com.informationUpload.utils.StringUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: RegisterFragment
 * @Date 2016/1/18
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class RegisterFragment extends BaseFragment {
	
	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";

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
	 * 注册电话号码
	 */
	private String telNum;
	/**
	 * 注册密码
	 */
	private String rgpassword;
	/**
	 * 确认注册密码状态改变
	 */
	private CheckBox mConfirmChangeStatePassword;
	/**
	 * 确认密码et
	 */
	private EditText mConfirmRegisterPassword;
	/**
	 * 确认密码值
	 */
	private String confirmpassword;
	/**
	 * 获取验证码按钮
	 */
	private TextView mGetVerificationCodeTv;
	/**
	 * 获取验证码输入框
	 */
	private EditText mGetVerificationCodeEt;
    /**
     * 偏好设置
     */
	private SharedPreferences mSharePre;
    /**
     * 偏好设置编辑器
     */
	private Editor editor;

	@Override
	public void onDataChange(Bundle bundle) {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.register, null, true);
		mSharePre = getActivity().getSharedPreferences(SystemConfig.VERIFICATION_CODE,
				Context.MODE_PRIVATE);
		initView(view);
		//添加监听器
		addListeners();
		return view;
	}

	private void initView(View view ){
		
		mTelephoneNum=(EditText)view.findViewById(R.id.register_telephonenum_et);
		mRegisterPassword=(EditText)view.findViewById(R.id.register_password);
		mConfirmRegisterPassword= (EditText)view.findViewById(R.id.confirm_register_password);
		mChangeStatePassword=(CheckBox)view.findViewById(R.id.change_state_password);
		mConfirmChangeStatePassword = (CheckBox) view.findViewById(R.id.confirm_change_state_password);
		mRegister=(TextView)view.findViewById(R.id.register_tv);
		register_back=(RelativeLayout) view.findViewById(R.id.register_back);
		mGetVerificationCodeTv  =    (TextView)  view.findViewById(R.id.get_verification_code_tv);
		mGetVerificationCodeEt=  (EditText)   view.findViewById(R.id.get_verification_code_et);
	}
	//添加监听器
	private void addListeners() {
		//获取验证码
		mGetVerificationCodeTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//查重，看是否该手机号有 没有注册过
				queryrepeat();
			}
		});
		//返回
		register_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mFragmentManager.back();
			}
		});

		//注册
		mRegister.setOnClickListener(new View.OnClickListener() {



			@Override
			public void onClick(View arg0) {
				telNum = mTelephoneNum.getText().toString();
				rgpassword = mRegisterPassword.getText().toString();
				confirmpassword=mConfirmRegisterPassword.getText().toString();
				if (TextUtils.isEmpty(telNum)) {

					Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
					return;
				} else if (!StringTool.isTelNum(telNum)) {
					Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(rgpassword)) {
					Toast.makeText(getActivity(), "请输入密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(rgpassword.trim().length()<6){
					Toast.makeText(getActivity(), "密码不能小于6位",Toast.LENGTH_SHORT).show();
					return;
				}
				if(!confirmpassword.equals(rgpassword)){
					Toast.makeText(getActivity(), "输入密码与确认密码不同", Toast.LENGTH_SHORT).show();
					return;
				}
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
				  
				imm.hideSoftInputFromWindow(mRegister.getWindowToken(), 0); //强制隐藏键盘  

				//注册
				LoginHelper.register(getActivity(), telNum, rgpassword, new LoginHelper.OnEnginCallbackListener() {
					@Override
					public void onSuccess() {
						if(mIsActive)
							MyFragmentManager.getInstance().switchFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));
					}

					@Override
					public void onFailed() {

					}
				});

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
		// 确认密码显示隐藏
		mConfirmChangeStatePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked == true) {
					//
					mConfirmRegisterPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					mConfirmRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mConfirmRegisterPassword
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
					// inputPassword
					// .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

					mConfirmRegisterPassword.setSelection(mConfirmRegisterPassword.getText()
							.toString().trim().length());
					mConfirmRegisterPassword.setTextColor(Color.parseColor("#666666"));
				} else {
					//
					mConfirmRegisterPassword
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					mConfirmRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

					mConfirmRegisterPassword
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
					//
					mConfirmRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

					mConfirmRegisterPassword.setSelection(mConfirmRegisterPassword.getText()
							.toString().trim().length());
					mConfirmRegisterPassword.setTextColor(Color.parseColor("#666666"));
				}
			}
		});
	}
    //情报用户查重接口，看是否注册电话已经注册过
    public void queryrepeat(){
    	HashMap<String,Object> map=new HashMap<String, Object>();
    	map.put("tel",mTelephoneNum.getText().toString());
    	ServiceEngin.getInstance().Request(getActivity(), map,"inforqueryuserexit",new EnginCallback(getActivity()){
    		@Override
    		public void onSuccess(ResponseInfo arg0) {
    			// TODO Auto-generated method stub
    			super.onSuccess(arg0);
    			String result;
    			result=arg0.result.toString();
    			
    		    parseJson(result);
    			Log.e("请求成功", result);
    			
    		}
    		@Override
    		public void onFailure(HttpException arg0, String arg1) {
    			// TODO Auto-generated method stub
    			super.onFailure(arg0, arg1);
    		}
    	});
    }
    /**
     * 
     * @param result
     */
    protected void parseJson(String result) {
		           JSONObject  jsonObj=JSON.parseObject(result);
		           String errcode = jsonObj.getString("errcode");
		           String errmsg = jsonObj.getString("errmsg");
		           if(null!=errcode&&!"".equals(errcode)&&errcode.equals("0")){
		        	   JSONObject dataObj = jsonObj.getJSONObject("data");
		        	   String exit = dataObj.getString("exit");
		        	   if(exit.equals("true")){
		        		   Toast.makeText(getActivity(), "您好该号已经被注册！", Toast.LENGTH_LONG).show();
		        	   }else if(exit.equals("false")){
		        		   ;
		        		   if (TextUtils.isEmpty(mTelephoneNum.getText().toString())) {

		   					Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
		   					return;
		   				} else if (!StringTool.isTelNum(mTelephoneNum.getText().toString())) {
		   					Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
		   					return;
		   				}
		        		  new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								   sendSmsMessage();
								
							}
						}).start();
		        		
		        	   }
		           }else{
		        	   Toast.makeText(getActivity(),errmsg, Toast.LENGTH_LONG).show();
		           }
		          
		
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
					editor.putString(SystemConfig.GET_VERIFICATION_CODE,mobile_code+"").commit();
				Log.i("info","短信提交成功！！！");
			}else{
				Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
    }

}
