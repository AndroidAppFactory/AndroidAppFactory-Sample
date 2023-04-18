package com.bihe0832.android.app.ui

import android.os.Bundle
import com.bihe0832.android.app.R
import com.bihe0832.android.app.ui.navigation.AAFNavigationDrawerFragment
import com.bihe0832.android.app.ui.navigation.addRedDotAction
import com.bihe0832.android.app.ui.navigation.checkMsgAndShowFace
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.common.main.CommonActivityWithNavigationDrawer
import com.bihe0832.android.common.navigation.drawer.NavigationDrawerFragment
import com.bihe0832.android.framework.ZixieContext


open class AAFCommonMainActivity : CommonActivityWithNavigationDrawer() {

    private val mAAFNavigationDrawerFragment = AAFNavigationDrawerFragment()
    
    override fun getNavigationDrawerFragment(): NavigationDrawerFragment? {
        return mAAFNavigationDrawerFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addRedDotAction(findViewById(R.id.title_icon_unread))
        checkMsgAndShowFace(this)
        updateTitle(titleName)
        showQrcodeScan(needSound = true, needVibrate = true)
    }

    override fun onResume() {
        super.onResume()
        UpdateManager.checkUpdateAndShowDialog(this, false, ZixieContext.isOfficial())
    }

    fun disableDrawerGesture() {
        mAAFNavigationDrawerFragment.disableDrawerGesture()
    }

    override fun getTitleName(): String {
        return getString(R.string.app_name)
    }
}
