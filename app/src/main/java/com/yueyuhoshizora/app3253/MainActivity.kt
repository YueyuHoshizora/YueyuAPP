package com.yueyuhoshizora.app3253

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    private val urlMap = mapOf(
        "code" to "https://github.com/YueyuHoshizora",
        "video" to "https://www.youtube.com/channel/UCLXRu8yatzwjUpaFD1o07KA",
        "home" to "https://yueyuhoshizora.carrd.co",
        "music" to "https://yueyuhoshizora.music",
        "art" to "https://www.pixiv.net/users/73186283"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // supportActionBar?.hide()

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // 進度條處理：讀取中顯示，完成後隱藏
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress

                if (newProgress < 100) {
                    if (progressBar.visibility != View.VISIBLE) {
                        progressBar.alpha = 0f
                        progressBar.visibility = View.VISIBLE
                        progressBar.animate().alpha(1f).setDuration(200).start()
                    }
                } else {
                    progressBar.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            progressBar.visibility = View.GONE
                        }
                        .start()
                }
            }
        }

        // 網頁內部跳轉也保持在 App 內 + 同步進度
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }
        }

        // 首頁載入
        webView.loadUrl(urlMap["home"]!!)
    }

    // Footer 導覽點擊切換網址
    fun onFooterClick(view: View) {
        val key = view.tag as? String ?: return
        val url = urlMap[key] ?: return
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}