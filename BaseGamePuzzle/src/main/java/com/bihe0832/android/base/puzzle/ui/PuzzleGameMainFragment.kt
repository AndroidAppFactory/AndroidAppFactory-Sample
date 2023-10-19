package com.bihe0832.android.base.puzzle.ui

import android.graphics.Bitmap
import android.view.View
import com.bihe0832.android.base.puzzle.PuzzleGameManager
import com.bihe0832.android.base.puzzle.PuzzleGameMode
import com.bihe0832.android.base.puzzle.R
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.ui.BaseFragment
import com.bihe0832.android.lib.config.Config
import com.bihe0832.android.lib.ui.dialog.callback.OnDialogListener
import com.bihe0832.android.lib.ui.dialog.tools.DialogUtils
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.addLevel
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.changeMode
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.game_level
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.game_mode
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.image_preview
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.puzzleLayout
import kotlinx.android.synthetic.main.fragment_puzzle_game_main.reduceLevel

/**
 * Created by zixie on 16/6/30.
 */
class PuzzleGameMainFragment : BaseFragment() {

    private val CONFIG_PUZZLE_LEVEL = this.javaClass.name + ".level"
    private val CONFIG_PUZZLE_MODE = this.javaClass.name + ".mode"

    override fun getLayoutID(): Int {
        return R.layout.fragment_puzzle_game_main
    }

    override fun initView(view: View) {
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
                if (it == PuzzleGameMode.EXCHANGE) {
                    PuzzleGameManager.changeGameMode(PuzzleGameMode.NORMAL)
                } else {
                    PuzzleGameManager.changeGameMode(PuzzleGameMode.EXCHANGE)
                }
            }
        }
    }

    override fun initData() {
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

            override fun onGameModeChanged(puzzleGameMode: PuzzleGameMode) {
                updateGameModeText()
                Config.writeConfig(CONFIG_PUZZLE_MODE, puzzleGameMode.ordinal)
            }

            override fun onGameSuccess(level: Int, puzzleGameMode: PuzzleGameMode) {
                DialogUtils.showConfirmDialog(
                    context!!,
                    "你已完成<b><font color='#38ADFF'> " + puzzleGameMode.desc + " </font>的 $level 级</b>挑战，要换张图继续么？",
                    true,
                    object : OnDialogListener {
                        override fun onPositiveClick() {
                        }

                        override fun onNegativeClick() {
                            try {
                                activity?.finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onCancel() {}
                    },
                )
            }
        })
        PuzzleGameManager.changeGameMode(PuzzleGameMode.getGameMode(Config.readConfig(CONFIG_PUZZLE_MODE, PuzzleGameMode.NORMAL.ordinal)))
        PuzzleGameManager.changeGameLevel(Config.readConfig(CONFIG_PUZZLE_LEVEL, 1))
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
