package com.informationUpload.utils;

import java.io.File;
import java.io.IOException;

import com.informationUpload.R;
import com.informationUpload.VoiceSpeech.VoiceSpeechManager;
import com.informationUpload.VoiceSpeech.VoiceSpeechManager.OnParseListener;
import com.informationUpload.entity.ChatMessage;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;


/**
 * @author zcs
 * @version V1.0
 * @ClassName: PoiRecordPopWindow.java
 * @Date 2015年10月31日 上午11:01:20
 * @Description: 弹出录音
 */
public class PoiRecordPopup {
    protected static final String TAG = "PoiRecordPopup";
    private Context context;
    private View touchView;// 控件录音的控件
    private View showView;
    View voice_rcd_hint_rcding;
    ImageView volume;
    private String mDirPath;//声音文件的路径
    private boolean isLoading = false;
    private boolean bo;

    private String mVoicePath;
    /**
     * 科达讯飞声音管理类
     */
    private VoiceSpeechManager voiceManager;
    /**
     * 科大讯飞解析后的字符串
     */
    private String parsestring = "";


    private static volatile PoiRecordPopup mInstance;

    public static PoiRecordPopup getInstance() {
        if (mInstance == null) {
            synchronized (PoiRecordPopup.class) {
                if (mInstance == null) {
                    mInstance = new PoiRecordPopup();
                }
            }
        }
        return mInstance;
    }

    private PoiRecordPopup() {

    }

