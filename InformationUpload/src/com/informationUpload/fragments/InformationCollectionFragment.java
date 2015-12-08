package com.informationUpload.fragments;



import java.util.ArrayList;
import java.util.List;


import com.informationUpload.Activity.ActivityInstanceStateListener;
import com.informationUpload.Activity.MainActivity;
import com.informationUpload.Activity.MyApplication;
import com.informationUpload.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.informationUpload.adapter.ChatAdapter;
import com.informationUpload.contents.AbstractOnContentUpdateListener;
import com.informationUpload.contents.ContentsManager;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;
import com.informationUpload.utils.PoiRecordPopup;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.TitleView;

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
//	private ScrollView additional_remarks_sv;
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


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		registerOnContentUpdateListener(new AbstractOnContentUpdateListener() {
			@Override
			public void onContentUpdated(List<Object[]> values) {
				if (values != null) {
					String address = (String) values.get(0)[0];
					select_position.setText(address);
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.fragment_information_collection,null);
		TitleView title = (TitleView)view.findViewById(R.id.title_view);
		title.setOnLeftAreaClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});
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
		list=new ArrayList<ChatMessage>();
		select_position=(TextView)view.findViewById(R.id.select_position);
		hliv=(ImageView) view.findViewById(R.id.hliv);    
		voice_collection_lv=(ListView) view.findViewById(R.id.voice_collection_lv);
		hscrollview=(HorizontalScrollView)view.findViewById(R.id.hscrollview);
		hlinearlayout=(LinearLayout)view.findViewById(R.id.hlinearlayout);                   
//		additional_remarks_sv=(ScrollView)view.findViewById(R.id.additional_remarks_sv);
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
				Bundle bundle = new Bundle();
				bundle.putBoolean(SystemConfig.HIDE_OTHER_FRAGMENT, true);
				mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(SelectPointFragment.class, bundle));
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


}
