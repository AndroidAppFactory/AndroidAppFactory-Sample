package com.bihe0832.android.base.photos

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.bihe0832.android.app.permission.AAFPermissionManager.SCENE_SELECT_PHOTO
import com.bihe0832.android.app.permission.AAFPermissionManager.SCENE_TAKE_PHOTO
import com.bihe0832.android.base.card.photo.IconTextData
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.swiperefresh.CommonListFragment
import com.bihe0832.android.common.photos.getAutoChangedPhotoUri
import com.bihe0832.android.common.photos.getPhotoContent
import com.bihe0832.android.common.photos.takePhoto
import com.bihe0832.android.common.photos.takePhotoPermission
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.framework.permission.PermissionResultOfAAF
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.permission.ui.PermissionsActivityV2
import com.bihe0832.android.lib.ui.recycleview.ext.SafeGridLayoutManager
import com.bihe0832.android.lib.utils.os.BuildUtils

open class PhotosSelectFragment : CommonListFragment() {


    protected val mDataList = ArrayList<CardBaseModule>()
    protected val ID_CAMERA = 1
    protected val ID_PHOTO = 2
    protected val ID_CLOUD = 3
    protected val ID_CUSTOM = 4

    var mTakePhotoUri: Uri? = null

    open fun getHorizontalItemNum(): Int {
        return 2
    }

    open fun getVerticalItemNum(): Int {
        return 2
    }

    open fun getHorizontalFix(): Int {
        return 0
    }


    open fun getVerticalFix(): Int {
        return 600
    }


    open fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(getCamera())
            add(getLocal())
            add(getIntenet())
            add(getCustom())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getAdapter().setOnItemClickListener { adapter, view, position ->
            if (position < mDataList.size && mDataList.get(position) is IconTextData) {
                when ((mDataList[position] as IconTextData).mIconID) {
                    ID_CAMERA -> {
                        takePhoto()
                    }

                    ID_PHOTO -> {
                        choosePhoto()
                    }

                    ID_CLOUD -> {
                        cloudPhoto()
                    }

                    ID_CUSTOM -> {
                        customPhoto()
                    }
                }
            }
        }
    }

    open fun takePhoto() {
        mTakePhotoUri = activity!!.getAutoChangedPhotoUri()
        if (PermissionManager.isAllPermissionOK(context!!, takePhotoPermission)) {
            activity!!.takePhoto(mTakePhotoUri)
        } else {
            PermissionManager.checkPermission(activity, SCENE_TAKE_PHOTO, true, PermissionsActivityV2::class.java, object : PermissionResultOfAAF(false) {
                override fun onFailed(msg: String) {
                }

                override fun onSuccess() {

                }

                override fun onUserCancel(scene: String, permissionGroupID: String, permission: String) {

                }

                override fun onUserDeny(scene: String, permissionGroupID: String, permission: String) {

                }

            }, takePhotoPermission)
        }
    }

    open fun choosePhoto() {
        if (BuildUtils.SDK_INT >= Build.VERSION_CODES.Q) {
            activity!!.getPhotoContent()
        } else {
            PermissionManager.checkPermission(activity!!, SCENE_SELECT_PHOTO, false, object : PermissionResultOfAAF(false) {
                override fun onSuccess() {
                    activity!!.getPhotoContent()
                }
            }, com.bihe0832.android.common.photos.selectPhotoPermission)
        }
    }

    open fun cloudPhoto() {
        ZixieContext.showWaiting()
    }


    open fun customPhoto() {
        ZixieContext.showWaiting()
    }

    override fun getLayoutManagerForList(): RecyclerView.LayoutManager {
        return SafeGridLayoutManager(context, getHorizontalItemNum())
    }

    override fun getCardList(): List<CardItemForCommonList>? {
        return mutableListOf<CardItemForCommonList>().apply {
            add(CardItemForCommonList(IconTextData::class.java))
        }
    }


    override fun getDataLiveData(): CommonListLiveData {
        return object : CommonListLiveData() {

            override fun loadMore() {
                postValue(mDataList)
            }

            override fun refresh() {
                mDataList.clear()
            }

            override fun hasMore(): Boolean {
                return false
            }

            override fun initData() {

                mDataList.addAll(getDataList())
                postValue(mDataList)
            }

            override fun canRefresh(): Boolean {
                return false
            }
        }
    }


    protected fun getCamera(): IconTextData {
        return IconTextData().apply {
            mIconID = ID_CAMERA
            mContentText = "相机拍摄"
            mContentResID = R.drawable.icon_camera
            mHorizontalNum = getHorizontalItemNum()
            mVerticalNum = getVerticalItemNum()
            mHorizontalFix = getHorizontalFix()
            mVerticalFix = getVerticalFix()
        }
    }

    protected fun getLocal(): IconTextData {
        return IconTextData().apply {
            mIconID = ID_PHOTO
            mContentText = "相册选取"
            mContentResID = R.drawable.icon_photos
            mHorizontalNum = getHorizontalItemNum()
            mVerticalNum = getVerticalItemNum()
            mHorizontalFix = getHorizontalFix()
            mVerticalFix = getVerticalFix()
        }
    }

    protected fun getIntenet(): IconTextData {
        return IconTextData().apply {
            mIconID = ID_CLOUD
            mContentText = "网络图片"
            mContentResID = R.drawable.icon_cloud
            mHorizontalNum = getHorizontalItemNum()
            mVerticalNum = getVerticalItemNum()
            mHorizontalFix = getHorizontalFix()
            mVerticalFix = getVerticalFix()
        }
    }

    protected fun getCustom(): IconTextData {
        return IconTextData().apply {
            mIconID = ID_CUSTOM
            mContentText = "系统推荐"
            mContentResID = R.drawable.icon_cycle
            mHorizontalNum = getHorizontalItemNum()
            mVerticalNum = getVerticalItemNum()
            mHorizontalFix = getHorizontalFix()
            mVerticalFix = getVerticalFix()
        }
    }
}