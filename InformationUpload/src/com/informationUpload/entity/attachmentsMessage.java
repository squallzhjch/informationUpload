package com.informationUpload.entity;

import java.io.Serializable;

import android.location.Location;
/**
 * 附件信息实体类
 * @author chentao
 *
 */
public class attachmentsMessage implements Serializable{
	 private int type;//附件类型
	 private String url;//附件url
	 private String time;//附件生成时间
	 private String remark;//附件说明
	 private locationMessage location;//附件位置信息对象
	 
	public attachmentsMessage() {
		super();
	}
	public attachmentsMessage(int type, String url, String time, String remark,
			locationMessage location) {
		super();
		this.type = type;
		this.url = url;
		this.time = time;
		this.remark = remark;
		this.location = location;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public locationMessage getLocation() {
		return location;
	}
	public void setLocation(locationMessage location) {
		this.location = location;
	}
	 
	 
	 
}
