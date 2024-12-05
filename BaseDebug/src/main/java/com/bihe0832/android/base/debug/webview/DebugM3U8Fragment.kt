package com.bihe0832.android.base.debug.webview

import android.view.View
import com.bihe0832.android.base.m3u8.bean.M3U8Info
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.common.debug.base.BaseDebugListFragment
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.item.getDebugItem
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.gson.JsonHelper
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.ui.dialog.callback.DialogCompletedStringCallback
import com.bihe0832.android.lib.ui.dialog.tools.DialogUtils

class DebugM3U8Fragment : BaseDebugListFragment() {
    val LOG_TAG = "DebugTTSFragment"

    override fun initView(view: View) {
        super.initView(view)
        M3U8DBManager.init(view.context)
    }

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(getDebugItem("批量导出下载记录") { export() })
            add(getDebugItem("批量新增下载地址") { add() })

        }
    }

    private fun add() {
        DialogUtils.showInputDialog(
            activity!!,
            "批量导入下载地址",
            "请导入下载地址列表的Json 结构",
            "",
            DialogCompletedStringCallback {
                JsonHelper.fromJsonList(it, M3U8Info::class.java)?.forEach {
                    M3U8DBManager.saveData(it)
                }
            },
        )
    }

    private fun export() {
        JsonHelper.toJson(M3U8DBManager.getAll().toList()).let {
            ZLog.e("\n\n\n !!! all m3u8 urls :\n $it \n !!! \n\n\n ")
        }
    }

}
