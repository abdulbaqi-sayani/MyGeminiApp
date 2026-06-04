package com.mygemini.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private ValueCallback<Uri[]> filePathCallback;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private final static int PERMISSIONS_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. إنشاء الـ WebView وعرضه بكامل الشاشة داخل نفس التطبيق
        myWebView = new WebView(this);
        setContentView(myWebView);

        // 2. ضبط إعدادات المحرك ليتوافق تماماً مع متطلبات موقع Gemini
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        
        // السماح بالتشغيل التلقائي للصوت والوسائط دون قيود
        webSettings.setMediaPlaybackRequiresUserGesture(false); 
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // إجبار موقع Gemini على معاملة التطبيق كمتصفح Chrome حديث وكامل
        String chromeUserAgent = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE + "; " + Build.MODEL + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
        webSettings.setUserAgentString(chromeUserAgent);

        // 3. منع فتح الروابط في المتصفح الخارجي وإبقائها داخل التطبيق
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // إجبار أي رابط داخل Gemini على الفتح في نفس التطبيق
                view.loadUrl(url);
                return true;
            }
        });

        // 4. معالجة طلبات الأذونات الحساسة (الميكروفون والكاميرا) لتعمل داخل الـ WebView
        myWebView.setWebChromeClient(new WebChromeClient() {
            
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // تشغيل خط منح الصلاحية للموقع على الواجهة الرئيسية للتطبيق فوراً
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            request.grant(request.getResources());
                        }
                    });
                }
            }

            // معالجة رفع الملفات والتقاط الصور من الاستوديو
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }
                MainActivity.this.filePathCallback = filePathCallback;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "إتمام الإجراء باستخدام:");

                startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);
                return true;
            }
        });

        // 5. طلب صلاحيات الميكروفون من نظام الأندرويد للمستخدم لمرة واحدة
        checkAndRequestPermissions();

        // 6. تحميل موقع Gemini الرسمي داخل التطبيق مباشرة
        myWebView.loadUrl("https://gemini.google.com");
    }

    // دالة طلب الصلاحيات من نظام أندرويد
    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        }
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

    // دعم زر الرجوع للهاتف ليتنقل داخل صفحات جيمناي بدلاً من إغلاق التطبيق فجأة
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
