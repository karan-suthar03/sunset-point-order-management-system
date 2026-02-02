package com.karan.admin_sunset_point;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.karan.admin_sunset_point.data.handler.NativeApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private long backPressedTime;
    private Toast backToast;
    private OnBackPressedCallback backPressedCallback;
    private ActivityResultLauncher<Intent> createDocumentLauncher;
    private static MainActivity instance;
    private String[] pendingBackupData;
    private String pendingBackupRequestId;

    public static MainActivity getInstance() {
        return instance;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.mainWebView);

        // Initialize file picker launcher
        createDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null && pendingBackupData != null) {
                            NativeApi nativeApi = new NativeApi(webView);
                            nativeApi.writeBackupToUri(uri, pendingBackupData, pendingBackupRequestId);
                            pendingBackupData = null;
                            pendingBackupRequestId = null;
                        }
                    } else {
                        // User cancelled
                        if (pendingBackupRequestId != null) {
                            String js = "window.__nativeResolve(" +
                                    "\"" + pendingBackupRequestId + "\"," +
                                    "\"{\\\"success\\\":false,\\\"message\\\":\\\"Backup cancelled\\\"}\")";
                            webView.post(() -> webView.evaluateJavascript(js, null));
                            pendingBackupRequestId = null;
                        }
                        pendingBackupData = null;
                    }
                }
        );

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
        webView.loadUrl("http://10.254.173.21:5173/");

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

    public void launchBackupFilePicker(String[] backupData, String requestId) {
        pendingBackupData = backupData;
        pendingBackupRequestId = requestId;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String filename = "sunset_point_backup_" + timestamp + ".zip";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        createDocumentLauncher.launch(intent);
    }
}