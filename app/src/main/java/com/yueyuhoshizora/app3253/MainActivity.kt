package com.yueyuhoshizora.app3253

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        webView = findViewById(R.id.mainWebView)

        // 啟用 JavaScript
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // 讓 WebView 在 App 內部開啟，而不是跳到外部瀏覽器
        webView.webViewClient = WebViewClient()

        // 載入指定網址
        webView.loadUrl("https://yueyuhoshizora.carrd.co")
    }

    // 讓 WebView 支援返回上一頁
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
