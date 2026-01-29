package com.karan.sunset_point.data.handler;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.karan.sunset_point.App;
import com.karan.sunset_point.data.AppDatabase;
import com.karan.sunset_point.data.entity.OrderItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NativeApi {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final WebView webView;

    public NativeApi(WebView webView) {
        this.webView = webView;
    }
    @JavascriptInterface
    public void getDishes(String requestId) {
        executor.execute(() -> {
            String result;

            try {
                result = Handler.getInstance().getDishes();
            } catch (Exception e) {
                result = "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ")";

            webView.post(() ->
                    webView.evaluateJavascript(js, null)
            );
        });
    }

    @JavascriptInterface
    public void createOrder(String requestId, String orderJson) {
        executor.execute(() -> {
            try {
                JSONObject orderObj = new JSONObject(orderJson);

                String tag = orderObj.getString("tag");
                JSONArray itemsArray = orderObj.getJSONArray("items");

                List<OrderItem> items = new ArrayList<>();

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject itemObj = itemsArray.getJSONObject(i);

                    OrderItem item = new OrderItem();
                    item.dish_id = itemObj.getInt("id");
                    item.dish_name_snapshot = itemObj.getString("name");
                    item.price_snapshot = itemObj.getInt("price");
                    item.quantity = itemObj.getInt("quantity");
                    items.add(item);
                }

                Handler.getInstance().createOrder(tag, items);

            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = "{\"success\":false,\"error\":\"karan suthar\"}";

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," + JSONObject.quote(result) +
                    ")";

            webView.post(() ->
                    webView.evaluateJavascript(js, null)
            );
        });
    }
    @JavascriptInterface
    public void getOrders(String requestId){
        executor.execute(() -> {
            String result;
            try {
                result = Handler.getInstance().getOrders();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Log.d("tag",result);

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," + JSONObject.quote(result) + ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }
}