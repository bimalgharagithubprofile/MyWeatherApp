package com.sample.myweather.ui.help

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import com.sample.myweather.R
import com.sample.myweather.utils.*
import kotlinx.android.synthetic.main.activity_help.*
import java.lang.Exception

class HelpActivity : AppCompatActivity() {

    private var isLoaded: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        loadWebURL()
    }

    private fun loadWebURL() {
        if (!isLoaded) {
            vProgressBar.show()
            try{
                v_webView.settings.javaScriptEnabled = true
                v_webView.settings.pluginState = WebSettings.PluginState.ON
                v_webView.settings.allowFileAccess = true
                v_webView.settings.domStorageEnabled=true

                v_webView.webViewClient = object : WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        vProgressBar.hide()
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        log("ERROR while loading URL ${error.toString()}")
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        log("HTTP ERROR while loading URL ${errorResponse.toString()}")
                    }
                }

                v_webView.loadUrl(CommonUtils.HELP_WEBSITE_LINK)

            }catch (e: Exception){
                e.printStackTrace()
                vProgressBar.hide()
                toast("Something went wrong !")
            } finally {
                isLoaded = true
            }
        }
    }

}