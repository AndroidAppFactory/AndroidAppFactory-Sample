package com.bihe0832.android.app.puzzle

import android.content.Intent
import com.bihe0832.android.app.puzzle.navigation.PuzzleNavigationDrawerFragment
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.AAFCommonMainActivity
import com.bihe0832.android.common.navigation.drawer.NavigationDrawerFragment
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_PUZZLE)
class MainActivity : AAFCommonMainActivity() {
    private val mAAFNavigationDrawerFragment = PuzzleNavigationDrawerFragment()

    override fun getNavigationDrawerFragment(): NavigationDrawerFragment? {
        return mAAFNavigationDrawerFragment
    }

    override fun getRootFragmentClassName(): String {
        return MainFragment::class.java.name
    }
}
