package com.bihe0832.android.test

import android.os.Bundle
import com.bihe0832.android.app.AppFactoryInit
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.common.splash.SplashActivity
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_SPLASH)
class DebugSplashActivity : SplashActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun doNext() {
        AppFactoryInit.initAll(application)
        super.doNext()
    }

    override fun getMainRouter(): String {
        return RouterConstants.MODULE_NAME_DEBUG
    }
}