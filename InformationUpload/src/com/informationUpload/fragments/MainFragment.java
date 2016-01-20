package com.informationUpload.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.PopupWindow;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.informationUpload.R;
import com.informationUpload.contentProviders.Informations;
import com.informationUpload.entity.AroundInformation;
import com.informationUpload.fragments.utils.IntentHelper;

import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.tool.PointTool;
import com.informationUpload.utils.ChangePointUtil;
import com.informationUpload.system.SystemConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MainFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class MainFragment extends BaseFragment {
    /**
     * 发现情况按钮
     */
    private Button mDiscoverySituationBtn;
    /**
     * 个人中心按钮
     */
    private ImageView mCenterBtn;
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
    /**
     * 刷新地图码点按钮
     */
    private ImageView main_refresh;
    /**
     * 重新定位按钮
     */
    private ImageView main_fix_position;
    /**
     * 对地图放大按钮
     */
    private FrameLayout main_enlarge_map;
    /**
     * 对地图缩小按钮
     */
    private FrameLayout main_reduce_map;
    /**
     * 地图管理类
     */
    private MapManager mapManager;
    /**
     * 地图上的图标背景
     */
    private BitmapDescriptor bd;
    /**
     * 地图上的点显示信息
     */
    private String text;

    private View mengView;
    /**
     * 是否需要刷新
     */
    private boolean isneedrefresh = true;

    /**
     * infowindow显示的tv
     */
    private TextView tv1;
    /**
     * inforwindow显示的tv
     */
    private TextView tv2;

    @Override
    public void onDataChange(Bundle bundle) {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container) {

        view = inflater.inflate(R.layout.fragment_main, null, true);
        mapManager = MapManager.getInstance();
        mengView = view.findViewById(R.id.meng_view);
        popview = inflater.inflate(R.layout.main_fragment_select_pop, null);
        //初始化popview
        initPopview();
        //添加popwindow监听器
        addPopListeners();
        popview.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
        popview.setFocusableInTouchMode(true);
        popview.setOnKeyListener(backlistener);

        mDiscoverySituationBtn = (Button) view.findViewById(R.id.discovery_situation);
        //发现情况按钮
        mDiscoverySituationBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopview();
            }
        });
        //个人中心按钮
        mCenterBtn = (ImageView) view.findViewById(R.id.center_btn);
        mCenterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(UserCenterFragment.class, null));
            }
        });
        //刷新地图麻点按钮
        main_refresh = (ImageView) view.findViewById(R.id.main_refresh);
        main_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //刷新码点
                refreshmap(true);
            }
        });


        //重新定位按钮
        main_fix_position = (ImageView) view.findViewById(R.id.main_fix_position);
        main_fix_position.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mapManager.setCenter();
            }
        });
        //对地图放大按钮
        main_enlarge_map = (FrameLayout) view.findViewById(R.id.main_enlarge_map);
        main_enlarge_map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mapManager.zoomIn();

            }
        });
        //对地图缩小按钮
        main_reduce_map = (FrameLayout) view.findViewById(R.id.main_reduce_map);
        main_reduce_map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mapManager.zoomOut();

            }
        });
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (isneedrefresh) {
                    if (mLocationManager.getCurrentPoint().getLat() != 0.0 && mLocationManager.getCurrentPoint().getLon() != 0.0) {
                        refreshmap(false);
                        isneedrefresh = false;
                    }
                }
            }
        }).start();

        return view;
    }

    //刷新码点
    protected void refreshmap(boolean bLoading) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        double[] ret = ChangePointUtil.baidutoreal(mLocationManager.getCurrentPoint().getLat(), mLocationManager.getCurrentPoint().getLon());
        map.put("latitude", (ret[0] + "").substring(0, (ret[0] + "").lastIndexOf(".") + 5));
        map.put("longitude", (ret[1] + "").substring(0, (ret[1] + "").lastIndexOf(".") + 5));

        ServiceEngin.getInstance().Request(getActivity(), map, "inforquery", new EnginCallback(getActivity(), bLoading) {
            @Override
            public void onSuccess(ResponseInfo arg0) {
                super.onSuccess(arg0);
                if (!mIsActive)
                    return;
                Log.e("请求成功", arg0.result.toString());
                //进行json解析
                parseJson(arg0.result.toString());
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                super.onFailure(arg0, arg1);
                Log.e("请求失败", arg1);
            }
        });

    }

    //进行json解析
    protected void parseJson(String json) {
        if(TextUtils.isEmpty(json)){
            return;
        }
        JSONObject jsonObj = JSON.parseObject(json);
        String errcode = jsonObj.getString("errcode");

        String errmsg = jsonObj.getString("errmsg");
        if (null != errcode && !"".equals(errcode) &&  "0".equals(errcode)) {
            JSONArray data = jsonObj.getJSONArray("data");
            ArrayList<AroundInformation> list = new ArrayList<AroundInformation>();
            for (int i = 0; i < data.size(); i++) {
                JSONObject obj = (JSONObject) data.get(i);
                String info_type = obj.getString("info_type");

                String info_intel_id = obj.getString("info_intel_id");
                String address = obj.getString("address");
                JSONObject locationObj = obj.getJSONObject("location");
                String latitude = locationObj.getString("latitude");
                String longitude = locationObj.getString("longitude");
                double[] ret_pos = ChangePointUtil.realtobaidu(Double.parseDouble(latitude), Double.parseDouble(longitude));
                GeoPoint gp = new GeoPoint(ret_pos[0], ret_pos[1]);

                AroundInformation aioformation = new AroundInformation(info_intel_id, info_type, address, gp);
                list.add(aioformation);

            }
            if (mapManager != null && mapManager.getMap() != null)
                mapManager.clear(true);
            //在地图上添加覆盖物
            initOverlay(list);
        } else {
            Toast.makeText(getActivity(), errmsg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 子地图上添加覆盖物
     *
     * @param list
     */
    private void initOverlay(ArrayList<AroundInformation> list) {

        // add marker overlay
        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getInfoType().equals("1")) {
                bd = BitmapDescriptorFactory
                        .fromResource(R.drawable.type_bus);
                text = "公交";
            } else if (list.get(i).getInfoType().equals("2")) {
                bd = BitmapDescriptorFactory
                        .fromResource(R.drawable.type_establishment);
                text = "设施";
            } else if (list.get(i).getInfoType().equals("3")) {
                bd = BitmapDescriptorFactory
                        .fromResource(R.drawable.type_road);
                text = "道路";
            } else if (list.get(i).getInfoType().equals("4")) {
                bd = BitmapDescriptorFactory
                        .fromResource(R.drawable.type_other);
                text = "周边变化";
            }
            double[] ret = ChangePointUtil.realtobaidu(list.get(i).getGp().getLat(), list.get(i).getGp().getLon());
            LatLng ll_Point = new LatLng(ret[0], ret[1]);
            MarkerOptions ooA = new MarkerOptions().position(ll_Point).icon(bd)
                    .zIndex(9).draggable(true);

            String title = text + ":" + list.get(i).getAddress();
            mapManager.setMarker(ooA, title);
        }

        mapManager.getMap().setOnMarkerClickListener(new OnMarkerClickListener() {
            //弹窗
            private InfoWindow mInfoWindow;
            private LayoutParams lp;

            @Override
            public boolean onMarkerClick(Marker marker) {

                tv1 = null;
                tv2 = null;
                LatLng ll = marker.getPosition();
                double lat = ll.latitude;
                double lon = ll.longitude;
                mapManager.setCenter(new GeoPoint(lat, lon));

                String[] title = marker.getTitle().split(":");
                for (int i = 0; i < title.length; i++) {
                    if (i == title.length - 1) {
                        tv1 = new TextView(getActivity());
                        tv1.setText(title[i]);
                        if (title.length == 1) {
                            tv1.setTextSize(20);
                        } else {
                            tv1.setTextSize(15);
                        }

                    } else {
                        tv2 = new TextView(getActivity());
                        tv2.setText(title[i]);
                        tv2.setTextSize(20);
                    }
                }
                LinearLayout linear = new LinearLayout(getActivity()
                );
                LinearLayout.LayoutParams linear_lp = new LinearLayout.LayoutParams(300, 100);
                linear_lp.gravity = Gravity.CENTER;
                linear.setLayoutParams(linear_lp);

                linear.setOrientation(LinearLayout.VERTICAL);
                linear.setBackgroundResource(R.drawable.select_point_pop_bg);
                if (title.length == 1) {
                    lp = new LinearLayout.LayoutParams(800, 240);
                    lp.gravity = Gravity.CENTER;
                } else {
                    lp = new LinearLayout.LayoutParams(800, 120);
                    lp.gravity = Gravity.CENTER;
                }
                if (tv2 != null) {
                    tv2.setLayoutParams(lp);
                    tv2.setTextColor(Color.WHITE);
                    linear.addView(tv2);
                }
                if (tv1 != null) {
                    tv1.setLayoutParams(lp);
                    tv1.setTextColor(Color.WHITE);
                    linear.addView(tv1);
                }
                OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        mapManager.getMap().hideInfoWindow();

                    }
                };
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(linear), ll, -100, listener);
                mapManager.getMap().showInfoWindow(mInfoWindow);
                return true;
            }
        });
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
        //
        mengView.setVisibility(View.VISIBLE);
        popupWindow = new PopupWindow(popview, PointTool.Dp2Px(getActivity(), 250), PointTool.Dp2Px(getActivity(), 250));
        //设置popwindow显示位置
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 200);
        //获取popwindow焦点
        popupWindow.setFocusable(true);
        //		ColorDrawable cd = new ColorDrawable(0x000000);
        //
        //		popupWindow.setBackgroundDrawable(cd);
        //设置popwindow如果点击外面区域，便关闭。
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                mengView.setVisibility(View.GONE);

            }
        });
        popupWindow.update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isneedrefresh = false;
    }

    @Override
    public boolean onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("退出！")
                    .setMessage("您确定要退出吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("取消", null).show();
        }
        return true;
    }

    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public boolean isFragmentAllowedSwitch() {
        return true;
    }

    @Override
    public void onFragmentActive() {
        super.onFragmentActive();
        if(mapManager != null) {
            mapManager.showMarkers();
        }
    }
}
