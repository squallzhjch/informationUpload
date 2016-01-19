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

    public static void loginOut(){
        ConfigManager.getInstance().setLogin(false);
    }

    public static void checkLogin(Context context, final OnCheckLoginListener listener){
        String userId = ConfigManager.getInstance().getUserId();
        String userTel = ConfigManager.getInstance().getUserTel();
        boolean isLogin = ConfigManager.getInstance().isLogin();
        boolean isRegister = false;

        if(TextUtils.isEmpty(userId)){
            isRegister = false;
            userId = StringTool.createUserId();
            ConfigManager.getInstance().setUserId(userId);
        }else if(TextUtils.isEmpty(userTel)){
            isRegister = false;
        }else if(isLogin){
            isRegister = true;
        }
        isRegister = false;
        if(!isRegister){
             new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("您还没有绑定手机号或登录，作业战绩将不能记录在您名下，是否去\"绑定or登录\"")
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
    public static void Login(final Context context,final String tel,String password) {
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("tel",tel);
        map.put("pwd",password);
        ServiceEngin.Request(context, map, "inforlogin", new EnginCallback(context) {
            @Override
            public void onSuccess(ResponseInfo arg0) {
                super.onSuccess(arg0);
                Log.e("请求成功", arg0.result.toString());
                //进行json解析
                parseLoginJson(context, arg0.result.toString(), tel);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                super.onFailure(arg0, arg1);
                Log.e("请求失败", arg1);
            }
        });
    }

    //进行json解析
    private static void parseLoginJson(Context context , String json, String tel) {
        JSONObject jsonObj = JSON.parseObject(json);

        String errcode = "" +	jsonObj.getInteger("errcode");
        String errmsg =	jsonObj.getString("errmsg");
        if(null != errcode && !"".equals(errcode) &&"0".equals(errcode)){
            String userid =	jsonObj.getString("data");
            ConfigManager.getInstance().setUserId(userid);
            ConfigManager.getInstance().setUserTel(tel);
            ConfigManager.getInstance().setLogin(true);
            Bundle bundle = new Bundle();
            MyFragmentManager.getInstance().switchFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));
//            MainActivity mainActivity = new MainActivity();
//            if(mainActivity.mMainActivity != null) {
//                mainActivity.mMainActivity.finish();
//            }
//            LoginActivity.this.startActivity(new Intent(LoginActivity.this,MainActivity.class));
//            LoginActivity.this.finish();
        }else{
            Toast.makeText(context, errmsg, Toast.LENGTH_SHORT).show();
        }
    }

    //注册
    public static void register(final Context context, final String telNum,final String telpassword) {
        String userId = ConfigManager.getInstance().getUserId();
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("tel", telNum);
        map.put("pwd", telpassword);
        map.put("uuid", userId);
        ServiceEngin.Request(context, map, "inforegist", new EnginCallback(context) {
            @Override
            public void onSuccess(ResponseInfo arg0) {
                super.onSuccess(arg0);
                Log.e("请求成功", arg0.result.toString());
                //进行json解析
                parseRegisterJson(context, arg0.result.toString(), telNum);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                super.onFailure(arg0, arg1);
                Log.e("请求失败", arg1);
            }
        });

    }

    //进行json解析
    private static void parseRegisterJson(Context context, String json, String telNum) {
        JSONObject jsonObj = JSON.parseObject(json);
        String errcode=""+jsonObj.getInteger("errcode");
        Log.i("chentao", "errcode:" + errcode);
        String errmsg=jsonObj.getString("errmsg");
        if(null != errcode && !"".equals(errcode) &&"0".equals(errcode)){
            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
            ConfigManager.getInstance().setUserTel(telNum);
            ConfigManager.getInstance().setLogin(true);
            MyFragmentManager.getInstance().switchFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));
//            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//            RegisterActivity.this.finish();

        }else{
            Toast.makeText(context,errmsg, Toast.LENGTH_SHORT).show();
        }

    }
}
