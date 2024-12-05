package com.bihe0832.android.base.debug.webview

import android.text.TextUtils
import android.view.View
import com.bihe0832.android.app.router.openWebPage
import com.bihe0832.android.common.debug.base.BaseDebugListFragment
import com.bihe0832.android.common.debug.item.getDebugItem
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.ui.dialog.callback.DialogCompletedStringCallback

class DebugWebviewFragment : BaseDebugListFragment() {

    private var lastUrl = "https://blog.bihe0832.com"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(
                getDebugItem(
                    "打开指定Web页面",
                    View.OnClickListener {
                        showInputDialog(
                            "打开指定Web页面",
                            "请在输入框输入网页地址后点击“确定”",
                            lastUrl,
                            DialogCompletedStringCallback { result: String ->
                                try {
                                    if (!TextUtils.isEmpty(result)) {
                                        lastUrl = result
                                        openWebPage(result)
                                    } else {
                                        ZixieContext.showDebug("请输入正确的网页地址")
                                    }
                                } catch (e: Exception) {
                                }
                            },
                        )
                    },
                ),
            )
            add(
                getDebugItem(
                    "打开JSbridge调试页面",
                    View.OnClickListener { openWebPage("https://microdemo.bihe0832.com/jsbridge/index.html") },
                ),
            )
            add(getDebugItem("打开TBS调试页面", View.OnClickListener { openWebPage("http://debugtbs.qq.com/") }))
            add(getDebugItem("打开本地调试页", View.OnClickListener { openWebPage("file:///android_asset/index.html") }))
        }
    }
}
