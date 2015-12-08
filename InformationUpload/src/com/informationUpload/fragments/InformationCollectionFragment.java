package com.informationUpload.fragments;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;




import com.informationUpload.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
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

import com.informationUpload.adapter.ChatAdapter;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.utils.Bimp;
import com.informationUpload.utils.PoiRecordPopup;
/**
 * 
 * @author chentao
 *
 */
@SuppressLint("ValidFragment") 
public class InformationCollectionFragment extends Fragment{
	/**
	 * 存照片的list
	 */
	private ArrayList<View> listview;
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
	 * 图片保存路劲
	 */
	private String picturepath = "";
	/**
	 * 图片uri
	 */
	private Uri imageUri;
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
	/**
	 * 录音信息数组
	 */
	private ArrayList<ChatMessage> list;
	/**
	 * 录音信息adapter
	 */
	private ChatAdapter adapter;
	/**
	 * 屏幕宽度
	 */
	private int width;
	/**
	 * 主布局
	 */
	private int tab_container;
    public InformationCollectionFragment(int tab_container){
    	this.tab_container=tab_container;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.information_collection,null);
		DisplayMetrics metric = new DisplayMetrics();  
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);  
	    width = metric.widthPixels;     // 屏幕宽度（像素） 

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
		listview=new ArrayList<View>();
		list=new ArrayList<ChatMessage>();
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
		adapter=new ChatAdapter(getActivity(), list);
		voice_collection_lv.setAdapter(adapter);


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
                    if(time>1000&&time<6000){
                   ChatMessage chatmsg = new ChatMessage();
                   chatmsg.setAmp(amp);
                   chatmsg.setChattimelong(time);
		           chatmsg.setName(name);
		           chatmsg.setPath(path);
		           list.add(chatmsg);
		           adapter.setData(list);
		           adapter.notifadataset();
		           resetListView();
                    }

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
				//拍照
				photo();

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
	//重新计算高度
			private  void resetListView(){

				int count = this.adapter.getCount() ;

				View itemView = this.adapter.getView(0, null, null);
				if(itemView!=null){
					itemView.measure(
							MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
							MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

					int defHeight = count
							* (itemView.getMeasuredHeight() + voice_collection_lv
									.getDividerHeight());
					LayoutParams lp = voice_collection_lv.getLayoutParams();
					lp.height =defHeight ;
					voice_collection_lv.setLayoutParams(lp);
				}

			}
			
			

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
				boolean a=	fileDir.mkdirs();
					Log.i("chentao","a:"+a);
				}
				boolean b = fileDir.isDirectory();
				Log.i("chentao","b:"+b);
				File file = new File(fileDir, String.valueOf(System.currentTimeMillis()) + ".jpg");

				picturepath  = file.getPath();
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

			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				switch (requestCode) {
				case TAKE_PICTURE:
					
				      
				      ImageView imageView = new ImageView(getActivity());  
				      imageView.setLayoutParams(new LayoutParams(150,150));  
                      
					try {
						bitmap = Bimp.revitionImageSize(picturepath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                     
                      imageView.setImageBitmap(bitmap);
                      imageView.setTag(i);
                      imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							int num = (Integer) arg0.getTag();
							getFragmentManager().beginTransaction().addToBackStack(null).replace(tab_container,new PhotoViewpagerFragment(num,listview)).commit();
						
							
						}
					});
                      hlinearlayout.addView(imageView,0);
                      listview.add(imageView);
                      i++;
                      if(hscrollview.getWidth()>=width){
                    	  new Handler().post(new Runnable() {
							
							@Override
							public void run() {
								 hscrollview.scrollTo(hlinearlayout.getWidth()+20, 0);
								
							}
						});
                    	 
                      }
                 
					break;
				}
			}
			

}
