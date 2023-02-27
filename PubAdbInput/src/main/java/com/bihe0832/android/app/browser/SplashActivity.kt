package com.bihe0832.android.app.browser

import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.common.splash.SplashActivity
import com.bihe0832.android.lib.router.annotation.Module

@Module(RouterConstants.MODULE_NAME_SPLASH)
class SplashActivity : SplashActivity() {

    override fun getMainRouter(): String {
        return RouterConstants.MODULE_NAME_INPUT_GUIDE
    }
}