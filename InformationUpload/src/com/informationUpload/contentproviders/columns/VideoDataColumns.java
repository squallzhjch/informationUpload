package com.informationUpload.contentproviders.columns;

import android.provider.BaseColumns;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: PhotoColumns
 * @Date 2015/12/7
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public interface VideoDataColumns extends MyBaseColumns{
    /**
     * 0: 文字
     * 1：照片
     * 2：语音
     */
    String TYPE = "video_type";

    /**
     * 文字直接写文字 图片语音 写路径
     */
    String CONTENT = "video_content";

    /**
     * 资源唯一id
     */
    String PARENT_ID = "video_parent_id";

    String ROWKEY = "video_rowkey";

    /**
     * 创建修改时间
     */
    String TIME = "video_time";

    /**
     * 经纬度
     */
    String LATITUDE = "video_latitude";
    String LONGITUDE = "video_longitude";
    /**
     * 备注
     */
    String REMARK = "video_remark";
}
