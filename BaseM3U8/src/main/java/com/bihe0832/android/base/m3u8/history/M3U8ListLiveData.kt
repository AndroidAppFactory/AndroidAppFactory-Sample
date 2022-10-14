package com.bihe0832.android.base.m3u8.history

import com.bihe0832.android.common.list.CommonListLiveData


/**
 *
 * @author hardyshi code@bihe0832.com Created on 2020/12/1.
 *
 */
abstract class M3U8ListLiveData : CommonListLiveData() {

    override fun loadMore() {

    }

    override fun hasMore(): Boolean {
        return false
    }

    override fun canRefresh(): Boolean {
        return false
    }

    override fun refresh() {
    }
}