package com.bihe0832.android.app.file

import android.content.Context
import com.bihe0832.android.lib.download.DownloadErrorCode
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.DownloadListener
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.log.ZLog
import java.io.File

/**
 *
 *   所有cache目录的内容，都会在清除缓存时清空
 *   temp目录的内容仅保留30天，超过30天会自动清除
 *   user目录保存与具体用户相关的所有信息
 *
 *   目前暂时没有区分个人与会话，后续，个人录制、拍摄的单独存储，会话收到的以会话为单位分组存储，所有KH相关的IM群组一律都设置为临时存储
 */
object AAFDownload {

    private const val TAG = "AAFDownload"

    fun download(context: Context, url: String, finalPath: String, downloadListener: DownloadListener?) {
        if (File(finalPath).exists()) {
            FileUtils.deleteDirectory(File(finalPath))
            FileUtils.deleteFile(finalPath)
        }
        try {
            DownloadFile.download(context, url, File(finalPath).parent, downloadListener = object : SimpleDownloadListener() {
                override fun onComplete(downloadFilePath: String, item: DownloadItem) {
                    if (downloadFilePath.equals(finalPath)) {
                        ZLog.d(TAG, "download $url success")
                        downloadListener?.onComplete(finalPath, item)
                    } else {
                        FileUtils.copyFile(File(downloadFilePath), File(finalPath), false).let {
                            if (it) {
                                ZLog.d(TAG, "download $url success")
                                downloadListener?.onComplete(finalPath, item)
                            } else {
                                ZLog.d(TAG, "download $url failed: rename failed $downloadFilePath $finalPath")
                                downloadListener?.onFail(DownloadErrorCode.ERR_FILE_RENAME_FAILED, "download success and rename failed", item)
                            }
                        }
                    }

                }

                override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                    ZLog.d(TAG, "download $url failed: $errorCode $msg")
                    downloadListener?.onFail(errorCode, msg, item)
                }

                override fun onProgress(item: DownloadItem) {
                    downloadListener?.onProgress(item)
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 不检测4G，直接下载
    fun startDownload(context: Context, url: String, finalPath: String) {

        try {
            if (File(finalPath).exists()) {
                FileUtils.deleteDirectory(File(finalPath))
                FileUtils.deleteFile(finalPath)
            }
            DownloadFile.download(context, url, File(finalPath).parent, downloadListener = object : SimpleDownloadListener() {
                override fun onComplete(downloadFilePath: String, item: DownloadItem) {
                    if (downloadFilePath.equals(finalPath)) {
                        ZLog.d(TAG, "download $url success")
                    } else {
                        FileUtils.copyFile(File(downloadFilePath), File(finalPath), false).let {
                            if (it) {
                                ZLog.d(TAG, "download $url success")
                            } else {
                                ZLog.d(TAG, "download $url failed: rename failed $downloadFilePath $finalPath")
                            }
                        }
                    }
                }

                override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                    ZLog.d(TAG, "download $url failed: $errorCode $msg")

                }

                override fun onProgress(item: DownloadItem) {

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun pauseAll() {
        DownloadUtils.pauseAll()
    }
}