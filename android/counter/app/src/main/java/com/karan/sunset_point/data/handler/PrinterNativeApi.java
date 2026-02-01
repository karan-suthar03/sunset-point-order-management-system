package com.karan.sunset_point.data.handler;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.karan.sunset_point.OnPrinterConnected;
import com.karan.sunset_point.PrinterManager;
import com.karan.sunset_point.data.Responses.OrderItemResponse;
import com.karan.sunset_point.data.Responses.OrderResponse;
import com.karan.sunset_point.data.entity.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PrinterNativeApi {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final WebView webView;

    public PrinterNativeApi(WebView webView) {
        this.webView = webView;
    }

    @JavascriptInterface
    public void connectPrinter(String requestId){
        executor.execute(() -> {
            try {
                Log.d("conneting",requestId);
                PrinterManager.connect((deviceName, connection, printer) -> {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("name",deviceName);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    String js = "window.__nativeResolve(" +
                            JSONObject.quote(requestId) + "," +
                            JSONObject.quote(obj.toString()) +
                            ");";
                    webView.post(()->webView.evaluateJavascript(js,null));
                });
            } catch (Exception e) {
                // Show error message to user
                webView.post(() -> {
                    android.widget.Toast.makeText(
                        webView.getContext(),
                        e.getMessage() != null ? e.getMessage() : "Failed to connect printer",
                        android.widget.Toast.LENGTH_LONG
                    ).show();
                });
                
                String js = "window.__nativeResolve(" +
                        JSONObject.quote(requestId) +
                        ");";
                webView.post(()->webView.evaluateJavascript(js,null));
                e.printStackTrace();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String formatKotDantsu(OrderResponse order) {

        StringBuilder sb = new StringBuilder();

        int totalItems = 0;
        int slNo = 1;

// ================= TITLE =================
        sb.append("[C]================================\n");
        sb.append("[C]KOT\n");
        sb.append("[C]================================\n");

// ================= ORDER ID + DATE =================
        sb.append("[L]Order Number : ").append(order.id).append("\n");

// ================= CUSTOMER / TABLE =================
        if (order.tag != null) {
            sb.append("[L]Customer : ").append(order.tag).append("\n");
        }
        if (order.createdAt != null && order.createdAt.length() >= 16) {
            String date = order.createdAt.substring(0, 10);   // yyyy-MM-dd
            String time = order.createdAt.substring(11, 16);  // HH:mm
            sb.append("[R]").append(date).append(" ").append(time);
        }
        sb.append("\n");


// ================= ITEMS HEADER =================
        sb.append("[L]--------------------------------\n");
        sb.append("[L]Sl.No  Item Name             Qty\n");
        sb.append("[L]--------------------------------\n");

// ================= ITEMS =================
        for (OrderItemResponse item : order.items) {

            if ("CANCELLED".equalsIgnoreCase(item.status)) continue;

            totalItems += item.quantity;

            // Sl.No(5) Item(21) Qty(3)
            sb.append("[L]");
            sb.append(String.format(
                    "%-6d%-21s%3d",
                    slNo++,
                    item.name.length() > 21
                            ? item.name.substring(0, 21)
                            : item.name,
                    item.quantity
            ));
            sb.append("\n");
        }

// ================= FOOTER =================
        sb.append("[L]--------------------------------\n");
        sb.append("[L]Total Items : ").append(totalItems).append("\n");

// ================= FEED =================
        sb.append(" \n \n \n ");

        return sb.toString();
    }

    @JavascriptInterface
    public void printOrder(String requestId, String orderId_s){
        executor.execute(() -> {
            try {
                int orderId = Integer.parseInt(orderId_s);
                OrderResponse order = Handler.getInstance().getOrderForPrint(orderId);
                String text = formatKotDantsu(order);
                PrinterManager.print(text);
            } catch (Exception e) {
                // Show error message to user
                webView.post(() -> {
                    android.widget.Toast.makeText(
                        webView.getContext(),
                        e.getMessage() != null ? e.getMessage() : "Failed to print order",
                        android.widget.Toast.LENGTH_LONG
                    ).show();
                });
                e.printStackTrace();
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) +
                    ");";
            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }
}