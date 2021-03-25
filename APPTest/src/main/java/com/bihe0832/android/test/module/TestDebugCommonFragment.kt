package com.bihe0832.android.test.module

import android.view.View
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.common.test.item.TestTipsData
import com.bihe0832.android.lib.adapter.CardBaseModule

open class TestDebugCommonFragment : com.bihe0832.android.common.test.module.TestDebugCommonFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestTipsData("APPFactory的通用组件和工具"))
            add(TestItemData("查看应用版本及环境", View.OnClickListener { showAPPInfo() }))
            add(TestItemData("查看使用情况", View.OnClickListener { showUsedInfo() }))
            add(TestItemData("查看设备信息", View.OnClickListener { showMobileInfo() }))
            add(TestItemData("查看第三方应用信息", View.OnClickListener { showOtherAPPInfo() }))
            add(TestTipsData("APPFactory支持的应用"))
            add(TestItemData("应用信息获取") {
                startActivity("com.bihe0832.android.app.apk.MainActivity")
            })

            add(TestItemData("来拼图") {
                startActivity("com.bihe0832.android.app.puzzle.MainActivity")
            })

            add(TestItemData("M3U8下载器") {
                startActivity("com.bihe0832.android.app.m3u8.MainActivity")
            })

        }
    }
}