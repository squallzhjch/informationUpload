package com.informationUpload.voiceSpeech;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.informationUpload.tool.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: VoiceManager
 * @Date 2015/12/4
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class VoiceSpeechManager {

    private Context mContext;
    private SpeechRecognizer mIat;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private OnParseListener mListener;

    private static volatile VoiceSpeechManager mInstance;
    public static VoiceSpeechManager getInstance() {
        if (mInstance == null) {
            synchronized (VoiceSpeechManager.class) {
                if (mInstance == null) {
                    mInstance = new VoiceSpeechManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext = context;
      
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=565eb095");
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(context, null);

        setParam(mIat);
    }

    public void setParam(SpeechRecognizer iat) {
        // 清空参数
        iat.setParameter(SpeechConstant.PARAMS, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        iat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置听写引擎
        iat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_MIX);
        // 设置返回结果格式
        iat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String lag = "mandarin";
        if (lag.equals("en_us")) {
            // 设置语言
            iat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            iat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            iat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        iat.setParameter(SpeechConstant.VAD_BOS, "10000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        iat.setParameter(SpeechConstant.VAD_EOS, "10000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        iat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        iat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
      
        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        iat.setParameter(SpeechConstant.ASR_DWA, "0");

        iat.setParameter(SpeechConstant.ASR_WBEST, "5");
    }
    //设置科达讯飞的音效名字
    public void setPath(String path){
    	mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, path);
    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener(){
        //听写结果回调接口(返回Json格式结果，用户可参见附录12.1)；
        //一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
        //关于解析Json的代码可参见MscDemo中JsonParser类；
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            
            StringBuffer resultBuffer = new StringBuffer();
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

                for (String key : mIatResults.keySet()) {
                    resultBuffer.append(mIatResults.get(key));
                }

            }
            if(isLast && mListener!=null){
                mListener.onParseString(resultBuffer.toString());
            }
        }
        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true); //获取错误码描述
            if(mListener!=null){
                mListener.onError();
            }
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }
        //开始录音
        public void onBeginOfSpeech() {}
        //音量值0~30
        public void onVolumeChanged(int volume){}
        //结束录音
        public void onEndOfSpeech() {
            if(mListener != null){
                mListener.onEndOfSpeech();
            }
        }
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
   
    public void start(){
        mIat.cancel();
        mIatResults.clear();
        mIat.startListening(mRecoListener);
    }

    public void onDestroy() {
        mIat.cancel();
        mIat.destroy();
    }

    public void onPause() {
        mIat.cancel();
    }

    public void stop() {
        mIat.stopListening();
    }
    public interface OnParseListener{
    	void onParseString(String parsestr);
        void onEndOfSpeech();
        void onError();
    }
    	
    public void setParseListener(OnParseListener listener){
    	this.mListener = listener;
    }

}
