package com.bihe0832.android.base.m3u8

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.bihe0832.android.app.router.RouterConstants
import com.bihe0832.android.base.m3u8.bean.M3U8Info
import com.bihe0832.android.base.m3u8.bean.M3U8TSInfo
import com.bihe0832.android.base.m3u8.db.M3U8DBManager
import com.bihe0832.android.base.m3u8.tools.M3U8DownloadImpl
import com.bihe0832.android.base.m3u8.tools.M3U8Tools
import com.bihe0832.android.framework.file.AAFFileTools
import com.bihe0832.android.framework.ui.BaseActivity
import com.bihe0832.android.lib.download.DownloadItem
import com.bihe0832.android.lib.download.wrapper.DownloadFile
import com.bihe0832.android.lib.download.wrapper.SimpleDownloadListener
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.file.provider.ZixieFileProvider
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.media.Media
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.request.URLUtils
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.thread.ThreadManager
import com.bihe0832.android.lib.ui.dialog.callback.OnDialogListener
import com.bihe0832.android.lib.ui.dialog.tools.DialogUtils
import kotlinx.android.synthetic.main.m3u8_activity_download.*
import java.io.File
import java.io.InputStream
import java.net.URLDecoder

@Module(RouterConstants.MODULE_NAME_M3U8)
class M3u8DownloadActivity : BaseActivity() {

    private var m3u8Info: M3U8Info = M3U8Info()

    private val mM3U8DownloadImpl = M3U8DownloadImpl(
        this,
        object : M3U8Listener {
            override fun onFail(errorCode: Int, msg: String) {
                ThreadManager.getInstance().runOnUIThread {
                    showResult("<b>下载异常</b>（$errorCode）：$msg <BR> 请检查BaseURL是否正确")
                }
            }

            override fun onComplete() {
            }

            override fun onProcess(finished: Int, total: Int) {
                ThreadManager.getInstance().runOnUIThread {
                    showResult("<b>分片下载</b>中，共 <b><font color='#8e44ad'>$total</font></b> 分片，当前已完成：<b><font color='#8e44ad'>$finished</font></b> 分片")
                }
            }
        },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        M3U8ModuleManager.init(this)
        setContentView(R.layout.m3u8_activity_download)
        initToolbar(R.id.common_toolbar, "M3U8视频下载器", true)
        initPermission()
        initActionView()
        initM3u8URL()
        updateM3U8Info()
        initGuide()
    }

    fun close() {
        super.finish()
    }

    override fun finish() {
        // 存在内存泄露，后续将下载抽离后解决
        DialogUtils.showConfirmDialog(
            this,
            "资源下载中，退出时是否继续下载？",
            false,
            object : OnDialogListener {
                override fun onPositiveClick() {
                    close()
                }

                override fun onNegativeClick() {
                    mM3U8DownloadImpl.cancelDownload()
                    close()
                }

                override fun onCancel() {
                    close()
                }
            },
        )
    }

