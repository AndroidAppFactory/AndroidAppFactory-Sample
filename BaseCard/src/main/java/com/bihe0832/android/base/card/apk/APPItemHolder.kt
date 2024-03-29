package com.bihe0832.android.base.card.apk

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bihe0832.android.base.card.R
import com.bihe0832.android.lib.adapter.CardBaseHolder
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.utils.apk.APKUtils
import com.bihe0832.android.lib.utils.encrypt.rsa.RSAUtils
import com.bihe0832.android.lib.utils.time.DateUtil
import java.util.Locale

/**
 * @author zixie code@bihe0832.com
 * Created on 2019-11-21.
 * Description: Description
 */
class APPItemHolder(itemView: View?, context: Context?) : CardBaseHolder(itemView, context) {

    private var app_icon: ImageView? = null
    private var app_name: TextView? = null
    private var app_version: TextView? = null
    private var app_package: TextView? = null
    private var app_install: TextView? = null
    private var app_update: TextView? = null
    private var app_md5: TextView? = null
    private var signature_value: TextView? = null

    override fun initView() {
        app_icon = getView(R.id.app_icon)
        app_name = getView(R.id.title_info)
        app_version = getView(R.id.app_version)
        app_package = getView(R.id.download_url)
        app_install = getView(R.id.app_install)
        app_update = getView(R.id.app_update)
        app_md5 = getView(R.id.app_md5)
        signature_value = getView(R.id.signature_value)
    }

    override fun initData(item: CardBaseModule) {
        val data = item as APPItemData
        addOnClickListener(R.id.app_layout)
        addOnClickListener(R.id.settings)
        addOnClickListener(R.id.app_icon)
        addOnLongClickListener(R.id.app_icon)
        app_icon?.setImageDrawable(data.app_icon)
        app_name?.text = data.app_name
        app_version?.text = "当前版本：${data.app_version}"
        app_package?.text = "应用包名：${data.app_package}"
        app_install?.text = "安装时间：${DateUtil.getDateEN(data.app_install_time)}"
        app_update?.text = "最后更新：${DateUtil.getDateEN(data.app_update_time)}"
        app_md5?.text = TextFactoryUtils.getSpannedTextByHtml("<B> APK MD5</B>：${data.app_md5}")
        if (data.signature_value.isNullOrBlank()) {
            data.signature_value =
                APKUtils.getSigMessageDigestByPkgName(context, data.signature_type, data.app_package, true)
                    .uppercase(Locale.getDefault())
        }

        if (data.androidPublicKey.isNullOrBlank()) {
            APKUtils.getSigPublicKey(context, data.app_package)?.let { key ->
                data.androidPublicKey = RSAUtils.getPublicKeyPemString(key)
                data.windowsPublicKey = RSAUtils.transPublicKeyByteStringToWindows(RSAUtils.getPublicKeyByteString(key))
                    .uppercase(Locale.getDefault())
            }
        }

        signature_value?.text =
            TextFactoryUtils.getSpannedTextByHtml("<B> 签名 ${data.signature_type}</B>：${data.signature_value}")
    }
}
