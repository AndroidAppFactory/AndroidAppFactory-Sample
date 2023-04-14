package com.bihe0832.android.app.m3u8

import android.os.Bundle
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.AAFCommonMainActivity
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.base.m3u8.history.M3U8ListFragment
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_MAIN)
class MainActivity : AAFCommonMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DownloadUtils.init(this, 30, ZixieContext.isDebug())
        M3U8DBManager.init(this)
    }

    override fun getRootFragmentClassName(): String {
        return M3U8ListFragment::class.java.name
    }
}