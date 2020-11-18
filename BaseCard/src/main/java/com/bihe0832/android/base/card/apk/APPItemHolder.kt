package com.bihe0832.android.base.card.apk

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bihe0832.android.base.card.R
import com.bihe0832.android.lib.adapter.CardBaseHolder
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.utils.DateUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author hardyshi code@bihe0832.com
 * Created on 2019-11-21.
 * Description: Description
 */
class APPItemHolder(itemView: View?, context: Context?) : CardBaseHolder(itemView, context) {

    var app_icon: ImageView? = null
    var app_name: TextView? = null
    var app_version: TextView? = null
    var app_package: TextView? = null
    var app_install: TextView? = null
    var app_md5: TextView? = null
    var signature_md5: TextView? = null


    override fun initView() {
        app_icon = getView(R.id.app_icon)
        app_name = getView(R.id.app_name)
        app_version = getView(R.id.app_version)
        app_package = getView(R.id.app_package)
        app_install = getView(R.id.app_install)
        app_md5 = getView(R.id.app_md5)
        signature_md5 = getView(R.id.signature_md5)

    }

    override fun initData(item: CardBaseModule) {
        val data = item as APPItemData
        app_icon?.setImageDrawable(data.app_icon)
        app_name?.text = data.app_name
        app_version?.text = "当前版本号：${data.app_version}"
        app_package?.text = "包名：${data.app_package}"
        app_install?.text = "安装：${getDate(data.app_install_time)}  更新：${getDate(data.app_update_time)}"
        app_md5?.text = Html.fromHtml("<B> APK MD5</B>：${data.app_md5}")
        signature_md5?.text =  Html.fromHtml("<B> 签名 MD5</B>：${data.signature_md5}")
    }

    fun getDate(oldTimestamp: Long): String? {
        return SimpleDateFormat("yyyy年MM月dd日").format(Date(oldTimestamp))
    }
}