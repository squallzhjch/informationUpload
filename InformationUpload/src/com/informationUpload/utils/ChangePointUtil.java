package com.informationUpload.utils;
/**
 * 百度坐标跟实际坐标转换
 * @author zsy
 *
 */
public class ChangePointUtil {
	/**
	 * nav坐标转百度坐标
	 */
	private static double mBaiduPI = 3.14159265358979324 * 3000.0 / 180.0;  

	/**
	 * 百度坐标转经纬度坐标
	 * @param bd_lat
	 * @param bd_lon
	 * @return	[0]:lat, [1]lon
	 */
	public static double[] baidutoreal(double bd_lat, double bd_lon)
	{
		double[] ret = new double[2];

		double x = bd_lon - 0.0065, y = bd_lat - 0.006;  
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * mBaiduPI);  
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * mBaiduPI);  
		ret[1] = z * Math.cos(theta);  
		ret[0] = z * Math.sin(theta);
		return ret;  
	}
	/**
	 * 经纬度坐标转百度坐标
	 * @param gg_lat
	 * @param gg_lon
	 * @return
	 */
	public static double[] realtobaidu(double gg_lat, double gg_lon)  
	{  
		double[] ret = new double[2];

		double x = gg_lon, y = gg_lat;  
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * mBaiduPI);  
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * mBaiduPI);  
		ret[1] = z * Math.cos(theta) + 0.0065;  
		ret[0] = z * Math.sin(theta) + 0.006;  
		return ret;
	}

}
