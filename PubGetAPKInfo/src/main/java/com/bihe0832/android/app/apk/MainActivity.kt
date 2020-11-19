package com.bihe0832.android.app.apk

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.bihe0832.android.app.getapk.R
import com.bihe0832.android.app.router.RouterConstants.ROUTRT_NAME_APK_LIST
import com.bihe0832.android.base.card.apk.APPItemData
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.ui.list.CardItemForCommonList
import com.bihe0832.android.framework.ui.list.CommonListLiveData
import com.bihe0832.android.framework.ui.list.easyrefresh.swiperefresh.CommonListActivity
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.lifecycle.INSTALL_TYPE_NOT_FIRST
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.CommonDialog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.ui.recycleview.ext.SafeLinearLayoutManager
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.encypt.MD5

@APPMain
@Module(ROUTRT_NAME_APK_LIST)
class MainActivity : CommonListActivity() {
    var hasShowTips = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbar?.navigationIcon = resources.getDrawable(R.mipmap.ic_menu_white)
        initAdapter()
        showTips()
    }

    override fun getLayoutManagerForList(): RecyclerView.LayoutManager {
        return SafeLinearLayoutManager(this)
    }

    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(APPItemData::class.java))
            add(CardItemForCommonList(TipsData::class.java))
        }
    }

    override fun getDataLiveData(): CommonListLiveData {
        return object : CommonListLiveData() {
            override fun fetchData() {
                postValue(getTempData())
            }

            override fun clearData() {
            }

            override fun loadMore() {
                postValue(getTempData())
            }

            override fun hasMore(): Boolean {
                return false
            }

            override fun canRefresh(): Boolean {
                return true
            }

            override fun getEmptyText(): String {
                return "尚未安装应用"
            }
        }
    }

    private fun initAdapter() {
        mAdapter.apply {
            setOnItemClickListener { adapter, view, position ->
                var temp = adapter.data[position] as APPItemData
                if (temp.app_md5.isNullOrEmpty()) {
                    ThreadManager.getInstance().start {
                        try {

                            packageManager.getApplicationInfo(temp.app_package, PackageManager.GET_SIGNATURES)?.let {
                                temp.app_md5 = MD5.getFileMD5(it.sourceDir)
                                view.post {
                                    mAdapter.notifyDataSetChanged()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            setOnItemLongClickListener { adapter, view, position ->
                var temp = adapter.data[position] as APPItemData
                DebugTools.showInfo(this@MainActivity, temp.app_name + "基础信息", temp.toString(), "分享")
                return@setOnItemLongClickListener true
            }
        }
    }


    private fun showTips() {
        if (ZixieContext.isFirstStart() > INSTALL_TYPE_NOT_FIRST) {
            CommonDialog(this).apply {
                title = "温馨提示"
                positive = "我知道了"
                isSingle = true
                setHtmlContent(getTipsContent("#38ADFF"))
                setOnClickBottomListener(object : OnDialogListener {
                    override fun onPositiveClick() {
                        dismiss()
                    }

                    override fun onNegtiveClick() {
                        dismiss()
                    }

                    override fun onCloseClick() {
                        dismiss()
                    }
                })
                setShouldCanceled(true)
            }.show()
            hasShowTips = true
        }
    }

    override fun getTitleText(): String {
        return "应用信息查看"
    }

    private fun getTipsContent(color: String): String {
        return "1. <b><font color='$color'>点击</font>应用信息</b>，可以计算APK的MD5<BR>" +
                "2. <b><font color='$color'>长按</font>应用信息</b>，可以复制到剪切板或对外分享"
    }

    private fun getTempData(): List<CardBaseModule> {
        return mutableListOf<CardBaseModule>().apply {
            add(TipsData().apply {
                this.mContentText = "<big><b>使用说明：</b></big><BR>" + getTipsContent("#182B37")

            })
            var appList = packageManager.getInstalledApplications(PackageManager.GET_SIGNATURES)
            for (info in appList) {
                if (info.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    try {
                        var packageInfo = APKUtils.getInstalledPackage(applicationContext, info.packageName)
                        add(APPItemData().apply {
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
}