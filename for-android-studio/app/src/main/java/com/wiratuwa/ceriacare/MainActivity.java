package com.wiratuwa.ceriacare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webview);
        
        WebSettings webSettings = myWebView.getSettings();
        // Enable Javascript for React
        webSettings.setJavaScriptEnabled(true);
        // Enable Dom Storage (localStorage) for persistent states
        webSettings.setDomStorageEnabled(true);
        // Allow file access for bundled assets
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // Bypassing CORS for ES Modules by using WebViewAssetLoader
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();
        
        // Prevent opening external browser and intercept assets request
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        // Load React built index.html from a secure virtual domain instead of file:// scheme
        myWebView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    // Handle device back button
    @Override
    public void onBackPressed() {
        if (myWebView != null && myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
