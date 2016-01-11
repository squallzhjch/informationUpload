package com.informationUpload.fragments;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.Proxy.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.alibaba.fastjson.JSON;
import com.informationUpload.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.informationUpload.adapter.ChatAdapter;
import com.informationUpload.contentproviders.InformationManager;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.contents.AbstractOnContentUpdateListener;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.entity.PictureMessage;
import com.informationUpload.entity.attachmentsMessage;
import com.informationUpload.entity.locationMessage;
import com.informationUpload.utils.Bimp;
import com.informationUpload.utils.ChangePointUtil;
import com.informationUpload.utils.InnerScrollView;
import com.informationUpload.utils.PoiRecordPopup;
import com.informationUpload.utils.ZipUtil;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.utils.PoiRecordPopup.OnRecorListener;
import com.informationUpload.utils.SystemConfig;
/**
 * @author chentao
 * @version V1.0
 * @ClassName: InformationCollectionFragment
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */


public class InformationCollectionFragment extends BaseFragment {
	/**
	 * 上传文件全名
	 */
	private String path_all_name;
	/**
	 * 附件list
	 */
	private ArrayList<attachmentsMessage> list_att;
	/**
	 * 上传超时
	 */
	private static final int TIME_OUT = 10 * 10000000; 
	/**
	 *  设置编码
	 */
	private static final String CHARSET = "utf-8"; 
	/**
	 * 成功码
	 */
	public static final String SUCCESS = "1";
	/**
	 * 失败码
	 */
	public static final String FAILURE = "0";
	/**
	 * 上传时候的进度条
	 */
	private ProgressDialog pb;
	/**
	 * 返回的字节数组
	 */
	private static byte[] byt;
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
	private ArrayList<ChatMessage> mChatList=new ArrayList<ChatMessage>();
	/**
	 * 图片数组
	 */
	private ArrayList<PictureMessage> mPicList=new ArrayList<PictureMessage>();;
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
	/**
	 * 从数据库取出的科大讯飞转译字符串
	 */
	private String remark="";
	/**
	 * 返回按钮
	 */
	private RelativeLayout title_back ;
	/**
	 * 头部标题
	 */
	private TextView mInformationCollectTitle;
	/**
	 * 标题图片
	 */
	private Drawable draw_title;
	/**
	 * 删除照片按钮
	 */
	private TextView delete_photo;
	/**
	 * 重新选点按钮
	 */
	private TextView select_position_again;
	/**
	 * 写入文件的json串
	 */
	private String servicePara;
	/**
	 * 文档保存的文件路径
	 */
	private String 	path = Environment
			.getExternalStorageDirectory()+"/InformationUpload/Upload/";
	/**
	 * 图片保存路径
	 */
	private String path_pic=Environment
			.getExternalStorageDirectory()+"/InformationUpload/Picture/";
	/**
	 * 语音保存路径
	 */
	private String path_chat=Environment
			.getExternalStorageDirectory()+"/InformationUpload/Chat/";
	/**
	 * 时间格式刷
	 */
	private SimpleDateFormat df;
	/**
	 *标题头文字 
	 */
	private String draw_txt;
	/**
	 * 城市code
	 */
	private String mAdminCode;
	/**
	 * 垂直的自定义scrollview
	 */
	private InnerScrollView isv;
	/**
	 * 垂直主布局的Linearlayout
	 */
	private LinearLayout svll;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				new Thread(new Runnable() {

					@Override
					public void run() {

						uploadFile(new File(path_all_name));


					}
				}).start();
				break;

			case 1:
				pb.dismiss();
				Toast.makeText(getActivity(),"压缩失败", Toast.LENGTH_SHORT).show();
				Log.i("chentao","uploadFile:"+"压缩失败");
				break;
			case 2:
				pb.dismiss();
				ContentValues values = new ContentValues();
				values.put(Informations.Information.STATUS, Informations.Information.STATUS_SERVER);
				mInformationManager.updateInformation(mRowkey,values);
				mFragmentManager.back();
				Toast.makeText(getActivity(),"上传成功", Toast.LENGTH_SHORT).show();
			    
