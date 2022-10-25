package com.bihe0832.android.test.module

import android.view.View
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.item.DebugTipsData
import com.bihe0832.android.common.debug.log.DebugLogActivity
import com.bihe0832.android.common.debug.module.DebugCommonFragment
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule

open class DebugCommonFragment : DebugCommonFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugTipsData("APPFactory的通用组件和工具"))
            add(DebugItemData("查看应用版本及环境", View.OnClickListener { showAPPInfo() }))
            add(DebugItemData("查看使用情况", View.OnClickListener { showUsedInfo() }))
            add(DebugItemData("查看设备信息", View.OnClickListener { showMobileInfo() }))
            add(
                DebugItemData(
                    "日志管理",
                    View.OnClickListener { startActivityWithException(DebugLogActivity::class.java) })
            )
            add(DebugTipsData("APPFactory支持的应用"))
            add(DebugItemData("应用信息获取") {
                openNew("com.bihe0832.android.app.apk.MainActivity")
            })

            add(DebugItemData("来拼图") {
                openNew("com.bihe0832.android.app.puzzle.MainActivity")
            })

            add(DebugItemData("M3U8下载器") {
                openNew("com.bihe0832.android.app.m3u8.MainActivity")
            })

        }
    }

    fun openNew(activityName: String) {
        try {
            startActivityWithException(activityName)
        } catch (e: Exception) {
            e.printStackTrace()
            ZixieContext.showDebug("APP模块未集成")
        }
    }
}