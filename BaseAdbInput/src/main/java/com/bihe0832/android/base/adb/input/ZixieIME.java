package com.bihe0832.android.base.adb.input;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bihe0832.android.base.adb.input.keyboard.InputManager;
import com.bihe0832.android.base.adb.input.keyboard.InputManager.InputServiceActionListener;
import com.bihe0832.android.framework.log.LoggerTrace;
import com.bihe0832.android.lib.log.ZLog;

import org.jetbrains.annotations.NotNull;


public class ZixieIME extends InputMethodService {

    public String IME_ADB_INPUT_ACTION_TEXT = "ZIXIE_ADB_INPUT_TEXT";
    public String IME_ADB_INPUT_ACTION_TEXT_COMMIT = "ZIXIE_ADB_INPUT_TEXT_COMMIT";

    public String IME_ADB_INPUT_ACTION_BASE64 = "ZIXIE_ADB_INPUT_BASE64";
    public String IME_ADB_INPUT_ACTION_BASE64_COMMIT = "ZIXIE_ADB_INPUT_BASE64_COMMIT";

    public static final String TAG = "InputService";
    private InputServiceActionListener mInputServiceActionListener = null;

    private BroadcastReceiver mReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        InputManager.INSTANCE.setInputMethodService(this);
    }

    @Override
    public View onCreateInputView() {
        InputManager.INSTANCE.onCreateInputView(this);
        View mInputView = getLayoutInflater().inflate(R.layout.layout_input_keyboard_global, null);
        initView(mInputView);
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter(IME_ADB_INPUT_ACTION_TEXT);
            filter.addAction(IME_ADB_INPUT_ACTION_TEXT_COMMIT);
            filter.addAction(IME_ADB_INPUT_ACTION_BASE64);
            filter.addAction(IME_ADB_INPUT_ACTION_BASE64_COMMIT);
            mReceiver = new AdbReceiver();
            registerReceiver(mReceiver, filter);
        }
        return mInputView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        ZLog.d(TAG, this + ":onStartInputView");
        super.onStartInputView(info, restarting);
        InputManager.INSTANCE.onStartInputView(info, restarting);
    }


    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        ZLog.d(TAG, this.toString() + ":onFinishInputView");
        InputManager.INSTANCE.onFinishInputView(finishingInput);
    }

    @Override
    public void onDestroy() {
        ZLog.d(TAG, this.toString() + ":onDestroy");
        InputManager.INSTANCE.clearInputServiceActionListener();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }


    @Override
    public View onCreateCandidatesView() {
        View mInputView = getLayoutInflater().inflate(R.layout.layout_input_toolbar, null);
        return mInputView;
    }

    private final void initView(final View mInputView) {
        InputManager.INSTANCE.addInputServiceActionListener(new InputServiceActionListener() {
            @Override
            public void onStartInputView(@NotNull EditorInfo info, boolean restarting) {
                if (null != mInputServiceActionListener) {
                    mInputServiceActionListener.onStartInputView(info, restarting);
                }
            }

            @Override
            public void onFinishInputView(boolean finishingInput) {
                if (null != mInputServiceActionListener) {
                    mInputServiceActionListener.onFinishInputView(finishingInput);
                }
            }

            @Override
            public void onCreateInputView() {
                if (null != mInputServiceActionListener) {
                    mInputServiceActionListener.onCreateInputView();
                }
            }

            @Override
            public void onChangeInputType(int currentInputType) {
                LoggerTrace.INSTANCE.showResult(TAG);
                if (null != mInputServiceActionListener) {
                    mInputServiceActionListener.onChangeInputType(currentInputType);
                }
            }
        });
    }

    class AdbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.startsWith(IME_ADB_INPUT_ACTION_TEXT)){
                String msg = intent.getStringExtra("msg");
                if (msg != null) {
                    InputManager.INSTANCE.commitResultText(msg);
                }
                if (intent.getAction().equals(IME_ADB_INPUT_ACTION_TEXT_COMMIT)) {
                    InputManager.INSTANCE.sendEnterAction();
                }
            }else if (action.startsWith(IME_ADB_INPUT_ACTION_BASE64)){
                String msg = intent.getStringExtra("msg");
                if (msg != null) {
                    String result = new String(Base64.decode(msg, Base64.DEFAULT));
                    InputManager.INSTANCE.commitResultText(result);
                }
                if (intent.getAction().equals(IME_ADB_INPUT_ACTION_BASE64_COMMIT)) {
                    InputManager.INSTANCE.sendEnterAction();
                }
            }
        }
    }

}
