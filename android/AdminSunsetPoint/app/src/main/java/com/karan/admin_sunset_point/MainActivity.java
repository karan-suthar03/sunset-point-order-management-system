package com.karan.admin_sunset_point;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.karan.admin_sunset_point.data.handler.NativeApi;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private long backPressedTime;
    private Toast backToast;
    private OnBackPressedCallback backPressedCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.mainWebView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        webView.addJavascriptInterface(new NativeApi(webView), "NativeApi");
        webView.setWebViewClient(new WebViewClient());


        // Load React build
        webView.loadUrl("http://192.168.31.55:5173/");

        // Setup back press handler using OnBackPressedDispatcher
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    // Nothing to go back to, show double-press-to-exit toast
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        // Second press within 2 seconds - exit app
                        if (backToast != null) {
                            backToast.cancel();
                        }
                        finishAffinity(); // This kills the app completely
                    } else {
                        // First press - show toast
                        backToast = Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT);
                        backToast.show();
                    }
                    backPressedTime = System.currentTimeMillis();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }
}