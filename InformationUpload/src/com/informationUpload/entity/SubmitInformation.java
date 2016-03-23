package com.informationUpload.entity;

import java.io.Serializable;

import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

public class SubmitInformation implements Serializable{
	private String info_fid;
	private String info_type;
	private String uploadDate;
	private String address;
	private String lat;
	private String lon;

	public SubmitInformation() {
		super();
	}

	

	public SubmitInformation(String info_fid, String info_type,
			String uploadDate, String address, String lat, String lon) {
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

	public String getInfo_type() {
		return info_type;
	}

	public void setInfo_type(String info_type) {
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



	public String getLat() {
		return lat;
	}



	public void setLat(String lat) {
		this.lat = lat;
	}



	public String getLon() {
		return lon;
	}



	public void setLon(String lon) {
		this.lon = lon;
	}

	


}
