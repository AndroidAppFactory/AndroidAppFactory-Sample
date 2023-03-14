package com.bihe0832.android.base.adb.input;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bihe0832.android.base.adb.input.keyboard.InputManager;
import com.bihe0832.android.base.adb.input.keyboard.InputManager.InputServiceActionListener;
import com.bihe0832.android.base.adb.input.service.AdbReceiver;
import com.bihe0832.android.framework.log.LoggerTrace;
import com.bihe0832.android.lib.log.ZLog;

import org.jetbrains.annotations.NotNull;


public class ZixieIME extends InputMethodService {

    public static final String SPECIAL_CHAR_ENTER = "\n";
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
            IntentFilter filter = new IntentFilter(AdbReceiver.IME_ADB_INPUT_ACTION_TEXT);
            filter.addAction(AdbReceiver.IME_ADB_INPUT_ACTION_BASE64);
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


}
