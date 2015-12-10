package com.informationUpload.contentproviders;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.Toast;

import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.DataBaseMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.entity.PictureMessage;
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

    private final static String WHERE_ROWKEY = Informations.Information.ROWKEY + " = ? ";
    private final static String[] INFORMATION_PROJECTION = new String[]{
            Informations.Information.ROWKEY
    };
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

    public void init(Context context) {
        mContext = context;
    }

    private InformationManager() {
        mThreadManager = ThreadManager.getInstance();
    }

    public void saveInformation(final String userId, final InformationMessage message) {
        if (TextUtils.isEmpty(userId) || message == null)
            return;
        mThreadManager.getHandler().post(
                new ThreadManager.OnDatabaseOperationRunnable<Boolean>() {
                    @Override
                    public Boolean doInBackground() {
                        boolean result = true;
                        ContentValues values = new ContentValues();
                        String rowkey = message.getRowkey();
                        if (TextUtils.isEmpty(rowkey)) {
                            rowkey = UUID.randomUUID().toString().replaceAll("-", "");
                        }
                        values.put(Informations.Information.ROWKEY, rowkey);
                        values.put(Informations.Information.USER_ID, userId);

                        if(!TextUtils.isEmpty(message.getAddress())) {
                            values.put(Informations.Information.ADDRESS, message.getAddress());
                        }
                        values.put(Informations.Information.LATITUDE, message.getLat());
                        values.put(Informations.Information.LONGITUDE, message.getLon());
                        values.put(Informations.Information.TYPE, message.getType());
                        values.put(Informations.Information.STATUS, Informations.Information.STATUS_LOCAL);
                        if (!TextUtils.isEmpty(message.getRemark())) {
                            values.put(Informations.Information.REMARK, message.getRemark());
                        }
                        ContentResolver contentResolver = mContext.getContentResolver();
                        Cursor cursor = null;
                        try {
                            cursor = contentResolver.query(Informations.Information.CONTENT_URI, INFORMATION_PROJECTION, WHERE_ROWKEY, new String[]{rowkey}, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                contentResolver.update(Informations.Information.CONTENT_URI, values, WHERE_ROWKEY, new String[]{rowkey});
                            } else {
                                contentResolver.insert(Informations.Information.CONTENT_URI, values);
                            }
                            contentResolver.update(Informations.Information.CONTENT_URI, values, null, null);
                        } catch (Exception e) {
                            result = false;
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                                cursor = null;
                            }
                        }

                        if (message.getChatMessageList() != null) {
                            for (ChatMessage message1 : message.getChatMessageList()) {
                                message1.setParentId(rowkey);
                                insertVideoData(contentResolver, values, message1, Informations.VideoData.TYPE_CHAT);
                            }
                        }

                        if (message.getPictureMessageList() != null) {
                            for (PictureMessage message1 : message.getPictureMessageList()) {
                                insertVideoData(contentResolver, values, message1, Informations.VideoData.TYPE_PICTURE);
                            }
                        }
                        return result;
                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        if (value) {
                            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    public boolean saveVideoData(DataBaseMessage message){
        ContentResolver contentResolver = mContext.getContentResolver();
        if(message instanceof ChatMessage){
            return insertVideoData(contentResolver, null, message, Informations.VideoData.TYPE_CHAT);
        }else if(message instanceof PictureMessage){
            return insertVideoData(contentResolver, null, message, Informations.VideoData.TYPE_PICTURE);
        }
        return  false;
    }

    private boolean insertVideoData(ContentResolver contentResolver, ContentValues values, DataBaseMessage message, int type) {
        if (message == null || !TextUtils.isEmpty(message.getRowkey()))
            return false;
        if (values == null)
            values = new ContentValues();
        values.clear();
        values.put(Informations.VideoData.ROWKEY, UUID.randomUUID().toString());
        values.put(Informations.VideoData.PARENT_ID, message.getParentId());
        values.put(Informations.VideoData.CONTENT, message.getPath() + message.getName());
        values.put(Informations.VideoData.LATITUDE, message.getLat());
        values.put(Informations.VideoData.LONGITUDE, message.getLon());
        values.put(Informations.VideoData.TIME, message.getTime());
        values.put(Informations.VideoData.TYPE, type);
        if (!TextUtils.isEmpty(message.getRemark())) {
            values.put(Informations.VideoData.REMARK, message.getRemark());
        }
        try {
            contentResolver.insert(Informations.VideoData.CONTENT_URI, values);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    public void saveInformation(String rowkey, String userId) {
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


}
