package com.mygemini.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = findViewById(R.id.geminiWebView);
        WebSettings webSettings = myWebView.getSettings();
        
        // تفعيل جافا سكريبت ضروري جداً لعمل Gemini
        webSettings.setJavaScriptEnabled(true);
        
        // منع فتح الروابط في المتصفح الخارجي
        myWebView.setWebViewClient(new WebViewClient());
        
        // تحميل الموقع
        myWebView.loadUrl("https://gemini.google.com");
    }
}
