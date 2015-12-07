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
import com.informationUpload.fragments.InformationCollectionFragment;
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

public class MainActivity extends FragmentActivity  {

    private Button mVoice;
    private Button mPhoto;
    private SpeechRecognizer mIat;
    private EditText editTextKDXF;

    private MapManager mMapManager;

    // 用于记录定位参数, 以显示到 UI
    private String mRequestParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mMapManager = MapManager.getInstance();
        mMapManager.init(this, mapView);


        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=565eb095");
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(this, null);


        mPhoto = (Button)findViewById(R.id.photo);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakePicture();
            }
        });
        editTextKDXF = (EditText) findViewById(R.id.kdxf_text);
        mVoice = (Button) findViewById(R.id.voice);
        mVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mIat.cancel();
                    mIatResults.clear();
                    mIat.startListening(mRecoListener);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIat.stopListening();
                }
                return true;
            }
        });
        setParam();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ImageTool.setDisplayMetrics(dm);
        
        
       startActivity(new Intent(MainActivity.this,InformationCollectionActivity.class));
        
    }

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = "mandarin";
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "10000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/FastMap/iat.wav");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, "0");

        mIat.setParameter(SpeechConstant.ASR_WBEST, "5");
    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener(){
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("Result:", results.getResultString());
            if(results != null) {
                String text = JsonParser.parseIatResult(results.getResultString());

                String sn = null;
                // 读取json结果中的sn字段
                try {
                    JSONObject resultJson = new JSONObject(results.getResultString());
                    sn = resultJson.optString("sn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mIatResults.put(sn, text);
                StringBuffer resultBuffer = new StringBuffer();
                for (String key : mIatResults.keySet()) {
                    resultBuffer.append(mIatResults.get(key));
                }
                editTextKDXF.setText(resultBuffer.toString() + "\n" + results.getResultString());
            }
        }
        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true); //获取错误码描述
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }
        //开始录音
        public void onBeginOfSpeech() {}
        //音量值0~30
        public void onVolumeChanged(int volume){}
        //结束录音
        public void onEndOfSpeech() {}
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
    @Override
    protected void onDestroy() {
        if(mMapManager != null){
            mMapManager.onDestroy();
        }
        mIat.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(mMapManager != null) {
            mMapManager.onPause();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
//        mMapView.onResume();
//        startLocation();
        super.onResume();
    }

    @Override
    protected void onStop() {
//        mMapView.onStop();
        super.onStop();
    }

//    @Override
//    public void onLocationChanged(TencentLocation location, int error, String s) {
//        if (error == TencentLocation.ERROR_OK) {
//            mLocation = location;
//
//            // 定位成功
//            StringBuilder sb = new StringBuilder();
//            sb.append("定位参数=").append(mRequestParams).append("\n");
//            sb.append("(纬度=").append(location.getLatitude()).append(",经度=")
//                    .append(location.getLongitude()).append(",精度=")
//                    .append(location.getAccuracy()).append("), 来源=")
//                    .append(location.getProvider()).append(", 地址=")
//                    .append(location.getAddress());
//
////            // 更新 status
////            mStatus.setText(sb.toString());
//
//            // 更新 location 图层
//            mLocationOverlay.setAccuracy(mLocation.getAccuracy());
//            mLocationOverlay.setGeoCoords(Utils.of(mLocation));
//            mMapView.invalidate();
//        }
//    }


//    private void startLocation() {
//        TencentLocationRequest request = TencentLocationRequest.create();
//        request.setInterval(5000);
//        mLocationManager.requestLocationUpdates(request, this);
//
//        mRequestParams = request.toString() + ", 坐标系="
//                + DemoUtils.toString(mLocationManager.getCoordinateType());
//    }




    private static final int TAKE_PICTURE_CODE = 505;
    private void doTakePicture() {
        // 拍照
        Uri uri = getImageUri();
        if(uri == null){
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
    public static final int RESULT_OK       = -1;
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
        if(uri == null){
            Toast.makeText(this, "资源存放位置分配失败", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageViewEx view = (ImageViewEx)View.inflate(this, R.layout.image_ex_layout, null);
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
}

