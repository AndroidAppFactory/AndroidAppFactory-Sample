package com.bihe0832.android.app.puzzle

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.bihe0832.android.base.puzzle.ui.PuzzlePhotosFragment
import com.bihe0832.android.framework.ui.BaseFragment
import com.bihe0832.android.lib.text.TextFactoryUtils

class MainFragment : BaseFragment() {

    override fun getLayoutID(): Int {
        return R.layout.fragment_puzzle_main
    }

    override fun initView(view: View) {
        super.initView(view)
        view.findViewById<TextView>(R.id.puzzle_desc).text = TextFactoryUtils.getSpannedTextByHtml(
                "使用教程<BR>" +
                        " 1. <b><font color='#38ADFF'>点击</font>下方图标</b>，选择拼图的素材<BR>" +
                        " 2. 选择图片后，进入<b><font color='#38ADFF'>拼图游戏</font>页面，可自由切换模式和难度</b>"
        )
        loadRootFragment(R.id.puzzle_content, PuzzlePhotosFragment())
    }
}
