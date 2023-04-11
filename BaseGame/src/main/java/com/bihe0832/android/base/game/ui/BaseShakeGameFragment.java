package com.bihe0832.android.base.game.ui;

import android.view.View;

import androidx.annotation.NonNull;

import com.bihe0832.android.framework.ui.BaseFragment;
import com.bihe0832.android.lib.device.shake.ShakeManager;

public abstract class BaseShakeGameFragment extends BaseFragment {

    private boolean mIsLocked = false;


    @Override
    protected void initView(@NonNull View view) {
        super.initView(view);
        ShakeManager.INSTANCE.setOnShakeListener(new ShakeManager.OnShakeListener() {
            @Override
            public void onShake() {
                if (!mIsLocked) {
                    restartGame();
                }
            }
        });
        initGameUI();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser, boolean hasCreateView) {
        super.setUserVisibleHint(isVisibleToUser, hasCreateView);
        if (isVisibleToUser && hasCreateView) {
            ShakeManager.INSTANCE.start();
        } else {
            ShakeManager.INSTANCE.stop();
        }
    }

    public void restartGame() {
        resetGameUI();
        startGame();
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public void setLocked(boolean mIsLocked) {
        this.mIsLocked = mIsLocked;
    }

    public abstract void initGameUI();

    public abstract void startGame();

    public abstract void resetGameUI();

}
