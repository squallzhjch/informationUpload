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

    public static String HIDE_OTHER_FRAGMENT = "fragment_hide_other";

    public static String FRAGMENT_UPDATE_SELECT_POINT_ADDRESS = "fragment_update_select_point_address";
}
