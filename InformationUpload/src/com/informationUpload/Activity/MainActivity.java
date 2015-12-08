package com.informationUpload.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import com.informationUpload.VoiceSpeech.VoiceSpeechManager;
import com.informationUpload.fragments.BusReportFragment;
import com.informationUpload.fragments.InformationCollectionFragment;
import com.informationUpload.fragments.MainFragment;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;


import com.informationUpload.map.MapManager;
import com.informationUpload.tool.DemoUtils;
import com.informationUpload.tool.JsonParser;
import com.informationUpload.R;
import com.informationUpload.utils.Utils;
import com.informationUpload.tool.ImageTool;
import com.informationUpload.widget.ImageViewEx;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.Overlay;
import com.tencent.tencentmap.mapsdk.map.Projection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends BaseActivity implements OnClickListener, ActivityInstanceStateListener {

    private MapManager mMapManager;
    private VoiceSpeechManager mVoiceSpeechManager;
    private MyFragmentManager myFragmentManager;
    private volatile boolean mOnSaveInstanceStateInvoked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnSaveInstanceStateInvoked = false;
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main);

            MapView mapView = (MapView) findViewById(R.id.mapView);
            mMapManager = MapManager.getInstance();
            mMapManager.init(this, mapView);
            myFragmentManager = MyFragmentManager.getInstance();
            myFragmentManager.init(getApplicationContext(), getSupportFragmentManager(), this);
            mVoiceSpeechManager = VoiceSpeechManager.getInstance();

            setParam();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            ImageTool.setDisplayMetrics(dm);

            myFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));
//       startActivity(new Intent(MainActivity.this,InformationCollectionActivity.class));
        }
    }

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public void setParam() {
        // 清空参数
//        mIat.setParameter(SpeechConstant.PARAMS, null);
//        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
//        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
//        // 设置听写引擎
//        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//        // 设置返回结果格式
//        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//
//            });
//            editTextKDXF = (EditText) findViewById(R.id.kdxf_text);
//            mVoice = (Button) findViewById(R.id.voice);
//            mVoice.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        mVoiceSpeechManager.start();
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        mVoiceSpeechManager.stop();
//                    }
//                    return true;
//                }
//            });
//            mSubmitBtn = (Button)findViewById(R.id.submit_btn);
//            mSubmitBtn.setOnClickListener(this);
//
//
//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            ImageTool.setDisplayMetrics(dm);
//        }
    }

    @Override
    protected void onDestroy() {
        if (mMapManager != null) {
            mMapManager.onDestroy();
        }
        mVoiceSpeechManager.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mMapManager != null) {
            mMapManager.onPause();
        }
        mVoiceSpeechManager.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMapManager != null) {
            mMapManager.onResume();
        }
        mOnSaveInstanceStateInvoked = false;
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (mMapManager != null) {
            mMapManager.onStop();
        }
        super.onStop();
    }

    private static final int TAKE_PICTURE_CODE = 505;

    private void doTakePicture() {
        // 拍照
        Uri uri = getImageUri();
        if (uri == null) {
            Toast.makeText(this, "资源存放位置分配失败", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(cameraIntent, TAKE_PICTURE_CODE);
    }

    private Uri getImageUri() {
        String imagefilename = "new_pic.jpg";

        if (TextUtils.isEmpty(imagefilename)) {
            return null;
        } else {
            return Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory() + "/FastMap/", imagefilename));
        }
    }

    public static final int RESULT_OK = -1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case TAKE_PICTURE_CODE: {
                    setImageView(getImageUri(), true);
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setImageView(Uri uri, boolean need) {
        if (uri == null) {
            Toast.makeText(this, "资源存放位置分配失败", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageViewEx view = (ImageViewEx) View.inflate(this, R.layout.image_ex_layout, null);
//        ImageViewEx imageViewEx = (ImageViewEx) view.findViewById(R.id.imageViewEx);


        int nDegree = ImageTool.readPictureDegree(uri.getPath());
        view.setImageByFilePath(uri.getPath(), nDegree);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
//        markerOptions.markerView(view);
//        mMapView.getMap().addMarker(markerOptions);
//        String locationInfo = "null";
//        if (mLocation != null) {
//            GeoPoint pLoc = new GeoPoint(
//                    (int) (mLocation.getLatitude() * 3.6E6),
//                    (int) (mLocation.getLongitude() * 3.6E6));
//
//            GeoPoint pE = Encryption.EncryptionGps(pLoc);
//            Geohash eh = new Geohash();
//            int nLat = pE.getLatitudeE6();
//            int nLlon = pE.getLongitudeE6();
//            double dLat = nLat / 3.6E6;
//            double dLon = nLlon / 3.6E6;
//            locationInfo = eh.encode(dLat, dLon);
//
//            path = path.replace("_pnull_n", "_p" + locationInfo + "_n");
//        }
//
//        CommonToolkit.renameFile(uri.getPath(), path);

    }
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        mOnSaveInstanceStateInvoked = true;
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn: {
                myFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(BusReportFragment.class, null));
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!myFragmentManager.back()) {
            finish();
            System.exit(0);
        }
    }
    @Override
    public boolean isInstanceStateSaved() {
        return mOnSaveInstanceStateInvoked;
    }
}

