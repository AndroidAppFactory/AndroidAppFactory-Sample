package com.bihe0832.android.app.apk

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.bihe0832.android.app.getapk.R
import com.bihe0832.android.app.router.RouterConstants.ROUTRT_NAME_APK_LIST
import com.bihe0832.android.base.card.apk.APPItemData
import com.bihe0832.android.framework.ui.list.CardItemForCommonList
import com.bihe0832.android.framework.ui.list.CommonListLiveData
import com.bihe0832.android.framework.ui.list.easyrefresh.swiperefresh.CommonListActivity
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.recycleview.ext.SafeLinearLayoutManager
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.encypt.MD5


@Module(ROUTRT_NAME_APK_LIST)
class MainActivity : CommonListActivity() {
    val mDataList = ArrayList<CardBaseModule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbar?.navigationIcon = resources.getDrawable(R.mipmap.ic_menu_white)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            try {
                var temp = adapter.data[position] as APPItemData
                ThreadManager.getInstance().start {
                    packageManager.getApplicationInfo(temp.app_package, PackageManager.GET_SIGNATURES)?.let {
                        temp.app_md5 = MD5.getFileMD5(it.sourceDir)
                        mAdapter.notifyItemChanged(position)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            var temp = adapter.data[position] as APPItemData
            DebugTools.sendInfo(applicationContext, "分享" + temp.app_name + " 信息", temp.toString())
            return@setOnItemLongClickListener true
        }
    }

    override fun getLayoutManagerForList(): RecyclerView.LayoutManager {
        return SafeLinearLayoutManager(this)
    }

    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(APPItemData::class.java))
        }
    }

    override fun getDataLiveData(): CommonListLiveData {
        return object : CommonListLiveData() {
            override fun fetchData() {
                mDataList.addAll(getTempData())
                postValue(mDataList)
            }

            override fun clearData() {
                mDataList.clear()
            }

            override fun loadMore() {
                mDataList.addAll(getTempData())
                postValue(mDataList)
            }

            override fun hasMore(): Boolean {
                return false
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
            var appList = packageManager.getInstalledApplications(PackageManager.GET_SIGNATURES)
            for (info in appList) {
                if (info.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    try {
                        var packageInfo = APKUtils.getInstalledPackage(applicationContext, info.packageName)
                        mDataList.add(APPItemData().apply {
                            this.app_icon = packageInfo?.applicationInfo?.loadIcon(packageManager)
                            this.app_name = packageInfo?.applicationInfo?.loadLabel(packageManager)?.toString()
                                    ?: ""
                            this.app_version = packageInfo.versionName + "." + packageInfo.versionCode
                            this.app_package = info.packageName
                            this.signature_md5 = APKUtils.getSigMd5ByPkgName(applicationContext, info.packageName)
                            this.app_install_time = packageInfo.firstInstallTime
                            this.app_update_time = packageInfo.lastUpdateTime
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun getTitleText(): String {
        return "应用列表"
    }
}