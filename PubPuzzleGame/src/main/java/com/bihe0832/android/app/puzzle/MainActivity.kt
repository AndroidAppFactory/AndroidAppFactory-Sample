package com.bihe0832.android.app.puzzle

import android.os.Bundle
import android.text.Html
import androidx.core.content.ContextCompat
import com.bihe0832.android.app.message.AAFMessageManager
import com.bihe0832.android.app.message.addMessageAction
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.base.puzzle.ui.PuzzlePhotosFragment
import com.bihe0832.android.framework.ui.BaseActivity
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import kotlinx.android.synthetic.main.activity_puzzle_main.*

@APPMain
@Module(RouterConstants.MODULE_NAME_PUZZLE)
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle_main)

        initToolbar(R.id.common_toolbar, getString(R.string.app_name), needTitleCenter = false, needBack = false, iconRes = R.mipmap.ic_menu_white)
        mToolbar?.apply {
            setNavigationOnClickListener {
                RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            }
        }

        addMessageAction(findViewById(R.id.message), findViewById(R.id.message_unread))

        puzzle_desc.text = Html.fromHtml(
                "使用教程<BR>" +
                        " 1. <b><font color='#38ADFF'>点击</font>下方图标</b>，选择拼图的素材<BR>" +
                        " 2. 选择图片后，进入<b><font color='#38ADFF'>拼图游戏</font>页面，可自由切换模式和难度</b>"
        )

    }

    override fun getStatusBarColor(): Int {
        return ContextCompat.getColor(this, R.color.colorPrimary)
    }


    override fun getNavigationBarColor(): Int {
        return ContextCompat.getColor(this, R.color.transparent)
    }

    override fun onResume() {
        super.onResume()
        if (findFragment(PuzzlePhotosFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, PuzzlePhotosFragment())
        }
        UpdateManager.checkUpdateAndShowDialog(this, false)
    }
}
