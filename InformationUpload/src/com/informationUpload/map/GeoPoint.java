package com.informationUpload.map;

import java.io.Serializable;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: GeoPoint
 * @Date 2015/12/9
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class GeoPoint implements Serializable{
    private double lat;
    private double lon;
    
    

    public GeoPoint() {
		super();
	}

	public GeoPoint(double lat,double lon) {
		this.lat=lat;
		this.lon=lon;
				
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
}
