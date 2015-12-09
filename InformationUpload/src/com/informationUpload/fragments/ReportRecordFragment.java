package com.informationUpload.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.informationUpload.R;
import com.informationUpload.adapter.LocalInformationAdapter;
import com.informationUpload.contentproviders.Informations;

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
    private LocalInformationAdapter mLocalAdapter;
    private static final int LOADER_TYPE_LOCAL = 0;
    private static final int LOADER_TYPE_SERVICE = 1;

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
    }
}
