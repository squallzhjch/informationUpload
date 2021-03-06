package com.informationUpload.fragments;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.utils.RoundBitmapUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author chentao
 *
 */
public class MyRecordFragment extends BaseFragment {

	/**
	 * 主view	
	 */
	private View view;
	/**
	 * 我的名字
	 */
	private TextView mTvName;
	/**
	 * 我的记录个数
	 */
	private TextView mMyrecordNumTv;
	/**
	 * 返回按钮
	 */
	private RelativeLayout mMyrecordNumBack;
	private String editionTime;
	private boolean islogin;
	private String path;
	private ImageView mMyRecordNumIvHead;
	private TextView mMyRecordNumTvName;
	private Bitmap initBitmap;

	@Override
	public void onDataChange(Bundle bundle) {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		view =inflater.inflate(R.layout.fragment_my_record,null);
		//初始化界面
		init();
		//添加监听器

		addListeners();
		//获得工作量统计
		getworkInfo();

		return view;
	}
	/**
	 * 获得工作量统计
	 */
	private void getworkInfo() {
		HashMap<String,Object> map=new HashMap<String, Object>();
		
		//		map.put("userid", userName);
	
		map.put("userid", ConfigManager.getInstance().getUserId());
		ServiceEngin.getInstance().Request(getActivity(), map, "inforcount", new EnginCallback(getActivity()) {
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				if(!mIsActive)
					return;
				Log.e("请求成功", arg0.result.toString());
				//进行json解析
				parseJson(arg0.result.toString());
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				Log.e("请求失败", arg1);
			}
		});

	}
	//进行json解析
	protected void parseJson(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		String errcode = jsonObj.getString("errcode");
		String errmsg = jsonObj.getString("errmsg");
		if(null!=errcode&&!"".equals(errcode)&&"0".equals(errcode)){
		JSONObject data = jsonObj.getJSONObject("data");
		JSONObject total = data.getJSONObject("total");
		String count = total.getString("count");
		mMyrecordNumTv.setText(count);
		}else{
			Toast.makeText(getActivity(),errmsg,Toast.LENGTH_SHORT).show();
		}
	}
	//初始化界面
	private void init() {
		InputStream is = getResources().openRawResource(R.drawable.headshot);  

	    initBitmap = BitmapFactory.decodeStream(is);
	    
		
		mMyrecordNumTv=(TextView)view.findViewById(R.id.myrecord_num_tv);
		mMyrecordNumBack=(RelativeLayout)view.findViewById(R.id.myrecord_num_back);
		mMyRecordNumIvHead=(ImageView)    view.findViewById(R.id.myrecord_num_iv_head);
		mMyRecordNumIvHead.setImageBitmap(initBitmap);
		mMyRecordNumTvName  = (TextView)   view.findViewById(R.id.myrecord_num_tv_name);
		editionTime= ConfigManager.getInstance().getEditionTime();
		islogin= ConfigManager.getInstance().isLogin();
		/**
		 * 将字符串数据转化为毫秒数
		 */
		Calendar c_init = Calendar.getInstance();
		try {
			c_init.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(editionTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(islogin){
			  String userName = ConfigManager.getInstance().getUserName();
			  if(null!=userName&&!"".equals(userName)){
				  mMyRecordNumTvName.setText(userName);
			  }
			 
				if(hasSDcard()){
					path = SystemConfig.SDCARD_HEAD_IMG_PATH;    
				}else{
					path = SystemConfig.INNER_HEAD_IMG_PATH; 
				}
				String pathName_init = path+c_init.getTimeInMillis()+"_"+ConfigManager.getInstance().getUserId();
		
				File file_init=new File(pathName_init);
				if(file_init.exists()){
					final Bitmap bitmap_init = BitmapFactory.decodeFile(pathName_init);
					mMyRecordNumIvHead.setImageBitmap(initBitmap);
					new Handler().post(new Runnable() {
						
						@Override
						public void run() {
							mMyRecordNumIvHead.setImageBitmap(RoundBitmapUtil.toRoundBitmap(mMyRecordNumIvHead,bitmap_init));
							
						}
					});
				
				}
		
}
	}
	//添加监听器
	private void addListeners() {
		//返回
		mMyrecordNumBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mFragmentManager.back();

			}
		});

	}
	public static boolean hasSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
