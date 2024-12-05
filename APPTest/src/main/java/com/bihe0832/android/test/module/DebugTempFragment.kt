package com.bihe0832.android.test.module

import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.base.debug.webview.DebugM3U8Fragment
import com.bihe0832.android.base.debug.webview.DebugWebviewFragment
import com.bihe0832.android.common.debug.base.BaseDebugListFragment
import com.bihe0832.android.common.debug.item.getDebugItem
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.intent.IntentUtils

class DebugTempFragment : BaseDebugListFragment() {
    val LOG_TAG = "DebugTempFragment"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(getDebugItem("简单测试函数") { testFunc() })
            add(getDebugItem("通用测试预处理") { preTest() })
            add(getDebugItem("测试自定义请求") { testOneRequest() })
            add(getDebugItem("默认关于页") { RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT) })
            add(getDebugItem("APP设置") { IntentUtils.startAppDetailSettings(context) })
            add(getDebugFragmentItemData("WebView 调试", DebugWebviewFragment::class.java))
            add(getDebugFragmentItemData("M3U8 调试", DebugM3U8Fragment::class.java))
        }
    }

    private fun testOneRequest() {
    }

    private fun preTest() {
        CommonDBManager.getData("sss")


    }

    private fun testFunc() {
//        CommonDBManager.saveData("sss", "Fsdfsd")
    }


}