package com.bihe0832.android.test.module

import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper


class DebugRouterFragment : com.bihe0832.android.common.debug.module.DebugRouterFragment() {

    override fun getRouterList(): ArrayList<RouterItem> {
        return ArrayList<RouterItem>().apply {
            add(RouterItem("zixie://zweb?url=https%3A%2F%2Fblog.bihe0832.com"))
            add(RouterItem("zixie://zfeedback?url=https%3A%2F%2Fsupport.qq.com%2Fproduct%2F290858"))
            add(RouterItem((RouterHelper.getFinalURL(RouterConstants.MODULE_NAME_BASE_ABOUT))))
            add(RouterItem("zm3u8://m3u8"))
            add(RouterItem("zm3u8://m3u8list"))
            add(RouterItem(RouterHelper.getFinalURL(RouterConstants.MODULE_NAME_INPUT_GUIDE)))
            add(RouterItem(RouterHelper.getFinalURL(RouterConstants.MODULE_NAME_MESSAGE)))
        }
    }

}