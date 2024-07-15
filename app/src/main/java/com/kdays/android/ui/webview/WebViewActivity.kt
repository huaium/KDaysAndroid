package com.kdays.android.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.kdays.android.BaseActivity
import com.kdays.android.databinding.ActivityWebviewBinding


class WebViewActivity : BaseActivity() {
    companion object {
        private const val URL = "url"

        fun actionStart(
            context: Context,
            url: String,
        ) {
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra(URL, url)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityWebviewBinding
    private val url: String by lazy { intent.getStringExtra(URL) ?: "" }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.webViewToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val webView: WebView = binding.webView

        // Set up WebView Settings
        webView.settings.javaScriptEnabled = true

        val progressBar = binding.webViewProgressBar

        // Set up WebViewClient to handle page navigation
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.progress = 0
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.INVISIBLE
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                super.shouldOverrideUrlLoading(view, request)

                // always load in this WebView
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                // Update progress when it changes
                progressBar.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)

                if (title != null) {
                    binding.webViewToolbar.toolbar.title = title
                }
            }
        }

        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}