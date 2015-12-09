package com.informationUpload.utils;

import android.os.Environment;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: SystemConfig
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class SystemConfig {

    public static String DATA_SDCRAD_PATH = Environment
            .getExternalStorageDirectory() + "/InformationUpload/data/";

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
}
