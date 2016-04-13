package com.informationUpload.utils;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.contents.ContentsManager;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.SystemConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 公共对话框
 * 
 * @author chentao
 * 
 */
public class CommonDialog extends Dialog {
	/**
	 * 上下文环境
	 */
	private Context context;
	/**
	 * 昵称输入edittext
	 */
	private EditText mEtName;
	/**
	 * 昵称输入框输入字数显示textview
	 */
	private TextView mTvNameNum;
	/**
	 * 取消textview
	 */
	private TextView mCancel;
	/**
	 * 确认textview
	 */
	private TextView mConfirm;
	/**
	 * 要修改的昵称
	 */
	private String nickName;
	/**
	 * 要修改的账户号码
	 */
	private String telNum;
	/**
	 * 昵称显示栏
	 */
	private TextView mNameTv;
	private RelativeLayout mConfirmRl;
	private RelativeLayout mCancelRl;

	public CommonDialog(Context context,TextView mNameTv) {
		super(context, R.style.customerDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mNameTv=mNameTv;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.common_dialog);

		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		params.width = (int) (display.getWidth()* 0.7);
		params.height =  (int) (display.getHeight() *0.25);
				
		//		params.x=260;
		//	    params.y=100;
		//		this.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		this.getWindow().setAttributes(params);
		//初始化
        init();
        //添加监听器
        addListeners();
      
	}
    
    //添加监听器
    private void addListeners() {
    	//确认
    	mConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String nickName = mEtName.getText().toString();
				if("".equals(nickName)){
					Toast.makeText(context,"修改昵称，昵称不能为空！",Toast.LENGTH_SHORT).show();
				}else{
					dismiss();
					modify(nickName);
				}
				
				
			}

			

		
		});
    	//确认
    	mConfirmRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String nickName = mEtName.getText().toString();
				if("".equals(nickName)){
					Toast.makeText(context,"修改昵称，昵称不能为空！",Toast.LENGTH_SHORT).show();
				}else{
					dismiss();
					modify(nickName);
				}
				
			}
		});
    	//取消
        mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
				
			}
		});
        //取消
        mCancelRl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
				
			}
		});
		
	}
    //初始化
	private void init() {
		   mEtName=    (EditText)findViewById(R.id.et_name);
	    
	       mCancel  =  (TextView)findViewById(R.id.cancel);
	       mConfirm =  (TextView)findViewById(R.id.confirm);
		   mConfirmRl =(RelativeLayout)findViewById(R.id.confirm_rl);
		   mCancelRl=(RelativeLayout)findViewById(R.id.cancel_rl);
	}

	public String getEtName(){
    	return mEtName.getText().toString();
    }
    public void setTvNameNum(String num){
    	mTvNameNum.setText(num);
    }
	//11.情报用户信息修改接口,修改昵称
    public void modify(String nickName){
    	this.nickName=nickName;
    	telNum=ConfigManager.getInstance().getUserTel();
    	if(null==telNum||"".equals(telNum)){
    		Toast.makeText(context,"您好！您还没有登录！不能修改昵称！",Toast.LENGTH_SHORT).show();
    		return;
    	}
    	HashMap<String,Object> map=new HashMap<String, Object>();
    	
    	map.put("userPhone",telNum);
  
//	    map.put("pwd",password);
    	map.put("nickname",nickName);
    	ServiceEngin.getInstance().Request(context, map,"informodifyuserinfo",new EnginCallback(context){
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
	    	  Toast.makeText(context,"昵称修改成功！！！",Toast.LENGTH_SHORT).show();
	    	  ConfigManager.getInstance().setUserName(nickName);
	    	  ContentsManager mContentsManager = ContentsManager.getInstance();
	    	  mContentsManager.notifyContentUpdateSuccess(SystemConfig.MODIFY_USERNAME, nickName);
	    	  this.dismiss();
	    	  mNameTv.setText(nickName);
	    	  
//	    	  getActivity().finish();
	      }else{
	    	  Toast.makeText(context,errmsg,Toast.LENGTH_SHORT).show();
	      }
	}
}

