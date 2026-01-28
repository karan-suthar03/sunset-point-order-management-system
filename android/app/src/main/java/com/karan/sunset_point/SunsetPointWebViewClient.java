package com.karan.sunset_point;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;

public class SunsetPointWebViewClient extends WebViewClient {

    @Override
    public WebResourceResponse shouldInterceptRequest(
            WebView view,
            WebResourceRequest request
    ) {
        String url = request.getUrl().toString();

        if (url.startsWith("http://localhost:3000")) {
            Log.d("LOCALHOST_LOG", "Intercepted request: " + url);

        }

        return super.shouldInterceptRequest(view, request);
    }
}
