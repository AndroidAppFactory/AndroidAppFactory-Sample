package com.bihe0832.android.test

import android.Manifest
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.framework.ui.main.CommonActivity
import com.bihe0832.android.lib.adapter.CardInfoHelper
import com.bihe0832.android.lib.immersion.hideBottomUIMenu
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module(RouterConstants.MODULE_NAME_DEBUG)
class TestMainActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar("TestMainActivity", false)
        CardInfoHelper.getInstance().setAutoAddItem(true)

        PermissionManager.addPermissionDesc(HashMap<String, String>().apply {
            put(Manifest.permission.CAMERA, "相机")
        })

        PermissionManager.addPermissionScene(HashMap<String, String>().apply {
            put(Manifest.permission.CAMERA, "扫描二维码")
        })
        hideBottomUIMenu()
    }

    override fun getStatusBarColor(): Int {
        return ContextCompat.getColor(this, R.color.colorPrimary)
    }


    override fun getNavigationBarColor(): Int {
        return ContextCompat.getColor(this, R.color.transparent)
    }

    override fun getPermissionList(): List<String> {
        return ArrayList<String>().apply {
            add(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        if (findFragment(TestMainFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, TestMainFragment())
        }
        UpdateManager.checkUpdateAndShowDialog(this, false)
//        hideBottomUIMenu()
    }


    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }
}
