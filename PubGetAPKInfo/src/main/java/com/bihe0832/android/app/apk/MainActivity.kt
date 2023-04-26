package com.bihe0832.android.app.apk

import com.bihe0832.android.app.apk.navigation.APKNavigationDrawerFragment
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.AAFCommonMainActivity
import com.bihe0832.android.common.navigation.drawer.NavigationDrawerFragment
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import java.util.*

@APPMain
@Module(RouterConstants.MODULE_NAME_APK_LIST)
class MainActivity : AAFCommonMainActivity() {
    private val mAAFNavigationDrawerFragment = APKNavigationDrawerFragment()

    override fun getNavigationDrawerFragment(): NavigationDrawerFragment? {
        return mAAFNavigationDrawerFragment
    }

    override fun getRootFragmentClassName(): String {
        return MainFragment::class.java.name
    }
}