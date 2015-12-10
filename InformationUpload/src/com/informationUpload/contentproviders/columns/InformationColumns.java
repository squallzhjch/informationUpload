package com.informationUpload.contentproviders.columns;


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
     * 预留
     */
    String DATA_1 = "information_data1";
    String DATA_2 = "information_data2";
    String DATA_3 = "information_data3";
    String DATA_4 = "information_data4";
    String DATA_5 = "information_data5";
    String DATA_6 = "information_data6";
    String DATA_7 = "information_data7";
    String DATA_8 = "information_data8";
    String DATA_9 = "information_data9";
    String DATA_10 = "information_data10";
}
