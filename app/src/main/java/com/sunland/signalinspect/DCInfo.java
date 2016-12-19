package com.sunland.signalinspect;

/**
 * Created by Zero on 2016/12/18.
 */

public class DCInfo {
    private String mDC;
    private String mItem;
    private String mSetThumb;
    private String mBackThumb;

    public DCInfo() {
    }

    public DCInfo(String dc, String item, String setThumb, String backThumb) {
        this.mDC = dc;
        this.mItem = item;
        this.mSetThumb = setThumb;
        this.mBackThumb = backThumb;
    }

    public void setDC(String dc) {
        mDC = dc;
    }

    public String getDC() {
        return mDC;
    }

    public void setItem(String item) {
        mItem = item;
    }

    public String getItem() {
        return mItem;
    }

    public void setSetThumb(String thumb) {
        mSetThumb = thumb;
    }

    public String getSetThumb() {
        return mSetThumb;
    }

    public void setBackThumb(String thumb) {
        mBackThumb = thumb;
    }

    public String getBackThumb() {
        return mBackThumb;
    }
}
