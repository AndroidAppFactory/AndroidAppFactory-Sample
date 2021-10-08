package com.bihe0832.android.base.photos

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.bihe0832.android.base.card.photo.IconTextData
import com.bihe0832.android.common.list.CardItemForCommonList
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.swiperefresh.CommonListFragment
import com.bihe0832.android.common.photos.choosePhoto
import com.bihe0832.android.common.photos.getDefaultPhoto
import com.bihe0832.android.common.photos.takePhoto
import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.ui.recycleview.ext.SafeGridLayoutManager

open class PhotosSelectFragment : CommonListFragment() {
    protected val mDataList = ArrayList<CardBaseModule>()
    protected val ID_CAMERA = 1
    protected val ID_PHOTO = 2
    protected val ID_CLOUD = 3
    protected val ID_CUSTOM = 4

    val takePhotoPermission = arrayOf(Manifest.permission.CAMERA)
    val selectPhotoPermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    init {
        PermissionManager.addPermissionDesc(HashMap<String, String>().apply {
            put(Manifest.permission.CAMERA, "相机")
        })

        PermissionManager.addPermissionScene(HashMap<String, String>().apply {
            put(Manifest.permission.CAMERA, "拍摄拼图原图")
        })
    }

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
        PermissionManager.checkPermission(context, "takePhoto", false, object : PermissionManager.OnPermissionResult {
            override fun onFailed(msg: String) {
            }

            override fun onSuccess() {
                activity!!.takePhoto(activity!!.getDefaultPhoto())
            }

            override fun onUserCancel(scene: String, permission: String) {

            }

            override fun onUserDeny(scene: String, permission: String) {

            }

        }, *takePhotoPermission)
    }

    open fun choosePhoto() {
        PermissionManager.checkPermission(context, "choosePhoto", false, object : PermissionManager.OnPermissionResult {
            override fun onFailed(msg: String) {
            }

            override fun onSuccess() {
                activity!!.choosePhoto()
            }

            override fun onUserCancel(scene: String, permission: String) {

            }

            override fun onUserDeny(scene: String, permission: String) {

            }

        }, *selectPhotoPermission)
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
            override fun fetchData() {
                mDataList.clear()
                mDataList.addAll(getDataList())
                postValue(mDataList)
            }

            override fun clearData() {
                mDataList.clear()
            }

            override fun loadMore() {
                postValue(mDataList)
            }

            override fun hasMore(): Boolean {
                return false
            }

            override fun canRefresh(): Boolean {
                return false
            }

            override fun getEmptyText(): String {
                return ""
            }
        }
    }


    protected fun getCamera(): IconTextData {
        return IconTextData().apply {
            mIconID = ID_CAMERA
            mContentText = "相机拍摄"
            mContentResID = R.mipmap.icon_camera
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
            mContentResID = R.mipmap.icon_photos
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
            mContentResID = R.mipmap.icon_cloud
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
            mContentResID = R.mipmap.icon_cycle
            mHorizontalNum = getHorizontalItemNum()
            mVerticalNum = getVerticalItemNum()
            mHorizontalFix = getHorizontalFix()
            mVerticalFix = getVerticalFix()
        }
    }
}