package com.mygemini.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.geminiWebView);
        WebSettings webSettings = myWebView.getSettings();

        // إعدادات المتصفح الأساسية لتشغيل Gemini
        webSettings.setJavaScriptEnabled(true); // تفعيل جافا سكريبت
        webSettings.setDomStorageEnabled(true); // تفعيل التخزين المحلي (حل مشكلة الشاشة البيضاء)
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);

        // فتح الروابط داخل التطبيق نفسه
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://gemini.google.com");
    }

    // برمجة زر الرجوع ليعود لصفحة الويب السابقة بدلاً من إغلاق التطبيق
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
