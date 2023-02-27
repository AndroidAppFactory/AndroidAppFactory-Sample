package com.bihe0832.android.base.adb.input.keyboard

import android.view.KeyEvent

/**
 * @author zixie code@bihe0832.com Created on 5/31/21.
 */
interface InputToolBarActionListener {
    fun onBack():Boolean
    fun onClearText():Boolean
    fun enterAction():Boolean
    fun onSendKeyEvent(keyEvent: KeyEvent?):Boolean
    fun closeKeyboard():Boolean
}