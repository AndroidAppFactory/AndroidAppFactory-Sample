package com.bihe0832.android.base.card.apk

import android.graphics.drawable.Drawable
import com.bihe0832.android.base.card.R
import com.bihe0832.android.lib.adapter.CardBaseHolder
import com.bihe0832.android.lib.adapter.CardBaseModule

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2019-11-21.
 * Description: Description
 */
class APPItemData : CardBaseModule() {
    override fun getResID(): Int {
        return R.layout.card_apk_info
    }

    override fun getViewHolderClass(): Class<out CardBaseHolder?> {
        return APPItemHolder::class.java
    }

    var app_icon: Drawable? = null
    var app_name = ""
    var app_version = ""
    var app_package = ""
    var app_install_time = 0L
    var app_update_time = 0L
    var app_md5 = ""
    var signature_md5 = ""
    override fun toString(): String {
        return "$app_name 信息：\n\n当前版本号：$app_version\n包名：$app_package\n安装于：$app_install_time\n更新于：$app_update_time \nAPK MD5：$app_md5\n签名 MD5：$signature_md5"
    }


}