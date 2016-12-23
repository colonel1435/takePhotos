package com.sunland.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.sunland.signalinspect.ActionSearchActivity;
import com.sunland.signalinspect.DCInfo;
import com.sunland.signalinspect.DcItemInfo;
import com.sunland.signalinspect.DepotActivity;
import com.sunland.signalinspect.SearchInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zero on 2016/12/12.
 */
public class CustomUtils {

    public static final String TAG = "wumin";
    public static final String JPG_POSTFIX = ".jpg";
    public static String getCurTime(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String now = sDateFormat.format(new Date());
        return now;
    }

    public static List<DcItemInfo> read4XML(String fileName) throws IOException, XmlPullParserException {
        InputStream fis = new FileInputStream(new File(fileName));
        XmlPullParser xmlPullParser= Xml.newPullParser();
        xmlPullParser.setInput(fis, "UTF-8");
        int eventType=xmlPullParser.getEventType();
        Log.i(TAG, "DC parse....");
        List<DcItemInfo> items = null;
        DcItemInfo item = null;
        while(eventType!=XmlPullParser.END_DOCUMENT){
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    Log.i(TAG, "DC START_DOCUMENT");
                    items = new ArrayList<DcItemInfo>();
                    break;
                case XmlPullParser.START_TAG:
                    Log.i(TAG, "DC START_TAG");
                    if (xmlPullParser.getName().equals("depot")) {
                        item = new DcItemInfo();
                        item.setItem(xmlPullParser.getAttributeValue(null, "item"));
                        item.setNum(Integer.parseInt(xmlPullParser.getAttributeValue(null, "num")));
                        Log.i(TAG, "DC START_TAG Finding");
                    }
//                    if(xmlPullParser.getName().equals("DC")){
//                        item.setItem(xmlPullParser.nextText());
//                        Log.i(TAG, "DC " + xmlPullParser.nextText());
//                    }
//                    if(xmlPullParser.getName().equals("dc_name")){
//                        Log.i(TAG, "dc_num " + xmlPullParser.nextText());
//                        item.setNum(Integer.parseInt(xmlPullParser.nextText()));
//                    }
//                    Log.i(TAG, "KEY :" + xmlPullParser.getName());
                    break;
                case XmlPullParser.END_TAG:
                    Log.i(TAG, "DC END_TAG");
                    if(xmlPullParser.getName().equals("depot")){
                        Log.i(TAG, "DC END_TAG depot");
                        items.add(item);
                        item=null;
                    }
                    break;
                case XmlPullParser.END_DOCUMENT:
                    fis.close();
            }
            eventType = xmlPullParser.next();
        }
        return items;
    }

    public static void write2XML(String fileName, List<DcItemInfo> lists) throws IOException {
        XmlSerializer xs = Xml.newSerializer();
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        xs.setOutput(fos, "UTF-8");
        xs.startDocument("UTF-8", true);
        for (DcItemInfo item : lists) {
            xs.startTag(null,"depot");
            xs.attribute(null, "num", Integer.toString(item.getNum()));
            xs.attribute(null, "item", item.getItem());

//            xs.startTag(null, "DC");
//            xs.text(item.getItem());
//            xs.endTag(null, "DC");
//            Log.i(TAG, "DC -> " + item.getItem());
//
//            xs.startTag(null, "dc_num");
//            xs.text(Integer.toString(item.getNum()));
//            xs.endTag(null, "dc_num");
//            Log.i(TAG, "DC_num -> " + item.getNum());
            xs.endTag(null, "depot");
        }
        xs.endDocument();
        fos.flush();
        fos.close();
    }
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static String getRealFilePath( final Context context, final Uri uri ) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    public static List<String> splitString(String src, String sep) {
        String[] strs = src.split(sep);
        return Arrays.asList(strs);
    }

    public static String replaceStrFromBegin(String src, String newVal, String oldVal, String parent) {
        int lenSrc = src.length();
        int beginPos = src.indexOf(parent);
        int endPos = src.indexOf(DepotActivity.DC_ITEM_SEP, beginPos);
        String item = src.substring(beginPos, endPos);
        item = item.replace(oldVal, newVal);
        Log.i(TAG, "ITEM -> " + item);
        String newString = src.substring(0, beginPos) + item + src.substring(endPos, lenSrc);
        return newString;
    }
    public static String addStrFromBegin(String src, String addStr, String before, String begin) {
        int beginPos = src.indexOf(begin);
        int postion = src.indexOf(before, beginPos) + before.length();
        int lenSrc = src.length();
        String newString = src.substring(0,postion) + addStr + src.substring(postion, lenSrc);

        return newString;
    }
    public static String delStrFromBegin(String src, String delStr, String begin) {
        int beginPos = src.indexOf(begin);
        int postion = src.indexOf(delStr, beginPos);
        int lenSrc = src.length();
        int lenDel = delStr.length();
        String newString = src.substring(0,postion) + src.substring(postion + lenDel, lenSrc);

        return newString;
    }
    public static String delStr2End(String src, String begin, String end) {
        int lenSrc = src.length();
        int beginPos = src.indexOf(begin);
        int endPos = src.indexOf(end, beginPos);
        String newString = src.substring(0, beginPos) + src.substring(endPos+1, lenSrc);
        Log.i(TAG, "LEN SRC -> " + lenSrc + " begin -> " + beginPos + " end -> " + endPos + " new ->" + newString);

        return newString;
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

    public static void getThumbList(List<String> fileList, String path) {
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

    public static Bitmap getImageThumbnail(String img, int width, int height) {
        String workDir = Environment.getExternalStorageDirectory() + DepotActivity.WORK_DIR;
        String path = workDir + img;
        Log.i(TAG, "Path -> " + path + " Thumb -> " + img);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
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

        String thumbFile = workDir + ActionSearchActivity.THUMBNAIL_LABEL + img;
        Log.i(TAG, "ThumbFile -> " + thumbFile);
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
