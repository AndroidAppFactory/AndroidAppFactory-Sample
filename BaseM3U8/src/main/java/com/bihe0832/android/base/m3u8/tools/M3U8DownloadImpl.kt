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
import com.bihe0832.android.lib.download.core.list.DownloadingList
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.DownloadTools
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.thread.ThreadManager
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
    private val mGlobalDownloadListener = object : SimpleDownloadListener() {

        override fun onComplete(filePath: String, item: DownloadItem): String {
            ZLog.d(TAG, "onComplete-> $filePath")
            if (filePath.endsWith(M3U8TSInfo.FILE_EXTENTION)) {
                mDownloadTSURLList[item.downloadURL] = true
                startNew(0)
                msgHandler.sendEmptyMessageDelayed(MSG_TYPE_NOTIFY, 0)
            }
            return filePath
        }

        override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
            ZLog.d(TAG, "onFail-> errorCode:$errorCode, msg:$msg ")
            mM3U8Listener.onFail(errorCode, msg)
            startNew(1000)
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
                            it.shuffled().firstOrNull()?.let { item ->
                                if (!mDownloadTSURLList.containsKey(item.getM3u8TSFullURL(mM3U8Info?.getBaseURL()))) {
                                    addNewItem(item)
                                }
                            }
                        }
                    } else {
                        startNew(1000)
                    }
                }

                MSG_TYPE_NOTIFY -> {
                    notifyProcess()
                }
            }
        }
    }

    private fun notifyProcess() {
        if (System.currentTimeMillis() - lastNotify > 1000) {
            lastNotify = System.currentTimeMillis()
            var finished = mDownloadTSURLList.values.size
            mM3U8Listener.onProcess(finished, mM3U8Info?.getTsSize() ?: 1)
            if (finished == mM3U8Info?.getTsSize()) {
                ZixieContext.showToast("下载完成")
                mM3U8Listener.onComplete()
            }
        } else {
            msgHandler.sendEmptyMessageDelayed(MSG_TYPE_START, 1000)
        }
    }

    private fun startNew(delay: Int) {
        if (!hasStop) {
            msgHandler.sendEmptyMessageDelayed(MSG_TYPE_START, delay.toLong())
        }
    }

    @Synchronized
    private fun addNewItem(tsInfo: M3U8TSInfo) {
        if (System.currentTimeMillis() - lastStart > 1000) {
            lastStart = System.currentTimeMillis()
            var fileDir = M3U8ModuleManager.getDownloadPath(mM3U8Info?.getM3u8URL() ?: "")
            if (!mDownloadTSURLList.containsKey(tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL()))) {
                ZLog.d(TAG, "addNewItem-> ${tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL())}")
                DownloadFile.download(context, tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL()), fileDir + tsInfo.localFileName, true, null)
                if (!TextUtils.isEmpty(tsInfo.m3u8TSKeyURL)) {
                    DownloadFile.download(context, tsInfo.getM3u8TSFullURL(mM3U8Info?.getBaseURL()), fileDir + tsInfo.localKeyName, true, null)
                }
            }
        } else {
            startNew(1000)
        }
    }

    fun startDownload(m3u8: M3U8Info) {
        mM3U8Info = m3u8
        hasStop = false
        DownloadTools.addGlobalDownloadListener(mGlobalDownloadListener)
        ThreadManager.getInstance().start {
            if (mM3U8Info?.getTsSize() ?: 0 > MAX_DOWNLOAD) {
                mM3U8Info?.getTsSize() ?: 0
            } else {
                mM3U8Info?.getTsSize() ?: 0
            }.let {
                for (i in 0..it) {
                    startNew(0)
                }
            }
        }
    }

    fun cancelDownload() {
        hasStop = true
        DownloadUtils.pauseAll()
        DownloadTools.removeGlobalDownloadListener(mGlobalDownloadListener)
    }
}