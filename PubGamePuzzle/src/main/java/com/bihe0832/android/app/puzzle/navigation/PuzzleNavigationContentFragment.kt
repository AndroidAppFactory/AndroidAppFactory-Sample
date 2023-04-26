package com.bihe0832.android.app.puzzle.navigation

import com.bihe0832.android.app.message.AAFMessageManager
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.navigation.AAFNavigationContentFragment
import com.bihe0832.android.common.settings.SettingsItem
import com.bihe0832.android.framework.router.RouterAction
import com.bihe0832.android.framework.update.UpdateInfoLiveData
import com.bihe0832.android.lib.adapter.CardBaseModule

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2023/4/10.
 * Description: Description
 *
 */
open class PuzzleNavigationContentFragment : AAFNavigationContentFragment() {


    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(getAboutAPP(UpdateInfoLiveData.value) {
                RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            })
            add(SettingsItem.getMessage(AAFMessageManager.getUnreadNum()) {
                RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_MESSAGE)
            })

            add(SettingsItem.getPermission(PuzzlePermissionFragment::class.java))
            addAll(getBaseDataList())
        }.apply {
            processLastItemDriver()
        }
    }


}