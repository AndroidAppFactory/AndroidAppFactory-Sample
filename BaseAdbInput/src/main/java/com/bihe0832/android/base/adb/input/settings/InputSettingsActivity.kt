package com.bihe0832.android.base.adb.input.settings

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.base.adb.input.R
import com.bihe0832.android.framework.ui.BaseActivity
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.base.adb.input.ZixieIME
import com.bihe0832.android.base.adb.input.switcher.InputSwitchTools
import kotlinx.android.synthetic.main.activity_input_settings.*


@Module(RouterConstants.MODULE_NAME_INPUT_GUIDE)
class InputSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_settings)
        initToolbar(R.id.common_toolbar, "输入法设置引导", needTitleCenter = false, needBack = true, iconRes = R.mipmap.icon_left_arrow_white)
        showInputMethodPicker(intent)
        if (findFragment(InputSettingsFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, InputSettingsFragment())
        }
        about.setOnClickListener {
            RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
        }
        UpdateManager.checkUpdateAndShowDialog(this, false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showInputMethodPicker(intent)
    }

    private fun showInputMethodPicker(intent: Intent?) {
        if (InputSwitchTools.hasInstall(this)) {
            var type = intent?.extras?.getString(RouterConstants.INTENT_EXTRA_KEY_INPUT_GUIDE_TYPE, "")
                    ?: ""
            if (type == RouterConstants.INTENT_EXTRA_VALUE_INPUT_SWITCH) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ZLog.d(ZixieIME.TAG, "$this:onResume")
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isAdded) {
                fragment.userVisibleHint = true
            }
        }
    }

    override fun onBackPressedSupport() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        ZLog.d(ZixieIME.TAG, "$this:onPause")
    }
}
