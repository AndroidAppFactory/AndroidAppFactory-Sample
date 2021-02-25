package com.bihe0832.android.base.m3u8.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bihe0832.android.lib.utils.encypt.MD5;


/**
 * @author hardyshi code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
public class M3U8TSInfo implements Comparable<M3U8TSInfo> {
    private String m3u8TSURL;
    private String m3u8TSKeyURL;
    private long fileSize;
    private float seconds;

    public M3U8TSInfo(String m3u8TSURL, String key, float seconds) {
        this.m3u8TSURL = m3u8TSURL;
        this.m3u8TSKeyURL = key;
        this.seconds = seconds;
    }

    public String getM3u8TSURL() {
        return m3u8TSURL;
    }

    public float getSeconds() {
        return seconds;
    }

    public String getM3u8TSKeyURL() {
        return m3u8TSKeyURL;
    }

    public String getLocalFileName() {
        if (TextUtils.isEmpty(m3u8TSURL)) {
            return "error.ts";
        } else {
            return MD5.getMd5(m3u8TSURL).concat(".ts");
        }
    }

    public String getLocalKeyName() {
        if (TextUtils.isEmpty(m3u8TSKeyURL)) {
            return "";
        } else {
            return MD5.getMd5(m3u8TSKeyURL);
        }
    }


    public String getFullUrl(String baseURL, String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        if (path.startsWith("http")) {
            return path;
        } else if (path.startsWith("//")) {
            return "http:".concat(path);
        } else {
            if (baseURL.endsWith("/")) {
                return baseURL + path;
            } else {
                return baseURL + "/" + path;
            }
        }
    }

    @Override
    public int compareTo(@NonNull M3U8TSInfo o) {
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
        return "M3U8TSInfo{" +
                "m3u8TSURL='" + m3u8TSURL + '\'' +
                ", m3u8TSKeyURL='" + m3u8TSKeyURL + '\'' +
                ", seconds=" + seconds +
                '}';
    }
}
