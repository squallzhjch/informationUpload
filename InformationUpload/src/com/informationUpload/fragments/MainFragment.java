package com.informationUpload.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.informationUpload.R;
import com.informationUpload.contentproviders.Informations;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.utils.SystemConfig;

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
    /**
     * popwindow弹出的view
     */
    private View popview;
    /**
     * 弹出窗
     */
    private PopupWindow popupWindow;
    /**
     * 主view
     */
    private View view;
    /**
     * popview公交按钮
     */
    private View mBusButton;
    /**
     * popview设施按钮
     */
    private View mEstablishmentButton;
    /**
     * popview道路按钮
     */
    private View mRoadButton;
    /**
     * popview周边改变按钮
     */
    private View mChangeNearButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.fragment_main, null, true);
        popview = inflater.inflate(R.layout.main_fragment_select_pop, null);
        //初始化popview
        initPopview();
        //添加popwindow监听器
        addPopListeners();
        popview.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
        popview.setFocusableInTouchMode(true);
        popview.setOnKeyListener(backlistener);
        mSubmitBtn = (Button) view.findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {

                showPopview();

            }
        });

        mCenterBtn = (Button) view.findViewById(R.id.center_btn);
        mCenterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(UserCenterFragment.class, null));
            }
        });

        return view;
    }

    //添加popwindow监听器
    private void addPopListeners() {
        //公交按钮
        mBusButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_BUS);
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

            }
        });
        //设施按钮
        mEstablishmentButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_ESTABLISHMENT);
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

            }
        });
        //
        // 道路按钮
        mRoadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_ROAD);
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

            }
        });
        //改变周边按钮
        mChangeNearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt(SystemConfig.BUNDLE_DATA_TYPE, Informations.Information.INFORMATION_TYPE_AROUND);
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(InformationCollectionFragment.class, bundle));

            }
        });


    }

    //初始化popview
    private void initPopview() {
        mBusButton = popview.findViewById(R.id.bus_button);
        mEstablishmentButton = popview.findViewById(R.id.establishment_button);
        mRoadButton = popview.findViewById(R.id.road_button);
        mChangeNearButton = popview.findViewById(R.id.change_near_button);

    }

    //打开popwindow
    protected void showPopview() {
        popupWindow = new PopupWindow(popview, 400, 400);
        //设置popwindow显示位置
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        //获取popwindow焦点
        popupWindow.setFocusable(true);
        //设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();


    }

    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            Log.i("chentao", "onKey1");
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                    Log.i("chentao", "onKey2");
                    popupWindow.dismiss();
                    return true;


                }
            }
            Log.i("chentao", "onKey3");
            return false;
        }
    };
}
