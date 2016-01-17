package com.informationUpload.contentProviders;

import android.content.ContentProvider;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lolo on 15/6/26.
 */
public abstract class ContentProviderWithDecorator extends ContentProvider implements ContentProviderDecorator {

    private static class UriParams  {
        Uri mUri;
        volatile int mCount;

        public UriParams(Uri uri, int count) {
            mUri = uri;
            mCount = count;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof UriParams) && mUri != null && mUri.equals(((UriParams)obj).mUri);
        }

        @Override
        public int hashCode () {
            if (mUri != null) {
                return mUri.hashCode();
            }
            return super.hashCode();
        }
    }

    private List<UriParams> mMonitoringUris = new ArrayList<UriParams>();


    public synchronized void startBatchNotification(Uri... uris) {
        if (uris != null && uris.length > 0) {
            UriParams uriParams;
            for (Uri uri : uris)
            if ((uriParams = getUriParams(uri)) == null) {
                mMonitoringUris.add(new UriParams(uri, 1));
            }
            else {
                uriParams.mCount++;
            }
        }
    }

    public synchronized void endBatchNotification(Uri...uris) {
        if (mMonitoringUris.size() > 0) {
            UriParams uriParams;
            for (Uri uri: uris) {
                if ((uriParams = getUriParams(uri)) != null) {
                    getContext().getContentResolver().notifyChange(uriParams.mUri, null);
                    uriParams.mCount--;
                    if (uriParams.mCount <= 0) {
                        mMonitoringUris.remove(uriParams);
                    }
                }
            }
        }
    }

    private UriParams getUriParams(Uri uri) {
        for (int idx = 0; idx < mMonitoringUris.size(); idx++) {
            if (uri.toString().startsWith(mMonitoringUris.get(idx).mUri.toString())) {
                return mMonitoringUris.get(idx);
            }
        }
        return null;
    }

    protected void notifyUriIfNeeded(Uri uri) {
        if (uri != null) {
            if (mMonitoringUris.size() == 0 || (getUriParams(uri) != null)) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
    }
}
