package com.bihe0832.android.app.browser;


import android.os.Bundle;
import com.bihe0832.android.common.webview.base.BaseWebviewFragment;
import com.bihe0832.android.common.webview.log.MyBaseJsBridgeProxy;
import com.bihe0832.android.lib.log.ZLog;
import com.bihe0832.android.lib.request.URLUtils;
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager;
import com.bihe0832.android.lib.utils.intent.IntentUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @author hardyshi code@bihe0832.com
 *         Created on 2019-07-26.
 *         Description: Description
 */
public class BrowserFragment extends BaseWebviewFragment {

    public static BrowserFragment newInstance(String url) {
        BrowserFragment fragment = new BrowserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_KEY_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected String getFinalURL(String url) {
        return url;
    }

    @Override
    protected String getUserAgentString() {
        return "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    }

    @Override
    protected boolean loadUseIntent(String url) {
        return false;
    }

    @Override
    protected WebViewClient getWebViewClient() {
        return new MySpecialWebView(getJsBridgeProxy());
    }

    private class MySpecialWebView extends MyWebViewClient {

        public MySpecialWebView(MyBaseJsBridgeProxy jsBridge) {
            super(jsBridge);
        }

        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            ZLog.d("WebPageFragment -> :shouldInterceptRequest url:" + url);
            saveURL(url);
            return super.shouldInterceptRequest(view, url);
        }

        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            ZLog.d("WebPageFragment -> :shouldInterceptRequest url:" + request.getUrl().toString());
            saveURL(request.getUrl().toString());
            return super.shouldInterceptRequest(view, request);
        }

        private void saveURL(String url) {
            if (url.endsWith(".m3u8")) {
                CommonDBManager.INSTANCE.saveData(url, mIntentUrl);
                IntentUtils.jumpToOtherApp("zm3u8://m3u8?url=" + URLUtils.encode(url), getContext());
            }
        }
    }

    @Override
    protected MyBaseJsBridgeProxy getJsBridgeProxy() {
        return new MyBaseJsBridgeProxy(mWebView, getActivity());
    }

    @Override
    protected void actionBeforeLoadURL(String url) {
        syncCookie();
    }
}
