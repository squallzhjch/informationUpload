package com.informationUpload.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.informationUpload.tool.ImageTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: ImageViewEx
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class ImageViewEx extends ImageView {

    private String mFilePath = "";
    private int nWidth = 50;
    private int nHeight = 100;

    public ImageViewEx(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ImageViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ImageViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public void setWidth(int width) {
        nWidth = width;
    }

    public void setHeight(int height) {
        nHeight = height;
    }

    public void removeImage() {

        if (TextUtils.isEmpty(mFilePath)) {
            return;
        }

        File file = new File(mFilePath);
        if (!file.exists()) {
            mFilePath = "";
            return;
        }

        this.setImageResource(0);

        file.delete();
        mFilePath = "";
    }

    public void resetImageFile(String filename) {
        mFilePath = filename;
    }

    public void setImageByFilePath(String filename, int angle) {
        try {
            ImageTool.compressImage(filename, 80);
        } catch (Exception ex) {
        }

        Bitmap srcBmp = null;
        srcBmp = ImageTool.getImageThumbnail(filename, nWidth,
                nHeight);

        if (srcBmp != null) {
            srcBmp = ImageTool.rotateBitmap(srcBmp, angle);
            this.setImageBitmap(srcBmp);

            // -删除上一图片 释放空间
            removeImage();
            mFilePath = filename;
        }
    }

    public String getImageFilePath() {
        return mFilePath;
    }

    private String mLocationInfo = "";
    public void setLocationInfo(String info){
        mLocationInfo = info;
    }

    public String getLocationInfo(){
        return mLocationInfo;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
        }
    }

    //设置图片，且不压缩
    public void setImageNoCompress(String path, int angle) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            if (bitmap != null) {
                bitmap = ImageTool.rotateBitmap(bitmap, angle);
                this.setImageBitmap(bitmap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}