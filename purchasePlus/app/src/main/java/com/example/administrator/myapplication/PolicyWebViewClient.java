package com.example.administrator.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

//================================================//
// NAME : PolicyWebViewClient
// PURPOSE : Get policy from internet
//================================================//
public class PolicyWebViewClient extends WebViewClient{

    private Context context;

    public PolicyWebViewClient(Context context) {
        this.context = context;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView myWebView, final String searchPolicy) {

        boolean loadImages = false;

        // Setup Java Settings, System Browser Override and WebView dimensions
        WebSettings webSettings = myWebView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        myWebView.loadUrl(searchPolicy);
        loadImages = true;

        return loadImages;
    }

}
