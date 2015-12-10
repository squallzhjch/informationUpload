package com.informationUpload.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InfromationMessage
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationMessage extends  BaseMessage{
    List<PictureMessage> pictureMessageList;
    List<ChatMessage> chatMessageList;

    private int type;
    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void addPictureMessage(PictureMessage message){
        if(message == null)
            return;
        if(pictureMessageList == null){
            pictureMessageList = new ArrayList<PictureMessage>();
        }
        pictureMessageList.add(message);
    }

    public void addChatMessage(ChatMessage message){
        if(message == null)
            return;
        if(chatMessageList == null){
            chatMessageList = new ArrayList<ChatMessage>();
        }
        chatMessageList.add(message);
    }


    public void removePictureMessage(PictureMessage message){
        if(message == null)
            return;
        if(pictureMessageList != null){
            pictureMessageList.remove(message);
        }
    }

    public void removeChatMessage(ChatMessage message){
        if(message == null)
            return;
        if(chatMessageList != null){
            chatMessageList.remove(message);
        }
    }

    public List<PictureMessage> getPictureMessageList() {
        return pictureMessageList;
    }

    public void setPictureMessageList(List<PictureMessage> pictureMessageList) {
        this.pictureMessageList = pictureMessageList;
    }

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }
}
