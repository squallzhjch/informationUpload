package com.informationUpload.entity;

import java.io.Serializable;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: BaseMessage
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class BaseMessage implements Serializable {
    String rowkey ;//对应的父类key

    private double lat;
    private double lon;
    private long Time;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
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

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }
}
