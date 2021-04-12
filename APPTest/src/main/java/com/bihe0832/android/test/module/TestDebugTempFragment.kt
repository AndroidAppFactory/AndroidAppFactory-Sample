package com.bihe0832.android.test.module

import android.view.View
import com.bihe0832.android.app.about.AboutActivity
import com.bihe0832.android.common.test.base.BaseTestFragment
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.intent.IntentUtils
import kotlin.collections.ArrayList


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
        CommonDBManager.getData("sss")


    }

    private fun testFunc() {
        CommonDBManager.saveData("sss","Fsdfsd")
    }
}