package com.yueyuhoshizora.app3253

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

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
        supportActionBar?.hide()

        webView = findViewById(R.id.webView)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webViewClient = WebViewClient()
        webView.loadUrl(urlMap["home"]!!)
    }

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
