package com.bihe0832.android.test.module


class DebugRouterFragment : com.bihe0832.android.common.debug.module.DebugRouterFragment() {

    override fun getRouterList(): ArrayList<RouterItem> {
        return ArrayList<RouterItem>().apply {
            add(RouterItem("zixie://zweb?url=https%3A%2F%2Fblog.bihe0832.com"))
            add(RouterItem("zixie://zfeedback?url=https%3A%2F%2Fsupport.qq.com%2Fproduct%2F290858"))
            add(RouterItem("zixie://zabout"))
            add(RouterItem("zixie://m3u8"))
            add(RouterItem("zixie://m3u8list"))
            add(RouterItem("zixie://guide"))
        }
    }

}