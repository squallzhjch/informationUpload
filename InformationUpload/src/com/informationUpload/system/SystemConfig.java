package com.informationUpload.system;

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

    //就地释放
    public static String FREE_FRAGMENT = "fragment_free";

    /**
     * fragment 接收消息的类别
     */
    // 地图选点 返回坐标和地址
    public static String FRAGMENT_UPDATE_SELECT_POINT_ADDRESS = "fragment_update_select_point_address";

    /**
     * Bundle 传值
     */
    //传递ROWKEY 显示相应的数据
    public static String BUNDLE_DATA_SOURCE="bundle_data_source";
    public static String BUNDLE_DATA_ROWKEY = "bundle_data_rowkey";
    public static String BUNDLE_DATA_PICTURE_NUM = "bundle_data_picture_num";
    
    public static String BUNDLE_DATA_PICTURE_LIST ="bundle_data_picture_list";
    
    public static String BUNDLE_DATA_GEOPOINT="bundle_data_geopoint";

    public static String BUNDLE_DATA_TYPE="bundle_data_type";

    public static String BUNDLE_DATA_LOGIN_OUT = "bundle_data_login_out";

//    	public static String REQUEST_URL = "http://172.23.44.11:8081/infor/information/";
//    public static String REQUEST_URL = "http://192.168.3.155:8083/infor/information/";
    public static String REQUEST_URL = "http://fs.navinfo.com/infor/information/";
//    public static String REQUEST_URL ="http://192.168.4.189/infor/information/";
    /**
     *解压过程中的消息
     */
    public static final int MSG_TASK_GET_DATA_UNZIP_START = 3120;
    public static final int MSG_TASK_GET_DATA_UNZIP_FAILED = 3121;
    public static final int MSG_TASK_GET_DATA_UNZIPPING = 3122;
    public static final int MSG_TASK_GET_DATA_UNZIP_SUCCESS = 3137;// 解压成功
    public static final int MSG_TASK_GET_DATA_FAILED = 3111;
    
    
 // 最大的进度
 	public static final String MAX_SIZE = "maxSize";
 // 进度变化的常量
 	public static final String CHANGE_SIZE = "changeSize";
 	
 	//网络状态变化
 	public static final String NET_WORK_STATE="NetWorkState";
}
