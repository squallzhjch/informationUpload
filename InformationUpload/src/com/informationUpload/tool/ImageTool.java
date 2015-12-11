package com.informationUpload.tool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import com.informationUpload.utils.SystemConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: ImageTool
 * @Date 2015/12/3
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class ImageTool {
    private static DisplayMetrics mDeviceDm;

    public static void setDisplayMetrics(DisplayMetrics dm) {
        mDeviceDm = dm;
    }

    public static void compressImage(String filename, int scale) {
        // -现将原文件重命名
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        String newfname = SystemConfig.DATA_PICTURE_PATH + "temp"
                + filename.replace(SystemConfig.DATA_PICTURE_PATH, "");
        String srcFile = newfname;
        if (!renameFile(filename, newfname)) {
            srcFile = filename;
        }

        Bitmap srcBmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {

            srcBmp = BitmapFactory.decodeFile(srcFile, options);
            // Calculate inSampleSize
            // -压缩到和手机分辨率一致，获取手机设备的分辨率
            options.inSampleSize = calculateInSampleSize(options,
                    mDeviceDm.widthPixels, mDeviceDm.heightPixels);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[100 * 1024];
            options.inPurgeable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // 实例化Bitmap
            srcBmp = BitmapFactory.decodeFile(srcFile, options);

            if (srcBmp != null) {
                String strTitle = "information:" + TimeTool.getCurrentDate()
                        + " " + TimeTool.getCurrentTime();
                srcBmp = watermarkBitmap(srcBmp, null, strTitle);
            }

        } catch (OutOfMemoryError e) {
            //
        }

        if (srcBmp == null) {
            renameFile(newfname, filename);
            return;
        }

        File myCaptureFile = new File(filename);
        BufferedOutputStream bos = null;
        boolean result = false;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            try {
                // 实例化Bitmap
                if (options.inSampleSize == 1) {
                    scale = 100;
                }
                srcBmp.compress(Bitmap.CompressFormat.JPEG, scale, bos);
            } catch (OutOfMemoryError e) {
                //
            }

            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }

                result = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (srcBmp != null && !srcBmp.isRecycled()) {
            srcBmp.recycle();
            srcBmp = null;
        }

        if (result) {
            // -生产新文件成功后 删除掉原文件
            deleteFile(newfname);
        } else {
            renameFile(newfname, filename);
        }
    }


    // 加水印 也可以加文字
    public static Bitmap watermarkBitmap(Bitmap src, Bitmap watermark,
                                         String title) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap newbitmap = null;
        // 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
        try {
            try {
                newbitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
            } catch (OutOfMemoryError e) {
            }

            if (newbitmap != null) {
                Canvas cv = new Canvas(newbitmap);
                cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
                // 加入文字 按红 黄 蓝 绿 灰 （左上 右上 左下 右下 中间）
                float fTextSize = 14;
                int nTemp = h;
                if (w > h) {
                    nTemp = w;
                }
                if (nTemp > 960) {
                    fTextSize = 26;
                }

                if (title != null) {
                    String familyName = "宋体";
                    Typeface font = Typeface
                            .create(familyName, Typeface.ITALIC);
                    TextPaint textPaint = new TextPaint();
                    textPaint.setColor(Color.RED);
                    textPaint.setTypeface(font);
                    textPaint.setTextSize(fTextSize);
                    // 这里是自动换行的
                    StaticLayout layout = new StaticLayout(title, textPaint, w,
                            Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                    layout.draw(cv);
                    textPaint.setColor(Color.YELLOW);
                    if (w > 480) {
                        cv.drawText(title, w - 240, h - 10, textPaint);
                    }

                    textPaint.setColor(Color.DKGRAY);
                    cv.drawText(title, w / 2, h / 2, textPaint);
                    // -增加版权标
                    title = "CopyRight© NavInfo" ;//+ getVerName(m_context);
                    textPaint.setColor(Color.BLUE);
                    cv.drawText(title, 0, h - 10, textPaint);
                    textPaint.setColor(Color.GREEN);
                    if (w > 480) {
                        cv.drawText(title, w - 240, 30, textPaint);
                    }
                }
                cv.save(Canvas.ALL_SAVE_FLAG);// 保存
                cv.restore();// 存储
            }

        } catch (Exception ex) {

        }

        return newbitmap;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verName;
    }

    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        if (options == null) {
            return 1;
        }

        int height = options.outHeight;
        int width = options.outWidth;

        // -处理横排照片照片宽度大于高度的情况
        if (options.outHeight < options.outWidth) {
            height = options.outWidth;
            width = options.outHeight;
        }

        if (height < 960) {
            return 1;
        }

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static boolean renameFile(String oldname, String newname) {
        boolean result = false;
        File oldfile = new File(oldname);
        File newfile = new File(newname);

        result = oldfile.renameTo(newfile);
        return result;
    }

    /**
     * 删除指定文件
     *
     * @param filename
     */
    public static void deleteFile(String filename) {
        File file = new File(filename);

        if (file != null) {
            delete(file);
        }
    }
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 批量删除
     *
     */
    public static void deleteFiles(ArrayList<String> files) {
        for (int i = 0; i < files.size(); i++) {
            deleteFile(files.get(i));
        }
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
     * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
     * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath
     *            图像的路径
     * @param width
     *            指定输出图像的宽度
     * @param height
     *            指定输出图像的高度
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 获取这个图片的宽和高，注意此处的bitmap为null
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            options.inJustDecodeBounds = false; // 设为 false
            // 计算缩放比
            int h = options.outHeight;
            int w = options.outWidth;
            int beWidth = w / width;
            int beHeight = h / height;
            int be = 1;
            if (beWidth < beHeight) {
                be = beWidth;
            } else {
                be = beHeight;
            }
            if (be <= 0) {
                be = 1;
            }
            options.inSampleSize = be;
            // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
            if (bitmap != null) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            }

        } catch (OutOfMemoryError e) {
        }

        return bitmap;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap
     *            原始图片
     * @param degrees
     *            原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees)
    {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Bitmap bmp = null;

        try {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.setRotate(degrees);
            bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (OutOfMemoryError e) {
            bmp = bitmap;
        }

        return bmp;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

}
