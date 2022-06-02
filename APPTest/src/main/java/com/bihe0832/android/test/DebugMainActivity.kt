package com.bihe0832.android.test

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.framework.ui.PermissionResultOfAAF
import com.bihe0832.android.framework.ui.main.CommonActivity
import com.bihe0832.android.lib.adapter.CardInfoHelper
import com.bihe0832.android.lib.immersion.hideBottomUIMenu
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.permission.ui.PermissionsActivity
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.os.BuildUtils

@APPMain
@Module(RouterConstants.MODULE_NAME_DEBUG)
class DebugMainActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar("DebugMainActivity", false)
        if (BuildUtils.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        }


        CardInfoHelper.getInstance().setAutoAddItem(true)

        PermissionManager.addPermissionDesc(HashMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "访问存储卡")
        })

        PermissionManager.addPermissionScene(HashMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储权限测试")
        })
        hideBottomUIMenu()
        CommonDBManager.init(this)

    }

    override fun getStatusBarColor(): Int {
        return ContextCompat.getColor(this, R.color.colorPrimary)
    }

    override fun getNavigationBarColor(): Int {
        return ContextCompat.getColor(this, R.color.transparent)
    }

    override fun getPermissionList(): List<String> {
        return ArrayList<String>().apply {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun getPermissionResult(): PermissionManager.OnPermissionResult {
        return PermissionResultOfAAF(false)
    }


    override fun getPermissionActivityClass(): Class<out PermissionsActivity> {
        return PermissionsActivity::class.java
    }

    override fun onResume() {
        super.onResume()
        if (findFragment(DebugMainFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, DebugMainFragment())
        }
        UpdateManager.checkUpdateAndShowDialog(this, false)
//        hideBottomUIMenu()
    }


    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
