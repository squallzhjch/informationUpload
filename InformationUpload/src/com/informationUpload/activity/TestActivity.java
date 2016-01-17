package com.informationUpload.activity;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.informationUpload.R;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: TestActivity
 * @Date 2015/12/14
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class TestActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_test);
            MapView mapView = (MapView) findViewById(R.id.baidu_map_view);
        }
    }
}
