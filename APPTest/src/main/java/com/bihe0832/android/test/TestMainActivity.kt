package com.bihe0832.android.test

import android.Manifest
import android.os.Bundle
import com.bihe0832.android.app.update.UpdateManager
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.ui.main.CommonActivity
import com.bihe0832.android.lib.adapter.CardInfoHelper
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module

@APPMain
@Module("test")
class TestMainActivity : CommonActivity() {
    val LOG_TAG = "TestHttpActivity"
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
    }

    override fun getPermissionList(): List<String> {
        return ArrayList<String>().apply {
            add(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        if (findFragment(TestMainFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, TestMainFragment.newInstance(0))
        }
        UpdateManager.checkUpdateAndShowDialog(this, false)
//        hideBottomUIMenu()
    }


    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }
}
