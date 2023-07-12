package com.bihe0832.android.app.permission

import android.Manifest
import com.bihe0832.android.common.permission.AAFPermissionManager
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.permission.PermissionManager

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2023/4/26.
 * Description: Description
 *
 */


val SCENE_TAKE_PHOTO = "takePhoto"
val SCENE_SELECT_PHOTO = "PhotoSelect"
val SCENE_GAME_ACCESSIBILITY = "Accessibility"

fun initPermission() {

    PermissionManager.addPermissionGroup("", Manifest.permission.CAMERA, AAFPermissionManager.takePhotoPermission)
    PermissionManager.addPermissionGroupDesc("", Manifest.permission.CAMERA, "相机")
    PermissionManager.addPermissionGroupScene("", Manifest.permission.CAMERA, "扫描、识别二维码")

    PermissionManager.addPermissionGroup(SCENE_TAKE_PHOTO, Manifest.permission.CAMERA, AAFPermissionManager.takePhotoPermission)
    PermissionManager.addPermissionGroupDesc(SCENE_TAKE_PHOTO, Manifest.permission.CAMERA, "相机")
    PermissionManager.addPermissionGroupScene(SCENE_TAKE_PHOTO, Manifest.permission.CAMERA, "扫描二维码、拍照")

    PermissionManager.addPermissionGroup("", Manifest.permission.READ_PHONE_STATE, mutableListOf<String>().apply {
        add(Manifest.permission.READ_PHONE_STATE)
    })
    PermissionManager.addPermissionGroupDesc("", Manifest.permission.READ_PHONE_STATE, "读取手机状态")
    PermissionManager.addPermissionGroupScene("", Manifest.permission.READ_PHONE_STATE, "获取应用安装时间等")

    PermissionManager.addPermissionGroupDesc(SCENE_GAME_ACCESSIBILITY, Manifest.permission.SYSTEM_ALERT_WINDOW, "悬浮窗")
    PermissionManager.addPermissionGroupScene(SCENE_GAME_ACCESSIBILITY, Manifest.permission.SYSTEM_ALERT_WINDOW, "按键精灵需要通过<font color ='#38ADFF'><b>悬浮窗实时展示</b></font>")
    PermissionManager.addPermissionGroupContent(
            SCENE_GAME_ACCESSIBILITY,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            "按键精灵需要通过<font color ='#38ADFF'><b>悬浮窗实时展示</b></font>。当前手机尚未授权，请点击「" + PermissionManager.getPositiveText(ZixieContext.applicationContext!!) + "」前往设置！"
    )
}
