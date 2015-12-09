package com.informationUpload.entity;

import java.io.Serializable;

public class ChatMessage extends DataBaseMessage {
	
	private long chattimelong;//录音时间长度 


	public long getChattimelong() {
		return chattimelong;
	}

	public void setChattimelong(long chattimelong) {
		this.chattimelong = chattimelong;
	}


}
