package com.informationUpload.contentproviders;

import android.net.Uri;

import com.informationUpload.contentproviders.columns.InformationColumns;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: Informations
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public final class Informations {
    public static final String AUTHORITY = "com.informationUpload.contentproviders.Informations";

    static class InformationsBase {

    }

    public static class Information extends InformationsBase implements InformationColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/Information");
    }
}
