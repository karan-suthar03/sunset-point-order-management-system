package com.karan.sunset_point;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.karan.sunset_point.data.handler.NativeApi;
import com.karan.sunset_point.data.handler.PrinterNativeApi;

public class MainActivity extends AppCompatActivity {

    private static WebView webView;
    private BroadcastReceiver bluetoothStateReceiver;
    private long backPressedTime;
    private Toast backToast;
    private OnBackPressedCallback backPressedCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestBluetoothPermission();

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
        webView.addJavascriptInterface(new PrinterNativeApi(webView), "PrinterNativeApi");
        webView.setWebViewClient(new WebViewClient());

        // Register Bluetooth state receiver
        registerBluetoothStateReceiver();

        // Load React build
        webView.loadUrl("http://10.254.173.21:5174/");
        
        // Send initial states after a short delay to ensure WebView is ready
        webView.postDelayed(() -> sendInitialStates(), 1000);

        // Setup back press handler using OnBackPressedDispatcher
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null) {
                    // Ask WebView to handle back press
                    webView.evaluateJavascript(
                        "(function() { " +
                        "  if (window.__handleBackPress) { " +
                        "    return window.__handleBackPress(); " +
                        "  } " +
                        "  return false; " +
                        "})();",
                        result -> {
                            // result will be "true" if WebView handled it, "false" if not
                            if ("true".equals(result)) {
                                // WebView handled the back press (closed a popup/card)
                                return;
                            }
                            
                            // Nothing to close in WebView, show double-press-to-exit toast
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
                    );
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    private void registerBluetoothStateReceiver() {
        bluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            notifyBluetoothState(false);
                            PrinterManager.resetConnection();
                            notifyPrinterState(false, null);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            notifyBluetoothState(true);
                            break;
                    }
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);
    }

    private void notifyBluetoothState(boolean enabled) {
        if (webView != null) {
            String js = String.format("window.__onBluetoothStateChanged && window.__onBluetoothStateChanged(%s);", enabled);
            webView.post(() -> webView.evaluateJavascript(js, null));
        }
    }

    public static void notifyPrinterState(boolean connected, String printerName) {
        if (webView != null) {
            String nameStr = printerName != null ? "\"" + printerName + "\"" : "null";
            String js = String.format("window.__onPrinterStateChanged && window.__onPrinterStateChanged(%s, %s);", connected, nameStr);
            webView.post(() -> webView.evaluateJavascript(js, null));
        }
    }
    
    private void sendInitialStates() {
        // Send initial Bluetooth state
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            notifyBluetoothState(bluetoothAdapter.isEnabled());
        }
        
        // Send initial printer state
        boolean printerConnected = PrinterManager.isConnected();
        String printerName = PrinterManager.getConnectedPrinterName();
        notifyPrinterState(printerConnected, printerName);
    }

    private static final int BLUETOOTH_PERMISSION_CODE = 1001;
    private static final int REQUEST_ENABLE_BT = 1002;

    private void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        BLUETOOTH_PERMISSION_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted 🎉
                checkAndEnableBluetooth();
            } else {
                // Permission denied ❌
                Toast.makeText(this, "Bluetooth permissions are required for printing", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void checkAndEnableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth is required for printing functionality", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check Bluetooth status when app resumes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                checkAndEnableBluetooth();
            }
        } else {
            checkAndEnableBluetooth();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothStateReceiver != null) {
            unregisterReceiver(bluetoothStateReceiver);
        }
    }

}