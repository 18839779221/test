package com.example.webview

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import kotlinx.android.synthetic.main.activity_simple_webview.*

/**
 * @author wanglun
 * @date 2022/06/23
 * @description
 */
class SimpleWebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_webview)
        //不支持缩放功能
//        configWebView(webView)
        webView.settings.apply {
            setSupportZoom(true)
            builtInZoomControls = false
            loadWithOverviewMode = true // 缩放至屏幕的大小
            displayZoomControls = false //隐藏原生的缩放控件
            useWideViewPort = true // 支持扩大比例缩放
        }

        val pageTitle = "测试标题"
        val publishTime = "2022年06月24日 17:44"
        var content =
            "<p>点击了解>><a href=\"https://feiyu.oceanengine.com/help-center/knowledge/122691?clue_account_id=1689492140848200\" target=\"_blank\">\n" +
                    "  功能详情\n" +
                    "  </a>；功能亮点：（1）支持差异化设置员工每日的工作时间和工作量上限；（2）管理员可调整员工的在线状态和工作设置；（3）管理员可快捷查看员工的工作情况和线索分配情况</p>"
        content = content.replace('"', '\'')
        val htmlString =
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=0.5,maximum-scale=0.5,user-scalable=no\">" +
                    "<div style=\"padding: 16px 32px 60px; line-height: 1.5\">\n" +
                    "    <div class=\"title\" style=\"margin-top: 12px; \n" +
                    "            line-height: 72px; \n" +
                    "            font-weight: 700; \n" +
                    "            font-size: 48px; \n" +
                    "            color: rgb(35, 39, 46)\">\n" +
                    "        测试标题\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <div class=\"time\" style=\"margin-top: 16px; \n" +
                    "            line-height: 44px; \n" +
                    "            font-size: 28px; \n" +
                    "            color: rgb(189, 189, 189)\">\n" +
                    "        2022年06月24日 17:44\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <div class=\"content\" ignoreFilterXss style=\"margin-top: 16px;\n" +
                    "            line-height: 56px;\n" +
                    "            font-size: 32px;\n" +
                    "            text-align: justify;\n" +
                    "            word-break: break-all;\n" +
                    "            color: rgb(35, 39, 46)\">\n" +
//                    "        <p>点击了解>><a href=\"https://feiyu.oceanengine.com/help-center/knowledge/122691?clue_account_id=1689492140848200\" target=\"_blank\">\n" +
//                    "                 功能详情\n" +
//                    "        </a>；功能亮点：（1）支持差异化设置员工每日的工作时间和工作量上限；（2）管理员可调整员工的在线状态和工作设置；（3）管理员可快捷查看员工的工作情况和线索分配情况</p>" +
                    "    </div>\n" +
                    "</div>\n" +
                    "        <script>\n" +
                    "            document.querySelector(\".content\").innerHTML = \"$content\"\n" +
                    "        </script>"
        webView.loadData(htmlString, "text/html", "UTF-8")
    }

    fun configWebView(webView: WebView?) {
        if (webView == null) {
            return
        }
        val webSettings: WebSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.savePassword = false

        // webview访问本地文件
        webSettings.allowFileAccess = false
        // 设置是否允许通过 file url 加载的 Js代码读取其他的本地文件
        webSettings.allowFileAccessFromFileURLs = false
        // 设置是否允许通过 file url 加载的 Javascript 可以访问其他的源(包括http、https等源)
        webSettings.allowUniversalAccessFromFileURLs = false

        // config zoom
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS // 自适应屏幕
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.useWideViewPort = true // 支持扩大比例缩放
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true

        //Application Cache 存储机制
        webSettings.setAppCacheEnabled(true)
        if (webView.getContext() != null && webView.getContext().getCacheDir() != null) {
            val path: String = webView.getContext().getCacheDir().getAbsolutePath()
            webSettings.setAppCachePath(path)
        }
        webSettings.setAppCacheMaxSize(50 * 1024 * 1024)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        try {
        } catch (tr: Throwable) {
            tr.printStackTrace()
        }

        // webview硬件加速
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.loadsImagesAutomatically = true

    }

}