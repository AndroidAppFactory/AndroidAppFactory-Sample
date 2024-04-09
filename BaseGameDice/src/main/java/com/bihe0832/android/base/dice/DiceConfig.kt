package com.bihe0832.android.base.dice

import com.bihe0832.android.lib.config.Config


/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2023/4/7.
 * Description: Description
 *
 */
object DiceConfig {

    const val GAME_DICE_NUM = "diceNum"
    const val GAME_RESULT_IS_LOCKED = "mIsLocked"
    const val GAME_RESULT_IS_SORTED = "mIsSort"

    fun getDiceNum(): Int {
        return Config.readConfig(GAME_DICE_NUM, 6)
    }

    fun setDiceNum(diceNum: Int): Boolean {
        return Config.writeConfig(GAME_DICE_NUM, diceNum)
    }

    fun isAutoLocked(): Boolean {
        return Config.isSwitchEnabled(GAME_RESULT_IS_LOCKED, false)
    }

    fun setAutoLocked(isLocked: Boolean): Boolean {
        return Config.writeConfig(GAME_RESULT_IS_LOCKED, isLocked)
    }

    fun isAutoSorted(): Boolean {
        return Config.isSwitchEnabled(GAME_RESULT_IS_SORTED, true)
    }

    fun setAutoSorted(mIsSort: Boolean): Boolean {
        return Config.writeConfig(GAME_RESULT_IS_SORTED, mIsSort)
    }

    fun getDiceResIDByNum(num: Int): Int {
        return when (num) {
            1 -> R.mipmap.dice_1
            2 -> R.mipmap.dice_2
            3 -> R.mipmap.dice_3
            4 -> R.mipmap.dice_4
            5 -> R.mipmap.dice_5
            6 -> R.mipmap.dice_6
            else -> -1
        }
    }
}