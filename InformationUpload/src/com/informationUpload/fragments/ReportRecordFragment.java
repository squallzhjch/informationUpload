package com.informationUpload.fragments;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.informationUpload.R;
import com.informationUpload.activity.ActivityInstanceStateListener;
import com.informationUpload.activity.MyApplication;
import com.informationUpload.adapter.LocalInformationAdapter;
import com.informationUpload.contentProviders.InformationCheckData;
import com.informationUpload.contentProviders.InformationManager;
import com.informationUpload.contentProviders.InformationObserver;
import com.informationUpload.contentProviders.InformationObserver.OnCheckMessageCountListener;
import com.informationUpload.contentProviders.Informations;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.entity.PictureMessage;
import com.informationUpload.entity.attachmentsMessage;
import com.informationUpload.entity.locationMessage;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.thread.ThreadManager;
import com.informationUpload.utils.ChangePointUtil;
import com.informationUpload.system.SystemConfig;
import com.informationUpload.utils.WriteFileUtil;
import com.informationUpload.utils.ZipUtil;
import com.informationUpload.widget.TitleView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: ReportRecordFragment
 * 
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class ReportRecordFragment extends BaseFragment{

	private static HashMap<Integer,Boolean> map=new HashMap<Integer,Boolean>();

	private LinearLayout mLocalLayout;
	private LinearLayout mServicLayout;
	private ListView mListView;
	private TitleView mTitleView;
	private TextView mLocalNum;
	private TextView mUploadNum;
	private LocalInformationAdapter mLocalAdapter;
	private static final int LOADER_TYPE_LOCAL = 0;
	private static final int LOADER_TYPE_SERVICE = 1;
	private InformationObserver mInformationObserver;
	private ThreadManager mThreadManager;
	private  CheckBox select_all;
	private TextView mSubmit;
	private Boolean bsubmit=true;
	private LocalInformationAdapter mServiceAdapter;
	/**
	 * 返回按钮
	 */
	private RelativeLayout mReportBack;
	/**
	 * 删除item
	 */
	private TextView mTvDeleteItem;
	/**
	 * 待提交下滑线
	 */
	private ImageView mWaitSubmitIv;
	/**
	 * 已提交下滑线
	 */
	private ImageView mAlreadySubmitIv;
	/**
	 * 存放可以上传的rowkey的数组
	 */
	private ArrayList<String> rowkeys=new ArrayList<String>();
	/**
	 * 上传对话框
	 * 
	 */
	private ProgressDialog pb;
	/**
	 * 附件list
	 */
	private ArrayList<attachmentsMessage> list_att;
	/**
	 * 日期格式刷
	 */
	private DateFormat df;
	/**
	 * 写入文件的json串
	 */
	private String servicePara;
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
	 * 文档保存的文件路径
	 */
	private static String path = Environment
			.getExternalStorageDirectory()+"/InformationUpload/Upload/";
	/**
	 * 上传文件全名
	 */
	private String path_all_name;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				new Thread(new Runnable() {

					@Override
					public void run() {
						ServiceEngin.getInstance().uploadFile(new File(path_all_name), handler,"inforimp");

					}
				}).start();
				break;

			case 1:
				pb.dismiss();
				Toast.makeText(getActivity(),"压缩失败", Toast.LENGTH_SHORT).show();
			
				break;
			case 2:
				pb.dismiss();
				for(int i=0;i<rowkeys.size();i++){
					ContentValues values = new ContentValues();
					values.put(Informations.Information.STATUS, Informations.Information.STATUS_SERVER);
					mInformationManager.updateInformation(rowkeys.get(i),values);
				}
				if(rowkeys!=null){
					rowkeys.clear();
				}
				final Toast toast=Toast.makeText(getActivity(),"上传成功",300);
				toast.show();

				//				  handler.postDelayed(new Runnable() {
				//						
				//						@Override
				//						public void run() {
				//							toast.cancel();
				//							mFragmentManager.back();
				//							
				//						}
				//					}, 600);
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
		if (activity instanceof ActivityInstanceStateListener) {
			mActivityInstanceStateListener = (ActivityInstanceStateListener) activity;
		}
		mThreadManager = ThreadManager.getInstance();
		mInformationObserver = new InformationObserver((MyApplication) getActivity().getApplication(), mThreadManager.getHandler());
		mInformationObserver.addOnCheckMessageListener(new OnCheckMessageCountListener() {
			@Override
			public void onCheckNewMessageSucceed(InformationCheckData data, boolean isFirs) {
				Log.e("jingo", "local = " + data.getLocalNum() + "upload = " + data.getUploadNum());
				mLocalNum.setText(String.valueOf(data.getLocalNum()));
				mUploadNum.setText(String.valueOf(data.getUploadNum()));
			}
		});

		mApplication.getContentResolver().registerContentObserver(
				Informations.Information.CONTENT_URI,
				true,
				mInformationObserver);
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.fragment_my_confirm_record, null, true);
		df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//初始化
		initView(view);
		initLoader();
		//注册监听器
		addListeners();
		return view;
	}
	//注册监听器
	private void addListeners() {
		//提交
		mSubmit.setOnClickListener(new OnClickListener() {





			@Override
			public void onClick(View arg0) {
				if(bsubmit){
					for(int i=0;i<mLocalAdapter.getCount();i++){
						Boolean bool = map.get(i);
						if(bool){
							rowkeys.add((String)mLocalAdapter.getView(i, null,null).getTag(R.id.cb));
							set(i, false);
							//							View view = mLocalAdapter.getView(i, null,null);
							//
							//							final String rowkey=(String) view.getTag(R.id.cb);
							//                           
							//							set(i, false);
							//							ContentValues values=new ContentValues();
							//							values.put(Informations.Information.STATUS,Informations.Information.STATUS_SERVER);
							//							InformationManager.getInstance().updateInformation(rowkey, values);


						} 



					}
					if(rowkeys.size()!=0){
						pb=new ProgressDialog(getActivity());
						pb.setMessage("正在上传");
						pb.show();
						ArrayList<String> list_servicepara=new ArrayList<String>();
						list_att=new ArrayList<attachmentsMessage>();
						for(int j=0;j<rowkeys.size();j++){
							String rok = rowkeys.get(j);
							InformationMessage infomessage = InformationManager.getInstance().getInformation(rok);
							HashMap<String,Object> map=new HashMap<String,Object>();
							map.put("info_fid",infomessage.getRowkey());
							double[] ret = ChangePointUtil.baidutoreal(infomessage.getLat(), infomessage.getLon());
							map.put("location", new locationMessage((float)ret[0],(float)ret[1]));
							map.put("info_type",infomessage.getType());
						
							map.put("adminCode",infomessage.getAdminCode());
							map.put("address",infomessage.getAddress());

							List<ChatMessage> chatmsglist = infomessage.getChatMessageList();
							List<PictureMessage> picmsglist = infomessage.getPictureMessageList();
							for(int m=0;m<chatmsglist.size();m++){
								ChatMessage chatmsg = chatmsglist.get(m);
							
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

							map.put("user_id",ConfigManager.getInstance().getUserId());
							map.put("remark", infomessage.getRemark());
							servicePara = JSON.toJSONString(map);
							
							list_servicepara.add(servicePara);
						}
						WriteFileUtil.doWriteFile(list_servicepara);

						new Thread(new Runnable() {





							@Override
							public void run() {
								try {
									ArrayList<String> listall=new ArrayList<String>();
									for(int h=0;h<list_att.size();h++){
										
										if(list_att.get(h).getType()==1){
										
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
								
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									handler.sendEmptyMessage(1);
									
								}

							}
						}).start();
					}

				}else{
					Toast.makeText(getActivity(),"此项是已提交数据，请勿重复提交，谢谢！",Toast.LENGTH_LONG).show();
				}

			}
		});
		//待提交
		mLocalLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LayoutParams lp = new LinearLayout.LayoutParams(0,8,1);
				LayoutParams lp1 = new LinearLayout.LayoutParams(0,5,1);
				mAlreadySubmitIv.setLayoutParams(lp1);
				mWaitSubmitIv.setLayoutParams(lp);
				bsubmit=true;
				select_all.setVisibility(View.VISIBLE);
				mListView.setAdapter(mLocalAdapter);
				mTvDeleteItem.setVisibility(View.VISIBLE);
				mSubmit.setVisibility(View.VISIBLE);
			}
		});
		//已提交
		mServicLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				HashMap<String,Object> map=new HashMap<String, Object>();
//
//				map.put("userid",ConfigManager.getInstance().getUserId());
//				Log.i("chentao","userid:"+ConfigManager.getInstance().getUserId());
//				ServiceEngin.getInstance().Request(getActivity(), map,"inforqueryuserworklist", new EnginCallback(getActivity()){
//					@Override
//					public void onSuccess(ResponseInfo arg0) {
//						super.onSuccess(arg0);
//						Log.e("请求成功", arg0.result.toString());
//					}
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
//						super.onFailure(arg0, arg1);
//					}
//
//				});
//				query();
//				modify();
				queryrepeat();
				mTvDeleteItem.setVisibility(View.INVISIBLE);
				mSubmit.setVisibility(View.INVISIBLE);
				LayoutParams lp = new LinearLayout.LayoutParams(0,8,1);
				LayoutParams lp1 = new LinearLayout.LayoutParams(0,5,1);
				mAlreadySubmitIv.setLayoutParams(lp);
				mWaitSubmitIv.setLayoutParams(lp1);
				select_all.setVisibility(View.INVISIBLE);
				bsubmit=false;

				//				mListView.setAdapter(mServiceAdapter);

			}
		});


	}


	public void initView(View view){
		select_all=(CheckBox)view.findViewById(R.id.select_all);
		mLocalLayout = (LinearLayout)view.findViewById(R.id.local_layout);
		mServicLayout = (LinearLayout)view.findViewById(R.id.service_layout);
		mListView = (ListView)view.findViewById(R.id.list_view);

		mLocalNum = (TextView)view.findViewById(R.id.local_num);
		mUploadNum = (TextView)view.findViewById(R.id.upload_num); 
		mSubmit   = (TextView)view.findViewById(R.id.submit);
		mTvDeleteItem = (TextView) view.findViewById(R.id.tv_delete_item);
		mReportBack= (RelativeLayout) view.findViewById(R.id.report_back);
		mWaitSubmitIv=(ImageView) view.findViewById(R.id.wait_submit_iv);
		mAlreadySubmitIv=(ImageView) view.findViewById(R.id.already_submit_iv);
		select_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1==true){
					for( int i=0;i<mLocalAdapter.getCount();i++){
						set(i,true);
						mLocalAdapter.notifyDataSetChanged();
					}
				}else{
					for( int i=0;i<mLocalAdapter.getCount();i++){
						set(i,false);
						mLocalAdapter.notifyDataSetChanged();
					}
				}

			}
		});
	}

	private void initLoader() {
		mLocalAdapter = new LocalInformationAdapter(mApplication, getActivity().managedQuery(
				Informations.Information.CONTENT_URI,
				LocalInformationAdapter.KEY_MAPPING,
				LocalInformationAdapter.WHERE,
				new String[]{ConfigManager.getInstance().getUserId(), String.valueOf(Informations.Information.STATUS_LOCAL)},
				LocalInformationAdapter.ORDER_BY
				), false,"local");

		mServiceAdapter = new LocalInformationAdapter(mApplication, getActivity().managedQuery(
				Informations.Information.CONTENT_URI,
				LocalInformationAdapter.KEY_MAPPING,
				LocalInformationAdapter.WHERE,
				new String[]{ConfigManager.getInstance().getUserId(), String.valueOf(Informations.Information.STATUS_SERVER)},
				LocalInformationAdapter.ORDER_BY
				), false,"server");
		for( int i=0;i<mLocalAdapter.getCount();i++){
			
			set(i,false);

		}

		mListView.setAdapter(mLocalAdapter);
		LayoutParams lp = new LinearLayout.LayoutParams(0,8,1);
		LayoutParams lp1 = new LinearLayout.LayoutParams(0,5,1);
		mAlreadySubmitIv.setLayoutParams(lp1);
		mWaitSubmitIv.setLayoutParams(lp);

		getLoaderManager().initLoader(LOADER_TYPE_LOCAL, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
				return new CursorLoader(mApplication, Informations.Information.CONTENT_URI,
						mLocalAdapter.KEY_MAPPING, mLocalAdapter.WHERE,
						new String[]{ConfigManager.getInstance().getUserId(), String.valueOf(Informations.Information.STATUS_LOCAL)}, LocalInformationAdapter.ORDER_BY);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				mLocalAdapter.swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				mLocalAdapter.swapCursor(null);
			}
		});
		getLoaderManager().initLoader(LOADER_TYPE_SERVICE, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
				return new CursorLoader(mApplication, Informations.Information.CONTENT_URI,
						mLocalAdapter.KEY_MAPPING, mLocalAdapter.WHERE,
						new String[]{ConfigManager.getInstance().getUserId(), String.valueOf(Informations.Information.STATUS_SERVER)}, LocalInformationAdapter.ORDER_BY);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				mServiceAdapter.swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				mServiceAdapter.swapCursor(null);
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					String rowkey = (String) view.getTag(R.id.cb);

					if (!TextUtils.isEmpty(rowkey)) {
						if(bsubmit){
							Bundle bundle = new Bundle();
							bundle.putString(SystemConfig.BUNDLE_DATA_ROWKEY, rowkey);
							bundle.putBoolean(SystemConfig.BUNDLE_DATA_SOURCE,false);
							mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		//返回
		mReportBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mFragmentManager.back();

			}
		});
		//删除
		mTvDeleteItem.setOnClickListener(new OnClickListener() {

			private View view;
			private int num;


			@Override
			public void onClick(View arg0) {

				for( int i=0;i<mLocalAdapter.getCount();i++){


					view=	mLocalAdapter.getView(i, null,null);

					final String rowkey=(String) view.getTag(R.id.cb);

					//				   boolean bl=(Boolean) view.getTag(R.id.type_tv);

					//						  boolean bl=mLocalAdapter.getpo(i);
					boolean bl=get(i);


					if(bl==true){

						InformationManager.getInstance().deleteInformation(rowkey);
					}



				}

			}
		});
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mApplication.getContentResolver().unregisterContentObserver(mInformationObserver);
	}

	@Override
	public void onDataChange(Bundle bundle) {

	}

	public static void set(Integer i,Boolean bl){
		//    	if(map==null){
		//    		map=new HashMap<Integer,Boolean>();
		//    	}
		map.put(i, bl);
	}
	public static Boolean get(Integer po){
		return map.get(po);
	}
	//10.情报数据提交任务信息查询接口
	public void query( ){
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("info_fid","5f7bbc7b3efa449b9cd6deb625c0d6e4");
		ServiceEngin.getInstance().Request(getActivity(), map,"inforqueryworkbyid", new EnginCallback(getActivity()){
			@Override
			public void onSuccess(ResponseInfo arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				Log.e("请求成功", arg0.result.toString());
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}
		});

	}
	//11.情报用户信息修改接口
    public void modify(){
    	HashMap<String,Object> map=new HashMap<String, Object>();
    	map.put("userid",ConfigManager.getInstance().getUserId());
    	map.put("pwd","444444");
//    	map.put("nickname","fchen");
    	ServiceEngin.getInstance().Request(getActivity(), map,"informodifyuserinfo",new EnginCallback(getActivity()){
    		@Override
    		public void onSuccess(ResponseInfo arg0) {
    			// TODO Auto-generated method stub
    			super.onSuccess(arg0);
    		
    		}
    		@Override
    		public void onFailure(HttpException arg0, String arg1) {
    			// TODO Auto-generated method stub
    			super.onFailure(arg0, arg1);
    		}
    	});
    }
    //12.情报用户查重接口
    public void queryrepeat(){
    	HashMap<String,Object> map=new HashMap<String, Object>();
    	map.put("tel","18611062750");
    	ServiceEngin.getInstance().Request(getActivity(), map,"inforqueryuserexit",new EnginCallback(getActivity()){
    		@Override
    		public void onSuccess(ResponseInfo arg0) {
    			// TODO Auto-generated method stub
    			super.onSuccess(arg0);
    			Log.e("请求成功", arg0.result.toString());
    		}
    		@Override
    		public void onFailure(HttpException arg0, String arg1) {
    			// TODO Auto-generated method stub
    			super.onFailure(arg0, arg1);
    		}
    	});
    }

}
