package com.bihe0832.android.base.test;

import com.bihe0832.android.lib.file.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
public class M3U8Info {
    //下载ts的基础URL
    private String baseURL = "";
    //m3u8文件URL
    private String m3u8URL;
    //文件总大小
    private long fileSize;
    //总时间，单位毫秒
    private long totalTime;
    //视频切片
    private List<M3U8TSInfo> tsList = new ArrayList<M3U8TSInfo>();

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getM3u8URL() {
        return m3u8URL;
    }

    public void setM3u8URL(String m3u8URL) {
        this.m3u8URL = m3u8URL;
    }


    public long getFileSize() {
        fileSize = 0;
        for (M3U8TSInfo m3U8Ts : tsList) {
            fileSize = fileSize + m3U8Ts.getFileSize();
        }
        return fileSize;
    }

    public String getFormatFileSize() {
        return FileUtils.INSTANCE.getFileLength(getFileSize());
    }

    public List<M3U8TSInfo> getTsList() {
        return tsList;
    }

    public void addTs(M3U8TSInfo ts) {
        this.tsList.add(ts);
    }

    public long getTotalTime() {
        totalTime = 0;
        for (M3U8TSInfo m3U8Ts : tsList) {
            totalTime = totalTime + (int) (m3U8Ts.getSeconds() * 1000);
        }
        return totalTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("m3u8URL: " + m3u8URL);
        sb.append("\nbaseURL: " + baseURL);
        sb.append("\nfileSize: " + getFileSize());
        sb.append("\ntotalTime: " + totalTime);

        for (M3U8TSInfo ts : tsList) {
            sb.append("\nts: " + ts);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof M3U8Info) {
            M3U8Info m3U8 = (M3U8Info) obj;
            return m3u8URL != null && m3u8URL.equals(m3U8.m3u8URL);
        }
        return false;
    }
}
