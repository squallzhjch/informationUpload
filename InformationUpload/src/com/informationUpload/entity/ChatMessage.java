package com.informationUpload.entity;

import java.io.Serializable;

public class ChatMessage implements Serializable {
	
	private String chattimelong;//录音时间长度 
	
	private String name;//音频名字
	
	private String path;//路径
	
	private String amp;//音量

	public String getChattimelong() {
		return chattimelong;
	}

	public void setChattimelong(String chattimelong) {
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

	public String getAmp() {
		return amp;
	}

	public void setAmp(String amp) {
		this.amp = amp;
	}
	
	
	
	

}
