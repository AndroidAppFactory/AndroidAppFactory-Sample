package com.bihe0832.android.base.game;

import android.content.Context;

import com.bihe0832.android.framework.ui.main.CommonRootActivity;

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2023/4/7.
 * Description: Description
 */
public class GameWrapper {
    public static void startGame(Context context, Class cls, String titleName) {
        CommonRootActivity.startCommonRootActivity(context, cls, titleName);
    }
}
