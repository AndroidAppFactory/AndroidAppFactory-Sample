package com.bihe0832.android.base.puzzle.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.base.photos.PhotosSelectFragment
import com.bihe0832.android.base.puzzle.PuzzleGameManager
import com.bihe0832.android.common.photos.cropPhoto
import com.bihe0832.android.common.photos.getAutoChangedCropUri
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.constant.ZixieActivityRequestCode
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.debug.InputDialogCompletedCallback
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.ZixieFileProvider
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.request.URLUtils
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.OnDialogListener
import com.bihe0832.android.lib.ui.image.BitmapUtil
import java.io.File

/**
 * Created by hardyshi on 16/6/30.
 */
class PuzzlePhotosFragment : PhotosSelectFragment() {

    private var mCropUri: Uri? = null

    private val PIC_URL = "https://api.nmb.show/xiaojiejie1.php"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCropUri = activity!!.getAutoChangedCropUri()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK == resultCode) {
            ZLog.d(
                "PhotoChooser",
                "in PhotoChooser onResult, $this, $requestCode, $resultCode, ${data?.data}"
            )
            when (requestCode) {
                ZixieActivityRequestCode.TAKE_PHOTO -> {
                    mCropUri = activity!!.getAutoChangedCropUri()
                    activity?.cropPhoto(mTakePhotoUri, mCropUri)
                }
                ZixieActivityRequestCode.CHOOSE_PHOTO -> {
                    activity?.cropPhoto(data?.data as Uri, activity!!.getAutoChangedCropUri())
                }
                ZixieActivityRequestCode.CROP_PHOTO -> {
                    PuzzleGameManager.setBitmap(
                        BitmapUtil.getLoacalBitmap(
                            ZixieFileProvider.uriToFile(
                                activity,
                                mCropUri
                            ).absolutePath
                        )
                    )
                    RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_PUZZLE_GAME)
                }
            }
        }
    }

    override fun cloudPhoto() {
        DebugTools.showInputDialog(
            activity!!,
            "使用网络图片进行游戏",
            "在下方输入想要使用图片的地址并点击确定开始游戏",
            PIC_URL,
            object : InputDialogCompletedCallback {
                override fun onInputCompleted(result: String?) {
                    if (URLUtils.isHTTPUrl(result)) {
                        useInternetURL(result!!)
                    } else {
                        ZixieContext.showToastJustAPPFront("请输入正确的图片地址")
                    }
                }

            }
        )
    }

    private fun useInternetURL(url: String) {
        DownloadFile.startDownloadWithProcess(
            activity!!,
            "图片加载",
            "正在加载网络图片，加载结束将直接进入游戏",
            url,
            "", "",
            canCancel = true,
            useMobile = true,
            listener = object : OnDialogListener {
                override fun onPositiveClick() {
                    ZixieContext.showToastJustAPPFront("图片加载已切换到后台")
                }

                override fun onNegativeClick() {
                    onCancel()
                }

                override fun onCancel() {
                    ZixieContext.showToastJustAPPFront("图片加载已经取消")
                }
            },
            downloadListener = object : SimpleDownloadListener() {
                override fun onComplete(filePath: String, item: DownloadItem) {
                    ZLog.d("onComplete:$item")
                    ThreadManager.getInstance().runOnUIThread {
                        mCropUri = activity!!.getAutoChangedCropUri()
                        activity?.cropPhoto(
                            ZixieFileProvider.getZixieFileProvider(activity!!, File(filePath)),
                            mCropUri
                        )
                    }
                }

                override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                    ZixieContext.showToastJustAPPFront("图片加载失败，请重试")
                }

                override fun onProgress(item: DownloadItem) {
                    ZLog.d("onProgress: $item")
                }
            })

    }

    override fun customPhoto() {
        useInternetURL(PIC_URL)
    }

    override fun getHorizontalFix(): Int {
        return 64
    }


    override fun getVerticalFix(): Int {
        return 800
    }
}