package com.bihe0832.android.app.browser

import android.os.Bundle
import com.bihe0832.android.common.webview.base.BaseWebviewFragment
import com.bihe0832.android.common.webview.log.MyBaseJsBridgeProxy
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.request.URLUtils
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager.saveData
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.CommonDialog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.utils.intent.IntentUtils
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * @author zixie code@bihe0832.com
 * Created on 2019-07-26.
 * Description: Description
 */
class BrowserFragment : BaseWebviewFragment() {

    private var mDialog: CommonDialog? = null

    override fun getFinalURL(url: String): String {
        return url
    }

    override fun getUserAgentString(): String {
        return "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
    }

    override fun loadUseIntent(url: String): Boolean {
        return false
    }

    override fun getWebViewClient(): WebViewClient {
        return MySpecialWebView(jsBridgeProxy)
    }

    private inner class MySpecialWebView(jsBridge: MyBaseJsBridgeProxy?) : MyWebViewClient(jsBridge) {
        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            ZLog.d("WebPageFragment -> :shouldInterceptRequest url:$url")
            saveURL(url)
            return super.shouldInterceptRequest(view, url)
        }

        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            ZLog.d("WebPageFragment -> :shouldInterceptRequest url:" + request.url.toString())
            saveURL(request.url.toString())
            return super.shouldInterceptRequest(view, request)
        }

        private fun saveURL(url: String) {
            if (url.endsWith(".m3u8")) {
                showDownload(url)
            }
        }
    }

    fun showDownload(url: String) {
        if (null == mDialog) {
            ThreadManager.getInstance().runOnUIThread {
                mDialog = CommonDialog(activity)
            }
        }

        mDialog?.apply {
            title = "发现新资源"
            setHtmlContent("发现新资源，是否前往下载？")
            negative = "再想想"
            positive = "去下载"
            shouldCanceled = true
            onClickBottomListener = object : OnDialogListener {
                override fun onPositiveClick() {
                    dismiss()
                    saveData(url, mIntentUrl)
                    IntentUtils.jumpToOtherApp("zm3u8://m3u8?url=" + URLUtils.encode(url), context)
                }

                override fun onNegativeClick() {
                    dismiss()
                }

                override fun onCancel() {
                    dismiss()
                }
            }

        }?.let { dialog ->
            ThreadManager.getInstance().runOnUIThread {
                dialog.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDialog?.dismiss()
    }

    override fun getJsBridgeProxy(): MyBaseJsBridgeProxy {
        return MyBaseJsBridgeProxy(mWebView, activity)
    }

    override fun actionBeforeLoadURL(url: String) {
        syncCookie()
    }

    companion object {
        fun newInstance(url: String?): BrowserFragment {
            val fragment = BrowserFragment()
            val bundle = Bundle()
            bundle.putString(INTENT_KEY_URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }
}