package com.bihe0832.android.test.module

import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.common.debug.base.BaseDebugListFragment
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.download.core.list.DownloadingList
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.intent.IntentUtils

class DebugTempFragment : BaseDebugListFragment() {
    val LOG_TAG = "DebugTempFragment"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugItemData("简单测试函数") { testFunc() })
            add(DebugItemData("通用测试预处理") { preTest() })
            add(DebugItemData("测试自定义请求") { testOneRequest() })
            add(DebugItemData("默认关于页") { RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT) })
            add(DebugItemData("APP设置") { IntentUtils.startAppDetailSettings(context) })
        }
    }

    private fun testOneRequest() {
    }

    private fun preTest() {
        CommonDBManager.getData("sss")


    }

    private fun testFunc() {
        ZLog.d(DownloadingList.getDownloadingNum().toString())
//        CommonDBManager.saveData("sss", "Fsdfsd")
    }


}