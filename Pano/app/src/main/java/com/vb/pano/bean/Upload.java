package com.vb.pano.bean;

import java.io.Serializable;

/**
 * Created by seven on 2017/1/3.
 */

public class Upload implements Serializable{
    private String panoId;
    private String panoName;
    private long captureTime;
    private long uploadTime;
    private int uploadStatus;
    private double uploadPercent;

    public long getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(long captureTime) {
        this.captureTime = captureTime;
    }

    public String getPanoId() {
        return panoId;
    }

    public void setPanoId(String panoId) {
        this.panoId = panoId;
    }

    public String getPanoName() {
        return panoName;
    }

    public void setPanoName(String panoName) {
        this.panoName = panoName;
    }

    public double getUploadPercent() {
        return uploadPercent;
    }

    public void setUploadPercent(double uploadPercent) {
        this.uploadPercent = uploadPercent;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
