package com.bihe0832.android.app.apk.navigation

import android.text.TextUtils
import android.view.View
import com.bihe0832.android.app.constants.ConfigConstants
import com.bihe0832.android.app.message.AAFMessageManager
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.app.ui.navigation.AAFNavigationContentFragment
import com.bihe0832.android.common.about.R
import com.bihe0832.android.common.settings.SettingsItem
import com.bihe0832.android.common.settings.card.SettingsData
import com.bihe0832.android.framework.router.RouterAction
import com.bihe0832.android.framework.update.UpdateInfoLiveData
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.config.Config
import com.bihe0832.android.lib.ui.dialog.impl.DialogUtils

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2023/4/10.
 * Description: Description
 *
 */
open class APKNavigationContentFragment : AAFNavigationContentFragment() {


    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(SettingsItem.getAboutAPP(UpdateInfoLiveData.value) {
                RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_BASE_ABOUT)
            })

            add(SettingsItem.getMessage(AAFMessageManager.getUnreadNum()) {
                RouterAction.openPageByRouter(RouterConstants.MODULE_NAME_MESSAGE)
            })
            add(getChangeSignatureType())
            add(SettingsItem.getPermission(APKPermissionFragment::class.java))
            addAll(getBaseDataList())
        }.apply {
            processLastItemDriver()
        }
    }

    fun getChangeSignatureType(): SettingsData {
        val title = "设置默认签名算法"
        return SettingsData(title).apply {
            mItemIconRes = R.drawable.icon_android
            mHeaderTextBold = true
            mShowDriver = true
            mShowGo = true
            mHeaderListener = View.OnClickListener {
                DialogUtils.showInputDialog(context!!, title, "在下方输入对应签名计算算法的Key值并点击确定即可切换默认签名算法", Config.readConfig(ConfigConstants.APK.KEY_SIGNATURE_TYPE, ConfigConstants.APK.VALUE_DEFAULT_SIGNATURE_TYPE)) { p0 ->
                    if (!TextUtils.isEmpty(p0)) {
                        Config.writeConfig(ConfigConstants.APK.KEY_SIGNATURE_TYPE, p0)
                    }
                }
            }
        }
    }


}