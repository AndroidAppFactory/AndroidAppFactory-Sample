package com.bihe0832.android.app

import android.Manifest
import android.content.Context
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.permission.PermissionManager

/**
 *
 * @author hardyshi code@bihe0832.com
 * Created on 2019-07-09.
 * Description: 加速器相关的初始化
 *
 */

object AppFactoryInit {
    // 全局变量的初始化
    var hasInit = false

    //目前仅仅主进程和web进程需要初始化
    @Synchronized
    fun initCore(ctx: Context) {
        if (!hasInit) {
            hasInit = true
            RouterHelper.initRouter()
            initPermission()
            DownloadUtils.init(ctx, 5, null, ZixieContext.isDebug())
        }
    }

    fun initUserLoginRetBeforeGetUser(openid: String) {
    }

    private fun initPermission() {
        PermissionManager.addPermissionDesc(
                HashMap<String, String>().apply {
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "访问存储卡")
                    put(Manifest.permission.READ_PHONE_STATE, "读取手机状态")
                }
        )

        PermissionManager.addPermissionScene(
                HashMap<String, String>().apply {
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取应用信息")
                    put(Manifest.permission.READ_PHONE_STATE, "获取应用安装时间等")
                }
        )
    }

}