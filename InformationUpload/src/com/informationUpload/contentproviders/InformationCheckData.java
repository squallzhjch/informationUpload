package com.informationUpload.contentproviders;

import java.io.Serializable;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: InformationCheckData
 * @Date 2015/12/12
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class InformationCheckData implements Serializable {
    int localNum;
    int uploadNum;

    public InformationCheckData( int localNum, int uploadNum){
        this.localNum = localNum;
        this.uploadNum = uploadNum;
    }

    public int getLocalNum() {
        return localNum;
    }

    public void setLocalNum(int localNum) {
        this.localNum = localNum;
    }

    public int getUploadNum() {
        return uploadNum;
    }

    public void setUploadNum(int uploadNum) {
        this.uploadNum = uploadNum;
    }
}
