package com.bihe0832.android.base.adb.input.settings

import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.bihe0832.android.app.router.openWebPage
import com.bihe0832.android.base.adb.input.R
import com.bihe0832.android.base.adb.input.config.InputSwitchConfig
import com.bihe0832.android.base.adb.input.switcher.InputSwitchTools
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.ui.BaseFragment
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.intent.IntentUtils
import kotlinx.android.synthetic.main.fragment_input_settings.*
import me.yokeyword.fragmentation.SupportActivity


/**
 * @author zixie code@bihe0832.com Created on 4/6/21.
 */
class InputSettingsFragment : BaseFragment() {

    override fun getLayoutID(): Int {
        return R.layout.fragment_input_settings
    }

    override fun initView(view: View) {
        super.initView(view)

        input_settings_global_input_add.setOnClickListener {
            IntentUtils.startSettings(activity, Settings.ACTION_INPUT_METHOD_SETTINGS)
        }

        input_settings_global_input_switch.setOnClickListener {
            (activity?.getSystemService(SupportActivity.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            ThreadManager.getInstance().start({
                ThreadManager.getInstance().runOnUIThread {
                    updateView()
                }
            }, 1)
        }

        input_settings_item_notify_switch.apply {
            isChecked = InputSwitchConfig.canNotify()
            setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    InputSwitchConfig.setNotify(isChecked)
                    updateView()
                }
            })
        }

        input_settings_item_save_switch.apply {
            isChecked = InputSwitchConfig.canRecord()
            setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    InputSwitchConfig.setRecord(isChecked)
                }
            })
        }

        var color = resources.getColor(R.color.colorPrimary)
        var tips = "1. 手机通过<b><font color='$color'>ADB</font>连接到电脑</b>，并将手机输入法<b>切换为<font color='$color'>${APKUtils.getAppName(context)}</font></b>" + "<BR>2. 激活需要输入内容的输入框" + "<BR>3. 在电脑端用命令行输入 <b><font color='$color'>adb shell am broadcast -a ZIXIE_ADB_INPUT --es msg \"DATA\"</font></b>，DATA 即为要输入的内容" + "<BR>4. <b>ADB 常用命令使用方法可以点击查看： <font color='$color'>https://blog.bihe0832.com/review_adb.html</font></b>"
        val list = HashMap<String, View.OnClickListener>().apply {
            put("https://blog.bihe0832.com/review_adb.html", View.OnClickListener {
                openWebPage("https://blog.bihe0832.com/review_adb.html")
            })
        }
        input_settings_input_help_tips.apply {
            setText(TextFactoryUtils.getCharSequenceWithClickAction(tips, list))
            setMovementMethod(LinkMovementMethod.getInstance())
        }

        updateView()
    }

    private fun updateView() {
        if (InputSwitchTools.hasInstall(context)) {
            input_settings_global_input_add.background = ContextCompat.getDrawable(context!!, R.drawable.keyboard_bg)
        } else {
            input_settings_global_input_add.background = ContextCompat.getDrawable(context!!, R.drawable.keyboard_enter_bg)
        }

        if (InputSwitchTools.isSelected(context)) {
            input_settings_global_input_switch.background = ContextCompat.getDrawable(context!!, R.drawable.keyboard_bg)
        } else {
            input_settings_global_input_switch.background = ContextCompat.getDrawable(context!!, R.drawable.keyboard_enter_bg)
        }

        if (!InputSwitchConfig.canNotify()) {
            InputSwitchTools.closeNotify(context)
        } else {
            InputSwitchTools.checkPermissionAndShow(activity!!)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean, hasCreateView: Boolean) {
        if (isVisibleToUser && hasCreateView) {
            updateView()
        }
    }
}