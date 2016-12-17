package com.sunland.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sunland.signalinspect.ActionSearchActivity;
import com.sunland.signalinspect.SearchInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static void getFileList(List<SearchInfo> fileList, String path) {
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile() && file.getName().endsWith(JPG_POSTFIX) && (!file.getName().startsWith(ActionSearchActivity.THUMBNAIL_LABEL))) {
                SearchInfo item = new SearchInfo(file.getName(), false);
                fileList.add(item);
            }
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Bitmap getImageThumbnail(String img, int width, int height) {
        Bitmap bmp = null;
        String path = ActionSearchActivity.workDir + img;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bmp = BitmapFactory.decodeFile(path, options);
        bmp = ThumbnailUtils.extractThumbnail(bmp, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        String thumbFile = ActionSearchActivity.workDir + ActionSearchActivity.THUMBNAIL_LABEL + img;
        File file = new File(thumbFile);
        if (file.exists())
            file.delete();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
