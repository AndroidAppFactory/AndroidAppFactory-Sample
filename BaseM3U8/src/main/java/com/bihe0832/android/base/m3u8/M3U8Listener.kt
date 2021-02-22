package com.bihe0832.android.base.m3u8

/**
 *
 * @author hardyshi code@bihe0832.com Created on 2/22/21.
 *
 */
interface M3U8Listener {

    /**
     * 任务下载失败通知，在任务下载失败后回调
     * 根据需要更新任务的UI状态显示，如:任务状态显示为”已失败“
     */
    fun onFail(errorCode: Int, msg: String)

    fun onComplete()

    fun onProcess(finished: Int, total: Int)
}