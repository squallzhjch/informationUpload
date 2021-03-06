package com.informationUpload.system;



import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: ConfigManager
 * @Date 2016/1/18
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class ConfigManager {

    private Context mContext;
    private SharedPreferences mSharePre;


    private static volatile ConfigManager mInstance;

    public static ConfigManager getInstance() {
        if (mInstance == null) {
            synchronized (ConfigManager.class) {
                if (mInstance == null) {
                    mInstance = new ConfigManager();
                }
            }
        }
        return mInstance;
    }
    public void init(Context context){
        mContext = context;
        mSharePre = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
    }

    public String getUserId(){
        return mSharePre.getString("user_id", null);
    }

    public void setUserPassword(String password){
        if(TextUtils.isEmpty(password)){
            return;
        }
        mSharePre.edit().putString("user_password", password).commit();
    }

    public String getUserPassword(){
        return mSharePre.getString("user_password", null);
    }

    public void setUserId(String userId){
        if(TextUtils.isEmpty(userId)){
            return;
        }
        mSharePre.edit().putString("user_id", userId).commit();
    }

    public String getUserName(){
        return mSharePre.getString("user_name", null);
    }
    public void setUserName(String userName){
    	 mSharePre.edit().putString("user_name", userName).commit();
    }

    public String getUserTel(){
        return mSharePre.getString("user_tel", "");
    }

    public void setUserTel(String text){
         mSharePre.edit().putString("user_tel", text).commit();
    }

    public boolean isLogin(){
        return mSharePre.getBoolean("is_login", false);
    }

    public void setLogin(boolean b){
        mSharePre.edit().putBoolean("is_login", b).commit();
    }
    
    
    public void setEditionTime(String editiontime){
    	mSharePre.edit().putString("edition_time",editiontime).commit();
    }
    
    public String getEditionTime(){
    	return mSharePre.getString("edition_time","");
    }
    public void setJpgPath(String jpgpath){
    	mSharePre.edit().putString("jpg_path",jpgpath).commit();
    }
    public String getJpgPath(){
       return mSharePre.getString("jpg_path","");
    }
    public void setSaveHeadName(String name){
    	mSharePre.edit().putString("save_head_name", name);
    }
    public String getSaveHeadName(){
    	return mSharePre.getString("save_head_name","");
    }
}
