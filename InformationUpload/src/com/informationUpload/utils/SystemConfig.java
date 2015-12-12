package com.informationUpload.utils;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SystemConfig
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SystemConfig {

    public static String DATA_BASE_PATH = Environment
            .getExternalStorageDirectory() + "/InformationUpload/";

    public static String DATA_PICTURE_PATH = DATA_BASE_PATH + "Picture/";
    public static String DATA_CHAT_PATH = DATA_BASE_PATH + "Chat/";

    //隐藏所有其他fragment
    public static String HIDE_OTHER_FRAGMENT = "fragment_hide_other";

    /**
     * fragment 接收消息的类别
     */
    // 地图选点 返回坐标和地址
    public static String FRAGMENT_UPDATE_SELECT_POINT_ADDRESS = "fragment_update_select_point_address";

    /**
     * Bundle 传值
     */
    //传递ROWKEY 显示相应的数据
    public static String BUNDLE_DATA_ROWKEY = "bundle_data_rowkey";
    public static String BUNDLE_DATA_PICTURE_NUM = "bundle_data_picture_num";
    
    public static String BUNDLE_DATA_PICTURE_LIST ="bundle_data_picture_list";
    
    public static String BUNDLE_DATA_GEOPOINT="bundle_data_geopoint";

    public static String BUNDLE_DATA_TYPE="bundle_data_type";

}
