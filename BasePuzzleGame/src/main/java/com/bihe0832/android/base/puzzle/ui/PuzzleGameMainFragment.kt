package com.bihe0832.android.base.puzzle.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bihe0832.android.base.puzzle.GameMode
import com.bihe0832.android.base.puzzle.PuzzleGameManager
import com.bihe0832.android.base.puzzle.R
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.ui.BaseFragment
import com.bihe0832.android.lib.config.Config
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.CommonDialog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.*

/**
 * Created by hardyshi on 16/6/30.
 */
class PuzzleGameMainFragment : BaseFragment() {

    private val CONFIG_PUZZLE_LEVEL = this.javaClass.name + ".level"
    private val CONFIG_PUZZLE_MODE = this.javaClass.name + ".mode"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_puzzle_game_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        PuzzleGameManager.init(puzzleLayout)
        PuzzleGameManager.addListener(object : PuzzleGameManager.GameStateListener {
            override fun onImageChanged(bitmap: Bitmap) {
                updatePreviewImg()
            }

            override fun onLevelChanged(level: Int) {
                updateLevetText()
                Config.writeConfig(CONFIG_PUZZLE_LEVEL, level)
                addLevel.isEnabled = true
                reduceLevel.isEnabled = true
            }

            override fun onGameModeChanged(gameMode: GameMode) {
                updateGameModeText()
                Config.writeConfig(CONFIG_PUZZLE_MODE, gameMode.ordinal)
            }

            override fun onGameSuccess(level: Int, gameMode: GameMode) {
                ThreadManager.getInstance().runOnUIThread {
                    CommonDialog(context).apply {
                        title = getString(R.string.success_title)
                        setHtmlContent("你已完成<b><font color='#38ADFF'> " + gameMode.desc + " </font>的 $level 级</b>挑战，要换张图继续么？")
                        negative = getString(R.string.success_negative)
                        positive = getString(R.string.success_positive)
                        setShouldCanceled(true)
                        setOnClickBottomListener(object : OnDialogListener {
                            override fun onPositiveClick() {
                                try {
                                    activity?.finish()
                                    dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onNegativeClick() {
                                try {
                                    dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onCancel() {}
                        })
                    }.let {
                        it.show()
                    }
                }
            }
        })
        PuzzleGameManager.changeGameMode(GameMode.getGameMode(Config.readConfig(CONFIG_PUZZLE_MODE, GameMode.NORMAL.ordinal)))
        PuzzleGameManager.changeGameLevel(Config.readConfig(CONFIG_PUZZLE_LEVEL, 1))
        updateLevetText()
        updateGameModeText()
        updatePreviewImg()
        addLevel.setOnClickListener {
            if (PuzzleGameManager.getLevel() == PuzzleGameManager.getMaxLevel()) {
                ZixieContext.showLongToastJustAPPFront(getString(R.string.top_level))
                it.isEnabled = false
            } else {
                PuzzleGameManager.addLevel()
            }

        }

        reduceLevel.setOnClickListener {
            if (PuzzleGameManager.getLevel() == PuzzleGameManager.getMinLevel()) {
                ZixieContext.showLongToastJustAPPFront(getString(R.string.bottom_level))
                it.isEnabled = false
            } else {
                PuzzleGameManager.reduceLevel()
            }
        }
        changeMode.setOnClickListener {
            PuzzleGameManager.getGameMode().let {
                if (it == GameMode.EXCHANGE) {
                    PuzzleGameManager.changeGameMode(GameMode.NORMAL)
                } else {
                    PuzzleGameManager.changeGameMode(GameMode.EXCHANGE)
                }
            }
        }
    }


    private fun updatePreviewImg() {
        image_preview.setImageBitmap(PuzzleGameManager.getCurrentGameSourceBitmap())
    }

    private fun updateLevetText() {
        game_level.text = String.format(getString(R.string.game_level), PuzzleGameManager.getLevel())
    }

    private fun updateGameModeText() {
        game_mode.text = String.format(getString(R.string.game_mode), PuzzleGameManager.getGameMode().desc)
    }
}