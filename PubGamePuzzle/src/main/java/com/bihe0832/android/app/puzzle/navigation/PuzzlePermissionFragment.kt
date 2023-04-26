package com.bihe0832.android.app.puzzle.navigation

import android.Manifest
import com.bihe0832.android.app.permission.AAFPermissionFragment
import com.bihe0832.android.app.permission.AAFPermissionManager.SCENE_TAKE_PHOTO
import com.bihe0832.android.common.permission.PermissionItem
import com.bihe0832.android.lib.adapter.CardBaseModule


open class PuzzlePermissionFragment : AAFPermissionFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(PermissionItem.getPermissionSetting(activity!!, SCENE_TAKE_PHOTO, Manifest.permission.CAMERA).apply {
                mShowDriver = true
            })
        }.apply {
            processLastItemDriver()
        }
    }
}