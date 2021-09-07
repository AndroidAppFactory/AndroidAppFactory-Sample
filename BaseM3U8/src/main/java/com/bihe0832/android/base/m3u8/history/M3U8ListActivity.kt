package com.bihe0832.android.base.m3u8.history

import android.os.Bundle
import android.view.View
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterConstants.MODULE_NAME_M3U8_LIST
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.base.card.m3u8.M3U8ViewData
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.base.m3u8.M3U8ModuleManager
import com.bihe0832.android.base.m3u8.R
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.easyrefresh.CommonListActivity
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.router.annotation.Module
import kotlinx.android.synthetic.main.m3u8_activity_list.*

@Module(MODULE_NAME_M3U8_LIST)
open class M3U8ListActivity : CommonListActivity() {

    override fun getResID(): Int {
        return R.layout.m3u8_activity_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start_new.setOnClickListener {
            RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_M3U8)
        }
    }

    open fun getDataList(): ArrayList<CardBaseModule> {
        var data = ArrayList<CardBaseModule>()
        M3U8DBManager.getAll().let {
            if (it.isNotEmpty()) {
                data.add(TipsData("点击下方列表<b>下载信息</b>即可进入对应视频下载页面"))
                it.forEach { m3u8Info ->
                    data.add(M3U8ViewData().apply {
                        this.m3u8URL = m3u8Info.m3u8URL
                        this.baseURl = m3u8Info.baseURL
                        this.downloadTime = m3u8Info.downloadTime
                        this.localpath = M3U8ModuleManager.getDownloadPath(m3u8Info.m3u8URL)
                        this.mListener = View.OnClickListener {
                            var data = HashMap<String, String>().apply {
                                put(RouterConstants.INTENT_EXTRA_KEY_WEB_URL, m3u8Info.m3u8URL)
                                put(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL, m3u8Info.baseURL)
                            }
                            RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_M3U8, data)
                        }
                    })
                }
            }
        }
        return data
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

    override fun onResume() {
        super.onResume()
        mTestDataLiveData.fetchData()
    }
    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(M3U8ViewData::class.java))
            add(CardItemForCommonList(TipsData::class.java))
        }
    }

    override fun getTitleText(): String {
        return "M3U8下载记录"
    }
}