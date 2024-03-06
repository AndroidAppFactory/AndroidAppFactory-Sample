package com.bihe0832.android.base.m3u8.bean;

import android.text.TextUtils;

import com.bihe0832.android.lib.utils.encrypt.messagedigest.MD5;


/**
 * @author zixie code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
public class M3U8TSInfo implements Comparable<M3U8TSInfo> {
	public static final String FILE_EXTENTION = ".ts";
	private String m3u8TSURL;
	private String m3u8TSKeyURL;
	private long fileSize;
	private float seconds;
	public boolean finished = false;

	public M3U8TSInfo(String m3u8TSURL, String key, float seconds) {
		this.m3u8TSURL = m3u8TSURL;
		this.m3u8TSKeyURL = key;
		this.seconds = seconds;
	}

	public String getM3u8TSURL() {
		return m3u8TSURL;
	}

	public String getM3u8TSFullURL(String baseURL) {
		return getFullUrl(baseURL, m3u8TSURL);
	}

	public String getM3u8TSKeyURL() {
		return m3u8TSKeyURL;
	}

	public String getM3u8TSKKeyFullURL(String baseURL) {
		return getFullUrl(baseURL, m3u8TSKeyURL);
	}

	public static final String getFullUrl(String baseURL, String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		} else if (path.startsWith("http")) {
			return path;
		} else if (path.startsWith("//")) {
			return "http:" + path;
		} else {
			return mergeURL(baseURL, path);
		}

	}

	public static String mergeURL(String baseUrl, String tempUrl) {
		String newBaseURL = baseUrl;
		if (!baseUrl.endsWith("/")) {
			newBaseURL = baseUrl + "/";
		}

		String newTempURL = tempUrl;
		if (tempUrl.startsWith("/")) {
			newTempURL = tempUrl.substring(1);
		}

		String[] tempList = newTempURL.split("/");
		if (newBaseURL.contains(tempList[0])) {
			String sameString = newBaseURL.substring(newBaseURL.lastIndexOf(tempList[0]));
			if (newTempURL.contains(sameString)) {
				return newBaseURL + newTempURL.substring(sameString.length());
			}
		}

		return newBaseURL + newTempURL;
	}

	public float getSeconds() {
		return seconds;
	}


	public String getLocalFileName() {
		if (TextUtils.isEmpty(m3u8TSURL)) {
			return "error" + FILE_EXTENTION;
		} else {
			return MD5.getMd5(m3u8TSURL).concat(FILE_EXTENTION);
		}
	}

	public String getLocalKeyName() {
		if (TextUtils.isEmpty(m3u8TSKeyURL)) {
			return "";
		} else {
			return MD5.getMd5(m3u8TSKeyURL);
		}
	}


	@Override
	public int compareTo(M3U8TSInfo o) {
		return m3u8TSURL.compareTo(o.m3u8TSURL);
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "M3U8TSInfo{" + "m3u8TSURL='" + m3u8TSURL + '\'' + ", m3u8TSKeyURL='" + m3u8TSKeyURL + '\'' + ", seconds=" + seconds + '}';
	}
}
