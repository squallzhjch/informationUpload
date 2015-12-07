package com.informationUpload.entity;

import java.io.Serializable;

public class ChatMessage implements Serializable {
	
	private long chattimelong;//录音时间长度 
	
	private String name;//音频名字
	
	private String path;//路径
	
	private double amp;//音量

	public long getChattimelong() {
		return chattimelong;
	}

	public void setChattimelong(long chattimelong) {
		this.chattimelong = chattimelong;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public double getAmp() {
		return amp;
	}

	public void setAmp(double amp) {
		this.amp = amp;
	}
	
	
	
	

}
