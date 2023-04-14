package com.bihe0832.android.app.apk

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import com.bihe0832.android.app.getapk.R
import com.bihe0832.android.base.card.apk.APPItemData
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.swiperefresh.CommonListFragment
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.install.InstallUtils
import com.bihe0832.android.lib.lifecycle.INSTALL_TYPE_NOT_FIRST
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.impl.DialogUtils
import com.bihe0832.android.lib.ui.dialog.input.InputDialogCompletedCallback
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.encrypt.MD5
import java.util.*

class MainFragment : CommonListFragment() {
    var hasShowTips = false
    var defaultSignatureType = MD5.MESSAGE_DIGEST_TYPE_MD5

    private val mCommonListLiveData = object : CommonListLiveData() {

        override fun loadMore() {
            postValue(getTempData())
        }

        override fun refresh() {
            postValue(getTempData())
        }

        override fun hasMore(): Boolean {
            return false
        }

        override fun initData() {
            postValue(getTempData())
        }

        override fun canRefresh(): Boolean {
            return true
        }

    }

    override fun initView(view: View) {
        super.initView(view)
        initAdapter()
        showTips()
    }

    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(APPItemData::class.java))
            add(CardItemForCommonList(TipsData::class.java))
        }
    }

    override fun getDataLiveData(): CommonListLiveData {
        return mCommonListLiveData
    }

    private fun initAdapter() {
        mAdapter.apply {
            setOnItemChildClickListener { adapter, view, position ->
                var temp = adapter.data[position] as APPItemData
                when (view.id) {
                    R.id.app_layout -> {
                        if (temp.app_md5.isNullOrEmpty()) {
                            ThreadManager.getInstance().start {
                                try {
                                    context?.packageManager?.getApplicationInfo(temp.app_package, PackageManager.GET_SIGNATURES)?.let {
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
                    R.id.uninstall -> {
                        InstallUtils.uninstallAPP(context?.applicationContext, temp.app_package)
                        view.post {
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                    R.id.app_icon -> {
                        DialogUtils.showInputDialog(activity!!, "签名信息获取算法", "在下方输入框输入算法名称后，点击「确认」即可设定并计算签名信息", defaultSignatureType, object : InputDialogCompletedCallback {
                            override fun onInputCompleted(p0: String?) {
                                p0?.let {
                                    defaultSignatureType = it
                                    temp.signature_type = it
                                    view.post {
                                        mAdapter.notifyDataSetChanged()
                                    }
                                }
                            }

                        })
                    }
                }
            }

            setOnItemLongClickListener { adapter, view, position ->
                var temp = adapter.data[position] as APPItemData
                DebugTools.showInfo(context, temp.app_name + "基础信息", temp.toString(), "分享")
                return@setOnItemLongClickListener true
            }
        }
    }


    private fun showTips() {
        if (ZixieContext.isFirstStart() > INSTALL_TYPE_NOT_FIRST) {
            hasShowTips = true
            context?.let {
                DialogUtils.showAlertDialog(it, "温馨提示", getTipsContent("#38ADFF"), "我知道了", true, null)
            }
        }
    }

    private fun getTipsContent(color: String): String {
        return "1. <b><font color='$color'>点击</font>应用信息</b>，可以计算APK的MD5<BR>" + "2. <b><font color='$color'>长按</font>应用信息</b>，可以复制到剪切板或对外分享<BR>" + "3. <b><font color='$color'>点击</font>应用图标</b>，可以调整应用签名计算算法"
    }

    private fun getTempData(): List<CardBaseModule> {
        return mutableListOf<CardBaseModule>().apply {
            add(TipsData().apply {
                this.mContentText = "<big><b>使用说明：</b></big><BR>" + getTipsContent("#182B37")

            })
            var appList = context?.packageManager?.getInstalledApplications(PackageManager.GET_SIGNATURES)
                    ?: emptyList()
            for (info in appList) {
                if ((info.flags and ApplicationInfo.FLAG_SYSTEM == 0) && !info.packageName.equals(context?.packageName)) {
                    try {
                        var packageInfo = APKUtils.getInstalledPackage(context, info.packageName)
                        add(APPItemData().apply {
                            this.app_icon = packageInfo?.applicationInfo?.loadIcon(context!!.packageManager)
                            this.app_name = packageInfo?.applicationInfo?.loadLabel(context!!.packageManager)?.toString()
                                    ?: ""
                            this.app_version = packageInfo.versionName + "." + packageInfo.versionCode
                            this.app_package = info.packageName
                            this.app_install_time = packageInfo.firstInstallTime
                            this.app_update_time = packageInfo.lastUpdateTime
                            this.signature_value = APKUtils.getSigMessageDigestByPkgName(context, signature_type, app_package, false).uppercase(Locale.getDefault())
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}