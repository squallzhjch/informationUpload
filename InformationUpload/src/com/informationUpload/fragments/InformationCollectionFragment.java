package com.informationUpload.fragments;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import com.informationUpload.R;
import com.informationUpload.R.drawable;

import android.R.color;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.informationUpload.adapter.ChatAdapter;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.contents.AbstractOnContentUpdateListener;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.entity.PictureMessage;
import com.informationUpload.utils.Bimp;
import com.informationUpload.utils.PoiRecordPopup;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.utils.PoiRecordPopup.OnRecorListener;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.TitleView;

public class InformationCollectionFragment extends BaseFragment {


	/**
	 * 点击的拍照LinearLayout中 的哪一个
	 */
	private int i;
	/**
	 * 压缩后的bitmap
	 */
	private Bitmap bitmap;
	/**
	 * 拍照参数返回值
	 */
	private static final int TAKE_PICTURE = 0x000000;
	/**
	 * 图片uri
	 */
	private Uri imageUri;

	/**
	 * 存照片的list
	 */
	private ArrayList<View> listview;
	/**
	 * 主view
	 */
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
	/**
	 * 录音信息数组
	 */
	private ArrayList<ChatMessage> mChatList;
	/**
	 * 图片数组
	 */
	private ArrayList<PictureMessage> mPicList;
	/**
	 * 录音信息adapter
	 */
	private ChatAdapter adapter;
	/**
	 * 屏幕宽度
	 */
	private int width;


	/**
	 * 该条信息的唯一标识
	 */
	private String mRowkey;

