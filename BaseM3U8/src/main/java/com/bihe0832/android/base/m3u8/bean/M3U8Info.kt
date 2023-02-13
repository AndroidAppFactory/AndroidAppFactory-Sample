package com.bihe0832.android.base.m3u8.bean

import com.bihe0832.android.base.m3u8.M3U8ModuleManager
import com.bihe0832.android.lib.file.FileUtils.getFileLength
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author zixie code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
class M3U8Info {

    //Header
    var header = ""

    //下载ts的基础URL
    private var baseURL = ""

    //m3u8文件URL
    private var m3u8URL = ""

    //更新时间
    private var downloadTime: Long = 0

    //文件总大小
    private var fileSize: Long = 0

    //总时间，单位毫秒
    private var totalTime: Long = 0

    //视频切片
    private val tsList: CopyOnWriteArrayList<M3U8TSInfo> = CopyOnWriteArrayList()

    // 实际去重以后得切片数量
    private var mTsSize = 0

    fun getBaseURL(): String {
        return baseURL
    }

    fun setBaseURL(baseURL: String) {
        this.baseURL = baseURL!!
    }

    fun getM3u8URL(): String {
        return m3u8URL
    }

    fun setM3u8URL(m3u8URL: String) {
        this.m3u8URL = m3u8URL
    }

    fun getDownloadTime(): Long {
        return downloadTime
    }

    fun setDownloadTime(downloadTime: Long) {
        this.downloadTime = downloadTime
    }

    fun getFileSize(): Long {
        fileSize = 0
        for (m3U8Ts in tsList) {
            fileSize += m3U8Ts.fileSize
        }
        return fileSize
    }

    fun getFormatFileSize(): String? {
        return getFileLength(getFileSize())
    }

    fun getTsList(): List<M3U8TSInfo> {
        return tsList
    }

    fun getTsSize(): Int {
        if (mTsSize == 0) {
            mTsSize = this.tsList.distinctBy { it.m3u8TSURL }.size
        }
        return mTsSize
    }

    fun addTs(ts: M3U8TSInfo) {
        this.tsList.add(ts)
        this.mTsSize = this.tsList.distinctBy { it.m3u8TSURL }.size
    }

    fun getTotalTime(): Long {
        totalTime = 0
        for (m3U8Ts in tsList) {
            totalTime += (m3U8Ts.seconds * 1000).toInt()
        }
        return totalTime
    }

    fun isOK(): Boolean {
        return tsList.isNotEmpty()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("视频信息: \n\n")
        sb.append("m3u8URL: ").append(m3u8URL).append("\n\n")
        sb.append("baseURL: ").append(baseURL).append("\n\n")
        sb.append("保存地址: ").append(M3U8ModuleManager.getDownloadPath(m3u8URL)).append("\n\n")
        sb.append("视频地址: ").append(M3U8ModuleManager.getFinalVideoPath(m3u8URL)).append("\n\n")
        sb.append("分片数量: ").append(tsList.size).append(",").append("实际分片数量: ").append(getTsSize()).append("\n\n")
        if (tsList.isNotEmpty()) {
            sb.append("第一分片信息: ").append(tsList.get(0).toString())
        }
        return sb.toString()
    }


    override fun equals(other: Any?): Boolean {
        if (other is M3U8Info) {
            return m3u8URL != null && m3u8URL == other.m3u8URL
        }
        return false
    }
}