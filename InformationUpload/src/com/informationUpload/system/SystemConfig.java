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

	public static String SDCARD_HEAD_IMG_PATH=DATA_BASE_PATH+"HeadImgPath/";
	public static String INNER_HEAD_IMG_PATH=Environment.getRootDirectory().getPath()+"/HeadImgPath/";
	//隐藏所有其他fragment
	public static String HIDE_OTHER_FRAGMENT = "fragment_hide_other";

	//就地释放
	public static String FREE_FRAGMENT = "fragment_free";
    
	/**
	 * fragment 接收消息的类别
	 */
	// 地图选点 返回坐标和地址
	public static String FRAGMENT_UPDATE_SELECT_POINT_ADDRESS = "fragment_update_select_point_address";
    //修改昵称返回昵称
	public static String MODIFY_USERNAME="modify_username";
	//修改头像图片
	public static String MODIFY_HEAD_PIC="modify_head_pic";
	/**
	 * Bundle 传值
	 */
	//传递ROWKEY 显示相应的数据
	public static String BUNDLE_DATA_LIST_POSITION="bundle_data_list_position";
	public static String BUNDLE_DATA_SOURCE="bundle_data_source";
	public static String BUNDLE_DATA_ROWKEY = "bundle_data_rowkey";
	public static String BUNDLE_DATA_PICTURE_NUM = "bundle_data_picture_num";

	public static String BUNDLE_DATA_PICTURE_LIST ="bundle_data_picture_list";

	public static String BUNDLE_DATA_GEOPOINT="bundle_data_geopoint";

	public static String BUNDLE_DATA_TYPE="bundle_data_type";

	public static String BUNDLE_DATA_LOGIN_OUT = "bundle_data_login_out";
    public  static String MAIN_URL="http://fs.navinfo.com/";
	//  public static String REQUEST_URL = "http://172.23.125.202:8081/infor/information/";
	//  public static String REQUEST_URL = "http://192.168.3.155:8083/infor/information/";
	
	public static String REQUEST_URL = MAIN_URL+"infor/information/";
	//	public static String REQUEST_URL ="http://192.168.4.63:8098/infor/information/";

//	public static String REQUEST_URL ="http://192.168.4.189:/infor/information/";
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
	//验证码
	public static final String VERIFICATION_CODE="verification_code";
	//注册获取验证码
	public static final String GET_VERIFICATION_CODE="get_verification_code";
	//找回密码获取验证码
	public static final String FINDPASSWORD_VERIFICATION_CODE="findpassword_verification_code";
}
