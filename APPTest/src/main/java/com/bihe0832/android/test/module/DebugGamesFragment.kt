package com.bihe0832.android.test.module

import com.bihe0832.android.base.dice.DiceFragment
import com.bihe0832.android.base.game.GameWrapper
import com.bihe0832.android.base.puzzle.ui.PuzzleGameMainFragment
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.item.DebugTipsData
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule

open class DebugGamesFragment : DebugCommonFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugTipsData("APPFactory支持的一些小游戏应用"))
            add(getGameFragmentItemData("摇骰子", DiceFragment::class.java))
            add(getGameFragmentItemData("来拼图", PuzzleGameMainFragment::class.java))
        }
    }

    fun getGameFragmentItemData(content: String, clazz: Class<*>): DebugItemData {
        return DebugItemData(content) { GameWrapper.startGame(ZixieContext.applicationContext, clazz, content) }
    }
}