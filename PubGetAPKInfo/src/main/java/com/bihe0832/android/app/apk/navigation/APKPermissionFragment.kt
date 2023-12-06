package com.bihe0832.android.app.apk.navigation

import android.Manifest
import com.bihe0832.android.common.permission.settings.PermissionFragment
import com.bihe0832.android.common.permission.settings.PermissionItem
import com.bihe0832.android.lib.adapter.CardBaseModule

open class APKPermissionFragment : PermissionFragment() {

    override fun getDataList(processLast: Boolean): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(
                PermissionItem.getPermissionSetting(activity!!, Manifest.permission.CAMERA).apply {
                    mShowDriver = true
                },
            )
            add(PermissionItem.getPermissionSetting(activity!!, Manifest.permission.READ_PHONE_STATE))
        }.apply {
            processLastItemDriver(processLast)
        }
    }
}
