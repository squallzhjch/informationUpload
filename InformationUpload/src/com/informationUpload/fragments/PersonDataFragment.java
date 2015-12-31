package com.informationUpload.fragments;


import java.io.File;



import com.informationUpload.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PersonDataFragment extends BaseFragment{

	/**
	 * 请求识别码
	 */
	private static final int SELECT_PICTURE = 0xa0;
	private static final int TAKE_PICTURE = 0xa1;
	/**
	 * 主view
	 */
	private View view;
	/**
	 * 头像
	 */
	private ImageView IvHead;
	/**
	 * 图片存储路径
	 */
	private Uri imageUri;
	/**
	 * 返回按钮
	 */
	private TextView PersondataBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.frgment_user_info,null);
		//初始化
		init();
		//添加监听器
		addListeners();
		return view;
	}

	//初始化
	private void init() {
		IvHead = (ImageView) view.findViewById(R.id.iv_head);
		PersondataBack=(TextView)view.findViewById(R.id.persondata_back);

	}
	//添加监听器
	private void addListeners() {
		//头像
		IvHead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new PopupWindows(getActivity(),view);

			}
		});
		//返回按钮
		PersondataBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mFragmentManager.back();
				
			}
		});

	}
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();
			// 从相册中选择
			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_select);
			// 拍照
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			// 取消
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
					intent.addCategory(Intent.CATEGORY_OPENABLE);  
					intent.setType("image/*");  
					startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);   

					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});

			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}



	private String path = "";


	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

		StringBuffer sDir = new StringBuffer();
		if (hasSDcard()) {
			sDir.append(Environment.getExternalStorageDirectory() + "/MyPicture/");
		} else {
			String dataPath = Environment.getRootDirectory().getPath();
			sDir.append(dataPath + "/MyPicture/");
		}

		File fileDir = new File(sDir.toString());
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		File file = new File(fileDir, String.valueOf(System.currentTimeMillis()) + ".jpg");

		path = file.getPath();
		imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public static boolean hasSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {


		
		switch (requestCode) {

		case TAKE_PICTURE:

			IvHead.setImageURI(imageUri);
			break;
		case SELECT_PICTURE:
			imageUri = intent.getData();
			IvHead.setImageURI(imageUri);


			break;
		}
	}
}
