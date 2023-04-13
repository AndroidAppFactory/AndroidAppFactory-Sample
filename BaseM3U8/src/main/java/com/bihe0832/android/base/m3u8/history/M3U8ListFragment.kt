package com.bihe0832.android.base.m3u8.history

import android.view.View
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.base.card.m3u8.M3U8ViewData
import com.bihe0832.android.base.card.m3u8.M3U8ViewHolder
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.base.m3u8.M3U8ModuleManager
import com.bihe0832.android.base.m3u8.R
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.base.m3u8.tools.M3U8Tools
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.easyrefresh.CommonListFragment
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.ui.dialog.impl.DialogUtils

open class M3U8ListFragment : CommonListFragment() {

    override fun getResID(): Int {
        return R.layout.fragment_m3u8_list
    }

    override fun initView(view: View) {
        super.initView(view)
        view.findViewById<View>(R.id.start_new).setOnClickListener {
            RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_M3U8)
        }
    }

    open fun getDataList(): ArrayList<CardBaseModule> {
        var data = ArrayList<CardBaseModule>()
        M3U8DBManager.getAll().let { it ->
            if (it.isNotEmpty()) {
                data.add(TipsData("点击下方列表<b>下载信息</b>即可进入对应视频下载页面"))
                it.forEach { m3u8Info ->
                    ZLog.d(m3u8Info.toString())
                    data.add(M3U8ViewData().apply {
                        this.m3u8URL = m3u8Info.getM3u8URL()
                        this.baseURl = m3u8Info.getBaseURL()
                        this.downloadTime = m3u8Info.getDownloadTime()
                        this.localpath = M3U8ModuleManager.getDownloadPath(m3u8Info.getM3u8URL())
                        this.mListener = object : M3U8ViewHolder.OnClickListener {
                            override fun onClick() {
                                var data = HashMap<String, String>().apply {
                                    put(RouterConstants.INTENT_EXTRA_KEY_WEB_URL, m3u8Info.getM3u8URL())
                                    put(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL, m3u8Info.getBaseURL())
                                }
                                RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_M3U8, data)
                            }

                            override fun onDelete() {
                                activity?.let {
                                    DialogUtils.showConfirmDialog(it, "删除M3U8下载历史", "确定删除视频<BR>${m3u8Info.getFinalVideoName()}<BR>的下载记录么？<BR>" + m3u8Info.getM3u8URL(), "再想想", "删除", true, object : OnDialogListener {
                                        override fun onPositiveClick() {

                                        }

                                        override fun onNegativeClick() {
                                            M3U8DBManager.deleteData(m3u8Info.getM3u8URL())
                                            m3u8DataLiveData.refresh()
                                        }

                                        override fun onCancel() {

                                        }
                                    })
                                }

                            }
                        }
                    })
                }
            }
            return data
        }
    }

    private val m3u8DataLiveData by lazy {
        object : M3U8ListLiveData() {

            override fun initData() {
                postValue(getDataList())
            }

            override fun refresh() {

                postValue(getDataList())
            }
        }
    }

    override fun getDataLiveData(): CommonListLiveData {
        return m3u8DataLiveData
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean, hasCreateView: Boolean) {
        super.setUserVisibleHint(isVisibleToUser, hasCreateView)
        if (isVisibleToUser && hasCreateView) {
            m3u8DataLiveData.refresh()
        }
    }

    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(M3U8ViewData::class.java))
            add(CardItemForCommonList(TipsData::class.java))
        }
    }
}