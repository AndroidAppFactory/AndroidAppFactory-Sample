package com.bihe0832.android.app.browser

import android.os.Bundle
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.common.webview.WebPageActivity
import com.bihe0832.android.common.webview.base.BaseWebviewFragment
import com.bihe0832.android.lib.router.annotation.Module
import java.util.*

@Module(RouterConstants.MODULE_NAME_MAIN)
class MainActivity : WebPageActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateManager.checkUpdateAndShowDialog(this, false)
    }

    override fun getWebViewFragment(): BaseWebviewFragment? {
        return BrowserFragment.newInstance(url)
    }

    override fun getWebViewFragmentClass(): Class<*>? {
        return BrowserFragment::class.java
    }
}

fun openMain(url: String) {
    val map = HashMap<String, String>()
    map[RouterConstants.INTENT_EXTRA_KEY_WEB_URL] = url
    RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_MAIN, map)
}