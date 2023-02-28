package com.bihe0832.android.app.apk

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bihe0832.android.app.getapk.R
import com.bihe0832.android.app.message.AAFMessageManager
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.base.card.apk.APPItemData
import com.bihe0832.android.base.card.tips.TipsData
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.swiperefresh.CommonListActivity
import com.bihe0832.android.common.message.data.MessageInfoItem
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.install.InstallUtils
import com.bihe0832.android.lib.lifecycle.INSTALL_TYPE_NOT_FIRST
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.impl.DialogUtils
import com.bihe0832.android.lib.ui.dialog.input.InputDialogCompletedCallback
import com.bihe0832.android.lib.ui.recycleview.ext.SafeLinearLayoutManager
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.encrypt.MD5

@APPMain
@Module(RouterConstants.MODULE_NAME_APK_LIST)
class MainActivity : CommonListActivity() {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbar?.apply {
            navigationIcon = resources.getDrawable(R.mipmap.ic_menu_white)
            setNavigationOnClickListener {
                RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            }
        }
        initAdapter()
        showTips()
        UpdateManager.checkUpdateAndShowDialog(this, false)
        AAFMessageManager.getMessageLiveData().observe(this) { t ->
            t?.filter { it.canShow(true) }?.forEach {
                AAFMessageManager.showNotice(this@MainActivity, it, true)
            }
        }
    }

    override fun getStatusBarColor(): Int {
        return ContextCompat.getColor(this, R.color.colorPrimary)
    }

    override fun onResume() {
        super.onResume()
        PermissionManager.checkPermission(this, mutableListOf(Manifest.permission.READ_PHONE_STATE))

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
                    R.id.uninstall -> {
                        InstallUtils.uninstallAPP(applicationContext, temp.app_package)
                        view.post {
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                    R.id.app_icon -> {
                        DialogUtils.showInputDialog(this@MainActivity, "签名信息获取算法", "在下方输入框输入算法名称后，点击「确认」即可设定并计算签名信息", defaultSignatureType, object : InputDialogCompletedCallback {
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
                DebugTools.showInfo(this@MainActivity, temp.app_name + "基础信息", temp.toString(), "分享")
                return@setOnItemLongClickListener true
            }
        }
    }


    private fun showTips() {
        if (ZixieContext.isFirstStart() > INSTALL_TYPE_NOT_FIRST) {
            hasShowTips = true
            DialogUtils.showAlertDialog(this, "温馨提示", getTipsContent("#38ADFF"), "我知道了", true, null)
        }
    }

    override fun getTitleText(): String {
        return "应用信息查看"
    }

    private fun getTipsContent(color: String): String {
        return "1. <b><font color='$color'>点击</font>应用信息</b>，可以计算APK的MD5<BR>" + "2. <b><font color='$color'>长按</font>应用信息</b>，可以复制到剪切板或对外分享<BR>" + "3. <b><font color='$color'>点击</font>应用图标</b>，可以调整应用签名计算算法"
    }

    private fun getTempData(): List<CardBaseModule> {
        return mutableListOf<CardBaseModule>().apply {
            add(TipsData().apply {
                this.mContentText = "<big><b>使用说明：</b></big><BR>" + getTipsContent("#182B37")

            })
            var appList = packageManager.getInstalledApplications(PackageManager.GET_SIGNATURES)
            for (info in appList) {
                if ((info.flags and ApplicationInfo.FLAG_SYSTEM == 0) && !info.packageName.equals(this@MainActivity.packageName)) {
                    try {
                        var packageInfo = APKUtils.getInstalledPackage(applicationContext, info.packageName)
                        add(APPItemData().apply {
                            this.app_icon = packageInfo?.applicationInfo?.loadIcon(packageManager)
                            this.app_name = packageInfo?.applicationInfo?.loadLabel(packageManager)?.toString()
                                    ?: ""
                            this.app_version = packageInfo.versionName + "." + packageInfo.versionCode
                            this.app_package = info.packageName
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