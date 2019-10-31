package com.cna.mineru.cna.Utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.R

class WebViewActivity : AppCompatActivity() {
    private var mWebView: WebView? = null
    private var myUrl: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        myUrl = getString(R.string.ip_set) + "/policy"
        val url = getIntent().getStringExtra("value")

        mWebView = findViewById(R.id.webview) as WebView
        mWebView!!.webViewClient = WebViewClient()
        val webSettings = mWebView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        mWebView!!.loadUrl("$myUrl/$url") // 접속 URL

    }
}
