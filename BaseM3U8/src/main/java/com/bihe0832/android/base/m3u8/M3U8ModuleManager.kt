package com.bihe0832.android.base.m3u8

import android.content.Context
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.utils.encrypt.messagedigest.MD5
import java.io.File

/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2021/3/27.
 * Description: Description
 *
 */
object M3U8ModuleManager {

    private var mApplicationContext: Context? = null
    private var hasInit = false

    fun init(context: Context) {
        mApplicationContext = context
        if (hasInit) {
            return
        }
        hasInit = true
        DownloadUtils.init(context, 30, ZixieContext.isDebug())
        M3U8DBManager.init(context)
    }

    fun getBasePath(): String {
        return FileUtils.getFolderPathWithSeparator(ZixieContext.getZixieFolder())
    }

    fun getDownloadPath(m3u8Url: String): String {
        return FileUtils.getFolderPathWithSeparator(getBasePath() + MD5.getMd5(m3u8Url) + File.separator)
    }

    fun getFinalVideoPath(m3u8Url: String): String {
        val finalOutPutFile =
            getBasePath() + "pictures" + File.separator + "m3u8" + File.separator + getFinalVideoName(m3u8Url)
        FileUtils.checkAndCreateFolder(File(finalOutPutFile).parentFile.absolutePath)
        return finalOutPutFile
    }

    fun getFinalVideoName(m3u8Url: String): String {
        return MD5.getMd5(m3u8Url) + ".mp4"
    }
}
