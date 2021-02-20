package com.bihe0832.android.test.module

import android.view.View
import com.bihe0832.android.app.about.AboutActivity
import com.bihe0832.android.base.test.M3U8Tools
import com.bihe0832.android.common.test.base.BaseTestFragment
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.file.ZixieFileProvider.*
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.utils.encypt.MD5
import com.bihe0832.android.lib.utils.intent.IntentUtils
import java.io.File


class
TestDebugTempFragment : BaseTestFragment() {
    val LOG_TAG = "TestDebugTempFragment"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestItemData("简单测试函数", View.OnClickListener { testFunc() }))
            add(TestItemData("通用测试预处理", View.OnClickListener { preTest() }))
            add(TestItemData("测试自定义请求", View.OnClickListener { testOneRequest() }))
            add(TestItemData("默认关于页", View.OnClickListener { startActivity(AboutActivity::class.java) }))
            add(TestItemData("APP设置", View.OnClickListener { IntentUtils.startAppDetailSettings(context) }))
        }
    }

    private fun testOneRequest() {
    }

    private fun preTest() {

    }

    private fun testFunc() {
        val url = "http://1257120875.vod2.myqcloud.com/0ef121cdvodtransgzp1257120875/3055695e5285890780828799271/v.f230.m3u8"
        val bastpath = url.substring(0, url.lastIndexOf("/") + 1)
        val path = getZixieFilePath(context!!) + MD5.getMd5(url) + File.separator
        DownloadFile.startDownload(context!!, url, path + FileUtils.getFileName(url), object : SimpleDownloadListener() {
            override fun onComplete(filePath: String, item: DownloadItem) {
                var a = M3U8Tools.parseIndex(bastpath, filePath)
                M3U8Tools.downloadM3U8(context!!, bastpath, path, a)
            }

            override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                ZLog.d(errorCode.toString())
            }

            override fun onProgress(item: DownloadItem) {
                ZLog.d(item.toString())
            }

        })
    }
}