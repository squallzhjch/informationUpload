package com.informationUpload.entity;

import java.io.Serializable;
/**
 * 坐标信息实体类
 * @author chentao
 *
 */
public class locationMessage implements Serializable {
   
	private float latitude;//纬度
	private float longitude;//经度
	
	public locationMessage() {
		super();
	}
	public locationMessage(float latitude, float longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	
	
}
