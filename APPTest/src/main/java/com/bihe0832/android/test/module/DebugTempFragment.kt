package com.bihe0832.android.test.module

import android.view.View
import com.bihe0832.android.app.about.AboutActivity
import com.bihe0832.android.common.debug.base.BaseDebugListFragment
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.intent.IntentUtils

class DebugTempFragment : BaseDebugListFragment() {
    val LOG_TAG = "DebugTempFragment"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugItemData("简单测试函数", View.OnClickListener { testFunc() }))
            add(DebugItemData("通用测试预处理", View.OnClickListener { preTest() }))
            add(DebugItemData("测试自定义请求", View.OnClickListener { testOneRequest() }))
            add(
                    DebugItemData(
                            "默认关于页",
                            View.OnClickListener { startActivity(AboutActivity::class.java) })
            )
            add(
                    DebugItemData(
                            "APP设置",
                            View.OnClickListener { IntentUtils.startAppDetailSettings(context) })
            )
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