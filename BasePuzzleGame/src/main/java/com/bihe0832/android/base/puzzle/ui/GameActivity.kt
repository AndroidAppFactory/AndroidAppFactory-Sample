package com.bihe0832.android.base.puzzle.ui

import android.os.Bundle
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.base.puzzle.R
import com.bihe0832.android.framework.ui.main.CommonActivity
import com.bihe0832.android.lib.router.annotation.Module

@Module(RouterConstants.MODULE_NAME_PUZZLE_GAME)
class GameActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar("游戏中", true)
    }

    override fun onResume() {
        super.onResume()
        if (findFragment(PuzzleGameMainFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, PuzzleGameMainFragment())
        }
    }
}
