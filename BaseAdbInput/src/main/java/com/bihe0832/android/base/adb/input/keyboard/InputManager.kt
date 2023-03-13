package com.bihe0832.android.base.adb.input.keyboard


import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.bihe0832.android.base.adb.input.settings.InputSettingsActivity
import com.bihe0832.android.base.adb.input.switcher.InputSwitchTools
import com.bihe0832.android.base.adb.input.tools.logInput
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.log.ZLog
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author zixie code@bihe0832.com Created on 4/9/21.
 */
object InputManager {

    private const val LENGTH_PER = 100
    private val mInputServiceActionListener = CopyOnWriteArrayList<InputServiceActionListener>()
    private var mInputMethodService: InputMethodService? = null

    interface InputServiceActionListener {
        fun onStartInputView(info: EditorInfo, restarting: Boolean)
        fun onFinishInputView(finishingInput: Boolean)
        fun onCreateInputView()
        fun onChangeInputType(type: Int)
    }

    fun setInputMethodService(service: InputMethodService) {
        mInputMethodService = service
    }

    fun getCurrentInputConnection(): InputConnection? {
        return mInputMethodService?.currentInputConnection
    }

    fun addInputServiceActionListener(listener: InputServiceActionListener) {
        mInputServiceActionListener.add(listener)
    }

    fun clearInputServiceActionListener() {
        mInputServiceActionListener.clear()
    }

    fun onCreateInputView(context: Context?) {
        InputSwitchTools.showInputSwitchNotify(context)
        mInputServiceActionListener.forEach {
            it.onCreateInputView()
        }
    }

    fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        mInputServiceActionListener.forEach {
            it.onStartInputView(info, restarting)
        }
    }

    fun hideWindow() {
        mInputMethodService?.hideWindow()
    }

    fun sendDeleteAction() {
        onSendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
    }

    fun onSendKeyEvent(keyEvent: KeyEvent) {
        ZLog.d("发送按键事件：$keyEvent")
        getCurrentInputConnection()?.sendKeyEvent(keyEvent)
    }

    fun sendEnterAction() {
        ZLog.d("发送回车")
        mInputMethodService?.sendKeyChar('\n')
    }

    fun setKeyChar(charCode: Char) {
        mInputMethodService?.sendKeyChar(charCode)
    }

    fun clearText() {
        ZLog.d("清空输入框")
        val totalNum: Int = getTotalNum()
        getCurrentInputConnection()!!.deleteSurroundingText(totalNum, totalNum)
        onSendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
    }

    private fun getTotalNum(): Int {
        var totalNum = 0
        var charSequence: CharSequence
        do {
            charSequence = getCurrentInputConnection()?.getTextBeforeCursor(totalNum + LENGTH_PER, InputConnection.GET_TEXT_WITH_STYLES).toString()
            totalNum = totalNum + LENGTH_PER
        } while (charSequence.length >= totalNum)
        do {
            charSequence = getCurrentInputConnection()?.getTextAfterCursor(totalNum + LENGTH_PER, InputConnection.GET_TEXT_WITH_STYLES).toString()
            totalNum = totalNum + LENGTH_PER
        } while (charSequence.length >= totalNum)
        return totalNum
    }

    fun onFinishInputView(finishingInput: Boolean) {
        mInputServiceActionListener.forEach {
            it.onFinishInputView(finishingInput)
        }
    }


    fun switchInput() {
        (ZixieContext.applicationContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
    }

    fun openSettings() {
        val intent = Intent()
        intent.setClass(ZixieContext.applicationContext!!, InputSettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ZixieContext.applicationContext!!.startActivity(intent)
    }

    fun commitResultText(text: String) {
        logInput(text)
        getCurrentInputConnection()?.commitText(text, 1)
    }
}