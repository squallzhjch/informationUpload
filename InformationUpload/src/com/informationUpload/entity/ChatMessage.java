package com.informationUpload.entity;

import java.io.Serializable;

public class ChatMessage extends DataBaseMessage {
	
	private long chattimelong;//录音时间长度 
	private double amp;//音量

	public long getChattimelong() {
		return chattimelong;
	}

	public void setChattimelong(long chattimelong) {
		this.chattimelong = chattimelong;
	}

	public double getAmp() {
		return amp;
	}

	public void setAmp(double amp) {
		this.amp = amp;
	}
	
}
