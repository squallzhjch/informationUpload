package com.informationUpload.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.informationUpload.R;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.LocationManager;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.SelectPointView;
import com.informationUpload.widget.TitleView;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SelectPointFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SelectPointFragment extends BaseFragment implements BaiduMap.OnMapStatusChangeListener {


    private SelectPointView mSelectView;
    private String mAddress;
    private String mAdminCode;
    private GeoPoint mPoint;
    private MapManager mMapManager;
    private ImageView pointIcon;
    /**
     * 选点背景图片
     */
    private BitmapDescriptor bd;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMapManager = MapManager.getInstance();
        mLocationManager = LocationManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_point, null);
        
        TitleView title = (TitleView) view.findViewById(R.id.title_view);
        title.setOnLeftAreaClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.back();
            }
        });

        mMapManager.setOnMapStatusChangeListener(this);
        mSelectView = (SelectPointView) view.findViewById(R.id.point_view);
        mSelectView.setOnSubmitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentsManager.notifyContentUpdateSuccess(SystemConfig.FRAGMENT_UPDATE_SELECT_POINT_ADDRESS, mAddress, mAdminCode, mPoint);
                mFragmentManager.back();
            }
        });

        pointIcon = (ImageView) view.findViewById(R.id.point_icon);

        mPoint = new GeoPoint();
        mPoint.setLat(mLocationManager.getCurrentPoint().getLat());
        mPoint.setLon(mLocationManager.getCurrentPoint().getLon());

        mMapManager.searchAddress(mPoint, new OnSearchAddressListener() {

            @Override
            public void OnSuccess(String address, String adminCode) {

                mAdminCode = adminCode;
                mAddress = address;
                mSelectView.setAddressText(address);
            }

            @Override
            public void onFailure() {
                mSelectView.setAddressText("");
            }
        });
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mMapManager.removeOnMapTouchListener(this);
    }

////    @Override
//    public void onMapClick(LatLng latLng) {
//        mMapManager.getMap().clear();
//        bd = BitmapDescriptorFactory
//                .fromResource(R.drawable.type_bus);
//
//        LatLng ll_Point = new LatLng(latLng.latitude, latLng.longitude);
//        MarkerOptions ooA = new MarkerOptions().position(ll_Point).icon(bd)
//                .zIndex(9).draggable(true);
//        Marker mMarker = (Marker) (mMapManager.getMap().addOverlay(ooA));
//        mPoint = new GeoPoint();
//
//        mPoint.setLat(latLng.latitude);
//        mPoint.setLon(latLng.longitude);
//        mMapManager.searchAddress(mPoint, new OnSearchAddressListener() {
//            @Override
//            public void OnSuccess(String address, String adminCode) {
//                mMapManager.setCenter(mPoint);
//                mAdminCode = adminCode;
//                mAddress = address;
//                mSelectView.setAddressText(address);
//            }
//
//            @Override
//            public void onFailure() {
//                mSelectView.setAddressText("");
//            }
//        });
//    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapManager.getMap().clear();
        if (bd != null) {
            bd.recycle();
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        pointIcon.setBackgroundResource(R.drawable.drag_point);
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        pointIcon.setBackgroundResource(R.drawable.default_point);
        GeoPoint point = new GeoPoint(mapStatus.target.latitude, mapStatus.target.longitude);
        mMapManager.searchAddress(point, new OnSearchAddressListener() {
            @Override
            public void OnSuccess(String address, String adminCode) {
                mAdminCode = adminCode;
                mAddress = address;
                mSelectView.setAddressText(address);
            }

            @Override
            public void onFailure() {
                mSelectView.setAddressText("");
            }
        });
    }
}
