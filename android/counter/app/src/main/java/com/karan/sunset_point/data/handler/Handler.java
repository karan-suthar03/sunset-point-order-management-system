package com.karan.sunset_point.data.handler;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;
import com.karan.sunset_point.App;
import com.karan.sunset_point.data.Responses.OrderResponse;
import com.karan.sunset_point.data.entity.OrderItem;

import java.util.List;

public class Handler {

    private static Handler handler;

    private static final String BASE =
            "content://com.karan.sunset_point.provider/";

    private Handler(){}

    public static Handler getInstance(){
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }

    private Cursor query(String path) {
        Uri uri = Uri.parse(BASE + path);
        return App.context.getContentResolver()
                .query(uri, null, null, null, null);
    }

    private void insert(String path, ContentValues values) {
        Uri uri = Uri.parse(BASE + path);
        App.context.getContentResolver().insert(uri, values);
    }

    private int update(String path, ContentValues values) {
        Uri uri = Uri.parse(BASE + path);
        return App.context.getContentResolver()
                .update(uri, values, null, null);
    }

    private int delete(String path) {
        Uri uri = Uri.parse(BASE + path);
        return App.context.getContentResolver()
                .delete(uri, null, null);
    }

    // ---------------- READ ----------------

    public String getOrders() {
        try {
            Cursor c = query("orders");
            if (c != null && c.moveToFirst()) {
                String json = c.getString(0);
                c.close();
                return json;
            }
            return "[]";
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public String getDishes() {
        try {
            Cursor c = query("dishes");
            if (c != null && c.moveToFirst()) {
                String json = c.getString(0);
                c.close();
                return json;
            }
            return "{}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public OrderResponse getOrderForPrint(int orderId) {
        try {
            Cursor c = query("orderPrint/" + orderId);
            if (c != null && c.moveToFirst()) {
                String json = c.getString(0);
                c.close();
                return new Gson().fromJson(json, OrderResponse.class);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // ---------------- WRITE ----------------

    public void createOrder(String tag, List<OrderItem> items) {
        try {
            ContentValues v = new ContentValues();
            v.put("tag", tag);
            v.put("items", new Gson().toJson(items));
            insert("createOrder", v);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String toggleServedStatus(int orderId, int itemId) {
        try {
            ContentValues v = new ContentValues();
            v.put("orderId", orderId);
            v.put("itemId", itemId);

            // Capture the return code from the provider
            int result = update("toggleServed", v);

            // Logic: 1 = SERVED, 2 = PENDING, 0 = Error
            if (result == 1) {
                return "SERVED";
            } else if (result == 2) {
                return "PENDING";
            } else {
                return "ERROR";
            }

        } catch (Exception e){
            e.printStackTrace();
            return "ERROR";
        }
    }

    public void closeOrder(int orderId) {
        try {
            ContentValues v = new ContentValues();
            v.put("orderId", orderId);
            update("closeOrder", v);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteOrderItem(int itemId) {
        try {
            delete("deleteItem/" + itemId);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean toggleOrderPayment(int orderId) {
        try {
            ContentValues v = new ContentValues();
            v.put("orderId", orderId);

            // Capture the return code
            int result = update("togglePayment", v);

            // Logic: 1 = TRUE (Paid), 2 = FALSE (Unpaid)
            if (result == 1) {
                return true;
            } else if (result == 2) {
                return false;
            } else {
                return false; // Error or failure
            }

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void cancelOrder(int orderId) {
        try {
            ContentValues v = new ContentValues();
            v.put("orderId", orderId);
            update("cancelOrder", v);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
