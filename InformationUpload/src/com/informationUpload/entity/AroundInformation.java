package com.informationUpload.entity;

import java.io.Serializable;

import com.informationUpload.map.GeoPoint;

public class AroundInformation implements Serializable {

	private String InfoIntelId;
	private String InfoType;
	private String address;
	private GeoPoint gp;
	
	public AroundInformation() {
		super();
	}
	public AroundInformation(String infoIntelId, String infoType,
			String address, GeoPoint gp) {
		super();
		InfoIntelId = infoIntelId;
		InfoType = infoType;
		this.address = address;
		this.gp = gp;
	}
	public String getInfoIntelId() {
		return InfoIntelId;
	}
	public void setInfoIntelId(String infoIntelId) {
		InfoIntelId = infoIntelId;
	}
	public String getInfoType() {
		return InfoType;
	}
	public void setInfoType(String infoType) {
		InfoType = infoType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public GeoPoint getGp() {
		return gp;
	}
	public void setGp(GeoPoint gp) {
		this.gp = gp;
	}
	
	
}
