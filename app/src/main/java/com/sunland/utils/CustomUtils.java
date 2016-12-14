package com.sunland.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zero on 2016/12/12.
 */
public class CustomUtils {
    public static String getCurTime(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String now = sDateFormat.format(new Date());
        return now;
    }
}
