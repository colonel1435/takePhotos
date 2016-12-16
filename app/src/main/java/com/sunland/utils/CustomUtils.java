package com.sunland.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zero on 2016/12/12.
 */
public class CustomUtils {

    public static final String JPG_POSTFIX = ".jpg";
    public static String getCurTime(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String now = sDateFormat.format(new Date());
        return now;
    }

    public static String delStr(String src, String delStr) {
            int postion = src.indexOf(delStr);
            int lenSrc = src.length();
            int lenDel = delStr.length();
            String newString = src.substring(0,postion) + src.substring(postion + lenDel, lenSrc);

            return newString;
        }

    public static void getFileList(List<String> fileList, String path) {
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile() && file.getName().endsWith(JPG_POSTFIX)) {
                fileList.add(file.getName());
            }
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
