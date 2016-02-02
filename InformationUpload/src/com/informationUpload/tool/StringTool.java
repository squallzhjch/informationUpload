package com.informationUpload.tool;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: StringTool
 * @Date 2016/1/17
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class StringTool {

    public static boolean isTelNum(String tel){
        if(TextUtils.isEmpty(tel))
            return false;
        if(tel.trim().length() != 11){
            return false;
        }
        if(tel.startsWith("1")){
            return true;
        }
//        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
//
//        Pattern p = Pattern.compile(regExp);
//
//        Matcher m = p.matcher(tel);
//
//        return m.find();//boolean
        return false;
    }

    public static String createUserId(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = df.format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return time + uuid;
    }
}
