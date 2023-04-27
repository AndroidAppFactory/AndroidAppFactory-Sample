package com.bihe0832.android.app.ui.navigation

import android.view.View
import com.bihe0832.android.app.message.AAFMessageManager
import com.bihe0832.android.app.permission.AAFPermissionFragment
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.common.about.R
import com.bihe0832.android.common.main.CommonNavigationContentFragment
import com.bihe0832.android.common.settings.SettingsItem
import com.bihe0832.android.common.settings.card.SettingsData
import com.bihe0832.android.framework.router.RouterAction
import com.bihe0832.android.framework.update.UpdateDataFromCloud
import com.bihe0832.android.framework.update.UpdateInfoLiveData
import com.bihe0832.android.lib.adapter.CardBaseModule

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2023/4/10.
 * Description: Description
 *
 */
open class AAFNavigationContentFragment : CommonNavigationContentFragment() {

    override fun initView(view: View) {
        super.initView(view)
        AAFMessageManager.getMessageLiveData().observe(this) { t ->
            mDataLiveData.initData()
        }

        UpdateInfoLiveData.observe(this) { t ->
            mDataLiveData.initData()
        }
    }

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(getAboutAPP(UpdateInfoLiveData.value) {
                RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            })
            add(SettingsItem.getMessage(AAFMessageManager.getUnreadNum()) {
                RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_MESSAGE)
            })
            add(SettingsItem.getPermission(AAFPermissionFragment::class.java))
            addAll(super.getDataList())
        }.apply {
            processLastItemDriver()
        }
    }

    fun getBaseDataList(): ArrayList<CardBaseModule> {
        return super.getDataList()
    }
    fun getAboutAPP(cloud: UpdateDataFromCloud?, listener: View.OnClickListener): SettingsData {
        return SettingsData("关于应用").apply {
            mItemIconRes = R.drawable.icon_android
            mHeaderTextBold = true
            mShowDriver = true
            mShowGo = true
            mItemIsNew = cloud?.canShowNew() ?: false
            mHeaderListener = listener
        }
    }
}