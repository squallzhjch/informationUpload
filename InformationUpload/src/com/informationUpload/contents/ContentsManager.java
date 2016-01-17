package com.informationUpload.contents;


import android.text.TextUtils;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lolo on 15/2/26.
 */
public class ContentsManager {

    private static volatile ContentsManager mInstance;
    private final List<WeakReference<OnContentUpdateListener>> mOnContentUpdateListeners = new ArrayList<WeakReference<OnContentUpdateListener>>();

    public static ContentsManager getInstance() {

        if (mInstance == null) {
            synchronized (ContentsManager.class) {
                if (mInstance == null) {
                    mInstance = new ContentsManager();
                }
            }
        }
        return mInstance;
    }

    public boolean registerOnContentUpdateListener(OnContentUpdateListener onContentUpdateListener) {
        synchronized (mOnContentUpdateListeners) {
            for (WeakReference<OnContentUpdateListener> weakRef : mOnContentUpdateListeners) {
                if (weakRef != null
                        && weakRef.get() == onContentUpdateListener) {
                    return false;
                }
            }

            mOnContentUpdateListeners.add(new WeakReference<OnContentUpdateListener>(onContentUpdateListener));
            return true;
        }
    }

    public void unregisterOnContentUpdateListener(OnContentUpdateListener onContentUpdateListener) {
        synchronized (mOnContentUpdateListeners) {
            WeakReference<OnContentUpdateListener> weakRef;
            for (int idx = 0; idx < mOnContentUpdateListeners.size(); idx++) {
                if ((weakRef = mOnContentUpdateListeners.get(idx)) != null) {
                    if (weakRef.get() == onContentUpdateListener) {
                        mOnContentUpdateListeners.remove(idx);
                        return;
                    }
                }
                else {
                    mOnContentUpdateListeners.remove(idx);
                    idx--;
                }
            }
        }
    }

    public void notifyContentUpdateSuccess(String key, Object... params) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        synchronized (mOnContentUpdateListeners) {
            for (WeakReference<OnContentUpdateListener> weakRef : mOnContentUpdateListeners) {
                if (weakRef != null
                        && weakRef.get() != null) {
                    if (key.equalsIgnoreCase(weakRef.get().getKey())) {
                        weakRef.get().onContentUpdateSucceed(params);
                    }
                }
            }
        }
    }
}
