package com.bihe0832.android.app.ui.navigation

import android.view.View
import com.bihe0832.android.app.R
import com.bihe0832.android.app.message.AAFMessageManager
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.common.main.CommonNavigationContentFragment
import com.bihe0832.android.common.settings.SettingsItem
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.router.RouterAction
import com.bihe0832.android.framework.update.UpdateInfoLiveData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.theme.ThemeResourcesManager

/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2023/4/10.
 * Description: Description
 *
 */
open class AAFNavigationContentFragment : CommonNavigationContentFragment() {

    override fun initView(view: View) {
        super.initView(view)
        AAFMessageManager.getMessageLiveData().observe(this) { t ->
            changeMessageRedDot(
                ThemeResourcesManager.getString(R.string.settings_message_title),
                AAFMessageManager.getUnreadNum(),
            )
        }
        UpdateInfoLiveData.observe(this) { t ->
            changeUpdateRedDot(SettingsItem.getAboutTitle(), t, false)
        }
    }

    override fun getDataList(processLast: Boolean): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(
                SettingsItem.getAboutAPP(UpdateInfoLiveData.value) {
                    RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
                },
            )
            if (AAFMessageManager.getUnreadNum() > 0) {
                add(
                    SettingsItem.getMessage(AAFMessageManager.getUnreadNum()) {
                        RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_MESSAGE)
                    },
                )
            } else {
                add(
                    SettingsItem.getMessage(-1) {
                        RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_MESSAGE)
                    },
                )
            }
            addAll(getBaseDataList(processLast))
        }.apply {
            processLastItemDriver(processLast)
        }
    }

    fun getBaseDataList(processLast: Boolean): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(SettingsItem.getFeedbackURL())
            add(SettingsItem.getShareAPP(true))
            add(SettingsItem.getVersionList())
            add(SettingsItem.getClearCache(activity!!))
            add(SettingsItem.getZixie())
            if (!ZixieContext.isOfficial()) {
                add(SettingsItem.getDebug())
            }
        }.apply {
            processLastItemDriver(processLast)
        }
    }
}
