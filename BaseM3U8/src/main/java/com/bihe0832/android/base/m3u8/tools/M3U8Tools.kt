package com.bihe0832.android.base.m3u8.tools

import android.content.Context
import android.text.TextUtils
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
 * @author hardyshi code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
object M3U8Tools {
    private val NAME = "M3U8DownloadProcess"
    private var hasStop = false

    fun parseIndex(m3u8URL: String, baseURL: String, filePath: String): M3U8Info {
        return M3U8Info().apply {
            this.baseURL = baseURL
            this.m3u8URL = m3u8URL
            this.downloadTime = System.currentTimeMillis()
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
                                            line.substring(
                                                line.indexOf("\"") + 1,
                                                line.lastIndexOf("\"")
                                            )
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

    fun downloadM3U8(
        context: Context,
        baseURL: String,
        fileDir: String,
        info: M3U8Info,
        listener: M3U8Listener
    ) {
        hasStop = false
        var downItem = object : SimpleDownloadListener() {

            private var mDownloadList = ConcurrentHashMap<String, Boolean>()

            fun addNewItem(localFileName: String) {
                if (!mDownloadList.contains(localFileName)) {
                    mDownloadList.put(localFileName, false)
                }
            }

            fun downloadItemList(): ConcurrentHashMap<String, Boolean> {
                return mDownloadList
            }

            fun startNew(){
                if(!hasStop){
                    info.tsList.find { !mDownloadList.containsKey(it.localFileName) }?.let {
                        addNewItem(it.localFileName)
                        startDownload(
                            context!!,
                            getFullUrl(baseURL, it.m3u8TSURL),
                            fileDir + it.localFileName
                        )
                    }
                }
            }
            override fun onComplete(filePath: String, item: DownloadItem) {
                mDownloadList.put(FileUtils.getFileName(filePath), true)
                startNew()
            }

            override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                listener.onFail(errorCode, msg)
                startNew()
            }

            override fun onProgress(item: DownloadItem) {
            }
        }
        DownloadUtils.addDownloadListener(downItem)
        info.tsList.subList(0,if(info.tsList.size > 10) 10 else info.tsList.size).forEach {
            downItem.addNewItem(it.localFileName)
            startDownload(
                context!!,
                getFullUrl(baseURL, it.m3u8TSURL),
                fileDir + it.localFileName
            )
            if (!TextUtils.isEmpty(it.m3u8TSKeyURL)) {
                downItem.addNewItem(it.localFileName)
                startDownload(
                    context!!,
                    getFullUrl(baseURL, it.m3u8TSKeyURL),
                    fileDir + it.localKeyName
                )

            }
        }
        ThreadManager.getInstance().start {

            TaskManager.getInstance().addTask(object : BaseTask() {

                override fun getMyInterval(): Int {
                    return 2 * 1
                }

                override fun getNextEarlyRunTime(): Int {
                    return 0
                }

                override fun run() {
                    var finished = 0
                    downItem.downloadItemList().values.forEach {
                        if (it) {
                            finished++
                        }
                    }
                    listener.onProcess(finished, info.tsList.size)
                    if (finished == downItem.downloadItemList().size) {
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

    fun cancleDownload() {
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
            for (m3U8Ts in m3U8.tsList) {
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
        try {
            var a = parseIndex("", "", m3u8Dir + "local.m3u8")
            if (a.tsList.isEmpty()) {
                listener.onFail(-1, "local.m3u8 not exist。 请先点击解析 M3U8")
                return
            }
            val finalOutPutFile = File(videoName)
            if (finalOutPutFile.exists()) {
                finalOutPutFile.delete()
            }

            FileUtils.checkAndCreateFolder(finalOutPutFile.parentFile.absolutePath)
            finalOutPutFile.createNewFile()

            val fileOutputStream = FileOutputStream(finalOutPutFile, true)
            for (i in 0 until a.tsList.size) {
                try {
                    var ts = a.tsList[i]
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
                                AESUtils.decryptWithoutIV(
                                    FileUtils.getFileContent(m3u8Dir + ts.m3u8TSKeyURL)
                                        .toByteArray(), bytes
                                )
                            } else {
                                bytes
                            }
                            if (newbyte != null) {
                                fileOutputStream.write(newbyte)
                            }
                            listener.onProcess(i, a.tsList.size)
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
            return
        }
        return listener.onComplete()
    }

    fun getFullUrl(baseURL: String, path: String): String {
        if (TextUtils.isEmpty(path)) {
            return ""
        }
        return when {
            path.startsWith("http") -> {
                path
            }
            path.startsWith("//") -> {
                "http:$path"
            }
            else -> {
                mergeURL(baseURL, path)
            }
        }
    }

    private fun startDownload(context: Context, url: String, filePath: String) {
        DownloadUtils.startDownload(context, DownloadItem().apply {
            downloadURL = url
            if (!TextUtils.isEmpty(filePath)) {
                fileNameWithPath = filePath
            }
            isDownloadWhenUseMobile = true
            setCanDownloadByPart(false)
        }, false)
    }

    fun mergeURL(baseUrl: String, tempUrl: String): String {

        var newBaseURL = if (!baseUrl.endsWith("/")) {
            "$baseUrl/"
        } else {
            baseUrl
        }

        var newTempURL = if (tempUrl.startsWith("/")) {
            tempUrl.substring(1, tempUrl.length)
        } else {
            tempUrl
        }

        val tempList = newTempURL.split("/")
        if (newBaseURL.contains(tempList[0])) {
            var sameString =
                newBaseURL.substring(newBaseURL.lastIndexOf(tempList[0], newBaseURL.length))
            if (newTempURL.contains(sameString)) {
                return newBaseURL + newTempURL.substring(sameString.length, newTempURL.length)
            }
        }

        return newBaseURL + newTempURL

    }
}