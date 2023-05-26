package com.bihe0832.android.app.permission

import android.Manifest
import android.app.Activity
import android.view.View
import com.bihe0832.android.common.about.R
import com.bihe0832.android.common.accessibility.action.AAFAccessibilityManager
import com.bihe0832.android.common.permission.PermissionFragment
import com.bihe0832.android.common.permission.PermissionItem
import com.bihe0832.android.common.settings.card.SettingsDataV2
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.text.TextFactoryUtils


open class AAFPermissionFragment : PermissionFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(PermissionItem.getPermissionSetting(activity!!, Manifest.permission.CAMERA))
        }.apply {
            processLastItemDriver()
        }
    }

    fun getAccessibilitySettings(activity: Activity, permissionDesc: String, permissionScene: String): SettingsDataV2 {
        return SettingsDataV2().apply {
            title = permissionDesc
            description = permissionScene
            val hasPermission: Boolean = false
            tips = if (hasPermission) {
                TextFactoryUtils.getSpecialText("已开启", activity.resources.getColor(R.color.textColorSecondary))
            } else {
                TextFactoryUtils.getSpecialText("去设置", activity.resources.getColor(R.color.textColorPrimary))
            }
            onClickListener = View.OnClickListener {
                AAFAccessibilityManager.openSettings(activity)
            }
        }
    }
}