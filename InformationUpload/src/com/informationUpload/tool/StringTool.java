package com.informationUpload.tool;

import android.text.TextUtils;

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
        String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";

        Pattern p = Pattern.compile(regExp);

        Matcher m = p.matcher(tel);

        return m.find();//boolean
    }
}