package com.bihe0832.android.base.m3u8.history

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterConstants.MODULE_NAME_M3U8_LIST
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.framework.ui.list.CommonListLiveData
import com.bihe0832.android.framework.ui.list.easyrefresh.CommonListActivity
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.router.annotation.Module

@Module(MODULE_NAME_M3U8_LIST)
class M3U8ListActivity : CommonListActivity() {

    open fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            M3U8DBManager.getAll().forEach { m3u8Info ->
                add(TipsData(m3u8Info.m3u8URL) {
                    var data = HashMap<String, String>().apply {
                        put(RouterConstants.INTENT_EXTRA_KEY_WEB_URL, m3u8Info.m3u8URL)
                        put(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL, m3u8Info.baseURL)

                    }
                    RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_M3U8, data)
                })
            }

        }
    }

    private val mTestDataLiveData by lazy {
        object : M3U8ListLiveData() {
            override fun fetchData() {
                postValue(getDataList())
            }
        }
    }

    override fun getDataLiveData(): CommonListLiveData {
        return mTestDataLiveData
    }

    override fun getTitleText(): String {
        return "M3U8下载记录"
    }

}