    public boolean init(Context context, String path) {
        this.context = context;
        pop = new PopupWindow();
        pop.setWidth(LayoutParams.WRAP_CONTENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        View view = View.inflate(context, R.layout.fm_card_voice_rcd_hint_window, null);
        pop.setContentView(view);
        pop.update();

//        voice_rcd_hint_rcding = view.findViewById(R.id.voice_rcd_hint_rcding);
        volume = (ImageView) view.findViewById(R.id.volume);

        voiceManager = VoiceSpeechManager.getInstance();
        voiceManager.setParseListener(mParseListener);
        return setPath(path);
    }

    /**
     * 设置录音保存路径
     *
     * @param path
     */
    private boolean setPath(String path) {

        if (!new File(path).exists()) {
            try {
                new File(path).mkdirs();

            } catch (Exception e) {
                return false;
            }
        }
        this.mDirPath = path;

        return true;
    }

    /**
     * 可以设置要弹出的view
     *
     * @param showView
     */
    public void setShowView(View showView) {
        this.showView = showView;
    }

    /**
     * 重新设置触摸的view
     *
     * @param view
     */
    public void setViewTouch(View view) {
        this.touchView = view;
        view.setOnTouchListener(touchListener);
    }

    private OnParseListener mParseListener = new OnParseListener() {
        @Override
        public void onParseString(String parsestr) {
            parsestring = parsestr;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            volume.setBackgroundResource(R.drawable.amp1);
            mListener.onParseResult(mVoicePath, parsestr);
            isLoading = false;

//				voiceManager.stop();
            if (mListener != null) {
                Handler handler = new Handler();
                handler.post(new Runnable() {
//						private MediaPlayer md;
//						private int timeLong;

                    @Override
                    public void run() {
                        try {
//								md=new MediaPlayer();
//
//								md.prepare();
//								timeLong=md.getDuration();
//								TimeLeng_mediaplayer(timeLong,voiceName);
//								md.release();

                            //listener.stopListener(amp,path, voiceName, endVoiceT-startVoiceT);
//								listener.stopListener(parsestring,path, voiceName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onEndOfSpeech() {
            mListener.onStopSpeech(mVoicePath);
        }

        @Override
        public void onError() {

        }
    };

    /**
     * 触摸事件
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(context, "没有SD卡", Toast.LENGTH_SHORT).show();
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                    volume.setBackgroundResource(R.anim.pop_voice_img);
                    AnimationDrawable animation = (AnimationDrawable) volume.getBackground();
                    isLoading = true;
                    animation.start();
                    mVoicePath = mDirPath + System.currentTimeMillis() + ".wav";
                    voiceManager.setPath(mVoicePath);
                    voiceManager.start();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                default:
                    pop.dismiss();
                    voiceManager.stop();
                    break;
            }

            return true;
        }
    };

    /**
     * 判断时长
     *
     * @param startVoiceT 开始时间
     * @param endVoiceT   结束时间
     */
    private void TimeLeng(long startVoiceT, long endVoiceT, String name) {
//        System.out.println("已录音时长" + (endVoiceT - startVoiceT));
//        if (!isLoading && endVoiceT - startVoiceT < MinLeng && MinLeng != -1) {//时间过短删除
//            Toast.makeText(context, "时间过短", Toast.LENGTH_SHORT).show();
//            if (new File(path_name).isFile()) {//删除
//                Log.w(TAG, "时间过短删除  最小时长" + MinLeng + "   录音时长" + (endVoiceT - startVoiceT));
//                new File(path_name).delete();
//            }
//        } else if (endVoiceT - startVoiceT > MaxLeng && MaxLeng != -1) {
//            //超长
//            Toast.makeText(context, "录音超时长", Toast.LENGTH_SHORT).show();
//            Log.w(TAG, "时间过长停止" + "最大时长" + MaxLeng);
//            pop.dismiss();
////			double amp = mSensor.getAmplitude();
//            if (listener != null) {
//                listener.stopListener(parsestring, path, name, endVoiceT - startVoiceT);
//            }
//            if (new File(path_name).isFile()) {//删除
//                Log.w(TAG, "时间过长  最大时长" + MaxLeng + "   录音时长" + (endVoiceT - startVoiceT));
//                new File(path_name).delete();
//            }
//            isLoading = false;
////			mSensor.stop();
//            voiceManager.stop();
//            dismiss();
//        }
    }

    /**
     * 判断时长
     *
     * @param longtime 文件时间长度
     * @param name     音频名字
     */
    private void TimeLeng_mediaplayer(long longtime, String name) {
//        System.out.println("已录音时长" + longtime);
//        if (!isLoading && longtime < MinLeng && MinLeng != -1) {//时间过短删除
//            Toast.makeText(context, "时间过短", Toast.LENGTH_SHORT).show();
//            if (new File(path_name).isFile()) {//删除
//                Log.w(TAG, "时间过短删除  最小时长" + MinLeng + "   录音时长" + (longtime));
//                new File(path_name).delete();
//            }
//        } else if (longtime > MaxLeng && MaxLeng != -1) {
//            //超长
//            Toast.makeText(context, "录音超时长", Toast.LENGTH_SHORT).show();
//            Log.w(TAG, "时间过长停止" + "最大时长" + MaxLeng);
//            pop.dismiss();
//            if (listener != null) {
//                listener.stopListener(parsestring, path, name, longtime);
//            }
//            if (new File(path_name).isFile()) {//删除
//                Log.w(TAG, "时间过长  最大时长" + MaxLeng + "   录音时长" + longtime);
//                new File(path_name).delete();
//            }
//            isLoading = false;
//            voiceManager.stop();
//            dismiss();
//        }
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        if (pop != null) {
            pop.dismiss();
        }
    }

    private PopupWindow pop;
    private OnRecorListener mListener;
    /**
     * 最小时间
     */
    private long MinLeng = -1;
    /**
     * 最长时间
     */
    private long MaxLeng = -1;
    private ChatMessage mChatmessage;
    public View.OnTouchListener getOnTouch() {
        return touchListener;
    }

    /**
     * 添加录音监听
     *
     * @param listener
     */
    public void setRecordListener( OnRecorListener listener) {
        this.mListener = listener;
    }


    public interface OnRecorListener {
        /**
         * 录音停止
         *
         * @param path 路径
         */
        public void onStopSpeech(String path);
        public void onParseResult(String path, String result);
    }

    /**
     * 最小时长
     *
     * @return 毫秒
     */
    public long getMinLeng() {
        return MinLeng;
    }

    /**
     * 最小时长
     *
     * @param minLeng 毫秒
     */
    public void setMinLeng(long minLeng) {
        MinLeng = minLeng;
    }

    /**
     * 最大时长
     *
     * @return 毫秒
     */

    public long getMaxLeng() {
        return MaxLeng;
    }

    /**
     * 最大时长
     *
     * @param maxLeng 毫秒
     */
    public void setMaxLeng(long maxLeng) {
        MaxLeng = maxLeng;
    }

    public void onDestroy() {
        voiceManager.onDestroy();
    }

    public void onPause() {
        voiceManager.onPause();
    }
}
