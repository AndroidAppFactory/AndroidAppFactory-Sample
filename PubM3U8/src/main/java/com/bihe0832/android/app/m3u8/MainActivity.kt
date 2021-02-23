package com.bihe0832.android.app.m3u8

import android.os.Bundle
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_M3U8)
class MainActivity : com.bihe0832.android.base.m3u8.M3u8DownloadActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateManager.checkUpdateAndShowDialog(this, false)
    }

    override fun initToolbarAction() {
        super.initToolbarAction()
        mToolbar?.apply {
            setNavigationOnClickListener {
                RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            }
        }
    }

}
