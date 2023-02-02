package com.bihe0832.android.base.puzzle

import android.graphics.Bitmap
import com.bihe0832.android.base.puzzle.view.PuzzleLayout
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.media.image.BitmapUtil
import com.bihe0832.android.lib.thread.ThreadManager

/**
 *
 * @author zixie code@bihe0832.com Created on 1/7/21.
 *
 */
object PuzzleGameManager {
    interface GameStateListener {
        fun onImageChanged(bitmap: Bitmap)
        fun onLevelChanged(level: Int)
        fun onGameModeChanged(gameMode: GameMode)
        fun onGameSuccess(level: Int, gameMode: GameMode)
    }

    private var mPuzzleGame: PuzzleLayout? = null
    private val mGameListenerList = mutableListOf<GameStateListener>()
    private var mBitmap: Bitmap? = null


    fun init(gameMode: PuzzleLayout) {
        clearListener()
        mPuzzleGame = gameMode
        mPuzzleGame!!.addSuccessListener {
            notifySuccess(getLevel(), getGameMode())
        }
        mPuzzleGame!!.changeRes(getCurrentGameSourceBitmap())

    }

    fun addListener(listener: GameStateListener) {
        if (!mGameListenerList.contains(listener)) {
            mGameListenerList.add(listener)
        } else {
            ZLog.d("had add")
        }
    }

    fun removeListener(listener: GameStateListener) {
        if (mGameListenerList.contains(listener)) {
            mGameListenerList.remove(listener)
        } else {
            ZLog.d("not found")
        }
    }

    fun clearListener() {
        mGameListenerList.clear()
    }

    fun getCurrentGameSourceBitmap(): Bitmap {
        return mBitmap
                ?: BitmapUtil.getLocalBitmap(ZixieContext.applicationContext, R.mipmap.icon, 4)
    }

    fun setBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
        mPuzzleGame?.changeRes(bitmap)
    }

    fun addLevel(): Boolean {
        return if (mPuzzleGame?.addCount() == true) {
            notifyLevelChanged(getLevel())
            true
        } else {
            false
        }
    }

    fun reduceLevel(): Boolean {
        return if (mPuzzleGame?.reduceCount() == true) {
            notifyLevelChanged(getLevel())
            true
        } else {
            false
        }
    }

    fun getLevel(): Int {
        val count: Int = mPuzzleGame?.count ?: 3
        return count - 3 + 1
    }

    fun getMaxLevel(): Int {
        return PuzzleLayout.MAX_COUNT - 3 + 1
    }

    fun getMinLevel(): Int {
        return PuzzleLayout.MIN_COUNT - 3 + 1
    }

    fun changeGameLevel(level: Int): Boolean {
        val count: Int = level + 2
        return mPuzzleGame?.changeCount(count) ?: false
    }

    fun getGameMode(): GameMode {
        return mPuzzleGame?.gameMode ?: GameMode.BAD
    }

    fun changeGameMode(gameMode: GameMode) {
        mPuzzleGame?.changeMode(gameMode)
        notifyGameModeChanged(getGameMode())
    }


    private fun notifySuccess(level: Int, gameMode: GameMode) {
        ThreadManager.getInstance().start {
            mGameListenerList.forEach {
                it.onGameSuccess(level, gameMode)
            }
        }
    }

    private fun notifyLevelChanged(level: Int) {
        ThreadManager.getInstance().start {
            mGameListenerList.forEach {
                it.onLevelChanged(level)
            }
        }
    }

    private fun notifyGameModeChanged(gameMode: GameMode) {
        ThreadManager.getInstance().start {
            mGameListenerList.forEach {
                it.onGameModeChanged(gameMode)
            }
        }
    }
}