package com.informationUpload.fragments;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.R;
import com.informationUpload.serviceEngin.ServiceEngin;

import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.SystemConfig;

import com.informationUpload.utils.RoundBitmapUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private RelativeLayout PersondataBack;
	/**
	 * 昵称的relativelayout
	 */
	private RelativeLayout NameRl;
	/**
	 * 手机号的relativelayout
	 */
	private RelativeLayout TelRl;
	/**
	 * 从相册中选择的或者拍照后获得的bitmap
	 */
	private Bitmap bitmap;
	/**
	 * 上传头像图片时保存的bitmap的文件
	 */
	private File myCaptureFile;
	/**
	 * 是否登录true登录 false未登录
	 */
	private boolean islogin;
	/**
	 * 登录返回的下载头像图片路径
	 */
	private String jpgPath;
	/**
	 * 版本时间
	 */
	private String editionTime;
	/**
	 * 图片下载路径
	 */
	private String decode_path;

    /**
     * 开始上传的进度条
     */
	private ProgressDialog progressdialog;
	/**
	 * 下载头像后返回 的bitmap
	 */
	private Bitmap bitmap_init;
	/**
	 * 下载头像的进度条
	 */
	private ProgressDialog progressdialog_download;
	/**
	 * 初始化头像
	 */
	private Bitmap initBitmap;
	Handler handler=new Handler(){
	
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case ServiceEngin.UPLOAD_SUCCESS:
				String result = (String) msg.obj;
				parseUploadJson(result);
				Toast.makeText(getActivity(), "上传成功",Toast.LENGTH_LONG).show();
				break;
			case ServiceEngin.UPLOAD_FAILURE:
				progressdialog.cancel();
				Toast.makeText(getActivity(), "上传失败",Toast.LENGTH_LONG).show();
				break;
			case ServiceEngin.DOWNLOAD_SUCCESS:
				bitmap_init = (Bitmap) msg.obj;
				if(bitmap_init!=null){
				IvHead.setImageBitmap(RoundBitmapUtil.toRoundBitmap(IvHead,bitmap_init));
				progressdialog_download.cancel();
				/**
				 * 将字符串数据转化为毫秒数
				 */
				Calendar c_download = Calendar.getInstance();
				try {
					c_download.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ConfigManager.getInstance().getEditionTime()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					
					saveFileToLoacl(bitmap_init,c_download.getTimeInMillis()+"_"+ConfigManager.getInstance().getUserId());
				
				} catch (Exception e) {
				
					e.printStackTrace();
				}
				Toast.makeText(getActivity(), "头像下载成功！！！！",Toast.LENGTH_LONG).show();
				}else{
					progressdialog_download.cancel();
					Toast.makeText(getActivity(), "头像下载失败！！！！",Toast.LENGTH_LONG).show();
				}
				
				break;
			case ServiceEngin.START_UPLOAD:
				progressdialog=new ProgressDialog(getActivity());
				progressdialog.setMessage("正在上传头像！！！！");
				progressdialog.show();
				break;
			case ServiceEngin.START_DOWNLOAD:
				progressdialog_download=new ProgressDialog(getActivity());
				progressdialog_download.setMessage("正在下载头像！！！！");
				progressdialog_download.show();
				break;
		
			}
		}
	};
	
   @Override
	public void onDataChange(Bundle bundle) {

	}
	/**
	 * 对返回数据进行json解析
	 * @param result
	 */
	protected void parseUploadJson(String result) {
		JSONObject jsonObj = (JSONObject) JSON.parse(result);

		String errcode = jsonObj.getString("errcode");
		String errmsg = jsonObj.getString("errmsg");
		JSONObject dataObj = jsonObj.getJSONObject("data");
		final String edition = dataObj.getString("edition");

	
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if(myCaptureFile!=null){
					/**
					 * 将字符串数据转化为毫秒数
					 */
					Calendar c = Calendar.getInstance();
					try {
						c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(edition));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("时间转化后的毫秒数为：" + c.getTimeInMillis());
					String pathName = path+c.getTimeInMillis()+"_"+myCaptureFile.getName();
				
					myCaptureFile.renameTo(new File(path,c.getTimeInMillis()+"_"+myCaptureFile.getName()));
				
					ConfigManager.getInstance().setEditionTime(edition);
					IvHead.setImageBitmap(RoundBitmapUtil.toRoundBitmap(IvHead,bitmap));
					
					progressdialog.cancel();
					
				
				
					
			}   
				
				
				
			}
		});
	

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		view=inflater.inflate(R.layout.frgment_user_info,null);
		//初始化
		init();
		//添加监听器
		addListeners();
		return view;
	}

	//初始化

	private void init() {
		InputStream is = getResources().openRawResource(R.drawable.headshot);  

	  initBitmap = BitmapFactory.decodeStream(is);
	
		IvHead = (ImageView) view.findViewById(R.id.iv_head);
		IvHead.setImageBitmap(initBitmap);
		PersondataBack=(RelativeLayout)view.findViewById(R.id.persondata_back);
		NameRl =(RelativeLayout) view.findViewById(R.id.name_rl);
		TelRl=(RelativeLayout)view.findViewById(R.id.tel_rl);
		islogin=ConfigManager.getInstance().isLogin();
		jpgPath=ConfigManager.getInstance().getJpgPath();
		editionTime= ConfigManager.getInstance().getEditionTime();
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
				if(hasSDcard()){
					path = SystemConfig.SDCARD_HEAD_IMG_PATH;    
				}else{
					path = SystemConfig.INNER_HEAD_IMG_PATH; 
				}
				String pathName_init = path+c_init.getTimeInMillis()+"_"+ConfigManager.getInstance().getUserId();
		
				File file_init=new File(pathName_init);
				if(file_init.exists()){
					final Bitmap bitmap_init = BitmapFactory.decodeFile(pathName_init);
					IvHead.setImageBitmap(initBitmap);
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							IvHead.setImageBitmap(RoundBitmapUtil.toRoundBitmap(IvHead,bitmap_init));
							
						}
					});
				
				}else{
					if(null!=jpgPath&&!"".equals(jpgPath)){
						
						decode_path=SystemConfig.MAIN_URL+jpgPath.substring(6);
						
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										handler.sendEmptyMessage(ServiceEngin.START_DOWNLOAD);
										ServiceEngin.getInstance().getImageStream(decode_path,handler);

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
							}).start();
					}
				}
			
		}
		}
	
	//添加监听器
	private void addListeners() {
		//昵称rl
		//		NameRl.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				Intent intent =new Intent(getActivity(),RegisterActivity.class);
		//				getActivity().startActivity(intent);
		//
		//			}
		//		});
		//		//手机rl
		//		TelRl.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//
		//				Intent intent =new Intent(getActivity(),RegisterActivity.class);
		//				getActivity().startActivity(intent);
		//
		//			}
		//		});
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
					islogin = ConfigManager.getInstance().isLogin();

					if(!islogin){
						dismiss();
						Toast.makeText(getActivity(), "您还没有登录，不能进行头像修改！",Toast.LENGTH_SHORT).show();
					}else{
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
						intent.addCategory(Intent.CATEGORY_OPENABLE);  
						intent.setType("image/*");  
						startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);   

						dismiss();
					}
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					islogin = ConfigManager.getInstance().isLogin();

					if(!islogin){
						dismiss();
						Toast.makeText(getActivity(), "您还没有登录，不能进行头像修改！",Toast.LENGTH_SHORT).show();
					}else{
						photo();
						dismiss();
					}


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
		String fileName;
		switch (requestCode) {
		case TAKE_PICTURE:
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
			} catch (Exception e) {
				e.printStackTrace();
			} 

			fileName=ConfigManager.getInstance().getUserId();

			try {
				saveFile(bitmap, fileName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			break;
		case SELECT_PICTURE:
		
			imageUri = intent.getData();
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
			} catch (Exception e) {
				e.printStackTrace();
			}



			fileName=ConfigManager.getInstance().getUserId();

			try {
				saveFile(bitmap, fileName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		}
	}
	/**  
	 * 保存bitmap文件  
	 * @param bm  
	 * @param fileName  
	 * @throws IOException  
	 */    
	public void saveFile(Bitmap bm, String fileName) throws IOException { 
		if(hasSDcard()){
			path = SystemConfig.SDCARD_HEAD_IMG_PATH;    
		}else{
			path = SystemConfig.INNER_HEAD_IMG_PATH; 
		}

		File dirFile = new File(path);   

		if(!dirFile.exists()){    
			dirFile.mkdir();    
		}    
		myCaptureFile = new File(path + fileName);   

	
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));    
		bm.compress(Bitmap.CompressFormat.JPEG,80, bos);    
		bos.flush();    
		bos.close();  

		handler.post(new Runnable() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
                        handler.sendEmptyMessage(ServiceEngin.START_UPLOAD);
						ServiceEngin.getInstance().uploadFile_head(myCaptureFile,handler,"inforimpheadfile");
					}
				}).start();

			}
		});

	}  
	/**  
	 * 保存bitmap文件 到本地
	 * @param bm  
	 * @param fileName  
	 * @throws IOException  
	 */    
	public void saveFileToLoacl(Bitmap bm, String fileName) throws Exception{
		
		if(hasSDcard()){
	
			path = SystemConfig.SDCARD_HEAD_IMG_PATH;    
		}else{
			
			path = SystemConfig.INNER_HEAD_IMG_PATH; 
		}
	
		File dirFile = new File(path);   
	
		if(!dirFile.exists()){  
		
			dirFile.mkdir();    
		}    
	
		myCaptureFile = new File(path + fileName);   
        
	
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));    
		bm.compress(Bitmap.CompressFormat.JPEG,80, bos);    
		bos.flush();    
		bos.close();  
	}

}
