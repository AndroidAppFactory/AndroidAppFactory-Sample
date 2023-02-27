package com.bihe0832.android.base.adb.input.switcher

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.base.adb.input.ZixieIME
import com.bihe0832.android.base.adb.input.settings.InputSettingsActivity
import com.bihe0832.android.framework.R
import com.bihe0832.android.lib.notification.NotifyManager
import com.bihe0832.android.lib.permission.ui.PermissionDialog
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.utils.intent.IntentUtils

/**
 * @author zixie code@bihe0832.com Created on 4/9/21.
 */
object InputSwitchTools {

    private const val CHANNEL_ID = "voce_input"
    private const val NOTIFYL_ID = 1

    fun hasInstall(context: Context?): Boolean {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.let {
            it.enabledInputMethodList.forEach { inputMethodInfo ->
                if (inputMethodInfo.id.equals(ZixieIME.IME_NAME)) {
                    return true
                }
            }
        }
        return false
    }

    fun closeNotify(context: Context?) {
        context?.let {
            NotifyManager.cancelNotify(it, NOTIFYL_ID)
        }
    }

    fun showInputSwitchNotify(context: Context?) {
        ThreadManager.getInstance().start {
            context?.let {
                NotificationCompat.Builder(it, CHANNEL_ID).apply {
                    setSubText("输入法快捷设置")
                    setContentTitle("点击快速进入输入法设置页面")
                    setAutoCancel(false)
                    setSmallIcon(R.mipmap.icon)
                    setOngoing(true)
                    setWhen(System.currentTimeMillis())
                    setDefaults(Notification.DEFAULT_ALL)
                    setPriority(NotificationCompat.PRIORITY_HIGH)
                    try {
                        val resultIntent = Intent(context, InputSettingsActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra(RouterConstants.INTENT_EXTRA_KEY_INPUT_GUIDE_TYPE, RouterConstants.INTENT_EXTRA_VALUE_INPUT_SWITCH)
                        }
                        val pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                        setContentIntent(pendingIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.build().let { notification ->
                    ThreadManager.getInstance().runOnUIThread {
                        NotifyManager.sendNotifyNow(context, CHANNEL_ID, notification, NOTIFYL_ID)
                    }
                }
            }
        }
    }

    fun checkPermissionAndShow(activity: Activity) {
        if (NotificationManagerCompat.from(activity).areNotificationsEnabled()) {
            showInputSwitchNotify(activity)
        } else {
            PermissionDialog(activity).let {
                it.show("切换输入法", "通知", true, object : OnDialogListener {
                    override fun onPositiveClick() {
                        IntentUtils.startAppSettings(activity, Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        it.dismiss()
                    }

                    override fun onNegativeClick() {
                        showInputSwitchNotify(activity)
                        it.dismiss()
                    }

                    override fun onCancel() {
                        it.dismiss()
                    }
                })
            }
        }
    }
}