package com.bihe0832.android.base.m3u8

import android.os.Environment
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.file.ZixieFileProvider
import com.bihe0832.android.lib.utils.encrypt.MD5
import java.io.File

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2021/3/27.
 * Description: Description
 *
 */
object M3U8ModuleManager {

    fun getBasePath(): String {
        var path = ZixieFileProvider.getZixieFilePath(ZixieContext.applicationContext!!) + "m3u8" + File.separator
        FileUtils.checkAndCreateFolder(path)
        return path
    }

    fun getDownloadPath(m3u8Url: String): String {
        var path = getBasePath() + MD5.getMd5(m3u8Url) + File.separator
        FileUtils.checkAndCreateFolder(path)
        return path
    }

    fun getFinalVideoPath(m3u8Url: String): String {
        val finalOutPutFile = Environment.getExternalStorageDirectory().absolutePath + File.separator + "zixie" + File.separator + "pictures" + File.separator + "m3u8" + File.separator + MD5.getMd5(m3u8Url) + ".mp4"
        FileUtils.checkAndCreateFolder(File(finalOutPutFile).parentFile.absolutePath)
        return finalOutPutFile
    }
}