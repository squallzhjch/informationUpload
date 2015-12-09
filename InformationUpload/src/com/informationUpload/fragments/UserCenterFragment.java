package com.informationUpload.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.informationUpload.R;
import com.informationUpload.fragments.utils.IntentHelper;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: CenterFragment
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class UserCenterFragment extends BaseFragment{

    private RelativeLayout mRecordLayout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_user_center, null, true);
        initView(view);
        return view;
    }

    private void initView(View view){
        mRecordLayout = (RelativeLayout) view.findViewById(R.id.report_record);
        mRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(ReportRecordFragment.class, null));
            }
        });
    }
}
