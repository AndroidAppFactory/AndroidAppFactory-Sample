package com.bihe0832.android.base.m3u8.tools

import android.content.Context
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.bihe0832.android.base.m3u8.M3U8Listener
import com.bihe0832.android.base.m3u8.M3U8ModuleManager
import com.bihe0832.android.base.m3u8.bean.M3U8Info
import com.bihe0832.android.base.m3u8.bean.M3U8TSInfo
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.core.DownloadManager
import com.bihe0832.android.lib.download.core.list.DownloadingList
import com.bihe0832.android.lib.download.dabase.DownloadInfoDBManager
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.DownloadTools
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.ui.dialog.impl.DialogUtils
import com.bihe0832.android.lib.utils.time.DateUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2023/2/10.
 * Description: Description
 *
 */
open class M3U8DownloadImpl(private val context: Context, private val mM3U8Listener: M3U8Listener) {

    private val TAG = "M3U8DownloadImpl"
    private var lastStart = 0L
    private var lastNotify = 0L
    private var mM3U8Info: M3U8Info? = null
    private val MAX_DOWNLOAD = 10
    private var hasStop = true
    private var mDownloadTSURLList = ConcurrentHashMap<String, Boolean>()
    private var mDownloadKeyURLList = ConcurrentHashMap<String, Boolean>()
    private val mDownloadListener = object : SimpleDownloadListener() {

        fun notifyFinish(url: String, filePath: String) {
            ZLog.d(TAG, "notifyFinish-> $filePath")
            if (filePath.endsWith(M3U8TSInfo.FILE_EXTENTION)) {
                mDownloadTSURLList[url] = true
                startNew(0)
            }
            msgHandler.sendEmptyMessageDelayed(MSG_TYPE_NOTIFY, 0)
        }

        override fun onComplete(filePath: String, item: DownloadItem): String {
            ZLog.w(TAG, "onComplete-> $filePath")
            notifyFinish(item.downloadURL, filePath)
            DownloadInfoDBManager.clearDownloadInfoByID(item.downloadID)
            return filePath
        }

        override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
            ZLog.d(TAG, "onFail-> errorCode:$errorCode, msg:$msg ")
            mM3U8Listener.onFail(errorCode, msg)
            startNew(100)
        }

