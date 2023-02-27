package com.bihe0832.android.base.adb.input.config

import com.bihe0832.android.lib.config.Config

/**
 * @author zixie code@bihe0832.com Created on 4/9/21.
 */
object InputSwitchConfig {

    private val KEY_NOTIFY = "com.bihe0832.input.config.notify"
    private val KEY_RECORD = "com.bihe0832.input.config.record"

    fun canNotify(): Boolean {
        return Config.isSwitchEnabled(KEY_NOTIFY, true)
    }

    fun setNotify(notify: Boolean) {
        Config.writeConfig(KEY_NOTIFY, notify)
    }

    fun canRecord(): Boolean {
        return Config.isSwitchEnabled(KEY_RECORD, true)
    }

    fun setRecord(record: Boolean) {
        Config.writeConfig(KEY_RECORD, record)
    }

}