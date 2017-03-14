package com.vb.pano.bean;

import java.io.Serializable;

/**
 * Created by seven on 2016/11/14.
 * 相机的配置参数信息
 */

public class Config implements Serializable{
    private String cameraId;
    private String saturation;
    private String shutter_spd;
    private String iso;
    private String sharpness;
    private String brightness;
    private String contrast;
    private String wb;

    public String getWb() {
        return wb;
    }

    public void setWb(String wb) {
        this.wb = wb;
    }

    public String getBrightness() {
        return brightness;
    }

    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getContrast() {
        return contrast;
    }

    public void setContrast(String contrast) {
        this.contrast = contrast;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getSaturation() {
        return saturation;
    }

    public void setSaturation(String saturation) {
        this.saturation = saturation;
    }

    public String getSharpness() {
        return sharpness;
    }

    public void setSharpness(String sharpness) {
        this.sharpness = sharpness;
    }

    public String getShutter_spd() {
        return shutter_spd;
    }

    public void setShutter_spd(String shutter_spd) {
        this.shutter_spd = shutter_spd;
    }
}
