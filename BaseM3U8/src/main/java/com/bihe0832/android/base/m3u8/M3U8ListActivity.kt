package com.bihe0832.android.base.m3u8

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.bihe0832.android.app.router.RouterConstants.MODULE_NAME_M3U8_LIST
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.framework.R
import com.bihe0832.android.framework.ui.list.CardItemForCommonList
import com.bihe0832.android.framework.ui.list.CommonListLiveData
import com.bihe0832.android.framework.ui.list.easyrefresh.CommonListActivity
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.ui.recycleview.ext.SafeGridLayoutManager

@Module(MODULE_NAME_M3U8_LIST)
class M3U8ListActivity : CommonListActivity() {
    val mDataList = ArrayList<CardBaseModule>()
    var num = 0
    override fun getLayoutManagerForList(): RecyclerView.LayoutManager {
        return SafeGridLayoutManager(this, 4)
    }

    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(TipsData::class.java, true))
        }
    }

    override fun getNavigationBarColor(): Int {
        return ContextCompat.getColor(this, R.color.transparent)
    }

    override fun getDataLiveData(): CommonListLiveData {
        return object : CommonListLiveData() {
            override fun fetchData() {
                mDataList.addAll(getTempData())
                postValue(mDataList)
            }

            override fun clearData() {
                mDataList.clear()
                num = 0
            }

            override fun loadMore() {
                num ++
                mDataList.addAll(getTempData())
                postValue(mDataList)
            }

            override fun hasMore(): Boolean {
                return num < 5;
            }

            override fun canRefresh(): Boolean {
                return true
            }

            override fun getEmptyText(): String {
                return ""
            }
        }
    }

    private fun getTempData(): List<CardBaseModule> {
        return mutableListOf<CardBaseModule>().apply {
        }
    }

    override fun getTitleText(): String {
        return "Section测试"
    }
}