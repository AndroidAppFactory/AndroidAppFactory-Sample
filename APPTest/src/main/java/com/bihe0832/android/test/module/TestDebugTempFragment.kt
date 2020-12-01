package com.bihe0832.android.test.module

import android.content.Intent
import android.provider.MediaStore
import android.view.View
import com.bihe0832.android.app.about.AboutActivity
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.test.base.BaseTestFragment
import com.bihe0832.android.test.base.item.TestItemData
import java.lang.Exception


class
TestDebugTempFragment : BaseTestFragment() {
    val LOG_TAG = "TestDebugTempFragment"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestItemData("简单测试函数", View.OnClickListener { testFunc() }))
            add(TestItemData("通用测试预处理", View.OnClickListener { preTest() }))
            add(TestItemData("测试自定义请求", View.OnClickListener { testOneRequest() }))
            add(TestItemData("默认关于页", View.OnClickListener { startActivity(AboutActivity::class.java) }))
        }
    }

    private fun testOneRequest() {
    }

    private fun preTest() {

    }

    private fun testFunc() {
        ZLog.d("test")
        try {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //用来打开相机的Intent
            if (takePhotoIntent.resolveActivity(context!!.getPackageManager()) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                activity!!.startActivityForResult(takePhotoIntent, 1) //启动相机
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}