package com.informationUpload.contentproviders.columns;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: MyBaseColumns
 * @Date 2015/12/7
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public interface MyBaseColumns {
    String ID = "_id";
    String ROWKEY = "information_rowkey";
    String TIME = "information_time";
    /**
     * 经纬度
     */
    String LATITUDE = "information_latitude";
    String LONGITUDE = "information_longitude";
    /**
     * 备注
     */
    String REMARK = "information_remark";
}