				break;
			case 3:	
				pb.dismiss();
				Toast.makeText(getActivity(),"上传失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};




	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		registerOnContentUpdateListener(new AbstractOnContentUpdateListener() {


			@Override
			public void onContentUpdated(List<Object[]> values) {
				if (values != null) {
					mAddress = (String) values.get(0)[0];
					mAdminCode=(String) values.get(0)[1];
					select_position.setText(mAddress);
					mPoint = (GeoPoint) values.get(0)[2];
					Log.i("chentao","mAddress:"+mAddress+",mAdminCode:"+mAdminCode+",mPoint"+mPoint.getLat());
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
					mChatList=(ArrayList<ChatMessage>) message.getChatMessageList();

					mPicList=(ArrayList<PictureMessage>) message.getPictureMessageList();
					Log.i("chentao","PicList:"+mPicList.size());
					remark = message.getRemark();

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

		df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;     // 屏幕宽度（像素）


		//初始化
		init();
		switch(mType){
		case Informations.Information.INFORMATION_TYPE_BUS:
			draw_txt="公交";
			draw_title=getActivity().getResources().getDrawable(R.drawable.bus_select_title);
			break;
		case Informations.Information.INFORMATION_TYPE_ESTABLISHMENT:
			draw_txt="设施";
			draw_title=getActivity().getResources().getDrawable(R.drawable.establishment_select_title);
			break;
		case Informations.Information.INFORMATION_TYPE_ROAD:
			draw_txt="道路";
			draw_title=getActivity().getResources().getDrawable(R.drawable.road_select_title);
			break;
		case Informations.Information.INFORMATION_TYPE_AROUND:
			draw_txt="周边其他";
			draw_title=getActivity().getResources().getDrawable(R.drawable.change_near_select_title);
			break;
		}
		mInformationCollectTitle.setText(draw_txt);
		mInformationCollectTitle.setCompoundDrawablesWithIntrinsicBounds (draw_title,
				null,null,null);

		mapManager=MapManager.getInstance();
		if(mPoint == null){
			mPoint = mLocationManager.getCurrentPoint();
		}
		Log.i("chentao",""+mPoint.getLat());
		mapManager.searchAddress(mPoint, new OnSearchAddressListener() {



			@Override
			public void onFailure() {
				Log.i("chentao",":onFailure");
			}

			@Override
			public void OnSuccess(String address,String adminCode) {
				mAddress = address;
				mAdminCode=adminCode;
				Log.i("chentao",":"+mAddress);
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
		title_back = (RelativeLayout) view.findViewById(R.id.information_collect_back);
		delete_photo=(TextView)view.findViewById(R.id.delete_photo);
		select_position_again=(TextView)view.findViewById(R.id.select_position_again);
		isv=    (InnerScrollView)     view.findViewById(R.id.isv);
		svll=    (LinearLayout)     view.findViewById(R.id.svll);

		mInformationCollectTitle= (TextView)view.findViewById(R.id.information_collect_title);
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
		additional_remarks_et.setText(remark);
		adapter = new ChatAdapter(getActivity(),InformationCollectionFragment.this, mChatList);
		resetListView();
		voice_collection_lv.setAdapter(adapter);
		select_position.setText(mAddress);

		for(int i=0;i<mPicList.size();i++){
			ImageView imageView = new ImageView(getActivity());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(180,180);
			lp.setMargins(5,0,5,0);
			imageView.setLayoutParams(lp);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			imageView.setImageURI(Uri.fromFile(new File(mPicList.get(i).getPath()))
					);
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

			if (hscrollview.getWidth() >= width) {
				new Handler().post(new Runnable() {

					@Override
					public void run() {
						hscrollview.scrollTo(hlinearlayout.getWidth() + 20, 0);

					}
				});
			}
		}
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
			MediaPlayer md;
			@Override
			public void onStopSpeech(String path) {

				synchronized (mChatList) {

					//					try {
					//						md = new MediaPlayer();
					//						md.reset();
					//						md.setDataSource(path);
					//						md.prepare();
					//					} catch (Exception e) {
					//						e.printStackTrace();
					//
					//					}
					//					long timeLong = md.getDuration();
					//
					//					md.release();
					//
					//					ChatMessage chatmsg = new ChatMessage();
					//					mChatList.add(chatmsg);
					//					chatmsg.setPath(path);
					//					chatmsg.setChattimelong(timeLong);
					//					chatmsg.setParentId(mRowkey);
					//					chatmsg.setLat(mLocationManager.getCurrentPoint().getLat());
					//					chatmsg.setLon(mLocationManager.getCurrentPoint().getLon());
					//					chatmsg.setTime(System.currentTimeMillis());
					//					adapter.setData(mChatList);
					//					adapter.notifadataset();
					//					resetListView();
				}
			}
			@Override
			public void onParseResult(String path, String result) {
				synchronized (mChatList) {
					try {
						md = new MediaPlayer();
						md.reset();
						md.setDataSource(path);
						md.prepare();
					} catch (Exception e) {
						e.printStackTrace();

					}
					long timeLong = md.getDuration();

					md.release();
					md=null;
					if(timeLong>=1000&&timeLong<=6000){
						long longti=  (timeLong/1000);


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

							chatmsg.setChattimelong(longti);
							adapter.setData(mChatList);
							adapter.notifadataset();
							resetListView();
						}
						additional_remarks_et.setText(additional_remarks_et.getText() + "\n" + result);
					}else if(timeLong<1000){
						Log.i("chentao","录音时间过短");
						Toast.makeText(getActivity(),"录音时间过短", Toast.LENGTH_SHORT).show();
						if(new File(path).exists()){
							new File(path).delete();
						}
					}else if(timeLong>6000){
						Log.i("chentao","录音时间过长");
						Toast.makeText(getActivity(),"录音时间过长", Toast.LENGTH_SHORT).show();
						if(new File(path).exists()){
							new File(path).delete();
						}
					}
				}
			}
		});
		//返回按钮
		title_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});
		//选择位置点击
		select_position_again.setOnClickListener(new OnClickListener() {

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
				if(mAddress.equals("")){
					Toast.makeText(getActivity(),"您好，定位还没有成功！不能进行上报，请您耐心等待谢谢!", Toast.LENGTH_SHORT).show();
				}else{
					saveLocal();
					pb=new ProgressDialog(getActivity());
					pb.setMessage("正在上传");
					pb.show();

					ArrayList<String> list_servicepara=new ArrayList<String>();
					InformationMessage infomessage = InformationManager.getInstance().getInformation(mRowkey);

					HashMap<String,Object> map=new HashMap<String,Object>();
					map.put("info_fid",infomessage.getRowkey());
					double[] ret = ChangePointUtil.baidutoreal(infomessage.getLat(), infomessage.getLon());
					map.put("location", new locationMessage((float)ret[0],(float)ret[1]));
					map.put("info_type",infomessage.getType());
					map.put("adminCode",mAdminCode);
					map.put("address",mAddress);
					list_att=new ArrayList<attachmentsMessage>();
					List<ChatMessage> chatmsglist = infomessage.getChatMessageList();
					List<PictureMessage> picmsglist = infomessage.getPictureMessageList();
					for(int j=0;j<chatmsglist.size();j++){
						ChatMessage chatmsg = chatmsglist.get(j);
						Log.i("chentao","chatmsg:"+chatmsg.getPath());
						double[] ret_chat = ChangePointUtil.baidutoreal(chatmsg.getLat(),chatmsg.getLon());
						list_att.add(new attachmentsMessage(1,chatmsg.getPath().substring(chatmsg.getPath().lastIndexOf("/")+1, chatmsg.getPath().length()),
								df.format(chatmsg.getTime()),chatmsg.getRemark(),new locationMessage((float)ret_chat[0],(float)ret_chat[1])));
					}
					for(int f=0;f<picmsglist.size();f++){
						PictureMessage picmsg = picmsglist.get(f);
						double[] ret_pic = ChangePointUtil.baidutoreal(picmsg.getLat(),picmsg.getLon());
						list_att.add(new attachmentsMessage(0,picmsg.getPath().substring(picmsg.getPath().lastIndexOf("/")+1, picmsg.getPath().length()),
								df.format(picmsg.getTime()),picmsg.getRemark(),new locationMessage((float)ret_pic[0],(float)ret_pic[1])));
					}
					map.put("attachments",list_att);

					map.put("operateDate",df.format(infomessage.getTime()));
					SharedPreferences sp = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
					String userName = sp.getString("user_name",null);
					map.put("user_id",userName);
					map.put("remark", infomessage.getRemark());
					servicePara = JSON.toJSONString(map);
					Log.i("chentao",servicePara.toString());
					list_servicepara.add(servicePara);
					//将list在指定文件夹写成文本
					doWriteFile(list_servicepara);



					new Thread(new Runnable() {



						@Override
						public void run() {
							try {
								ArrayList<String> listall=new ArrayList<String>();
								for(int h=0;h<list_att.size();h++){
									Log.i("chentao","h:"+list_att.get(h).getUrl());
									if(list_att.get(h).getType()==1){
										Log.i("chentao","hurl:"+path_chat+list_att.get(h).getUrl());
										listall.add(path_chat+list_att.get(h).getUrl());
									}else if(list_att.get(h).getType()==0){
										listall.add(path_pic+list_att.get(h).getUrl());
									}
								}
								listall.add(path+"poi.txt");
								path_all_name=path+df.format(new Date())+".zip";
								ZipUtil.zip(
										listall, path_all_name);


								handler.sendEmptyMessage(0);
								Log.i("chentao", "压缩成功");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								handler.sendEmptyMessage(1);
								Log.i("chentao", "压缩失败");
							}

						}
					}).start();
				}





			}
		});
		//删除照片
		delete_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int len = listview.size();
				if(len!=0){
					hlinearlayout.removeView(listview.get(len-1));
					listview.remove(len-1);
				}else{
					Toast.makeText(getActivity(),"您好，您还没有拍照，不能进行删除操作！",Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	//重新计算高度
	public void resetListView() {

		int count = this.adapter.getCount();
		if(count!=0){
			View itemView = this.adapter.getView(0, null, null);
			if (itemView != null) {
				itemView.measure(
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

				int defHeight = count
						* (itemView.getMeasuredHeight() + voice_collection_lv
								.getDividerHeight());
				if(defHeight>180){
					LayoutParams lp =  voice_collection_lv.getLayoutParams();

					lp.height = defHeight;
					voice_collection_lv.setLayoutParams(lp);
				}
			}
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
		picMsg.setPath(file.getPath());
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
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(180,180);
			lp.setMargins(5,0,5,0);
			imageView.setLayoutParams(lp);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			try {

				bitmap = Bimp.revitionImageSize(file.getPath());

				if(new File(file.getPath()).exists()){
					new File(file.getPath()).delete();

					FileOutputStream out = new FileOutputStream(file.getPath());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
					byte[] byt = readInputStream(isBm);
					out.write(byt);
					out.flush();
					out.close();
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
	/**
	 * 将list写到文件中
	 * @param list
	 */
	public  void doWriteFile(ArrayList<String> list){


		if(!new File(path).exists()){
			new File(path).mkdirs();
		}

		FileWriter fw = null;
		PrintWriter pw = null;
		try{
			//创建字符流
			fw = new FileWriter(path+"poi.txt");
			Log.i("chentao","fw:"+path+"poi.txt");
			//封装字符流的过滤流
			pw = new PrintWriter(fw);
			//文件写入
			for(int i=0;i<list.size();i++){
				pw.print(list.get(i)+"\r\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			//关闭外层流
			if(pw != null){
				pw.close();
			}
		}
	}
	/**
	 * 保存到本地数据库
	 */
	private void  saveLocal(){
		if(mAddress.equals("")){
			Toast.makeText(getActivity(),"您好，定位还没有成功！不能进行保存，请您耐心等待谢谢!", Toast.LENGTH_SHORT).show();
		}else{
			if (TextUtils.isEmpty(mRowkey)) {
				mRowkey = UUID.randomUUID().toString().replaceAll("-", "");
			}
			InformationMessage message = new InformationMessage();
			message.setAddress(mAddress);
			message.setType(mType);
			message.setRowkey(mRowkey);

			message.setLat(mPoint.getLat());
			message.setLon(mPoint.getLon());
			message.setRemark(additional_remarks_et.getText().toString());
			message.setChatMessageList(mChatList);
			message.setPictureMessageList(mPicList);
			mInformationManager.saveInformation(mApplication.getUserId(), message);
		}

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

	public static byte[] readInputStream(InputStream inputStream) {

		// 1.建立通道对象
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// 2.定义存储空间
		byte[] buffer = new byte[1024];
		// 3.开始读文件
		int len = -1;
		try {
			if (inputStream != null) {
				while ((len = inputStream.read(buffer)) != -1) {
					// 将Buffer中的数据写到outputStream对象中
					outputStream.write(buffer, 0, len);
				}
			}
			// 4.关闭流
			byt = outputStream.toByteArray();
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byt;
	}

	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	private String uploadFile(File file) {
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
		// 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		String RequestURL = "http://192.168.3.155:8083/infor/information/inforimp/";
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			if (file != null) {
				System.out.println("----------" + file);
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				OutputStream outputSteam = conn.getOutputStream();

				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"filenamepath\"; filename=\"" + file.getName() + "\"" + LINE_END);
				System.out.println("----------" + file.getName());
				sb.append("Content-Type: application/octet-stream;charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				dos.write("**你好啊ABCDFERGDFDFSFSDFD".getBytes(), 0, 22);
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				// BufferedReader br = new BufferedReader(new
				// InputStreamReader(conn.getInputStream()));
				// JSONObject jsonObject =
				// JSONObject.parseObject(br.toString());
				// String resultMsg = (String)
				// jsonObject.get("ResultMsg");
				// System.out.println("resultMsg*******"+resultMsg);
				System.out.println("res----------------" + res);
				if (res == 200) {
					handler.sendEmptyMessage(2);
					return SUCCESS;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(3);
		return FAILURE;
	}
}
