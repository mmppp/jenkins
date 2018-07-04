package com.hanke.navi.skyair.offline;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class OffLineMapUtils {
	/**
	 * 获取map 缓存和读取目录
	 */
	public static String getSdCacheDir(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File fExternalStorageDirectory = Environment.getExternalStorageDirectory();
			File autonaviDir = new File(fExternalStorageDirectory, "amap");
			boolean result = false;
			if (!autonaviDir.exists()) {
				result = autonaviDir.mkdir();
			}
			java.io.File minimapDir = new java.io.File(autonaviDir, "offlineMap");
			if (!minimapDir.exists()) {
				result = minimapDir.mkdir();
			}
			return minimapDir.toString() + "/";
		} else {
			return "";
		}
	}
}
