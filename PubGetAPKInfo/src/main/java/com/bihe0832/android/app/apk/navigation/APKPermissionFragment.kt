package com.bihe0832.android.app.apk.navigation

import android.Manifest
import com.bihe0832.android.common.permission.PermissionItem
import com.bihe0832.android.lib.adapter.CardBaseModule


open class APKPermissionFragment : AAFPermissionFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(PermissionItem.getPermissionSetting(activity!!, Manifest.permission.CAMERA).apply {
                mShowDriver = true
            })
            add(PermissionItem.getPermissionSetting(activity!!, Manifest.permission.READ_PHONE_STATE))
        }.apply {
            processLastItemDriver()
        }
    }
}