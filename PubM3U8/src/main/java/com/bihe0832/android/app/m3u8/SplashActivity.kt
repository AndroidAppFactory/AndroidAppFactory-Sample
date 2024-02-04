package com.bihe0832.android.app.m3u8

import android.os.Bundle
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.base.m3u8.M3U8ModuleManager
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.common.splash.SplashActivity
import com.bihe0832.android.lib.router.annotation.Module

@Module(RouterConstants.MODULE_NAME_SPLASH)
class SplashActivity : SplashActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        M3U8ModuleManager.init(this)
    }

    override fun getMainRouter(): String {
        if (M3U8DBManager.getAll().isNotEmpty()) {
            return RouterConstants.MODULE_NAME_MAIN
        } else {
            return RouterConstants.MODULE_NAME_M3U8
        }
    }
}
