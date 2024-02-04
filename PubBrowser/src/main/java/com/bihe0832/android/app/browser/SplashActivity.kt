package com.bihe0832.android.app.browser

import android.text.TextUtils
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.tools.AAFTools
import com.bihe0832.android.common.splash.SplashActivity
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager

@Module(RouterConstants.MODULE_NAME_SPLASH)
class SplashActivity : SplashActivity() {

    override fun getMainRouter(): String {
        return RouterConstants.MODULE_NAME_MAIN
    }

    override fun doNext() {
        CommonDBManager.init(this)
        var url = AAFTools.pasteFromClipboard(this)
        if (TextUtils.isEmpty(url)) {
            RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BROWSER_LIST)
        } else {
            openMain(url)
        }
        finish()
    }
}
