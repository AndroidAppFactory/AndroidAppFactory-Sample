package com.bihe0832.android.base.puzzle.ui

import android.content.Intent
import android.net.Uri
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.app.router.RouterHelper
import com.bihe0832.android.base.photos.PhotosSelectFragment
import com.bihe0832.android.base.puzzle.PuzzleGameManager
import com.bihe0832.android.common.photos.cropPhoto
import com.bihe0832.android.common.photos.getDefaultPhoto
import com.bihe0832.android.common.photos.getPhotosFolder
import com.bihe0832.android.framework.constant.ZixieActivityRequestCode
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.ui.image.BitmapUtil

/**
 * Created by hardyshi on 16/6/30.
 */
class PuzzlePhotosFragment : PhotosSelectFragment() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK == resultCode) {
            ZLog.d("PhotoChooser", "in PhotoChooser onResult, $this, $requestCode, $resultCode, ${data?.data}")
            val finalPic = activity!!.getPhotosFolder() + "crop_puzzle.jpg"
            when (requestCode) {
                ZixieActivityRequestCode.TAKE_PHOTO -> {
                    activity?.cropPhoto(activity!!.getDefaultPhoto().absolutePath, finalPic)
                }
                ZixieActivityRequestCode.CHOOSE_PHOTO -> {
                    activity?.cropPhoto(data?.data as Uri, finalPic)
                }
                ZixieActivityRequestCode.CROP_PHOTO -> {
                    PuzzleGameManager.setBitmap(BitmapUtil.getLoacalBitmap(finalPic))
                    RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_PUZZLE_GAME)
                }
            }
        }
    }

    override fun customPhoto() {
        RouterHelper.openPageByRouter(RouterConstants.MODULE_NAME_PUZZLE_GAME)
    }

    override fun getHorizontalFix(): Int {
        return 64
    }


    override fun getVerticalFix(): Int {
        return 800
    }
}