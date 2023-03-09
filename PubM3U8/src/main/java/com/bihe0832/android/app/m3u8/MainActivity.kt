package com.bihe0832.android.app.m3u8

import android.os.Bundle
import android.widget.ImageView
import com.bihe0832.android.app.message.addMessageAction
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.base.m3u8.history.M3U8ListActivity
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_MAIN)
class MainActivity : M3U8ListActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateManager.checkUpdateAndShowDialog(this, false)
        mToolbar?.apply {
            setNavigationIcon(resources.getDrawable(R.mipmap.ic_menu_white))
            setNavigationOnClickListener {
                RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            }
        }
        M3U8DBManager.init(this)

        findViewById<ImageView>(R.id.message).apply {
            setColorFilter(resources.getColor(R.color.white))
            setOnClickListener {
                RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_MESSAGE)
            }
        }

        addMessageAction(findViewById(R.id.message), findViewById(R.id.message_unread))
    }

}
