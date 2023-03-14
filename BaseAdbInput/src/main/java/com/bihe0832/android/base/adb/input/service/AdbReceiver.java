package com.bihe0832.android.base.adb.input.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import com.bihe0832.android.base.adb.input.ZixieIME;
import com.bihe0832.android.base.adb.input.keyboard.InputManager;

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2023/3/14.
 * Description: Description
 */
public class AdbReceiver extends BroadcastReceiver {

    public static final String IME_ADB_INPUT_ACTION_TEXT = "ZIXIE_ADB_INPUT_TEXT";
    public static final String IME_ADB_INPUT_ACTION_BASE64 = "ZIXIE_ADB_INPUT_BASE64";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.startsWith(IME_ADB_INPUT_ACTION_TEXT)) {
            String msg = intent.getStringExtra("msg");
            if (msg != null) {
                InputManager.INSTANCE.commitResultText(msg);
            }

        } else if (action.startsWith(IME_ADB_INPUT_ACTION_BASE64)) {
            String msg = intent.getStringExtra("msg");
            if (msg != null) {
                String result = new String(Base64.decode(msg, Base64.DEFAULT));
                if (result.endsWith(ZixieIME.SPECIAL_CHAR_ENTER)) {
                    InputManager.INSTANCE.commitResultText(result.substring(0, result.lastIndexOf(ZixieIME.SPECIAL_CHAR_ENTER)));
                } else {
                    InputManager.INSTANCE.commitResultText(result);
                }

            }
        }
    }
}