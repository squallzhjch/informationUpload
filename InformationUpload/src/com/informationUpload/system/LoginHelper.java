package com.informationUpload.system;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.informationUpload.fragments.MainFragment;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;
import com.informationUpload.serviceEngin.EnginCallback;
import com.informationUpload.serviceEngin.ServiceEngin;
import com.informationUpload.tool.StringTool;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import java.util.HashMap;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: LoginManager
 * @Date 2016/1/18
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class LoginHelper {

    public interface OnCheckLoginListener{
        void onAgree();
        void onCancel();
    }

    public interface OnEnginCallbackListener{
        void onSuccess();
        void onFailed();
    }

    public static void loginOut(){
        ConfigManager.getInstance().setLogin(false);
    }

    public static void checkLogin(Context context, final OnCheckLoginListener listener){
        String userId = ConfigManager.getInstance().getUserId();
        String userTel = ConfigManager.getInstance().getUserTel();
        boolean isLogin = ConfigManager.getInstance().isLogin();
        boolean isRegister = false;

        if(TextUtils.isEmpty(userId)){
            isRegister = true;
            userId = StringTool.createUserId();
            ConfigManager.getInstance().setUserId(userId);
        }else if(TextUtils.isEmpty(userTel)){
            isRegister = true;
        }else if(isLogin){
            isRegister = true;
        }
        if(!isRegister){
             new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("您还没有登录，作业成绩将无法记录到您名下，是否马上登录？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (listener != null) {
                                listener.onAgree();
                            }
                        }
                    })
                     .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             if (listener != null) {
                                 listener.onCancel();
                             }
                         }
                     }).show();
        }
    }

    //登录
    public static void Login(final Context context,final String tel,final String password, final OnEnginCallbackListener listener) {
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("tel",tel);
        map.put("pwd",password);
        ServiceEngin.getInstance().Request(context, map, "inforlogin", new EnginCallback(context) {
            @Override
            public void onSuccess(ResponseInfo arg0) {
                super.onSuccess(arg0);
                Log.e("请求成功", arg0.result.toString());
                //进行json解析
                parseLoginJson(context, arg0.result.toString(), tel, password, listener);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                super.onFailure(arg0, arg1);
                Log.e("请求失败", arg1);
                if(listener != null){
                    listener.onFailed();
                }
            }
        });
    }

    //进行json解析
    private static void parseLoginJson(Context context , String json, String tel, String password, OnEnginCallbackListener listener) {
        JSONObject jsonObj = JSON.parseObject(json);

        String errcode = "" +	jsonObj.getInteger("errcode");
        String errmsg =	jsonObj.getString("errmsg");
        if(null != errcode && !"".equals(errcode) &&"0".equals(errcode)){
        	JSONObject obj = jsonObj.getJSONObject("data");
            String userid =	obj.getString("userid");
            String jpgpath= obj.getString("jpg");
            String editiontime = obj.getString("edition");
            ConfigManager.getInstance().setJpgPath(jpgpath);
            ConfigManager.getInstance().setEditionTime(editiontime);
            ConfigManager.getInstance().setUserId(userid);
            ConfigManager.getInstance().setUserTel(tel);
            ConfigManager.getInstance().setLogin(true);
            ConfigManager.getInstance().setUserPassword(password);
            if(listener != null){
                listener.onSuccess();
            }

        }else{
            if(listener != null){
                listener.onFailed();
            }
            Toast.makeText(context, errmsg, Toast.LENGTH_SHORT).show();
        }
    }

    //注册
    public static void register(final Context context, final String telNum,final String password,final OnEnginCallbackListener listener) {
        String userId = ConfigManager.getInstance().getUserId();
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("tel", telNum);
        map.put("pwd", password);
        map.put("uuid", userId);
        ServiceEngin.getInstance().Request(context, map, "inforegist", new EnginCallback(context) {
            @Override
            public void onSuccess(ResponseInfo arg0) {
                super.onSuccess(arg0);
                Log.e("请求成功", arg0.result.toString());
                //进行json解析
                parseRegisterJson(context, arg0.result.toString(), telNum, password,listener);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                super.onFailure(arg0, arg1);
                if(listener != null){
                    listener.onFailed();
                }
                Log.e("请求失败", arg1);
            }
        });

    }

    //进行json解析
    private static void parseRegisterJson(Context context, String json, String telNum, String password, OnEnginCallbackListener listener) {
        JSONObject jsonObj = JSON.parseObject(json);
        String errcode=""+jsonObj.getInteger("errcode");
   
        String errmsg=jsonObj.getString("errmsg");
        if(null != errcode && !"".equals(errcode) &&"0".equals(errcode)){
            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
            ConfigManager.getInstance().setUserTel(telNum);
            ConfigManager.getInstance().setLogin(true);
            ConfigManager.getInstance().setUserPassword(password);
            if(listener != null){
                listener.onSuccess();
            }
        } else{
            if(listener != null){
                listener.onFailed();
            }
            Toast.makeText(context,errmsg, Toast.LENGTH_SHORT).show();
        }

    }
}
