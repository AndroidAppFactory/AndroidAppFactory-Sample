package com.bihe0832.android.test.module

import com.bihe0832.android.base.dice.DiceFragment
import com.bihe0832.android.base.game.GameWrapper
import com.bihe0832.android.base.puzzle.ui.PuzzleGameMainFragment
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.item.getDebugItem
import com.bihe0832.android.common.debug.item.getTipsItem
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule

open class DebugGamesFragment : DebugCommonFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(getTipsItem("APPFactory支持的一些小游戏应用"))
            add(getGameFragmentItemData("摇骰子", DiceFragment::class.java))
            add(getGameFragmentItemData("来拼图", PuzzleGameMainFragment::class.java))
        }
    }

    fun getGameFragmentItemData(content: String, clazz: Class<*>): DebugItemData {
        return getDebugItem(content) { GameWrapper.startGame(ZixieContext.applicationContext, clazz, content) }
    }
}