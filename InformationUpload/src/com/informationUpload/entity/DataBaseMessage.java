package com.informationUpload.entity;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: DataMessage
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class DataBaseMessage extends BaseMessage{
    private String name;//名字
    private String path;//路径

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
}
