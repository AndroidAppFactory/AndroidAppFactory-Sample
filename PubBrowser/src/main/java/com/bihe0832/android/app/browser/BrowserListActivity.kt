package com.bihe0832.android.app.browser

import android.os.Bundle
import android.view.View
import com.bihe0832.android.app.getapk.R
import com.bihe0832.android.app.router.RouterConstants.MODULE_NAME_BROWSER_LIST
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.easyrefresh.CommonListActivity
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import kotlinx.android.synthetic.main.activity_browser_list.*

@Module(MODULE_NAME_BROWSER_LIST)
open class BrowserListActivity : CommonListActivity() {

    override fun getResID(): Int {
        return R.layout.activity_browser_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start_new.setOnClickListener {
            var result = new_url.text.toString()
            if(!result.startsWith("http")){
                result = "http://$result"
            }
            openMain(result)
        }
    }

    open fun getDataList(): ArrayList<CardBaseModule> {
        var data = ArrayList<CardBaseModule>()
        CommonDBManager.getAll().let {
            if (it.isNotEmpty()) {
                data.add(TipsData("点击下方列表<b>信息</b>即可进入对应页面"))
                it.forEach { dataInfo ->
                    data.add(TipsData().apply {
                        this.mContentText = dataInfo.value
                        this.mListener = View.OnClickListener {
                            new_url.setText(dataInfo.value)
                            CommonDBManager.deleteData(dataInfo.key)
                        }
                    })
                }
            }
        }
        return data
    }

    private val mTestDataLiveData by lazy {
        object : CommonListLiveData() {
            override fun canRefresh(): Boolean {
                return false
            }

            override fun clearData() {
                postValue(mutableListOf())
            }

            override fun fetchData() {
                postValue(getDataList())
            }

            override fun getEmptyText(): String {
                return ""
            }

            override fun hasMore(): Boolean {
                return false
            }

            override fun loadMore() {
            }
        }
    }

    override fun updateData(data: List<CardBaseModule>) {
        super.updateData(data)
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
            add(CardItemForCommonList(TipsData::class.java))
        }
    }

    override fun getTitleText(): String {
        return "浏览记录"
    }

    override fun onBack() {
        ZixieContext.exitAPP()
    }
}