package com.bihe0832.android.base.m3u8

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.base.m3u8.bean.M3U8Info
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.base.m3u8.tools.M3U8Tools
import com.bihe0832.android.framework.constant.Constants
import com.bihe0832.android.framework.ui.BaseActivity
import com.bihe0832.android.lib.config.Config
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.file.ZixieFileProvider
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.request.URLUtils
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.media.Media
import kotlinx.android.synthetic.main.activity_m3u8.*
import java.io.File
import java.io.InputStream
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


open class M3u8DownloadActivity : BaseActivity() {

    private var m3u8Info: M3U8Info = M3U8Info()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m3u8)
        initToolbarAction()
        initPermission()

        initGuide()
        reset()
        initM3u8URL()
        updateBaseURL()
        initActionView()
    }

    open fun initToolbarAction() {
        initToolbar(R.id.common_toolbar, "M3U8视频下载器", false, R.mipmap.ic_menu_white)
    }

    override fun getPermissionList(): List<String> {
        return ArrayList<String>().apply {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun initPermission() {
        PermissionManager.addPermissionDesc(HashMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读取和保存到存储卡")
        })

        PermissionManager.addPermissionScene(HashMap<String, String>().apply {
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "保存视频码")
        })

    }

    private fun initM3u8URL() {
        urlText.apply {
            val m3u8URL = if (intent?.extras?.containsKey(RouterConstants.INTENT_EXTRA_KEY_WEB_URL) == true) {
                URLDecoder.decode(intent.extras.getString(RouterConstants.INTENT_EXTRA_KEY_WEB_URL))
            } else {
                Config.readConfig(this@M3u8DownloadActivity.javaClass.name + "URL", "")
            }
            setText(m3u8URL)
            setSingleLine()
            if (!TextUtils.isEmpty(m3u8URL)) {
                requestFocus()
                selectAll()
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    reset()
                }

                override fun afterTextChanged(s: Editable?) {
                    Config.writeConfig(this@M3u8DownloadActivity.javaClass.name + "URL", getM3U8URL())
                }
            })
        }


        baseURl.apply {
            val baseURL = if (intent?.extras?.containsKey(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL) == true) {
                URLDecoder.decode(intent.extras.getString(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL))
            } else {
                getM3U8URL().substring(0, getM3U8URL().lastIndexOf("/") + 1)
            }
            setText(baseURL)
        }

    }

    private fun updateBaseURL() {
        baseURl.apply {
            setText(getM3U8URL().substring(0, getM3U8URL().lastIndexOf("/") + 1))
        }
    }

    private fun initGuide() {
        titleText.apply {
            this.text = TextFactoryUtils.getSpannedTextByHtml(
                    "<big><b>使用说明：</b></big><BR>" +
                            "1. 在下方<b>M3U8 输入框<font color='#8e44ad'>输入</font>视频m3u8文件</b>对应链接<BR>" +
                            "2. 在下方<b>BaseURL 输入框<font color='#8e44ad'>输入</font>视频分片</b>的公共链接<BR>" +
                            "3. 逐步<b><font color='#8e44ad'>点击</font> 解析M3U8、下载分片、合并视频</b>，完成视频下载与合并<BR>" +
                            "4. 即使没有下载完成所有分片，也可执行合并操作，此时会暂停下载并将已下载分片合并<BR>" +
                            "5. 如果下载过程中出现异常，可以在下载结束以后再次点击<b><font color='#8e44ad'>点击</font>下载分片</b>"
            )
        }
    }


    private fun initActionView() {

        downloadIndex.setOnClickListener {
            if (!TextUtils.isEmpty(getM3U8URL()) && URLUtils.isHTTPUrl(getM3U8URL())) {
                var finalPath = M3U8ModuleManager.getDownloadPath(getM3U8URL()) + FileUtils.getFileName(getM3U8URL())
                if (File(finalPath).exists()) {
                    File(finalPath).delete()
                }
                showResult("<b><font color='#8e44ad'>开始下载</font></b>：${getM3U8URL()}")
                DownloadFile.startDownload(this, getM3U8URL(), finalPath, object : SimpleDownloadListener() {
                    override fun onComplete(filePath: String, item: DownloadItem) {
                        try {
                            val inputStream: InputStream = File(filePath).inputStream()
                            inputStream.bufferedReader().useLines { lines ->
                                lines.forEach {
                                    var line = it
                                    if (line.contains("m3u8")) {
                                        showResult("<b>下载失败，M3U8地址已发生变化，请再次点击<font color='#8e44ad'>下载M3U8</font></b>，更新M3U3URL。<BR>原M3U8内容为：<BR>${FileUtils.getFileContent(filePath)}")
                                        urlText.setText(M3U8Tools.getFullUrl(getBaseURL(), line))
                                        return
                                    }
                                }
                            }
                            showResult("<b>下载成功，点击<font color='#8e44ad'>解析M3U8</font></b> 开始解析 $filePath<BR>：${FileUtils.getFileContent(filePath)} ")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                        showResult("<b>下载失败</b>（$errorCode）：$msg")
                    }

                    override fun onProgress(item: DownloadItem) {
                        ZLog.d(item.toString())
                    }

                })
            }
        }

        parseIndex.setOnClickListener {
            var finalPath = M3U8ModuleManager.getDownloadPath(getM3U8URL()) + FileUtils.getFileName(getM3U8URL())
            showResult("<b>开始解析</b>：${getM3U8URL()} $finalPath")
            m3u8Info = M3U8Tools.parseIndex(getM3U8URL(), getBaseURL(), finalPath)
            M3U8Tools.generateLocalM3U8(M3U8ModuleManager.getDownloadPath(getM3U8URL()), m3u8Info)
            showResult("<b>解析成功</b><BR>$m3u8Info")
            updateBaseURL()
            downloadPart.isEnabled = true
            mergePart.isEnabled = true
        }

        downloadPart.setOnClickListener {
            showResult("开始下载分片！")
            M3U8DBManager.saveData(m3u8Info)
            M3U8Tools.downloadM3U8(this, getBaseURL(), M3U8ModuleManager.getDownloadPath(getM3U8URL()), m3u8Info, object : M3U8Listener {
                override fun onFail(errorCode: Int, msg: String) {

                    ThreadManager.getInstance().runOnUIThread {
                        showResult("<b>下载异常</b>（$errorCode）：$msg")
                    }
                }

                override fun onComplete() {

                }

                override fun onProcess(finished: Int, total: Int) {
                    ThreadManager.getInstance().runOnUIThread {
                        showResult("<b>分片下载</b>中，共 <b><font color='#8e44ad'>$total</font></b> 分片，当前已完成：<b><font color='#8e44ad'>$finished</font></b> 分片")
                    }
                }
            })
        }

        mergePart.setOnClickListener {
            M3U8Tools.cancleDownload()
            showResult("分片下载已暂停，开始合并，合并结束以后，阔以点击下载分片继续下载！")
            mergePart.isEnabled = false
            M3U8Tools.mergeM3U8(M3U8ModuleManager.getDownloadPath(getM3U8URL()), M3U8ModuleManager.getFinalFilePath(getM3U8URL()), object : M3U8Listener {
                override fun onFail(errorCode: Int, msg: String) {

                    ThreadManager.getInstance().runOnUIThread {
                        mergePart.isEnabled = true
                        openVideo.isEnabled = true
                    }
                }

                override fun onComplete() {
                    ThreadManager.getInstance().runOnUIThread {
                        if (!TextUtils.isEmpty(M3U8ModuleManager.getFinalFilePath(getM3U8URL()))) {
                            showResult("<b>视频合并已经完成</b>，保存地址为：${M3U8ModuleManager.getFinalFilePath(getM3U8URL())}")
                        } else {
                            showResult("<b>合并失败</b>")
                        }
                        mergePart.isEnabled = true
                        openVideo.isEnabled = true
                        addToPhoto.isEnabled = true
                    }
                }

                override fun onProcess(finished: Int, total: Int) {
                    ThreadManager.getInstance().runOnUIThread {
                        showResult("共 <b><font color='#8e44ad'>$total</font></b> 分片，当前已完成：<b><font color='#8e44ad'>$finished</font></b> 分片")
                    }
                }
            })

        }

        openVideo.setOnClickListener {
            var file = File(M3U8ModuleManager.getFinalFilePath(getM3U8URL()))
            if (file.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                try {
                    ZixieFileProvider.setFileUriForIntent(this, intent, file, "video/*")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    showResult("视频打开失败！" + e.printStackTrace())
                }
            } else {
                showResult("视频文件不存在！")
            }
        }

        addToPhoto.setOnClickListener {
            var file = File(M3U8ModuleManager.getFinalFilePath(getM3U8URL()))
            if (file.exists()) {
                Media.addVideoToPhotos(this, file.absolutePath)
            } else {
                showResult("视频文件不存在！")
            }

        }
    }

    private fun reset() {
        m3u8Info = M3U8Info()
        downloadPart.isEnabled = false
        baseURl.apply { setText("") }
    }

    private fun getM3U8URL(): String {
        return urlText.text.toString()
    }

    private fun getBaseURL(): String {
        return baseURl.text.toString()
    }


    private fun showResult(tipsText: String) {
        runOnUiThread {
            tips.text = TextFactoryUtils.getSpannedTextByHtml("下载提示：<BR> $tipsText <BR><BR>")
        }
    }
}
