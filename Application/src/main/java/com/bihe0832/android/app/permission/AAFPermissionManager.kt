package com.bihe0832.android.app.permission

import android.Manifest
import com.bihe0832.android.common.photos.takePhotoPermission
import com.bihe0832.android.lib.permission.PermissionManager

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2023/4/26.
 * Description: Description
 *
 */
object AAFPermissionManager {


    val SCENE_TAKE_PHOTO = "takePhoto"
    val SCENE_SELECT_PHOTO = "PhotoSelect"


    fun initPermission() {

        PermissionManager.addPermissionGroup("", Manifest.permission.CAMERA, takePhotoPermission)
        PermissionManager.addPermissionGroupDesc("", Manifest.permission.CAMERA, "相机")
        PermissionManager.addPermissionGroupScene("", Manifest.permission.CAMERA, "扫描、识别二维码")

        PermissionManager.addPermissionGroup(SCENE_TAKE_PHOTO, Manifest.permission.CAMERA, takePhotoPermission)
        PermissionManager.addPermissionGroupDesc(SCENE_TAKE_PHOTO, Manifest.permission.CAMERA, "相机")
        PermissionManager.addPermissionGroupScene(SCENE_TAKE_PHOTO, Manifest.permission.CAMERA, "扫描二维码、拍照")

        PermissionManager.addPermissionGroup("", Manifest.permission.READ_PHONE_STATE, mutableListOf<String>().apply {
            add(Manifest.permission.READ_PHONE_STATE)
        })
        PermissionManager.addPermissionGroupDesc("", Manifest.permission.READ_PHONE_STATE, "读取手机状态")
        PermissionManager.addPermissionGroupScene("", Manifest.permission.READ_PHONE_STATE, "获取应用安装时间等")
    }
}