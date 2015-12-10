package com.informationUpload.contentproviders;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InformationObserver
 * @Date 2015/12/10
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationObserver extends ContentObserver{
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public InformationObserver(Handler handler) {
        super(handler);
    }

    private Runnable mCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkData();
        }
    };

    private void checkData() {
//            Cursor cursor = null;
//            boolean hasNewPrivateMessage = false;
//            boolean hasNewNotification = false;
//            try {
//                cursor = mContextResolver.query(Messages.SampleMessage.CONTENT_URI, SAMPLE_MESSAGE_PROJECTIONS, SAMPLE_MESSAGE_WHERE, SAMPLE_MESSAGE_ARGS, ORDER_BY_LAST_UPDATED_TIME);
//                if (cursor != null) {
//
//                    if (cursor.getCount() > 0) {
//                        while (cursor.moveToNext()) {
//                            int messageType = cursor.getInt(0);
//                            switch (messageType) {
//                                case Messages.Message.MESSAGE_TYPE_PRIVATE:
//                                    hasNewPrivateMessage = true;
//                                    break;
//                            }
//                        }
//                    }
//
//                }
//
//            } catch (Exception e) {
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//            if(NOTIFICATION_ARGS != null) {
//                try {
//                    cursor = mContextResolver.query(Notifications.Notification.CONTENT_URI, NOTIFICATION_PROJECTIONS, NOTIFICATION_WHERE, NOTIFICATION_ARGS, null);
//                    if (cursor != null && cursor.getCount() > 0) {
//                        hasNewNotification = true;
//                    }
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//            }
//            mLastGroupNewMessageData = new GroupSettingsManager.GroupNewMessageData(hasNewPrivateMessage, hasNewNotification);
//            notifyListeners(mLastGroupNewMessageData);
        }
}
