package com.bihe0832.android.app.dialog

import com.bihe0832.android.framework.ZixieContext
import com.bihe0832.android.lib.ui.dialog.CommonDialog
import com.bihe0832.android.lib.ui.dialog.LoadingDialog
import com.bihe0832.android.lib.ui.dialog.OnDialogListener

/**
 * Created by lwtorlu on 2022/1/15.
 *
 *  后续所有的Dialog都继承 CommonDialog，如果是底部弹出的就继承BottomDialog，如果是底部弹出的列表形式的，可以直接用 BottomListDialog。
 */
object AAFDialogManager {


    fun showTip(text: String, clickCallback: (() -> Unit)? = null) {
        showTip(text, "我知道了", clickCallback)
    }

    fun showTip(text: String, buttonText: String, clickCallback: (() -> Unit)? = null) {
        ZixieContext.getCurrentActivity()?.let {
            CommonDialog(it).apply {
                setTitle(text)
                setSingle(true)
                setPositive(buttonText)
                setOnClickBottomListener(object : OnDialogListener {
                    override fun onPositiveClick() {
                        clickCallback?.invoke()
                        dismiss()
                    }

                    override fun onNegativeClick() {
                        clickCallback?.invoke()
                        dismiss()
                    }

                    override fun onCancel() {
                        clickCallback?.invoke()
                        dismiss()
                    }
                })

            }.show()
        }
    }

    fun showActionConfirm(content: String, positiveString: String, confirmCallback: (() -> Unit)) {
        ZixieContext.getCurrentActivity()?.let {
            CommonDialog(it).apply {
                setTitle(content)
                setSingle(true)
                setPositive(positiveString)
                setNegative("取消")
                setOnClickBottomListener(object : OnDialogListener {
                    override fun onPositiveClick() {
                        confirmCallback?.invoke()
                        dismiss()
                    }

                    override fun onNegativeClick() {
                        dismiss()
                    }

                    override fun onCancel() {
                        dismiss()
                    }
                })

            }.show()
        }
    }


    fun showActionConfirm(content: String, positiveString: String, negativeString: String, callBack: OnDialogListener) {
        ZixieContext.getCurrentActivity()?.let {
            CommonDialog(it).apply {
                setTitle(content)
                setPositive(positiveString)
                setNegative(negativeString)
                setOnClickBottomListener(object : OnDialogListener {
                    override fun onPositiveClick() {
                        callBack.onPositiveClick()
                        dismiss()
                    }

                    override fun onNegativeClick() {
                        callBack.onNegativeClick()
                        dismiss()
                    }

                    override fun onCancel() {
                        callBack.onCancel()
                        dismiss()
                    }
                })

            }.show()
        }
    }

    fun showLoading(text: String = ""): LoadingDialog? {
        ZixieContext.getCurrentActivity()?.let {
            val loadingDialog = LoadingDialog(it)
            loadingDialog.show(text)
            return loadingDialog
        }
        return null
    }

}