package com.bihe0832.android.base.adb.input.settings

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.AAFCommonMainActivity
import com.bihe0832.android.base.adb.input.ZixieIME
import com.bihe0832.android.base.adb.input.switcher.InputSwitchTools
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.router.annotation.Module


@Module(RouterConstants.MODULE_NAME_INPUT_GUIDE)
class InputSettingsActivity : AAFCommonMainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showInputMethodPicker(intent)
    }

    override fun getTitleName(): String {
        return "输入法设置引导"
    }

    override fun getRootFragmentClassName(): String {
        return InputSettingsFragment::class.java.name
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
}
