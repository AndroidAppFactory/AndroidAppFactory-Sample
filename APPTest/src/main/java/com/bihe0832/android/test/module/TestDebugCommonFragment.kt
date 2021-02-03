package com.bihe0832.android.test.module

<<<<<<< HEAD
import android.content.Intent
import android.view.View
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.lifecycle.*
=======
import android.view.View
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.lib.adapter.CardBaseModule
>>>>>>> AAF更新

open class TestDebugCommonFragment : com.bihe0832.android.common.test.module.TestDebugCommonFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TipsData("APPFactory的通用组件和工具"))
            add(TestItemData("查看应用版本及环境", View.OnClickListener { showAPPInfo() }))
            add(TestItemData("查看使用情况", View.OnClickListener { showUsedInfo() }))
            add(TestItemData("查看设备信息", View.OnClickListener { showMobileInfo() }))
            add(TestItemData("查看第三方应用信息", View.OnClickListener { showOtherAPPInfo() }))
            add(TipsData("APPFactory支持的应用"))
            add(TestItemData("应用信息获取") {
                startActivity("com.bihe0832.android.app.apk.MainActivity")
            })

            add(TestItemData("来拼图") {
                startActivity("com.bihe0832.android.app.puzzle.MainActivity")
            })
        }
    }

<<<<<<< HEAD
    private fun startActivity(activityName: String) {
        try {
            val threadClazz = Class.forName(activityName)
            val intent = Intent(context, threadClazz)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            ZixieContext.showToast("请确认当前运行的测试模块是否包含该应用")
        }
    }
=======

>>>>>>> AAF更新
}