package com.bihe0832.android.test.module

import com.bihe0832.android.app.log.AAFLoggerFile
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.common.test.log.SectionDataContent
import com.bihe0832.android.common.test.log.SectionDataHeader
import com.bihe0832.android.common.test.log.TestLogActivity
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.log.LoggerFile.getZixieFileLogPathByModule
import com.bihe0832.android.framework.router.RouterInterrupt.MODULE_NAME_ROUTER
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.file.select.FileSelectTools


class TestLogActivity : TestLogActivity() {

    override fun getTempData(): List<CardBaseModule> {
        return mutableListOf<CardBaseModule>().apply {
            add(SectionDataHeader("通用日志工具"))
            add(TestItemData("选择并发送单个日志") { FileSelectTools.openFileSelect(this@TestLogActivity, ZixieContext.getLogFolder()) })
            add(SectionDataHeader("基础通用日志"))
            add(SectionDataContent("路由跳转", getZixieFileLogPathByModule(MODULE_NAME_ROUTER)))
            add(SectionDataContent("应用更新", getZixieFileLogPathByModule(AAFLoggerFile.MODULE_UPDATE)))
        }
    }

}