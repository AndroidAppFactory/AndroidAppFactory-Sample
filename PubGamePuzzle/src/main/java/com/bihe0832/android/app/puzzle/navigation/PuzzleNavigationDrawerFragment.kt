package com.bihe0832.android.app.puzzle.navigation

import com.bihe0832.android.common.main.CommonNavigationDrawerFragment

/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2023/4/10.
 * Description: Description
 *
 */
class PuzzleNavigationDrawerFragment : CommonNavigationDrawerFragment() {

    override fun loadFragment(containerId: Int) {
        loadRootFragment(containerId, PuzzleNavigationContentFragment())
    }
}