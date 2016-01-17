package com.informationUpload.fragments;

import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
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


import com.informationUpload.R;
import com.informationUpload.activity.ActivityInstanceStateListener;
import com.informationUpload.activity.MyApplication;
import com.informationUpload.adapter.LocalInformationAdapter;
import com.informationUpload.contentProviders.InformationCheckData;
import com.informationUpload.contentProviders.InformationManager;
import com.informationUpload.contentProviders.InformationObserver;
import com.informationUpload.contentProviders.InformationObserver.OnCheckMessageCountListener;
import com.informationUpload.contentProviders.Informations;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.thread.ThreadManager;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.TitleView;

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
				mLocalNum.setText(String.valueOf(data.getLocalNum()));
				mUploadNum.setText(String.valueOf(data.getUploadNum()));
			}
		});

		mApplication.getContentResolver().registerContentObserver(
				Informations.Information.CONTENT_URI,
				true,
				mInformationObserver);
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		View view = inflater.inflate(R.layout.fragment_my_confirm_record, null, true);
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

							View view = mLocalAdapter.getView(i, null,null);

							final String rowkey=(String) view.getTag(R.id.cb);

							set(i, false);
							ContentValues values=new ContentValues();
							values.put(Informations.Information.STATUS,Informations.Information.STATUS_SERVER);
							InformationManager.getInstance().updateInformation(rowkey, values);


						}
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

			}
		});
		//已提交
		mServicLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LayoutParams lp = new LinearLayout.LayoutParams(0,8,1);
				LayoutParams lp1 = new LinearLayout.LayoutParams(0,5,1);
				mAlreadySubmitIv.setLayoutParams(lp);
				mWaitSubmitIv.setLayoutParams(lp1);
				select_all.setVisibility(View.INVISIBLE);
				bsubmit=false;

				mListView.setAdapter(mServiceAdapter);

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
				new String[]{mApplication.getUserId(), String.valueOf(Informations.Information.STATUS_LOCAL)},
				LocalInformationAdapter.ORDER_BY
				), false,"local");

		mServiceAdapter = new LocalInformationAdapter(mApplication, getActivity().managedQuery(
				Informations.Information.CONTENT_URI,
				LocalInformationAdapter.KEY_MAPPING,
				LocalInformationAdapter.WHERE,
				new String[]{mApplication.getUserId(), String.valueOf(Informations.Information.STATUS_SERVER)},
				LocalInformationAdapter.ORDER_BY
				), false,"server");
		for( int i=0;i<mLocalAdapter.getCount();i++){
			Log.i("chentao",":"+i);
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
						new String[]{mApplication.getUserId(), String.valueOf(Informations.Information.STATUS_LOCAL)}, LocalInformationAdapter.ORDER_BY);
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
						new String[]{mApplication.getUserId(), String.valueOf(Informations.Information.STATUS_SERVER)}, LocalInformationAdapter.ORDER_BY);
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
	public static void set(Integer i,Boolean bl){
		//    	if(map==null){
		//    		map=new HashMap<Integer,Boolean>();
		//    	}
		map.put(i, bl);
	}
	public static Boolean get(Integer po){
		return map.get(po);
	}
	


}