        override fun onProgress(item: DownloadItem) {
        }
    }

    private val MSG_TYPE_START = 1
    private val MSG_TYPE_NOTIFY = 2
    private val msgHandler = object : Handler(ThreadManager.getInstance().getLooper(ThreadManager.LOOPER_TYPE_NORMAL)) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_TYPE_START -> {
                    if (!hasStop && DownloadingList.getDownloadingNum() < MAX_DOWNLOAD) {
                        mM3U8Info?.getTsList()?.filter { !mDownloadTSURLList.containsKey(it.getM3u8TSFullURL(mM3U8Info?.getBaseURL())) }?.let {
                            it.firstOrNull()?.let { item ->
                                if (!mDownloadTSURLList.containsKey(item.getM3u8TSFullURL(mM3U8Info?.getBaseURL()))) {
                                    addNewItem(item)
                                }
                            }
                        }
                    } else {
                        startNew(50)
                    }
                }

                MSG_TYPE_NOTIFY -> {
                    notifyProcess()
                }
            }
        }
    }

    private fun notifyProcess() {
        if (System.currentTimeMillis() - lastNotify > 50) {
            lastNotify = System.currentTimeMillis()
            var finished = mDownloadTSURLList.values.size
            mM3U8Listener.onProcess(finished, mM3U8Info?.getTsSize() ?: 1)
            if (finished == mM3U8Info?.getTsSize()) {
                ZixieContext.showToast("下载完成")
                mM3U8Listener.onComplete()
            }
        } else {
            msgHandler.sendEmptyMessageDelayed(MSG_TYPE_START, 100)
        }
    }

    private fun startNew(delay: Int) {
        if (!hasStop) {
            msgHandler.sendEmptyMessageDelayed(MSG_TYPE_START, delay.toLong())
        }
    }

    @Synchronized
    private fun addNewItem(tsInfo: M3U8TSInfo) {
        if (!hasStop && System.currentTimeMillis() - lastStart > 100) {
            lastStart = System.currentTimeMillis()
            var fileDir = M3U8ModuleManager.getDownloadPath(mM3U8Info?.getM3u8URL() ?: "")
            if (!mDownloadTSURLList.containsKey(tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL()))) {
                (fileDir + tsInfo.localFileName).let {
                    if (!FileUtils.checkFileExist(it)) {
                        ZLog.d(TAG, "addNewItem-> ${tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL())}")
                        ZLog.d(TAG, "addNewItem-> ${FileUtils.checkFileExist(it)} $it")
                        DownloadFile.download(context, tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL()), it, true, null)
                        downloadKey(tsInfo, fileDir)
                    } else {
                        ZLog.d(TAG, "addNewItem->  skip ${tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL())}")
                        mDownloadListener.notifyFinish(tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL()), it)
                    }
                }
            }
        } else {
            startNew(50)
        }
    }

    private fun downloadKey(tsInfo: M3U8TSInfo, fileDir: String) {
        if (!TextUtils.isEmpty(tsInfo.m3u8TSKeyURL)) {
            val localKey = fileDir + tsInfo.localKeyName
            val finalURL = tsInfo.getM3u8TSKKeyFullURL(mM3U8Info?.getBaseURL())
            if (!mDownloadKeyURLList.containsKey(finalURL) || !FileUtils.checkFileExist(localKey)) {
                mDownloadKeyURLList[finalURL] = true
                DownloadFile.download(context, "", "", finalURL, localKey, true, "", "", forceDownloadNew = true, UseMobile = true, downloadListener = null)
            }
        }
    }

    fun startDownload(m3u8: M3U8Info, needDeleteOld: Boolean) {
        mM3U8Info = m3u8
        cancelDownload()
        ThreadManager.getInstance().start({
            mDownloadTSURLList.clear()
            DownloadTools.addGlobalDownloadListener(mDownloadListener)
            hasStop = false
            mM3U8Info?.let { m3U8Info ->
                File(M3U8ModuleManager.getDownloadPath(m3U8Info.getM3u8URL())).let { folder ->
                    folder.listFiles().filter { it.absolutePath.endsWith(M3U8TSInfo.FILE_EXTENTION) && it.length() < 100 }.forEach { file ->
                        ZLog.d(TAG, "startDownload->  remove bad file ${file.absolutePath} ${file.length()}")
                        FileUtils.deleteFile(file.absolutePath)
                    }
                    if (needDeleteOld) {
                        folder.listFiles().filter { it.absolutePath.endsWith(M3U8TSInfo.FILE_EXTENTION) }.sortedBy { it.length() }.take(5).forEach { file ->
                            ZLog.d(TAG, "startDownload->  remove old small ${file.absolutePath} ${file.length()}")
                            FileUtils.deleteFile(file.absolutePath)
                        }

                        folder.listFiles().filter { it.absolutePath.endsWith(M3U8TSInfo.FILE_EXTENTION) }.sortedByDescending { it.lastModified() }.take(5).forEach { file ->
                            ZLog.d(TAG, "startDownload->  remove old last modify file ${file.absolutePath} ${DateUtil.getDateEN(file.lastModified())}")
                            if (file.endsWith(M3U8TSInfo.FILE_EXTENTION)) {
                                FileUtils.deleteFile(file.absolutePath)
                            }
                        }
                    }
                }

                if (m3U8Info.getTsSize() > MAX_DOWNLOAD) {
                    MAX_DOWNLOAD
                } else {
                    m3U8Info.getTsSize()
                }.let {
                    for (i in 0..it) {
                        startNew(0)
                    }
                }

            }
        }, 1)
    }

    fun cancelDownload() {
        hasStop = true
        DownloadTools.removeGlobalDownloadListener(mDownloadListener)
        DownloadUtils.pauseAll()
    }
}