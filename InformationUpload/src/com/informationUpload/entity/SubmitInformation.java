package com.informationUpload.entity;

import java.io.Serializable;

import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

public class SubmitInformation implements Serializable{
	private String info_fid;
	private int info_type;
	private String uploadDate;
	private String address;
	private double lat;
	private double lon;

	public SubmitInformation() {
		super();
	}

	

	public SubmitInformation(String info_fid, int info_type,
			String uploadDate, String address, double lat, double lon) {
		super();
		this.info_fid = info_fid;
		this.info_type = info_type;
		this.uploadDate = uploadDate;
		this.address = address;
		this.lat = lat;
		this.lon = lon;
	}



	public String getInfo_fid() {
		return info_fid;
	}

	public void setInfo_fid(String info_fid) {
		this.info_fid = info_fid;
	}

	public int getInfo_type() {
		return info_type;
	}

	public void setInfo_type(int info_type) {
		this.info_type = info_type;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}



	public double getLat() {
		return lat;
	}



	public void setLat(double lat) {
		this.lat = lat;
	}



	public double getLon() {
		return lon;
	}



	public void setLon(double lon) {
		this.lon = lon;
	}

	


}
