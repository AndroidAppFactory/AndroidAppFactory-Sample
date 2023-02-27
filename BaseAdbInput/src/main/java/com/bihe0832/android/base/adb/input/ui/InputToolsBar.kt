package com.bihe0832.android.base.adb.input.ui

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import com.bihe0832.android.app.log.AAFLoggerFile
import com.bihe0832.android.base.adb.input.R
import com.bihe0832.android.base.adb.input.ZixieIME
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.base.adb.input.ZixieIME.TAG
import com.bihe0832.android.base.adb.input.keyboard.InputManager
import com.bihe0832.android.base.adb.input.keyboard.InputToolBarActionListener
import com.bihe0832.android.base.adb.input.tools.logInput
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.text.ClipboardUtil
import kotlinx.android.synthetic.main.layout_input_toolbar.view.*


class InputToolsBar : LinearLayout {
    private val mHandler = Handler()
    private var mPendingLongPressKey: LongPressKey? = null
    private var mInputToolBarActionListener: InputToolBarActionListener? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }


    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_input_toolbar, this)
        initToolBar()
    }

    private fun initToolBar() {
        keyboard_switch.setOnClickListener {
            InputManager.switchInput()
        }

        keyboard_settings.setOnClickListener {
            InputManager.openSettings()
        }

        keyboard_logcat.setOnClickListener {
            logInput(TAG + " 剪切板数据：\n \n" + ClipboardUtil.pasteFromClipboard(context) + "\n \n")
            ZixieContext.showToast("剪切板数据已经打印到Logcat，TAG 为：$TAG")
        }

        keyboard_hide.setOnClickListener {
            InputManager.hideWindow()
        }

        keyboard_back.setOnClickListener {
            if (mInputToolBarActionListener?.onBack() != true) {
                InputManager.sendDeleteAction()
            }
        }

        keyboard_back.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                stopLongPress()
            } else if (event?.action == MotionEvent.ACTION_DOWN) {
                startLongPress(KeyEvent.KEYCODE_DEL)
            }
            false
        }

        keyboard_clear.setOnClickListener {
            if (mInputToolBarActionListener?.onClearText() != true) {
                InputManager.clearText()
            }
        }

        keyboard_enter.setOnClickListener {
            if (mInputToolBarActionListener?.enterAction() != true) {
                InputManager.sendEnterAction()
            }
        }
    }

    inner class LongPressKey(keyCode: Int, handler: Handler) : Runnable {
        private val currentKeycode = keyCode
        private val handler = handler
        override fun run() {
            InputManager.onSendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, currentKeycode))
            handler.postDelayed(this, 200)
        }
    }

    private fun startLongPress(keyCode: Int) {
        ZLog.e(TAG, "checkForLongClick")
        if (mPendingLongPressKey == null) {
            mPendingLongPressKey = LongPressKey(keyCode, mHandler)
        }
        mHandler.postDelayed(mPendingLongPressKey!!, 400)
    }

    private fun stopLongPress() {
        ZLog.e(TAG, "removeLongPressCallback")
        if (mPendingLongPressKey != null) {
            mHandler.removeCallbacks(mPendingLongPressKey!!)
            mPendingLongPressKey = null
        }
    }
}