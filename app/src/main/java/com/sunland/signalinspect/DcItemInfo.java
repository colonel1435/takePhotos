package com.sunland.signalinspect;

/**
 * Created by Administrator on 2016/12/23.
 */

public class DcItemInfo {

        private String mItem;
        private int mNum;

        public DcItemInfo() {
        }

        public DcItemInfo(String item, int num) {
            this.mItem = item;
            this.mNum = num;
        }

        public String getItem() {
            return mItem;
        }

        public void setItem(String item) {
            this.mItem = item;
        }

        public int getNum() {
            return mNum;
        }

        public void setNum(int num) {
            this.mNum = num;
        }
}
