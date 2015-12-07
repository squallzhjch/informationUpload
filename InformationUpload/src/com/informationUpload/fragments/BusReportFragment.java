package com.informationUpload.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.informationUpload.R;
import com.informationUpload.fragments.utils.MyFragmentManager;
import com.informationUpload.widget.TitleView;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: BusReportFragment
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class BusReportFragment extends BaseFragment{
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_reset_password, null, true);
        TitleView titleView = (TitleView)view.findViewById(R.id.fragment_title);
        titleView.setOnLeftAreaClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.back();
            }
        });
        return view;
    }

}
