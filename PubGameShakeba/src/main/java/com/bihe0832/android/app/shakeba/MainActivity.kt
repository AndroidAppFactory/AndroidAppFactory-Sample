package com.bihe0832.android.app.shakeba

import android.os.Bundle
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.AAFCommonMainActivity
import com.bihe0832.android.base.dice.DiceFragment
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_GAME_ROOT)
open class MainActivity : AAFCommonMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableDrawerGesture()
    }
    override fun getRootFragmentClassName(): String {
        return DiceFragment::class.java.name
    }

    override fun getTitleName(): String {
        return "骰子战争"
    }
}
