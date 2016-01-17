package com.informationUpload.tool;

import android.content.Context;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: PointTool
 * @Date 2016/1/15
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class PointTool {
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

}
