package com.informationUpload.contentproviders;

import android.net.Uri;

import com.informationUpload.contentproviders.columns.InformationColumns;
import com.informationUpload.contentproviders.columns.VideoDataColumns;

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
        public static final int STATUS_LOCAL = 0; //数据还在本地没有上传
        public static final int STATUS_SERVER = 1;//已经上传

        public static final int TYPE_TEXT = 0; //备注
        public static final int TYPE_PICTURE = 1;//图片
        public static final int TYPE_CHAT = 2; //语音
    }

    public static class Information extends InformationsBase implements InformationColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/Information");
        public static final Uri CONTENT_URI_WITH_VIDEO = Uri.parse("content://" + AUTHORITY + "/Information_With_Video");
    }

    public static class VideoData extends InformationsBase implements VideoDataColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/VideoData");
    }
}
