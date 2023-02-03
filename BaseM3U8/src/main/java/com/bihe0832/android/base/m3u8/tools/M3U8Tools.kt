package com.bihe0832.android.base.m3u8.tools

import android.content.Context
import android.text.TextUtils
import com.bihe0832.android.app.file.AAFDownload
import com.bihe0832.android.base.m3u8.M3U8Listener
import com.bihe0832.android.base.m3u8.bean.M3U8Info
import com.bihe0832.android.base.m3u8.bean.M3U8TSInfo
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.timer.BaseTask
import com.bihe0832.android.lib.timer.TaskManager
import com.bihe0832.android.lib.utils.encrypt.AESUtils
import java.io.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author zixie code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
object M3U8Tools {

    private val MAX_DOWNLOAD = 10
    private val NAME = "M3U8DownloadProcess"
    private var hasStop = true
    private var mGlobalDownloadListener: SimpleDownloadListener? = null

    fun parseIndex(m3u8URL: String, baseURL: String, filePath: String): M3U8Info {
        return M3U8Info().apply {
            this.setBaseURL(baseURL)
            this.setM3u8URL(m3u8URL)
            this.setDownloadTime(System.currentTimeMillis())
            try {
                val inputStream: InputStream = File(filePath).inputStream()
                var seconds = 0f
                var key = ""
                inputStream.bufferedReader().useLines { lines ->
                    lines.forEach {
                        var line = it
                        if (line.startsWith("#")) {
                            when {
                                line.startsWith("#EXTINF:") -> {
                                    line = line.substring(8)
                                    if (line.endsWith(",")) {
                                        line = line.substring(0, line.length - 1)
                                    }
                                    seconds = line.toFloat()
                                }
                                line.startsWith("#EXT-X-DISCONTINUITY") -> {
                                    key = ""
                                }
                                line.startsWith("#EXT-X-KEY:") -> {
                                    key = when {
                                        line.contains("METHOD=AES-128") -> {
                                            line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""))
                                        }
                                        line.contains("METHOD=NONE") -> {
                                            ""
                                        }
                                        else -> {
                                            ""
                                        }
                                    }
                                }
                            }
                        } else {
                            addTs(M3U8TSInfo(line, key, seconds))
                            seconds = 0f
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadM3U8(context: Context, baseURL: String, fileDir: String, info: M3U8Info, listener: M3U8Listener) {
        if (!hasStop) {
            return
        }
        hasStop = false
        var downloadListener = object : SimpleDownloadListener() {

            private var mDownloadTSURLList = ConcurrentHashMap<String, Boolean>()

            @Synchronized
            fun addNewItem(tsInfo: M3U8TSInfo) {
                if (!downloadItemList().containsKey(tsInfo.getM3u8TSFullURL(baseURL))) {
                    AAFDownload.startDownload(context!!, tsInfo.getM3u8TSFullURL(baseURL), fileDir + tsInfo.localFileName)
                    if (!TextUtils.isEmpty(tsInfo.m3u8TSKeyURL)) {
                        AAFDownload.startDownload(context!!, tsInfo.getM3u8TSFullURL(baseURL), fileDir + tsInfo.localKeyName)
                    }
                }
            }

            @Synchronized
            fun downloadItemList(): ConcurrentHashMap<String, Boolean> {
                return mDownloadTSURLList
            }

            fun startNew() {
                if (!hasStop && DownloadUtils.getDownloading().size < MAX_DOWNLOAD) {
                    info.getTsList().filter { !downloadItemList().containsKey(it.getM3u8TSFullURL(baseURL)) }.let {
                        it.shuffled().firstOrNull()?.let { item ->
                            if (!downloadItemList().containsKey(item.getM3u8TSFullURL(baseURL))) {
                                addNewItem(item)
                            }

                        }
                    }
                }
            }

            override fun onComplete(filePath: String, item: DownloadItem) {
                if (filePath.endsWith(M3U8TSInfo.FILE_EXTENTION)) {
                    downloadItemList()[item.downloadURL] = true
                    startNew()
                }

            }

            override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                listener.onFail(errorCode, msg)
                startNew()
            }

            override fun onProgress(item: DownloadItem) {
            }

            fun notifyProcess() {
                var finished = downloadItemList().values.size
                listener.onProcess(finished, info.getTsSize())
            }
        }
        if (mGlobalDownloadListener != null) {
            DownloadUtils.removeDownloadListener(mGlobalDownloadListener)
        }
        mGlobalDownloadListener = downloadListener
        DownloadUtils.addDownloadListener(mGlobalDownloadListener)
        ThreadManager.getInstance().start {
            if (info.getTsSize() > MAX_DOWNLOAD) {
                MAX_DOWNLOAD
            } else {
                info.getTsSize()
            }.let {
                for (i in 0..it) {
                    downloadListener.startNew()
                }
            }
            TaskManager.getInstance().addTask(object : BaseTask() {

                override fun getMyInterval(): Int {
                    return 2 * 1
                }

                override fun getNextEarlyRunTime(): Int {
                    return 0
                }

                override fun run() {
                    var finished = downloadListener.downloadItemList().size
                    downloadListener.notifyProcess()
                    if (finished == info.getTsSize()) {
                        ZixieContext.showToast("下载完成")
                        listener.onComplete()
                        TaskManager.getInstance().removeTask(NAME)
                    }
                }

                override fun getTaskName(): String {
                    return NAME
                }
            })
        }

    }

    fun cancelDownload() {
        hasStop = true
        DownloadUtils.pauseAll()
        TaskManager.getInstance().removeTask(NAME)
    }

    fun generateLocalM3U8(m3u8Dir: String, m3U8: M3U8Info): File {
        var localM3u8 = File(m3u8Dir, "local.m3u8")
        localM3u8.let {
            val bfw = BufferedWriter(FileWriter(it, false))
            bfw.write("#EXTM3U\n")
            bfw.write("#EXT-X-VERSION:3\n")
            bfw.write("#EXT-X-MEDIA-SEQUENCE:0\n")
            bfw.write("#EXT-X-TARGETDURATION:13\n")
            for (m3U8Ts in m3U8.getTsList()) {
                if (!TextUtils.isEmpty(m3U8Ts.m3u8TSKeyURL)) {
                    bfw.write("#EXT-X-KEY:METHOD=AES-128,URI=\"${m3U8Ts.localKeyName}\"\n")
                }
                bfw.write("#EXTINF:${m3U8Ts.seconds},\n")
                bfw.write(m3U8Ts.localFileName)
                bfw.newLine()
            }
            bfw.write("#EXT-X-ENDLIST")
            bfw.flush()
            bfw.close()
        }
        return localM3u8
    }

    fun mergeM3U8(m3u8Dir: String, videoName: String, listener: M3U8Listener) {
        var a = parseIndex("", "", m3u8Dir + "local.m3u8")
        if (!a.isOK()) {
            listener.onFail(-1, "local.m3u8 not exist。 请先点击解析 M3U8")
        }
        ThreadManager.getInstance().start {
            try {
                val finalOutPutFile = File(videoName)
                if (finalOutPutFile.exists()) {
                    finalOutPutFile.delete()
                }
                FileUtils.checkAndCreateFolder(finalOutPutFile.parentFile.absolutePath)
                finalOutPutFile.createNewFile()

                val fileOutputStream = FileOutputStream(finalOutPutFile, true)
                for (i in 0 until a.getTsSize()) {
                    try {
                        var ts = a.getTsList()[i]
                        File(m3u8Dir + ts.m3u8TSURL).let { file ->
                            ZLog.d("分片信息：$ts")
                            if (file.exists()) {
                                val fileInputStream = FileInputStream(file)
                                val b = ByteArray(4096)
                                var size = -1
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                while (fileInputStream.read(b, 0, b.size)?.also { size = it } != -1) {
                                    byteArrayOutputStream.write(b, 0, size)
                                }
                                fileInputStream.close()
                                val bytes: ByteArray = byteArrayOutputStream.toByteArray()
                                byteArrayOutputStream.close()

                                var newbyte = if (!TextUtils.isEmpty(ts.m3u8TSKeyURL)) {
                                    val keyData = FileUtils.getFileContent(m3u8Dir + ts.m3u8TSKeyURL).trim()
                                    AESUtils.decryptWithoutIV(keyData.toByteArray(), bytes)
                                } else {
                                    bytes
                                }
                                if (newbyte != null) {
                                    fileOutputStream.write(newbyte)
                                }
                                listener.onProcess(i, a.getTsSize())
                            } else {
                                ZLog.d("分片尚未下载：$ts")
                            }
                        }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                ZixieContext.showDebug("视频合并已经完成")
                listener.onComplete()
                if (fileOutputStream != null) {
                    fileOutputStream.close()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                listener.onFail(-2, e.toString())
            }
            listener.onComplete()
        }
    }
}