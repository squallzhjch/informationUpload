package com.informationUpload.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;

public class ImageCompress {
	public static final String CONTENT = "content";
	public static final String FILE = "file";

	public static class CompressOptions {
		public static final int DEFAULT_WIDTH = 150;
		public static final int DEFAULT_HEIGHT = 100;
		public int maxWidth = DEFAULT_WIDTH;
		public int maxHeight = DEFAULT_HEIGHT;
		/**
		 * 压缩后图片保存的文件
		 */
		public File destFile;
		/**
		 * 图片压缩格式,默认为jpg格式
		 */
		public CompressFormat imgFormat = CompressFormat.JPEG;
		/**
		 * 图片压缩比例 默认为30
		 */
		public int quality = 30;
		public Uri uri;
	}

	public Bitmap compressFromUri(Context context,
			CompressOptions compressOptions, Bitmap bm) {

		// String filePath = getFilePath(context, compressOptions.uri);
		// if (null == filePath) {
		// return null;
		// }
		// Bitmap temp = BitmapFactory.decodeFile(filePath, options);
		Bitmap bitmap = null;
		Bitmap destBitmap = null;
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream isBm = null;
		try {
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			isBm = new ByteArrayInputStream(baos.toByteArray());

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			destBitmap = BitmapFactory.decodeStream(isBm, null, options);

			int actualWidth = options.outWidth;
			int actualHeight = options.outHeight;

			int desiredWidth = getResizedDimension(compressOptions.maxWidth,
					compressOptions.maxHeight, actualWidth, actualHeight);
			int desiredHeight = getResizedDimension(compressOptions.maxHeight,
					compressOptions.maxWidth, actualHeight, actualWidth);
			options.inJustDecodeBounds = false;
			options.inSampleSize = findBestSampleSize(actualWidth,
					actualHeight, desiredWidth, desiredHeight);
			System.out.println("options.inSampleSize: " + options.inSampleSize);
			isBm.reset();
			destBitmap = BitmapFactory.decodeStream(isBm, null, options);

			if (destBitmap.getWidth() > desiredWidth
					|| destBitmap.getHeight() > desiredHeight) {
				bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth,
						desiredHeight, true);

			} else {
				bitmap = destBitmap;
			}
			if (null != compressOptions.destFile) {
				compressFile(compressOptions, bitmap);
			}
		} catch (OutOfMemoryError e) {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				isBm.close();
				destBitmap.recycle();
				destBitmap = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * Scales one side of a rectangle to fit aspect ratio.
	 * 
	 * @param maxPrimary
	 *            Maximum size of the primary dimension (i.e. width for max
	 *            width), or zero to maintain aspect ratio with secondary
	 *            dimension
	 * @param maxSecondary
	 *            Maximum size of the secondary dimension, or zero to maintain
	 *            aspect ratio with primary dimension
	 * @param actualPrimary
	 *            Actual size of the primary dimension
	 * @param actualSecondary
	 *            Actual size of the secondary dimension
	 */
	private static int getResizedDimension(int maxPrimary, int maxSecondary,
			int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	/**
	 * compress file from bitmap with compressOptions * * @param compressOptions
	 * * @param bitmap
	 */
	private void compressFile(CompressOptions compressOptions, Bitmap bitmap) {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(compressOptions.destFile);
		} catch (FileNotFoundException e) {
			Log.e("ImageCompress", e.getMessage());
		}
		bitmap.compress(compressOptions.imgFormat, compressOptions.quality,
				stream);
	}

	private static int findBestSampleSize(int actualWidth, int actualHeight,
			int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}
		return (int) n;
	}

	/**
	 * 获取文件的路径 * * @param scheme * @return
	 */
	private String getFilePath(Context context, Uri uri) {
		String filePath = null;
		if (CONTENT.equalsIgnoreCase(uri.getScheme())) {
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { Images.Media.DATA }, null, null, null);
			if (null == cursor) {
				return null;
			}
			try {
				if (cursor.moveToNext()) {
					filePath = cursor.getString(cursor
							.getColumnIndex(Images.Media.DATA));
				}
			} finally {
				cursor.close();
			}
		}
		if (FILE.equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		return filePath;
	}
}