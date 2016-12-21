package com.sunland.utils;

import android.util.Log;

import com.sunland.signalinspect.ActionSearchActivity;

import java.io.File;
import java.util.List;

public class BitmapUtils {
	private static final String TAG = "wumin";

	public static void getBmpUrl(List<String> fileList, String root) {
		File[] allFiles = new File(root).listFiles();
		for (int i = 0; i < allFiles.length; i++) {
			File file = allFiles[i];
			if (file.isFile() && file.getName().endsWith(CustomUtils.JPG_POSTFIX) && (!file.getName().startsWith(ActionSearchActivity.THUMBNAIL_LABEL))) {
				Log.i(TAG, "Add file -> " +  file.getAbsolutePath());
				fileList.add(file.getAbsolutePath());
			}
		}
	}
}
