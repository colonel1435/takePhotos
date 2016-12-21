package com.sunland.signalinspect;

/**
 * Created by Administrator on 2016/12/16.
 */

public class BrowseInfo {
    private String mTitle;
    private boolean mChecked;

    public BrowseInfo() {
        this.mTitle = "";
        this.mChecked = false;
    }
    public BrowseInfo(String title, boolean checked) {
        this.mTitle = title;
        this.mChecked = checked;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public boolean getChecked() {
        return mChecked;
    }

}
