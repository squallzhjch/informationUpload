package com.informationUpload.utils;

import com.informationUpload.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;



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

	public CommonDialog(Context context) {
		super(context, R.style.customerDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
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

		params.width = (int) (display.getWidth()* 0.8);
		params.height =  (int) (display.getHeight() *0.4);
		//		params.x=260;
		//	    params.y=100;
		//		this.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		this.getWindow().setAttributes(params);

       mEtName=    (EditText)findViewById(R.id.et_name);
       mTvNameNum= (TextView)findViewById(R.id.tv_name_num);
       mCancel  =  (TextView)findViewById(R.id.cancel);
       mConfirm =  (TextView)findViewById(R.id.confirm);
	}
    public void setConfirmClickListener(android.view.View.OnClickListener listener){
    	mConfirm.setOnClickListener(listener);
    }


}

