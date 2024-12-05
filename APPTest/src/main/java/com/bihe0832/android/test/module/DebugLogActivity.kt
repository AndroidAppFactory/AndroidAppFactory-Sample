package com.bihe0832.android.test.module

import androidx.recyclerview.widget.RecyclerView
import com.bihe0832.android.app.log.AAFLoggerFile
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.log.DebugLogActivity
import com.bihe0832.android.common.debug.log.SectionDataContent
import com.bihe0832.android.common.debug.log.SectionDataHeader
import com.bihe0832.android.common.webview.core.WebViewLoggerFile
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.router.RouterInterrupt
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.file.select.FileSelectTools
import com.bihe0832.android.lib.ui.recycleview.ext.SafeGridLayoutManager


class DebugLogActivity : DebugLogActivity() {
    override fun getLayoutManagerForList(): RecyclerView.LayoutManager {
        return SafeGridLayoutManager(this, 3)
    }

    override fun getTempData(): List<CardBaseModule> {
        return mutableListOf<CardBaseModule>().apply {
            add(SectionDataHeader("通用日志工具"))
            add(getOpenLogItem())
//            add(DebugItemData("上传日志") { })
            add(SectionDataHeader("基础通用日志"))
            add(SectionDataContent("路由跳转", RouterInterrupt.getRouterLogPath()))
            add(SectionDataContent("Webview", WebViewLoggerFile.getWebviewLogPath()))
            add(SectionDataContent("应用更新", AAFLoggerFile.getLogPathByModuleName(AAFLoggerFile.MODULE_UPDATE)))
        }
    }
}