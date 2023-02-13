package com.bihe0832.android.base.m3u8

import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.utils.encrypt.MD5
import java.io.File

/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2021/3/27.
 * Description: Description
 *
 */
object M3U8ModuleManager {

    fun getBasePath(): String {
        return FileUtils.getFolderPathWithSeparator(ZixieContext.getZixieFolder())
    }

    fun getDownloadPath(m3u8Url: String): String {
        return FileUtils.getFolderPathWithSeparator(getBasePath() + MD5.getMd5(m3u8Url) + File.separator)
    }

    fun getFinalVideoPath(m3u8Url: String): String {
        val finalOutPutFile = getBasePath() + "pictures" + File.separator + "m3u8" + File.separator + MD5.getMd5(m3u8Url) + ".mp4"
        FileUtils.checkAndCreateFolder(File(finalOutPutFile).parentFile.absolutePath)
        return finalOutPutFile
    }
}