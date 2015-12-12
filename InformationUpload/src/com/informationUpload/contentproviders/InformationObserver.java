package com.informationUpload.contentproviders;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import com.informationUpload.activity.MyApplication;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final Handler mBackgroundHandler;
    private final MyApplication mContext;
    private ContentResolver mContextResolver;
    private volatile String mUserId;
    private volatile long mLastCheckedTime = 0;
    private static final long DEFAULT_MAX_INTERVAL = 120;
    private InformationCheckData mCheckData;
    private final List<WeakReference<InformationManager.OnCheckMessageCountListener>> mOnCheckMessageCountListeners = new ArrayList<WeakReference<InformationManager.OnCheckMessageCountListener>>();



    private static final String[] SAMPLE_MESSAGE_PROJECTIONS = new String[] {
            Informations.Information.ROWKEY // Mapped to 0
    };

    private static final String SAMPLE_MESSAGE_WHERE = Informations.Information.STATUS + " = ? AND "
            + Informations.Information.USER_ID + " = ? ";


    public InformationObserver(MyApplication application, Handler handler) {
        super(handler);
        if(handler != null){
            mBackgroundHandler = handler;
        }else{
            mBackgroundHandler = mUIHandler;
        }
        mContext = application;
        mContextResolver = mContext.getContentResolver();
        checkData();
    }

    private Runnable mCheckRunnable = new Runnable() {
        @Override
        public void run() {
            checkData();
        }
    };

    @Override
    public void onChange(boolean selfChange) {
        long timestamp = System.currentTimeMillis();
        long interval;
        if ((interval = timestamp - mLastCheckedTime) > DEFAULT_MAX_INTERVAL) {
            mBackgroundHandler.removeCallbacks(mCheckRunnable);
            checkData();
        }
        else {
            // remove all existing posted runnable first.
            mBackgroundHandler.removeCallbacks(mCheckRunnable);
            mBackgroundHandler.postDelayed(mCheckRunnable, DEFAULT_MAX_INTERVAL - interval);
        }
    }

    private void checkData() {
        mLastCheckedTime = System.currentTimeMillis();
        Cursor cursor = null;
        int localMessage = 0;
        int serviceMessage = 0;
        try {
            cursor = mContextResolver.query(Informations.Information.CONTENT_URI, SAMPLE_MESSAGE_PROJECTIONS, SAMPLE_MESSAGE_WHERE, new String[]{String.valueOf(Informations.Information.STATUS_LOCAL), mContext.getUserId()}, null);
            if (cursor != null) {
                localMessage = cursor.getCount();
            }

        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        try {
            cursor = mContextResolver.query(Informations.Information.CONTENT_URI, SAMPLE_MESSAGE_PROJECTIONS, SAMPLE_MESSAGE_WHERE, new String[]{String.valueOf(Informations.Information.STATUS_SERVER), mContext.getUserId()}, null);
            if (cursor != null) {
                serviceMessage = cursor.getCount();
            }

        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        mCheckData = new InformationCheckData(localMessage, serviceMessage);
        notifyListeners(mCheckData);
    }

    private void notifyListeners(final InformationCheckData data) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mOnCheckMessageCountListeners) {
                    for (WeakReference<InformationManager.OnCheckMessageCountListener> weakRef : mOnCheckMessageCountListeners) {
                        if (weakRef != null
                                && weakRef.get() != null) {
                            weakRef.get().onCheckNewMessageSucceed(data, false);
                        }
                    }
                }
            }
        });
    }

   public InformationCheckData addOnCheckMessageListener(InformationManager.OnCheckMessageCountListener listener) {
        synchronized (mOnCheckMessageCountListeners) {
            for (WeakReference<InformationManager.OnCheckMessageCountListener> weakRef : mOnCheckMessageCountListeners) {
                if (weakRef != null
                        && weakRef.get() == listener) {
                    return mCheckData;
                }
            }
            mOnCheckMessageCountListeners.add(new WeakReference<InformationManager.OnCheckMessageCountListener>(listener));
        }

        return mCheckData;
    }
}
