package com.sunland.signalinspect;

/**
 * Created by Zero on 2016/12/13.
 */
public class DepotInfo {
    private int imgID;
    private String depotName;
    private String dcNum;
    private String date;

    public DepotInfo() {

    }
    public DepotInfo(int img, String depot, String num, String date) {
        this.imgID = img;
        this.depotName = depot;
        this.dcNum = num;
        this.date = date;
    }

    public void setImg(int id) {
        this.imgID = id;
    }
    public int getImg() {
        return imgID;
    }

    public void setDepot(String depot) {
        this.depotName = depot;
    }

    public String getDepot() {
        return depotName;
    }

    public void setDcNum(String num) {
        this.dcNum = num;
    }

    public String getDcNum() {
        return dcNum;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }


}
