package com.informationUpload.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.informationUpload.R;
import com.informationUpload.fragments.utils.IntentHelper;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MainFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MainFragment extends BaseFragment {
    private Button mSubmitBtn;
    private Button mCenterBtn;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_main, null, true);
        mSubmitBtn = (Button)view.findViewById(R.id.submit_btn);
        mSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, null));
            }
        });

        mCenterBtn = (Button)view.findViewById(R.id.center_btn);
        mCenterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(UserCenterFragment.class, null));
            }
        });

        return view;
    }

}
