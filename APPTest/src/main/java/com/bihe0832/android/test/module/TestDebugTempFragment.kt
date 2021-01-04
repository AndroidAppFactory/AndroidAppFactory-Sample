package com.bihe0832.android.test.module

import android.content.Intent
import android.view.View
import com.bihe0832.android.app.about.AboutActivity
import com.bihe0832.android.common.photos.cropPhoto
import com.bihe0832.android.common.photos.getDefaultPhoto
import com.bihe0832.android.common.photos.getPhotosFolder
import com.bihe0832.android.common.photos.showPhotoChooser
import com.bihe0832.android.framework.constant.ZixieActivityRequestCode
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.ui.photos.Photos
import com.bihe0832.android.lib.utils.intent.IntentUtils
import com.bihe0832.android.test.base.BaseTestFragment
import com.bihe0832.android.test.base.item.TestItemData


class
TestDebugTempFragment : BaseTestFragment() {
    val LOG_TAG = "TestDebugTempFragment"

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestItemData("简单测试函数", View.OnClickListener { testFunc() }))
            add(TestItemData("通用测试预处理", View.OnClickListener { preTest() }))
            add(TestItemData("测试自定义请求", View.OnClickListener { testOneRequest() }))
            add(TestItemData("默认关于页", View.OnClickListener { startActivity(AboutActivity::class.java) }))
            add(TestItemData("APP设置", View.OnClickListener { IntentUtils.startAppDetailSettings(context) }))
        }
    }

    private fun testOneRequest() {
    }

    private fun preTest() {

    }

    private fun testFunc() {
        ZLog.d("test")
        try {
            activity?.showPhotoChooser()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ZLog.d("PhotoChooser", "in PhotoChooser onResult, $this, $requestCode, $resultCode, ${data?.data}")
        if (requestCode == ZixieActivityRequestCode.TAKE_PHOTO) {
            Photos.addPicToPhotos(context, activity!!.getDefaultPhoto().absolutePath)
            activity?.cropPhoto(activity!!.getDefaultPhoto().absolutePath, activity!!.getPhotosFolder() + "a.jpg", 2)
        }
    }
}