package com.yueyuhoshizora.app3253

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import android.util.Xml

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

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

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.videoRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

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

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }
        }

        webView.loadUrl(urlMap["home"]!!)
    }

    fun onFooterClick(view: View) {
        val key = view.tag as? String ?: return
        if (key == "video") {
            recyclerView.visibility = View.VISIBLE
            webView.visibility = View.GONE
            loadYouTubeRss()
        } else {
            recyclerView.visibility = View.GONE
            webView.visibility = View.VISIBLE
            val url = urlMap[key] ?: return
            webView.loadUrl(url)
        }
    }

    private fun loadYouTubeRss() {
        Thread {
            try {
                val url = "https://www.youtube.com/feeds/videos.xml?channel_id=UCLXRu8yatzwjUpaFD1o07KA"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0")
                    .build()
                val response = OkHttpClient().newCall(request).execute()
                val xml = response.body?.string() ?: ""

                val parser = Xml.newPullParser()
                parser.setInput(xml.reader())

                val videos = mutableListOf<VideoItem>()
                var eventType = parser.eventType
                var tag = ""
                var title = ""
                var videoId = ""
                var published = ""
                var thumbnailUrl = ""

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            tag = parser.name
                            if (parser.name == "media:thumbnail") {
                                thumbnailUrl = parser.getAttributeValue(null, "url") ?: ""
                            }
                        }
                        XmlPullParser.TEXT -> {
                            when (tag) {
                                "title" -> if (title.isEmpty()) title = parser.text
                                "videoId" -> videoId = parser.text
                                "published" -> published = parser.text
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (parser.name == "entry") {
                                if (videoId.isNotEmpty()) {
                                    // fallback：如果 thumbnail 沒抓到，手動組出來
                                    if (thumbnailUrl.isEmpty()) {
                                        thumbnailUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
                                    }
                                    videos.add(VideoItem(title, videoId, published, thumbnailUrl))
                                }
                                title = ""
                                videoId = ""
                                published = ""
                                thumbnailUrl = ""
                            }
                            tag = ""
                        }
                    }
                    eventType = parser.next()
                }

                runOnUiThread {
                    recyclerView.adapter = VideoAdapter(videos)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "載入影片清單失敗：${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }


    override fun onBackPressed() {
        if (webView.visibility == View.VISIBLE && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

data class VideoItem(
    val title: String,
    val videoId: String,
    val published: String,
    val thumbnailUrl: String
)