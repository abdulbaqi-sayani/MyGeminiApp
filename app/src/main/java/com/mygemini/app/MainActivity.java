package com.mygemini.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private ValueCallback<Uri[]> filePathCallback;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // إنشاء الـ WebView برمجياً لتجنب مشاكل ملف الـ XML الخاص بالواجهة
        myWebView = new WebView(this);
        setContentView(myWebView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        myWebView.setWebViewClient(new WebViewClient());

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }
                MainActivity.this.filePathCallback = filePathCallback;

                // خيار الكاميرا
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                
                // خيار الفيديو
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                // خيار الملفات
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                // تجميع الخيارات في قائمة واحدة (التي طلبتها في الصورة)
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "إتمام الإجراء باستخدام:");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent, takeVideoIntent});

                startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);

                return true;
            }
        });

        // تشغيل موقع Gemini
        myWebView.loadUrl("https://gemini.google.com");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || filePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        filePathCallback.onReceiveValue(results);
        filePathCallback = null;
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
