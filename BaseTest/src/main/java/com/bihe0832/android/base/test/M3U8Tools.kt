package com.bihe0832.android.base.test

import android.content.Context
import android.text.TextUtils
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.utils.encypt.AESUtils
import java.io.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2021/2/20.
 * Description: Description
 */
object M3U8Tools {

    fun parseIndex(baseURL: String, filePath: String): M3U8Info {
        return M3U8Info().apply {
            this.baseURL = baseURL
            try {
                val reader = BufferedReader(InputStreamReader(FileInputStream(File(filePath))))
                var line: String
                var seconds = 0f
                var key = ""
                while (reader.readLine().also { line = it } != null) {
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
                        continue
                    }
                    addTs(M3U8TSInfo(line, key, seconds))
                    seconds = 0f
                }
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadM3U8(context: Context, baseURL: String, fileDir: String, info: M3U8Info) {
        var downItem = object : SimpleDownloadListener() {

            private var mDownloadList = ConcurrentHashMap<String, Boolean>()

            fun addNewItem(localFileName: String) {
                if (!mDownloadList.contains(localFileName)) {
                    mDownloadList.put(localFileName, false)
                }
            }

            override fun onComplete(filePath: String, item: DownloadItem) {
                mDownloadList.put(FileUtils.getFileName(filePath), true)
                var hasFinished = true
                mDownloadList.values.forEach {
                    if (!it) hasFinished = false
                }
                if (hasFinished) {
                    ZixieContext.showToast("下载完成")
                    generateLocalM3U8(fileDir, info)
                    mergeM3U8(fileDir)
                }
            }

            override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
            }

            override fun onProgress(item: DownloadItem) {
            }

        }
        DownloadUtils.addDownloadListener(downItem)

        info.tsList.forEach {
            DownloadFile.startDownload(context!!, it.getFullUrl(baseURL, it.m3u8TSURL), fileDir + it.localFileName, null)
            downItem.addNewItem(it.localFileName)
            if (!TextUtils.isEmpty(it.m3u8TSKeyURL)) {
                DownloadFile.startDownload(context!!, it.getFullUrl(baseURL, it.m3u8TSKeyURL), fileDir + it.localKeyName, null)
                downItem.addNewItem(it.localFileName)
            }
        }
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

    fun mergeM3U8(path: String) {
        val finalOutPutFile = File(path + "a.mp4")
        if(finalOutPutFile.exists()){
            finalOutPutFile.delete()
        }
        var a = parseIndex("", path + "local.m3u8")
        try {
            val fileOutputStream = FileOutputStream(finalOutPutFile, true)
            for (i in 0 until a.tsList.size) {
                try {
                    var ts = a.tsList[i]
                    val fileInputStream = FileInputStream(File(path + ts.m3u8TSURL))
                    val b = ByteArray(4096)
                    var size = -1
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    while (fileInputStream.read(b, 0, b.size).also { size = it } != -1) {
                        byteArrayOutputStream.write(b, 0, size)
                    }
                    fileInputStream.close()
                    val bytes: ByteArray = byteArrayOutputStream.toByteArray()
                    byteArrayOutputStream.close()

                    var newbyte = if (!TextUtils.isEmpty(ts.m3u8TSKeyURL)) {
                        AESUtils.decryptWithoutIV(FileUtils.getFileContent(path + ts.m3u8TSKeyURL).toByteArray(), bytes)
                    } else {
                        bytes
                    }
                    fileOutputStream.write(newbyte)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            if (fileOutputStream != null) {
                fileOutputStream.close()
            }
            ZixieContext.showDebug("视频合并已经完成")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}