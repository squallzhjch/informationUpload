package com.informationUpload.contentProviders;

import android.net.Uri;

/**
 * Created by lolo on 15/6/26.
 */
public interface ContentProviderDecorator {

    void startBatchNotification(Uri... uris);

    void endBatchNotification(Uri... uris);
}
