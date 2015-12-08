package com.informationUpload.contents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lolo on 15/2/26.
 */
public abstract class AbstractOnContentUpdateListener implements OnContentUpdateListener{

    private final List<Object[]> mCachedObjects = new ArrayList<Object[]>();
    private volatile boolean mUpdateHappened = false;

    public void onContentUpdateSucceed(Object... values) {
        if (isActive()) {
            List<Object[]> objList = null;
            if (values != null && values.length > 0) {
                objList = new ArrayList<Object[]>();
                objList.add(values);
            }
            onContentUpdated(objList);
        }
        else {
            mUpdateHappened = true;
            if (values !=null && values.length > 0) {
                mCachedObjects.add(values);
            }
        }
    }

    public abstract void onContentUpdated(List<Object[]> values);
    public abstract boolean isActive();

    /**
     * Check weather a notification was passed-in before
     * @return - true if a content update notification was passed-in for this listener, otherwise false.
     */
    public boolean isUpdateHappened() {
        return mUpdateHappened;
    }

    /**
     * Get a list of cached objects if exists
     * @return - a list of cached objects or size 0 list if no cached object found
     */
    public List<Object[]> getCachedObjects() {
        return mCachedObjects;
    }

    public void clearCache() {
        mCachedObjects.clear();
        mUpdateHappened = false;
    }
}
