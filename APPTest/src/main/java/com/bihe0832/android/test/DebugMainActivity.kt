package com.bihe0832.android.test

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.ui.AAFCommonMainActivity
import com.bihe0832.android.lib.adapter.CardInfoHelper
import com.bihe0832.android.lib.immersion.hideBottomUIMenu
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.os.BuildUtils

@APPMain
@Module(RouterConstants.MODULE_NAME_DEBUG)
class DebugMainActivity : AAFCommonMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(R.id.common_toolbar, "DebugMainActivity", false, needBack = true, iconRes = R.drawable.icon_left_arrow)
        if (BuildUtils.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        }

        CardInfoHelper.getInstance().setAutoAddItem(true)

        PermissionManager.addPermissionGroupDesc(HashMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "访问存储卡")
        })

        PermissionManager.addPermissionGroupScene(HashMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储权限测试")
        })
        hideBottomUIMenu()
        CommonDBManager.init(this)
    }


    override fun getRootFragmentClassName(): String {
        return DebugMainFragment::class.java.name
    }
}
