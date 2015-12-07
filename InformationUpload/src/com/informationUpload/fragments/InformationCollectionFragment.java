package com.informationUpload.fragments;



import java.io.File;


import com.informationUpload.R;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.informationUpload.utils.PoiRecordPopup;

public class InformationCollectionFragment extends BaseFragment{


	private View view;
	/**
	 * 显示位置textview
	 */
	private TextView select_position;
	/**
	 * 点击拍照添加图片
	 */
	private ImageView hliv;
	/**
	 * 语音的李斯特
	 */
	private ListView voice_collection_lv;
	/**
	 * 添加图片横向的HorizontalScrollView
	 */
	private HorizontalScrollView hscrollview;
	/**
	 * 添加图片横向的LinearLayout
	 */
	private LinearLayout hlinearlayout;
	/**
	 * 补充说明的scrollview
	 */
	private ScrollView additional_remarks_sv;
	/**
	 * 补充说明的edittext
	 */
	private EditText additional_remarks_et;
	/**
	 * 保存到本地按钮
	 */
	private Button savetolocal;
	/**
	 * 录音按钮
	 */
	private Button recordvoice;
	/**
	 * 立刻上报
	 */
	private Button report_at_once;
	/**
	 * 录音探出框
	 */


	private PoiRecordPopup mVoicePop;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.information_collection,null);
		//初始化
		init();
		//添加监听器
		addListeners();
		return view;
	}
	/**
	 * 初始化组件
	 */
	private void init() {
		select_position=(TextView)view.findViewById(R.id.select_position);
		hliv=(ImageView) view.findViewById(R.id.hliv);    
		voice_collection_lv=(ListView) view.findViewById(R.id.voice_collection_lv);
		hscrollview=(HorizontalScrollView)view.findViewById(R.id.hscrollview);
		hlinearlayout=(LinearLayout)view.findViewById(R.id.hlinearlayout);                   
		additional_remarks_sv=(ScrollView)view.findViewById(R.id.additional_remarks_sv);
		additional_remarks_et  = (EditText) view.findViewById(R.id.additional_remarks_et);
		savetolocal=(Button)view.findViewById(R.id.savetolocal);
		recordvoice= (Button)view.findViewById(R.id.recordvoice);
		report_at_once = (Button) view.findViewById(R.id.report_at_once);


	}
	/**
	 * 添加监听器
	 */
	private void addListeners() {
		mVoicePop = new PoiRecordPopup(getActivity());
		mVoicePop.setViewTouch(recordvoice);
		boolean  path_boolean=mVoicePop.setPath( Environment.getExternalStorageDirectory()+"/FastMap/");
		if(!path_boolean){
			Toast.makeText(getActivity(), Environment.getExternalStorageDirectory()+"/FastMap/"+"文件夹创建不成功，不能使用，谢谢！", Toast.LENGTH_SHORT).show();

		}
		mVoicePop.setMinLeng(1000);
		mVoicePop.setMaxLeng(1000 * 60);
		mVoicePop.setRecordListener(new PoiRecordPopup.RecorListener() {
			@Override
			public void stopListener(double amp, String path, String name, long time) {


			}
		});


		//		//录音
		//		recordvoice.setOnTouchListener(new OnTouchListener() {
		//
		//			@Override
		//			public boolean onTouch(View arg0, MotionEvent arg1) {
		//				// TODO Auto-generated method stub
		//				return false;
		//			}
		//
		//
		//
		//			
		//		});

		//选择位置点击
		select_position.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		//照相添加图片
		hliv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		//保存到本地
		savetolocal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		}); 

		//立刻上报
		report_at_once.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});



	}

}
