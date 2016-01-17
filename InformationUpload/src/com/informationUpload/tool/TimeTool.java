package com.informationUpload.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: TimeTool
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class TimeTool {

    public static String getCurrentDate() {
        String result = "";
        long nDate = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(nDate);// 获取当前时间
        result = formatter.format(curDate);
        return result;
    }

    public static String getCurrentTime() {
        String strValue = "";

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        strValue = formatter.format(curDate);

        return strValue;
    }

    public static String getCurrentIntTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sDateFormat.format(new java.util.Date());

        return strDate;
    }
}
