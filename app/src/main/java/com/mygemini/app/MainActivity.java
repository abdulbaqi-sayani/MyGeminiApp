package com.mygemini.app;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // رابط موقع جيمناي الرسمي
        String url = "https://gemini.google.com";

        // بناء وتخصيص حاوية متصفح كروم داخل تطبيقك
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        
        // تخصيص لون شريط العنوان العلوي ليناسب التطبيق (أزرق غامق متناسق)
        builder.setToolbarColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        
        // تفعيل ميزة إظهار عنوان الموقع لحماية أمن المستخدم
        builder.setShowTitle(true);

        // تفعيل تأثيرات الحركة البرمجية عند فتح وإغلاق التطبيق
        builder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);
        builder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);

        CustomTabsIntent customTabsIntent = builder.build();
        
        // تشغيل الرابط فوراً بمجرد فتح التطبيق
        customTabsIntent.launchUrl(this, Uri.parse(url));

        // إنهاء الـ Activity الحالية عند الخروج حتى لا تظل فارغة في الخلفية
        finish();
    }
}
