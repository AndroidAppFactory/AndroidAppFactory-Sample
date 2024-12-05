package com.bihe0832.android.test.module

import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.item.getRouterItem


class DebugRouterFragment : com.bihe0832.android.common.debug.module.DebugRouterFragment() {

    override fun getRouterList(): ArrayList<DebugItemData> {
        return ArrayList<DebugItemData>().apply {
            add(getRouterItem("zixie://zweb?url=https%3A%2F%2Fblog.bihe0832.com"))
            add(getRouterItem("zixie://zfeedback?url=https%3A%2F%2Fsupport.qq.com%2Fproduct%2F290858"))
            add(getRouterItem((RouterHelper.getFinalURL(RouterConstants.MODULE_NAME_BASE_ABOUT))))
            add(getRouterItem("zm3u8://m3u8"))
            add(getRouterItem("zm3u8://m3u8list"))
            add(getRouterItem(RouterHelper.getFinalURL(RouterConstants.MODULE_NAME_INPUT_GUIDE)))
            add(getRouterItem(RouterHelper.getFinalURL(RouterConstants.MODULE_NAME_MESSAGE)))
        }
    }

}