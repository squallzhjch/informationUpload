package com.informationUpload.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.informationUpload.R;
import com.informationUpload.map.GeoPoint;
import com.informationUpload.map.MapManager;
import com.informationUpload.map.MapManager.OnSearchAddressListener;
import com.informationUpload.utils.SystemConfig;
import com.informationUpload.widget.SelectPointView;
import com.informationUpload.widget.TitleView;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapClickListener;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SelectPointFragment
 * @Date 2015/12/8
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SelectPointFragment extends BaseFragment implements OnMapClickListener{

    private MapManager mMapManager;
    private SelectPointView mSelectView;
    private String mAddress;
	private GeoPoint point;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMapManager = MapManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view =inflater.inflate(R.layout.fragment_select_point,null);


        TitleView title = (TitleView)view.findViewById(R.id.title_view);
        title.setOnLeftAreaClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.back();
            }
        });

        mMapManager.setOnMapClickListener(this);
        mSelectView = (SelectPointView)view.findViewById(R.id.point_view);
        mSelectView.setOnSubmitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentsManager.notifyContentUpdateSuccess(SystemConfig.FRAGMENT_UPDATE_SELECT_POINT_ADDRESS, mAddress,point);
                mFragmentManager.back();
            }
        });
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mMapManager.removeOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
         point = new GeoPoint();
         
         point.setLat(latLng.getLatitude());
         point.setLon(latLng.getLongitude());
         mMapManager.searchAddress(point,new OnSearchAddressListener() {
            @Override
            public void OnSuccess(String address) {
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