    override fun onResume() {
        super.onResume()
//        PermissionManager.checkPermission(this, mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun initPermission() {
        PermissionManager.addPermissionGroupDesc(
            HashMap<String, String>().apply {
                put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读取和保存到存储卡")
            },
        )

        PermissionManager.addPermissionGroupScene(
            HashMap<String, String>().apply {
                put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "保存视频")
            },
        )
    }

    private fun initM3u8URL() {
        var intentData = intent?.extras ?: Bundle()
        urlText.apply {
            var m3u8URL = ""
            if (intentData.containsKey(RouterConstants.INTENT_EXTRA_KEY_WEB_URL)) {
                m3u8URL = URLDecoder.decode(intentData.getString(RouterConstants.INTENT_EXTRA_KEY_WEB_URL))
            }
            setText(m3u8URL)
            setSingleLine()
            if (TextUtils.isEmpty(m3u8URL)) {
                requestFocus()
                selectAll()
            }
        }

        baseURL.apply {
            val baseurl = if (intentData.containsKey(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL)) {
                URLDecoder.decode(intentData.getString(RouterConstants.INTENT_EXTRA_KEY_M3U8_BASE_URL))
            } else {
                getM3U8URL().substring(0, getM3U8URL().lastIndexOf("/") + 1)
            }
            setText(baseurl)
            setSingleLine()
            if (!TextUtils.isEmpty(urlText.text.toString()) && TextUtils.isEmpty(baseurl)) {
                requestFocus()
                selectAll()
            }
        }
    }

    private fun updateBaseURL() {
        if (TextUtils.isEmpty(baseURL.text.toString())) {
            baseURL.setText(getM3U8URL().substring(0, getM3U8URL().lastIndexOf("/") + 1))
        }
    }

    private fun getLocalIndexFile(): String {
        return M3U8ModuleManager.getDownloadPath(getM3U8URL()) + FileUtils.getFileName(getM3U8URL())
    }

    private fun updateM3U8Info() {
        ZLog.d("updateM3U8Info")
        getLocalIndexFile().let { localIndexFinalPath ->
            if (FileUtils.checkFileExist(localIndexFinalPath)) {
                ThreadManager.getInstance().runOnUIThread {
                    showResult("<b>开始解析</b>：${getM3U8URL()} $localIndexFinalPath")
                    updateBaseURL()
                    m3u8Info = M3U8Tools.parseIndex(getM3U8URL(), getBaseURL(), localIndexFinalPath)
                    M3U8DBManager.saveData(m3u8Info)
                    M3U8Tools.generateLocalM3U8(M3U8ModuleManager.getDownloadPath(getM3U8URL()), m3u8Info)
                    showResult("<b>解析成功，已更新本地存储</b><BR>${TextFactoryUtils.getTextHtmlAfterTransform(m3u8Info.toString())}")
                    ZLog.d(m3u8Info.toString())
                    downloadPart.isEnabled = true
                }
            } else {
                showResult("<b>解析异常</b>：$localIndexFinalPath  不存在，请重新解析")
            }
        }
    }

    private fun initGuide() {
        titleText.apply {
            this.text = TextFactoryUtils.getSpannedTextByHtml("<big><b>使用说明：</b></big><BR>" + "1. 在下方<b>M3U8 输入框<font color='#8e44ad'>输入</font>视频m3u8文件</b>对应链接<BR>" + "2. 在下方<b>BaseURL 输入框<font color='#8e44ad'>输入</font>视频分片</b>的公共链接<BR>" + "3. 逐步<b><font color='#8e44ad'>点击</font> 解析M3U8、下载分片、合并视频</b>，完成视频下载与合并<BR>" + "4. 即使没有下载完成所有分片，也可执行合并操作，此时会暂停下载并将已下载分片合并<BR>" + "5. 如果下载过程中出现异常，可以在下载结束以后再次点击<b><font color='#8e44ad'>点击</font>下载分片</b>")
        }
    }

    private fun getM3U8URL(): String {
        return urlText.text.toString()
    }

    private fun getBaseURL(): String {
        return baseURL.text.toString()
    }

    private fun showResult(tipsText: String) {
        runOnUiThread {
            if (!TextUtils.isEmpty(getM3U8URL())) {
                updateTitle("当前下载：" + M3U8ModuleManager.getFinalVideoName(getM3U8URL()))
            }
            tips.text = TextFactoryUtils.getSpannedTextByHtml("下载提示：<BR> $tipsText <BR><BR>")
        }
    }

    private fun initActionView() {
        baseURL.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                updateBaseURL()
            }
        }

        downloadIndex.setOnClickListener {
            if (!TextUtils.isEmpty(getM3U8URL()) && URLUtils.isHTTPUrl(getM3U8URL())) {
                var finalPath = getLocalIndexFile()

                showResult("<b><font color='#8e44ad'>开始下载</font></b>：${getM3U8URL()} 到 $finalPath")
                DownloadFile.download(
                    this,
                    getM3U8URL(),
                    finalPath,
                    true,
                    object : SimpleDownloadListener() {

                        override fun onComplete(filePath: String, item: DownloadItem): String {
                            try {
                                val inputStream: InputStream = File(filePath).inputStream()
                                inputStream.bufferedReader().useLines { lines ->
                                    lines.forEach {
                                        var line = it
                                        if (line.contains("m3u8")) {
                                            showResult(
                                                "<b>下载失败，M3U8地址已发生变化，请再次点击<font color='#8e44ad'>下载M3U8</font></b>，更新M3U3URL。<BR>原M3U8内容为：<BR>${
                                                    M3U8Tools.getIndexContent(
                                                        filePath,
                                                    )
                                                }",
                                            )
                                            urlText.setText(M3U8TSInfo.getFullUrl(getBaseURL(), line))
                                        } else {
                                            showResult(
                                                "<b>下载成功，点击 <font color='#8e44ad'>解析M3U8</font></b> 开始解析 <BR> $filePath<BR>：${
                                                    M3U8Tools.getIndexContent(
                                                        finalPath,
                                                    )
                                                } ",
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            return filePath
                        }

                        override fun onFail(errorCode: Int, msg: String, item: DownloadItem) {
                            showResult("<b>下载失败</b>（$errorCode）：$msg")
                        }

                        override fun onProgress(item: DownloadItem) {
                            ZLog.d(item.toString())
                        }
                    },
                )
            } else {
                showResult("请先输入需要下载的视频地址")
            }
        }

        parseIndex.setOnClickListener {
            updateM3U8Info()
        }

        previewIndex.setOnClickListener {
            AAFFileTools.openFile(getLocalIndexFile())
        }

        downloadPart.setOnClickListener {
            File(M3U8ModuleManager.getDownloadPath(getM3U8URL())).let { folder ->
                if (folder.isDirectory && folder.listFiles().size > 3) {
                    DialogUtils.showConfirmDialog(
                        this,
                        "当前已存在部分下载内容,是否删除最近下载的部分确保内容完整？",
                        false,
                        object : OnDialogListener {
                            override fun onPositiveClick() {
                                downloadM3u8(true)
                            }

                            override fun onNegativeClick() {
                                downloadM3u8(false)
                            }

                            override fun onCancel() {
                            }
                        },
                    )
                } else {
                    downloadM3u8(false)
                }
            }
        }

        mergePart.setOnClickListener {
            mM3U8DownloadImpl.cancelDownload()
            showResult("分片下载已暂停，开始合并，合并结束以后，阔以点击下载分片继续下载！")
            mergePart.isEnabled = false
            M3U8Tools.mergeM3U8(
                M3U8ModuleManager.getDownloadPath(getM3U8URL()),
                M3U8ModuleManager.getFinalVideoPath(getM3U8URL()),
                object : M3U8Listener {
                    override fun onFail(errorCode: Int, msg: String) {
                        ThreadManager.getInstance().runOnUIThread {
                            mergePart.isEnabled = true
                        }
                    }

                    override fun onComplete() {
                        ThreadManager.getInstance().runOnUIThread {
                            val path = M3U8ModuleManager.getFinalVideoPath(getM3U8URL())
                            if (!TextUtils.isEmpty(path)) {
                                showResult("<b>视频合并已经完成</b>，文件大小：${FileUtils.getFileLength(File(path).length())},保存地址为：$path")
                            } else {
                                showResult("<b>合并失败</b>")
                            }
                            mergePart.isEnabled = true
                        }
                    }

                    override fun onProcess(finished: Int, total: Int) {
                        ThreadManager.getInstance().runOnUIThread {
                            showResult("分片合并中，共 <b><font color='#8e44ad'>$total</font></b> 分片，当前已完成：<b><font color='#8e44ad'>$finished</font></b> 分片")
                        }
                    }
                },
            )
        }

        openVideo.setOnClickListener {
            var file = File(M3U8ModuleManager.getFinalVideoPath(getM3U8URL()))
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
            var file = File(M3U8ModuleManager.getFinalVideoPath(getM3U8URL()))
            if (file.exists()) {
                Media.addToPhotos(this, file.absolutePath)
            } else {
                showResult("视频文件不存在！")
            }
        }
    }

    fun downloadM3u8(needDeleteOld: Boolean) {
        showResult("开始下载分片！")
        updateM3U8Info()
        mM3U8DownloadImpl.startDownload(m3u8Info, needDeleteOld)
    }
}
