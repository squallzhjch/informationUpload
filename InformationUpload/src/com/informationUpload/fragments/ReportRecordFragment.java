package com.informationUpload.fragments;

import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.informationUpload.R;
import com.informationUpload.activity.ActivityInstanceStateListener;
import com.informationUpload.activity.MainActivity;
import com.informationUpload.activity.MyApplication;
import com.informationUpload.adapter.LocalInformationAdapter;
import com.informationUpload.contentproviders.InformationCheckData;
import com.informationUpload.contentproviders.InformationManager;
import com.informationUpload.contentproviders.InformationObserver;
import com.informationUpload.contentproviders.InformationObserver.OnCheckMessageCountListener;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.contents.ContentsManager;
import com.informationUpload.contents.OnContentUpdateListener;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;
import com.informationUpload.map.LocationManager;
import com.informationUpload.thread.ThreadManager;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.TitleView;

import org.w3c.dom.Text;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: ReportRecordFragment
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class ReportRecordFragment extends BaseFragment{

	private static HashMap<Integer,Boolean> map=new HashMap<Integer,Boolean>();;
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
	private CheckBox select_all;
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

		initView(view);
		initLoader();
		return view;
	}

	public void initView(View view){
		select_all=(CheckBox)view.findViewById(R.id.select_all);
		mLocalLayout = (LinearLayout)view.findViewById(R.id.local_layout);
		mServicLayout = (LinearLayout)view.findViewById(R.id.service_layout);
		mListView = (ListView)view.findViewById(R.id.list_view);
		mTitleView = (TitleView)view.findViewById(R.id.title_view);
		mLocalNum = (TextView)view.findViewById(R.id.local_num);
		mUploadNum = (TextView)view.findViewById(R.id.upload_num);
		mTitleView.setOnLeftAreaClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentManager.back();
			}
		});
		mTitleView.setRightImageResource(R.drawable.delete_item);
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
				), false);
		for( int i=0;i<mLocalAdapter.getCount();i++){
			set(i,false);
		}
		mListView.setAdapter(mLocalAdapter);

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

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					String rowkey = (String) view.getTag(R.id.cb);
				
					if (!TextUtils.isEmpty(rowkey)) {
						Bundle bundle = new Bundle();
						bundle.putString(SystemConfig.BUNDLE_DATA_ROWKEY, rowkey);
						mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		mTitleView.setOnRightAreaClickListener(new OnClickListener() {

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
