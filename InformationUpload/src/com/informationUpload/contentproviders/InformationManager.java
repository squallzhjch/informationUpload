package com.informationUpload.contentproviders;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.thread.ThreadManager;

import java.util.UUID;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InformationManager
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationManager {
    private volatile static InformationManager mInstance;
    private ThreadManager mThreadManager;
    private Context mContext;
    public static InformationManager getInstance() {

        if (mInstance == null) {
            synchronized (InformationManager.class) {
                if (mInstance == null) {
                    mInstance = new InformationManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context){
        mContext = context;
    }

    private InformationManager(){
        mThreadManager = ThreadManager.getInstance();
    }

    public void saveInformation(final String userId, final InformationMessage message){
        if(TextUtils.isEmpty(userId) || message == null)
            return;
        mThreadManager.getHandler().post(new ThreadManager.OnDatabaseOperationRunnable<Boolean>() {
            @Override
            public Boolean doInBackground() {
                boolean result = true;
                ContentValues values = new ContentValues();
                String rowkey = message.getRowkey();
                if(TextUtils.isEmpty(rowkey)){
                    rowkey = UUID.randomUUID().toString().replaceAll("-", "");
                }
                values.put(Informations.Information.ROWKEY, rowkey);
                values.put(Informations.Information.USER_ID, userId);
                values.put(Informations.Information.STATUS, Informations.Information.STATUS_LOCAL);
                ContentResolver contentResolver = mContext.getContentResolver();
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(Informations.Information.CONTENT_URI, INFORMATION_PROJECTION, WHERE_ROWKEY, new String[]{rowkey}, null);
                    if(cursor != null && cursor.getCount() > 0){
                        contentResolver.update(Informations.Information.CONTENT_URI, values, WHERE_ROWKEY, new String[]{rowkey});
                    }else{
                        contentResolver.insert(Informations.Information.CONTENT_URI, values);
                    }
                    contentResolver.update(Informations.Information.CONTENT_URI, values, null, null);
                }catch (Exception e){
                    result = false;
                }finally {
                    if(cursor != null){
                        cursor.close();
                        cursor = null;
                    }
                }
               if(message.getChatMessageList() != null) {
                for(ChatMessage message1:message.getChatMessageList()) {
                    values.clear();
                    values.put(Informations.VideoData.ROWKEY, rowkey);
                    values.put(Informations.VideoData.CONTENT, message1.getPath());
                    values.put(Informations.VideoData.LATITUDE, message1.getLat());
                    values.put(Informations.VideoData.LONGITUDE, message1.getLon());
                    values.put(Informations.VideoData.TIME, message1.getTime());
                    values.put(Informations.VideoData.TYPE, Informations.VideoData.TYPE_CHAT);
                    contentResolver.insert(Informations.Information.CONTENT_URI, values);
                }
               }
                return result;
            }

            @Override
            public void onSuccess(Boolean value) {

            }
        });

    }

    public void saveInformation(String rowkey, String userId){
        mThreadManager.getHandler().post(new ThreadManager.OnDatabaseOperationRunnable<Boolean>() {
            @Override
            public Boolean doInBackground() {
                return null;
            }

            @Override
            public void onSuccess(Boolean value) {

            }
        });
    }

    private final static String WHERE_ROWKEY = Informations.Information.ROWKEY + " = ? ";
    private final static String[] INFORMATION_PROJECTION = new String[]{
            Informations.Information.ROWKEY
    };
//    private boolean saveInfromation(ContentResolver contentResolver){
//        if()
//    }
}
