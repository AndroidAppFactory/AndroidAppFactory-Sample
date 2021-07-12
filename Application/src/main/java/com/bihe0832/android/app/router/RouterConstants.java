package com.bihe0832.android.app.router;


import com.bihe0832.android.app.about.AboutActivityKt;
import com.bihe0832.android.common.feedback.FeedbackActivity;
import com.bihe0832.android.common.webview.base.BaseWebviewFragment;
import com.bihe0832.android.common.webview.WebPageActivity;

/**
 * Created by hardyshi on 2017/10/25.
 */

public class RouterConstants extends com.bihe0832.android.framework.router.RouterConstants {

    public static final String MODULE_NAME_WEB_PAGE = WebPageActivity.MODULE_NAME_WEB_PAGE;
    public static final String MODULE_NAME_FEEDBACK = FeedbackActivity.MODULE_NAME_FEEDBACK;
    public static final String INTENT_EXTRA_KEY_WEB_URL = BaseWebviewFragment.INTENT_KEY_URL;
    public static final String MODULE_NAME_BASE_ABOUT = AboutActivityKt.MODULE_NAME_BASE_ABOUT;
    public static final String MODULE_NAME_APK_LIST = "apklist";
    public static final String MODULE_NAME_PUZZLE_GAME = "puzzlegame";
    public static final String MODULE_NAME_PUZZLE = "puzzle";
    public static final String MODULE_NAME_M3U8 = "m3u8";
    public static final String INTENT_EXTRA_KEY_M3U8_BASE_URL = "baseurl";

    public static final String MODULE_NAME_M3U8_LIST = "m3u8list";
    public static final String MODULE_NAME_MAIN = "main";

}
