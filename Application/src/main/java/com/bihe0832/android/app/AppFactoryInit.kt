package com.bihe0832.android.app

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import com.bihe0832.android.app.leakcanary.LeakCanaryManager
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.common.network.NetworkChangeManager
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.ZixieCoreInit
import com.bihe0832.android.framework.privacy.AgreementPrivacy
import com.bihe0832.android.lib.download.wrapper.DownloadUtils
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.network.MobileUtil
import com.bihe0832.android.lib.network.WifiManagerWrapper
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.utils.os.ManufacturerUtil
import com.bihe0832.android.lib.web.WebViewHelper
import com.tencent.smtt.sdk.TbsPrivacyAccess

/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2019-07-09.
 * Description: 加速器相关的初始化
 *
 */

object AppFactoryInit {
    // 全局变量的初始化
    var hasInit = false

    //目前仅仅主进程和web进程需要初始化
    @Synchronized
    private fun initCore(application: android.app.Application, processName: String) {
        val ctx = application.applicationContext
        if (!hasInit) {
            hasInit = true
            ZixieCoreInit.initAfterAgreePrivacy(application)

            if (ZixieContext.isDebug()) {
                LeakCanaryManager.init(application)
            }

            RouterHelper.initRouter()
            initPermission()
            DownloadUtils.init(ctx, 10, null, ZixieContext.isDebug())
            ThreadManager.getInstance().start({
                ZLog.e("Application process initCore web start")
                WebViewHelper.init(ctx, null, Bundle().apply {
                    putString(
                            TbsPrivacyAccess.ConfigurablePrivacy.MODEL.name,
                            ManufacturerUtil.MODEL
                    )
                    putString(
                            TbsPrivacyAccess.ConfigurablePrivacy.ANDROID_ID.name,
                            ZixieContext.deviceId
                    )
                    putString(
                            TbsPrivacyAccess.ConfigurablePrivacy.SERIAL.name,
                            ZixieContext.deviceId
                    )
                }, true)
            }, 5)
            ZLog.d("Application process $processName initCore ManufacturerUtil:" + ManufacturerUtil.MODEL)
        }
    }

    @Synchronized
    private fun initExtra(application: android.app.Application) {
        // 初始化网络变量和监听
        NetworkChangeManager.getInstance().init(application.applicationContext, true)
        WifiManagerWrapper.init(application.applicationContext, !ZixieContext.isOfficial())
        // 监听信号变化，统一到MobileUtil
        MobileUtil.registerMobileSignalListener(application.applicationContext)
    }

    fun initAll(application: android.app.Application) {
        if (AgreementPrivacy.hasAgreedPrivacy()) {
            val am = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses
            for (it in runningApps) {
                if (it.pid == android.os.Process.myPid() && it.processName != null &&
                        it.processName.contains(application.getPackageName())) {
                    ZLog.e("Application initCore process: name:" + it.processName + " and id:" + it.pid)
                    val processName = it.processName
                    initCore(application, processName)
                    if (processName.equals(application.packageName, ignoreCase = true)) {
                        initExtra(application)
                    }
                }
            }
        }
    }

    fun initUserLoginRetBeforeGetUser(openid: String) {
    }

    private fun initPermission() {
        PermissionManager.addPermissionGroupDesc(
                HashMap<String, String>().apply {
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "访问存储卡")
                    put(Manifest.permission.READ_PHONE_STATE, "读取手机状态")
                }
        )

        PermissionManager.addPermissionGroupScene(
                HashMap<String, String>().apply {
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取应用信息")
                    put(Manifest.permission.READ_PHONE_STATE, "获取应用安装时间等")
                }
        )
    }

}