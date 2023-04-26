package com.bihe0832.android.app.permission

import android.Manifest
import com.bihe0832.android.common.permission.PermissionFragment
import com.bihe0832.android.common.permission.PermissionItem
import com.bihe0832.android.common.settings.SettingsFragment
import com.bihe0832.android.lib.adapter.CardBaseModule


open class AAFPermissionFragment : PermissionFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(PermissionItem.getPermissionSetting(activity!!, Manifest.permission.CAMERA))
        }.apply {
            processLastItemDriver()
        }
    }
}