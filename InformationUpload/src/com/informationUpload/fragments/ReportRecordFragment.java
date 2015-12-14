package com.informationUpload.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.informationUpload.R;
import com.informationUpload.activity.ActivityInstanceStateListener;
import com.informationUpload.activity.MainActivity;
import com.informationUpload.activity.MyApplication;
import com.informationUpload.adapter.LocalInformationAdapter;
import com.informationUpload.contentproviders.InformationCheckData;
import com.informationUpload.contentproviders.InformationManager;
import com.informationUpload.contentproviders.InformationObserver;
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
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ActivityInstanceStateListener) {
            mActivityInstanceStateListener = (ActivityInstanceStateListener) activity;
        }
        mThreadManager = ThreadManager.getInstance();
        mInformationObserver = new InformationObserver((MyApplication) getActivity().getApplication(), mThreadManager.getHandler());
        mInformationObserver.addOnCheckMessageListener(new InformationObserver.OnCheckMessageCountListener() {
            @Override
            public void onCheckNewMessageSucceed(InformationCheckData data, boolean isFirs) {
                mLocalNum.setText(String.valueOf(data.getLocalNum()));
                mUploadNum.setText(String.valueOf(data.getUploadNum()));
            }
        });
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_my_confirm_record, null, true);
        initView(view);
        initLoader();
        return view;
    }

    public void initView(View view){
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
    }

    private void initLoader() {
        mLocalAdapter = new LocalInformationAdapter(mApplication, getActivity().managedQuery(
                Informations.Information.CONTENT_URI,
                LocalInformationAdapter.KEY_MAPPING,
                LocalInformationAdapter.WHERE,
                new String[]{mApplication.getUserId(), String.valueOf(Informations.Information.STATUS_LOCAL)},
                LocalInformationAdapter.ORDER_BY
        ), false);

        mListView.setAdapter(mLocalAdapter);

        getLoaderManager().initLoader(LOADER_TYPE_LOCAL, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new CursorLoader(mApplication, Informations.Information.CONTENT_URI,
                        mLocalAdapter.KEY_MAPPING, mLocalAdapter.WHERE,
                        new String[]{mApplication.getUserId(), String.valueOf(Informations.Information.STATUS_LOCAL)}, null);
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
                String rowkey = (String) view.getTag(R.id.rowkey_id);
                if (!TextUtils.isEmpty(rowkey)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SystemConfig.BUNDLE_DATA_ROWKEY, rowkey);
                    mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mApplication.getContentResolver().unregisterContentObserver(mInformationObserver);
    }

}
