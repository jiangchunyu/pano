package com.vb.pano.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by seven on 2016/11/11.
 */

public class Pano  implements Serializable,Comparable<Pano>{
    private String panoId;
    private String thumNail;
    private String name;
    private String address;
    private String timeMode;
    private String viewMode;
    private String descrip;
    private String qrCode;
    private String time;
    private String pto;
    private boolean isUpload;

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public String getPto() {
        return pto;
    }

    public void setPto(String pto) {
        this.pto = pto;
    }

    //按照时间排序
    @Override
    public int compareTo(Pano o) {
        Date time1=stringToDate(o.getTime());
        Date time2=stringToDate(this.getTime());
        return time1.compareTo(time2);
    }
    public Date stringToDate(String s)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date date =formatter.parse(s);

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPanoId() {
        return panoId;
    }

    public void setPanoId(String panoId) {
        this.panoId = panoId;
    }

    public String getThumNail() {
        return thumNail;
    }

    public void setThumNail(String thumNail) {
        this.thumNail = thumNail;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(String timeMode) {
        this.timeMode = timeMode;
    }

    public String getViewMode() {
        return viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode;
    }
}
