package com.informationUpload.contentProviders.columns;


/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InformationColumns
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public interface InformationColumns extends MyBaseColumns {
	String ADDRESS="information_address";
    /**
     * 用户ID
     */
    String USER_ID = "information_userId";
    /**
     * 情报类型
     */
    String TYPE = "information_type";
    /**
     * 作业状态
     * 0：未上传
     * 1：已上传
     */
    String STATUS = "information_status";
    /**
     * 唯一标示，子表的父id
     */
    String ROWKEY = "information_rowkey";

    /**
     * 创建修改时间
     */
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
