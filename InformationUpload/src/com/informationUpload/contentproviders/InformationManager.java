package com.informationUpload.contentproviders;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.Toast;

import com.informationUpload.entity.BaseMessage;
import com.informationUpload.entity.ChatMessage;
import com.informationUpload.entity.DataBaseMessage;
import com.informationUpload.entity.InformationMessage;
import com.informationUpload.entity.PictureMessage;
import com.informationUpload.thread.ThreadManager;
import com.informationUpload.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

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
    private final static String WHERE_PARENT = Informations.VideoData.PARENT_ID + " = ? ";
    private final static String[] INFORMATION_PROJECTION = new String[]{
            Informations.Information.STATUS, //0
            Informations.Information.TIME, //1
            Informations.Information.LONGITUDE, //2
            Informations.Information.LATITUDE, //3
            Informations.Information.TYPE, //4
            Informations.Information.REMARK, //5
            Informations.Information.ADDRESS, //6
            Informations.VideoData.REMARK,//7
            Informations.VideoData.CONTENT, //8
            Informations.VideoData.LATITUDE, //9
            Informations.VideoData.LONGITUDE, //10
            Informations.VideoData.TYPE, //11
            Informations.VideoData.ROWKEY, //12
            Informations.VideoData.TIME, //13
            Informations.VideoData.PARENT_ID //14

    };
    private final static String[] INFORMATION_PROJECTION_SMAPLE = new String[]{
            Informations.Information.ID, // 0
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
    public void updateInformation(final String rowkey, final ContentValues values) {
        if (TextUtils.isEmpty(rowkey) || values == null)
            return;
        mThreadManager.getHandler().post(
                new ThreadManager.OnDatabaseOperationRunnable<Boolean>() {
                    private boolean result;

					@Override
                    public Boolean doInBackground() {
                         result=false;
                        ContentResolver contentResolver = mContext.getContentResolver();
                        int index ;
                        try {
                            index = contentResolver.update(Informations.Information.CONTENT_URI, values,WHERE_ROWKEY, new String[]{rowkey});
                            if(index>0){
                            	result=true;
                            }
                        } catch (Exception e) {
                          result=false;
                        } 

         
                        return result;
                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        if (value) {
                            Toast.makeText(mContext, "更新成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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

                        if (!TextUtils.isEmpty(message.getAddress())) {
                            values.put(Informations.Information.ADDRESS, message.getAddress());
                        }
                        values.put(Informations.Information.TIME, System.currentTimeMillis());
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
                            cursor = contentResolver.query(Informations.Information.CONTENT_URI, INFORMATION_PROJECTION_SMAPLE, WHERE_ROWKEY, new String[]{rowkey}, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                contentResolver.update(Informations.Information.CONTENT_URI, values, WHERE_ROWKEY, new String[]{rowkey});
                            } else {
                                contentResolver.insert(Informations.Information.CONTENT_URI, values);
                            }
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
                                insertVideoData(contentResolver, values, message1, Informations.VideoData.VIDEO_TYPE_CHAT);
                            }
                        }

                        if (message.getPictureMessageList() != null) {
                            for (PictureMessage message1 : message.getPictureMessageList()) {
                                message1.setParentId(rowkey);
                                insertVideoData(contentResolver, values, message1, Informations.VideoData.VIDEO_TYPE_PICTURE);
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

    public boolean saveVideoData(DataBaseMessage message) {
        ContentResolver contentResolver = mContext.getContentResolver();
        if (message instanceof ChatMessage) {
            return insertVideoData(contentResolver, null, message, Informations.VideoData.VIDEO_TYPE_CHAT);
        } else if (message instanceof PictureMessage) {
            return insertVideoData(contentResolver, null, message, Informations.VideoData.VIDEO_TYPE_PICTURE);
        }
        return false;
    }

    private boolean insertVideoData(ContentResolver contentResolver, ContentValues values, DataBaseMessage message, int type) {
        if (message == null || !TextUtils.isEmpty(message.getRowkey()))
            return false;
        if (values == null)
            values = new ContentValues();
        values.clear();
        values.put(Informations.VideoData.ROWKEY, UUID.randomUUID().toString().replaceAll("-", ""));
        values.put(Informations.VideoData.PARENT_ID, message.getParentId());
        values.put(Informations.VideoData.CONTENT, message.getPath());
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
        } catch (Exception e) {
            return false;
        }

    }

    public void deleteInformation(final String rowkey){
        if(TextUtils.isEmpty(rowkey))
            return;
        mThreadManager.getHandler().post(
                new ThreadManager.OnDatabaseOperationRunnable<Boolean>() {

                    @Override
                    public Boolean doInBackground() {
                        InformationMessage message = getInformation(rowkey);
                        if(message.getPictureMessageList() != null && message.getPictureMessageList().size() > 0){
                            for(PictureMessage message1: message.getPictureMessageList()){
                                try {
                                    FileUtils.delFile(message1.getPath());
                                }catch (Exception e){

                                }

                            }
                        }

                        ContentResolver contentResolver = mContext.getContentResolver();
                        contentResolver.delete(Informations.Information.CONTENT_URI, WHERE_ROWKEY, new String[]{rowkey});
                        contentResolver.delete(Informations.VideoData.CONTENT_URI, WHERE_PARENT, new String[]{rowkey});
                        return true;
                    }

                    @Override
                    public void onSuccess(Boolean value) {

                    }
                });

    }

    public void deleteVideo(final String rowkey, final String path){
        if(TextUtils.isEmpty(rowkey))
            return;
        mThreadManager.getHandler().post(
                new ThreadManager.OnDatabaseOperationRunnable<Boolean>() {

                    @Override
                    public Boolean doInBackground() {
                        ContentResolver contentResolver = mContext.getContentResolver();
                        contentResolver.delete(Informations.VideoData.CONTENT_URI, WHERE_ROWKEY, new String[]{rowkey});
                        try {
                            FileUtils.delFile(path);
                        }catch (Exception e){

                        }
                        return true;
                    }

                    @Override
                    public void onSuccess(Boolean value) {

                    }
                });

    }



    public InformationMessage getInformation(String rowkey) {
        InformationMessage informationMessage = null;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Informations.Information.CONTENT_URI_WITH_VIDEO, INFORMATION_PROJECTION, WHERE_ROWKEY, new String[]{rowkey}, null);
            if (cursor != null && cursor.getCount() > 0) {

                informationMessage = new InformationMessage();
                List<ChatMessage> chatList = new ArrayList<ChatMessage>();
                List<PictureMessage> picList = new ArrayList<PictureMessage>();
                informationMessage.setPictureMessageList(picList);
                informationMessage.setChatMessageList(chatList);
                while (cursor.moveToNext()) {

                    informationMessage.setRowkey(rowkey);
                    informationMessage.setStatus(cursor.getInt(0));
                    informationMessage.setTime(cursor.getLong(1));
                    informationMessage.setLon(cursor.getDouble(2));
                    informationMessage.setLat(cursor.getDouble(3));
                    informationMessage.setType(cursor.getInt(4));
                    String remark = cursor.getString(5);
                    if (!TextUtils.isEmpty(remark)) {
                        informationMessage.setRemark(remark);
                    }
                    String address = cursor.getString(6);
                    if (!TextUtils.isEmpty(address)) {
                        informationMessage.setAddress(address);
                    }
                    String childRowkey = cursor.getString(12);
                    if(TextUtils.isEmpty(childRowkey)){
                        continue;
                    }
                    int type = cursor.getInt(11);
                    DataBaseMessage message = null;
                    if (type == Informations.VideoData.VIDEO_TYPE_CHAT) {
                        message = new ChatMessage();
                        chatList.add((ChatMessage) message);
                    } else if (type == Informations.VideoData.VIDEO_TYPE_PICTURE) {
                        message = new PictureMessage();
                        picList.add((PictureMessage) message);
                    }
                    remark = cursor.getString(7);
                    if (!TextUtils.isEmpty(remark)){
                        message.setRemark(remark);
                     }
                    String path = cursor.getString(8);
                    if(!TextUtils.isEmpty(path)) {
                        message.setPath(path);
                    }
                    message.setLat(cursor.getDouble(9));
                    message.setLon(cursor.getDouble(10));
                    message.setRowkey(cursor.getString(12));
                    message.setTime(cursor.getLong(13));
                    message.setParentId(cursor.getString(14));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return informationMessage;
    }


}
