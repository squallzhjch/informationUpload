package com.informationUpload.fragments;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.activity.LoginActivity;
import com.informationUpload.activity.MyApplication;
import com.informationUpload.serviceengin.EnginCallback;
import com.informationUpload.serviceengin.ServiceEngin;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private TextView mMyrecordNumBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		SharedPreferences sp = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
		String userName = sp.getString("user_name",null);
		//		map.put("userid", userName);
		map.put("userid","201512291815556b9ff21d591d465d8769451e2bf338d9");
		ServiceEngin.Request(getActivity(), map, "inforcount", new EnginCallback(getActivity()){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				Log.e("请求成功", arg0.result.toString());
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
		String errcode = jsonObj.getString("errcode");
		String errmsg = jsonObj.getString("errmsg");
		if(!"".equals(errcode)&&null!=errcode&&"0".equals(errcode)){
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
		mTvName=(TextView)view.findViewById(R.id.myrecord_num_tv_name);
		mMyrecordNumTv=(TextView)view.findViewById(R.id.myrecord_num_tv);
		mMyrecordNumBack=(TextView)view.findViewById(R.id.myrecord_num_back);




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
}