	/**
	 * 位置坐标点
	 */
	private GeoPoint mPoint;
	/**
	 * 地图管理类
	 */
	private MapManager mapManager;
	/**
	 * 地址
	 */
	private String mAddress;
	/**
	 * 图片名称
	 */
	private String picName;
	/**
	 * 图片名称
	 */
	private PictureMessage picMsg;
	/**
	 * 上报类型
	 */
	private int mType;
	/**
	 * 图片文件
	 */
	private File file;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		registerOnContentUpdateListener(new AbstractOnContentUpdateListener() {


			@Override
			public void onContentUpdated(List<Object[]> values) {
				if (values != null) {
					mAddress = (String) values.get(0)[0];
					select_position.setText(mAddress);
					mPoint = (GeoPoint) values.get(0)[1];
				}
			}

			@Override
			public boolean isActive() {
				return mIsActive;
			}

			@Override
			public String getKey() {
				return SystemConfig.FRAGMENT_UPDATE_SELECT_POINT_ADDRESS;
			}
		});
		Bundle bundle = getArguments();
		if (bundle != null) {
			mRowkey = bundle.getString(SystemConfig.BUNDLE_DATA_ROWKEY);
			mType = bundle.getInt(SystemConfig.BUNDLE_DATA_TYPE);
			if(!TextUtils.isEmpty(mRowkey)) {
				InformationMessage message = mInformationManager.getInformation(mRowkey);
				if(message == null){
					mRowkey = null;
					mPoint = null;
				}else{
					mType = message.getType();
					mRowkey =message.getRowkey();
				} 
			}else{
				mRowkey = null;
				mPoint = null;
			}
		}else{
			mRowkey = null;
			mPoint = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment1_information_collection, null);

		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;     // 屏幕宽度（像素）
		TitleView title = (TitleView) view.findViewById(R.id.title_view);
		title.setOnLeftAreaClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});
		//初始化
		init();
		Log.i("chentao","oncreateview");
		mapManager=MapManager.getInstance();
		if(mPoint == null){
			mPoint = mLocationManager.getCurrentPoint();
		}
		mapManager.searchAddress(mPoint, new OnSearchAddressListener() {

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub
			}

			@Override
			public void OnSuccess(String address) {
				mAddress = address;
				select_position.setText(address);

			}
		});
		//添加监听器
		addListeners();
		return view;
	}

	/**
	 * 初始化组件
	 */
	private void init() {
		listview = new ArrayList<View>();
		mChatList = new ArrayList<ChatMessage>();
		mPicList=new ArrayList<PictureMessage>();
		select_position = (TextView) view.findViewById(R.id.select_position);
		hliv = (ImageView) view.findViewById(R.id.hliv);
		voice_collection_lv = (ListView) view.findViewById(R.id.voice_collection_lv);
		hscrollview = (HorizontalScrollView) view.findViewById(R.id.hscrollview);
		hlinearlayout = (LinearLayout) view.findViewById(R.id.hlinearlayout);
		//		additional_remarks_sv=(ScrollView)view.findViewById(R.id.additional_remarks_sv);
		additional_remarks_et = (EditText) view.findViewById(R.id.additional_remarks_et);
		savetolocal = (Button) view.findViewById(R.id.savetolocal);
		recordvoice = (Button) view.findViewById(R.id.recordvoice);
		report_at_once = (Button) view.findViewById(R.id.report_at_once);
		adapter = new ChatAdapter(getActivity(), mChatList);
		voice_collection_lv.setAdapter(adapter);
		select_position.setText(mAddress);
	}

	/**
	 * 添加监听器
	 */
	private void addListeners() {
		mVoicePop = PoiRecordPopup.getInstance();
		mVoicePop.setViewTouch(recordvoice);
		//        mVoicePop.setMinLeng(1000);
		//        mVoicePop.setMaxLeng(1000 * 60);
		mVoicePop.setRecordListener(new OnRecorListener() {
			@Override
			public void onStopSpeech(String path) {
				synchronized (mChatList) {
					ChatMessage chatmsg = new ChatMessage();
					mChatList.add(chatmsg);
					chatmsg.setPath(path);
					chatmsg.setParentId(mRowkey);
					chatmsg.setLat(mLocationManager.getCurrentPoint().getLat());
					chatmsg.setLon(mLocationManager.getCurrentPoint().getLon());
					chatmsg.setTime(System.currentTimeMillis());
					adapter.setData(mChatList);
					adapter.notifadataset();
					resetListView();
				}
			}
			@Override
			public void onParseResult(String path, String result) {
				synchronized (mChatList) {
					boolean his = false;
					for(ChatMessage message:mChatList){
						if(!TextUtils.isEmpty(result) && !TextUtils.isEmpty(message.getPath()) && !TextUtils.isEmpty(path) && result.equals(message.getPath())){
							message.setRemark(result);
							his = true;
							break;
						}
					}
					if(!his){
						ChatMessage chatmsg = new ChatMessage();
						mChatList.add(chatmsg);
						chatmsg.setPath(path);
						chatmsg.setParentId(mRowkey);
						chatmsg.setRemark(result);
						chatmsg.setLat(mLocationManager.getCurrentPoint().getLat());
						chatmsg.setLon(mLocationManager.getCurrentPoint().getLon());
						chatmsg.setTime(System.currentTimeMillis());
						adapter.setData(mChatList);
						adapter.notifadataset();
						resetListView();
					}
					additional_remarks_et.setText(additional_remarks_et.getText() + "\n" + result);
				}
			}
		});

		//选择位置点击
		select_position.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putBoolean(SystemConfig.HIDE_OTHER_FRAGMENT, true);
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(SelectPointFragment.class, bundle));
			}
		});
		//照相添加图片
		hliv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//拍照
				startCamera();

			}
		});
		//保存到本地
		savetolocal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveLocal();
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

	//重新计算高度
	private void resetListView() {

		int count = this.adapter.getCount();

		View itemView = this.adapter.getView(0, null, null);
		if (itemView != null) {
			itemView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

			int defHeight = count
					* (itemView.getMeasuredHeight() + voice_collection_lv
							.getDividerHeight());
			LayoutParams lp =  voice_collection_lv.getLayoutParams();
			lp.height = defHeight;
			voice_collection_lv.setLayoutParams(lp);
		}
	}

	public void startCamera(){
		final Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		StringBuffer sDir = new StringBuffer();
		if (hasSDcard()) {
			sDir.append(SystemConfig.DATA_PICTURE_PATH);
		} else {
			Toast.makeText(mApplication, "没有检测到存储卡", Toast.LENGTH_SHORT).show();
			return;
		}

		File fileDir = new File(sDir.toString());

		if (!fileDir.exists()) {
			boolean a = fileDir.mkdirs();
			if(!a){
				Toast.makeText(mApplication, "手机存储空间不足", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		picName=String.valueOf(System.currentTimeMillis()) ;
		file = new File(fileDir,picName+".jpg");
		imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);



	}

	public void photo() {
		picMsg = new PictureMessage();

		picMsg.setParentId(mRowkey);
		picMsg.setPath(imageUri.toString());

		picMsg.setLat(mLocationManager.getCurrentPoint().getLat());
		picMsg.setLon(mLocationManager.getCurrentPoint().getLon());
		picMsg.setTime(System.currentTimeMillis());
		mPicList.add(picMsg);
		bitmap=null;
	}

	public static boolean hasSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LinearLayout.LayoutParams(150,150));


			try {

				InputStream in = Bimp.revitionImageSize(file.getPath());
				if(new File(file.getPath()).exists()){
					new File(file.getPath()).delete();
					
					FileOutputStream out=new FileOutputStream(file.getPath());
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch blockd
				e.printStackTrace();
			}
			if(bitmap!=null){


				imageView.setImageBitmap(bitmap);



				imageView.setTag(i);

				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						int num = (Integer) arg0.getTag();
						Bundle bundle=new Bundle();
						bundle.putInt(SystemConfig.BUNDLE_DATA_PICTURE_NUM,num);
						bundle.putSerializable(SystemConfig.BUNDLE_DATA_PICTURE_LIST, mPicList);
						mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(PhotoViewpagerFragment.class, bundle));

					}
				});


				hlinearlayout.addView(imageView, 0);
				listview.add(imageView);
				i++;
				if (hscrollview.getWidth() >= width) {
					new Handler().post(new Runnable() {

						@Override
						public void run() {
							hscrollview.scrollTo(hlinearlayout.getWidth() + 20, 0);

						}
					});
				}
				photo();
			}
			break;
		}
	}


	private void  saveLocal(){
		InformationMessage message = new InformationMessage();
		message.setAddress(mAddress);
		message.setType(mType);
		message.setRowkey(mRowkey);
		message.setLat(mPoint.getLat());
		message.setLon(mPoint.getLon());
		message.setChatMessageList(mChatList);
		message.setPictureMessageList(mPicList);
		mInformationManager.saveInformation(mApplication.getUserId(), message);
	}
	@Override
	public void onDetach() {
		super.onDetach();
		mVoicePop.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		mVoicePop.onPause();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
	}
}
