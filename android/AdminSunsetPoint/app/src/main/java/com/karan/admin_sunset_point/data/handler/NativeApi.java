package com.karan.admin_sunset_point.data.handler;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.karan.admin_sunset_point.data.entity.CategoryPerformance;
import com.karan.admin_sunset_point.data.entity.Dish;
import com.karan.admin_sunset_point.data.entity.DishPerformance;
import com.karan.admin_sunset_point.data.entity.HourlyRush;
import com.karan.admin_sunset_point.data.entity.Order;
import com.karan.admin_sunset_point.data.entity.OrderAnalysis;
import com.karan.admin_sunset_point.data.entity.OrderItem;
import com.karan.admin_sunset_point.data.entity.OrderSizeDistribution;
import com.karan.admin_sunset_point.data.entity.OrderSummary;
import com.karan.admin_sunset_point.data.entity.OrderWithItems;
import com.karan.admin_sunset_point.data.entity.SalesTrend;
import com.karan.admin_sunset_point.data.handler.DateRangeUtil.DateRange;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NativeApi {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final WebView webView;

    public NativeApi(WebView webView) {
        this.webView = webView;
    }

    @JavascriptInterface
    public void getAnalyticsByPredefinedRange(String requestId, String range){
        executor.execute(() -> {
            String result = "";
            try{
                DateRange dateRange = DateRangeUtil.getDateRange(range);
                OrderAnalysis orderAnalysis = Handler.getInstance().getAnalyticsByDateRange(dateRange.start, dateRange.end);
                JSONObject obj = new JSONObject();
                OrderSummary orderSummary = orderAnalysis.orderSummary;
                obj.put("totalRevenue", orderSummary.total_revenue);
                obj.put("avgOrdersPerDay", orderSummary.total_orders);
                obj.put("avgOrderValue", orderSummary.avg_order_value);
                obj.put("avgNumberOfItemsPerOrder", orderSummary.avg_number_of_items_per_order);
                obj.put("totalOrders",orderSummary.total_orders);

                JSONArray categoryPerformanceData = getJsonArray(orderAnalysis);
                obj.put("categoryPerformanceData", categoryPerformanceData);

                JSONArray hourlyRushData = getHourlyRushData(orderAnalysis);
                obj.put("hourlyRushData", hourlyRushData);

                JSONArray salesTrendData = getSalesTrendData(orderAnalysis);
                obj.put("salesTrendData", salesTrendData);

                JSONArray orderSizeData = getSizeData(orderAnalysis);
                obj.put("orderSizeData",orderSizeData);


                result = obj.toString();
            }catch (Exception e){
                e.printStackTrace();
            }

            Log.d("result",result);

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";

            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }

    private JSONArray getSizeData(OrderAnalysis orderAnalysis) throws JSONException {
        JSONArray orderSizeData = new JSONArray();
        List<OrderSizeDistribution> orderSizeDistribution = orderAnalysis.orderSizeDistribution;
        for (OrderSizeDistribution r : orderSizeDistribution) {
            JSONObject obj = new JSONObject();
            obj.put("size",r.size);
            obj.put("count",r.count);
            orderSizeData.put(obj);
        }
        return orderSizeData;
    }

    private JSONArray getSalesTrendData(OrderAnalysis orderAnalysis) throws JSONException {
        JSONArray salesTrendData = new JSONArray();
        List<SalesTrend> salesTrends = orderAnalysis.salesTrends;

        for (SalesTrend r : salesTrends) {
            JSONObject obj = new JSONObject();
            obj.put("date", r.date);      // YYYY-MM-DD
            obj.put("sales", r.sales);    // Number
            obj.put("orders", r.orders);  // Number
            obj.put("aov", r.aov);        // Number
            salesTrendData.put(obj);
        }
        return salesTrendData;
    }

    @NonNull
    private static JSONArray getHourlyRushData(OrderAnalysis orderAnalysis) throws JSONException {
        JSONArray hourlyRushData = new JSONArray();
        List<HourlyRush> hourlyRushes = orderAnalysis.hourlyRushes;
        for (HourlyRush r : hourlyRushes) {
            int h = r.hour;

            String label;
            if (h == 0) {
                label = "12 AM";
            } else if (h < 12) {
                label = h + " AM";
            } else if (h == 12) {
                label = "12 PM";
            } else {
                label = (h - 12) + " PM";
            }

            JSONObject hr = new JSONObject();
            hr.put("time", label);
            hr.put("orders", r.avg_orders);

            hourlyRushData.put(hr);
        }
        return hourlyRushData;
    }

    @NonNull
    private static JSONArray getJsonArray(OrderAnalysis orderAnalysis) throws JSONException {
        JSONArray categoryPerformanceData = new JSONArray();
        List<CategoryPerformance> categoryPerformances = orderAnalysis.categoryPerformances;
        for (CategoryPerformance categoryPerformance : categoryPerformances) {
            JSONObject categoryPerformanceObj = new JSONObject();
            categoryPerformanceObj.put("name", categoryPerformance.name);
            categoryPerformanceObj.put("sales", categoryPerformance.sales);
            categoryPerformanceObj.put("quantity", categoryPerformance.quantity);
            categoryPerformanceData.put(categoryPerformanceObj);
        }


        return categoryPerformanceData;
    }

    @JavascriptInterface
    public void getAnalyticsByDateRange(String requestId, String start, String end){
        executor.execute(() -> {
            String result = "";
            try{
                Log.d(start,end);
                DateRange dateRange = new DateRange(start, end);
                OrderAnalysis orderAnalysis = Handler.getInstance().getAnalyticsByDateRange(dateRange.start, dateRange.end);
                JSONObject obj = new JSONObject();
                OrderSummary orderSummary = orderAnalysis.orderSummary;
                obj.put("totalRevenue", orderSummary.total_revenue);
                obj.put("avgOrdersPerDay", orderSummary.total_orders);
                obj.put("avgOrderValue", orderSummary.avg_order_value);
                obj.put("avgNumberOfItemsPerOrder", orderSummary.avg_number_of_items_per_order);
                obj.put("totalOrders",orderSummary.total_orders);

                JSONArray categoryPerformanceData = getJsonArray(orderAnalysis);
                obj.put("categoryPerformanceData", categoryPerformanceData);

                JSONArray hourlyRushData = getHourlyRushData(orderAnalysis);
                obj.put("hourlyRushData", hourlyRushData);

                JSONArray salesTrendData = getSalesTrendData(orderAnalysis);
                obj.put("salesTrendData", salesTrendData);

                JSONArray orderSizeData = getSizeData(orderAnalysis);
                obj.put("orderSizeData",orderSizeData);


                result = obj.toString();
            }catch (Exception e){
                e.printStackTrace();
            }

            Log.d("result",result);

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";

            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }

    @JavascriptInterface
    public void getCategoryPerformanceByDateRange(String requestId, String start, String end){
        executor.execute(() -> {
            String result = "";
            try{
                DateRange dateRange = new DateRange(start, end);
                List<CategoryPerformance> categoryPerformances = Handler.getInstance().getCategoryPerformanceByDateRange(dateRange.start, dateRange.end);
                JSONArray categoryPerformanceData = getCategoryPerformanceData(categoryPerformances);
                result = categoryPerformanceData.toString();
            } catch (Exception e){
                e.printStackTrace();
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";

            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }

    @JavascriptInterface
    public void getCategoryPerformanceByPredefinedRange(String requestId, String range){
        executor.execute(() -> {
            String result = "";
            try{
                DateRange dateRange = DateRangeUtil.getDateRange(range);
                List<CategoryPerformance> categoryPerformances = Handler.getInstance().getCategoryPerformanceByDateRange(dateRange.start, dateRange.end);
                JSONArray categoryPerformanceData = getCategoryPerformanceData(categoryPerformances);
                result = categoryPerformanceData.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";

            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }

    @NonNull
    private static JSONArray getCategoryPerformanceData(List<CategoryPerformance> categoryPerformances) throws JSONException {
        JSONArray categoryPerformanceData = new JSONArray();
        for (CategoryPerformance categoryPerformance : categoryPerformances) {
            JSONObject categoryPerformanceObj = new JSONObject();
            categoryPerformanceObj.put("name", categoryPerformance.name);
            categoryPerformanceObj.put("sales", categoryPerformance.sales);
            categoryPerformanceObj.put("quantity", categoryPerformance.quantity);
            categoryPerformanceData.put(categoryPerformanceObj);
        }
        return categoryPerformanceData;
    }

    @JavascriptInterface
    public void getDishPerformanceByPredefinedRange(String requestId, String range,String type, String limit_s){
        executor.execute(() -> {
            String result = "";
            try{
                DateRange dateRange = DateRangeUtil.getDateRange(range);
                int limit = Integer.parseInt(limit_s);
                List<DishPerformance> dishPerformances = Handler.getInstance().getDishPerformance(dateRange.start,dateRange.end,type,limit);
                JSONArray dishPerformanceData = new JSONArray();

                for (DishPerformance d : dishPerformances) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", d.id);
                    obj.put("name", d.name);
                    obj.put("category", d.category);
                    obj.put("sales", d.sales);
                    obj.put("revenue", d.revenue);
                    dishPerformanceData.put(obj);
                }

                result = dishPerformanceData.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";

            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }

    @JavascriptInterface
    public void getDishPerformanceByDateRange(String requestId, String start, String end, String type, String limit_s){
        executor.execute(() -> {
            String result = "";
            try{
                DateRange dateRange = new DateRange(start, end);
                int limit = Integer.parseInt(limit_s);
                List<DishPerformance> dishPerformances = Handler.getInstance().getDishPerformance(dateRange.start,dateRange.end,type,limit);
                JSONArray dishPerformanceData = new JSONArray();

                for (DishPerformance d : dishPerformances) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", d.id);
                    obj.put("name", d.name);
                    obj.put("category", d.category);
                    obj.put("sales", d.sales);
                    obj.put("revenue", d.revenue);
                    dishPerformanceData.put(obj);
                }

                result = dishPerformanceData.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";

            webView.post(()->webView.evaluateJavascript(js,null));
        });
    }

    @JavascriptInterface
    public void getOrdersAdmin(String requestId, String params) {
        executor.execute(() -> {
            String result = "";
            try {
                JSONObject json = new JSONObject(params);

                String searchQuery   = json.optString("searchQuery", "");
                int page             = json.optInt("currentPage", 1);

                JSONObject dateRange = json.optJSONObject("dateRange");
                String startDate     = dateRange != null ? dateRange.optString("start", null) : null;
                String endDate       = dateRange != null ? dateRange.optString("end", null) : null;

                JSONObject sortConfig = json.optJSONObject("sortConfig");
                String sortKey        = sortConfig != null ? sortConfig.optString("key", "createdAt") : "createdAt";
                String sortDirection  = sortConfig != null ? sortConfig.optString("direction", "desc") : "desc";

                List<OrderWithItems> orderWithItems = Handler.getInstance().getOrdersAdmin(searchQuery, startDate, endDate, sortKey, sortDirection, page);

                JSONArray orders = new JSONArray();
                for (OrderWithItems orderWithItem : orderWithItems) {
                    Order order = orderWithItem.order;
                    JSONObject orderObj = new JSONObject();

                    orderObj.put("order_id", order.order_id);
                    orderObj.put("order_tag", order.order_tag);
                    orderObj.put("order_total", order.order_total);
                    orderObj.put("order_status", order.order_status);
                    orderObj.put("is_payment_done", order.is_payment_done);
                    orderObj.put("created_at", order.created_at);

                    JSONArray items = new JSONArray();
                    for (OrderItem item : orderWithItem.items) {
                        JSONObject itemObj = new JSONObject();
                        itemObj.put("order_item_id", item.order_item_id);
                        itemObj.put("quantity", item.quantity);
                        itemObj.put("item_status", item.item_status);
                        itemObj.put("price_snapshot", item.price_snapshot);
                        itemObj.put("dish_id", item.dish_id);
                        items.put(itemObj);
                    }
                    orderObj.put("items", items);
                    orders.put(orderObj);
                }

                result = orders.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

    @JavascriptInterface
    public void getOrderById(String requestId, String orderId) {
        executor.execute(() -> {
            String result = "";
            try {
                int orderIdInt = Integer.parseInt(orderId);
                OrderWithItems orderWithItem = Handler.getInstance().getOrderById(orderIdInt);

                if (orderWithItem != null) {
                    Order order = orderWithItem.order;
                    JSONObject orderObj = new JSONObject();

                    orderObj.put("order_id", order.order_id);
                    orderObj.put("order_tag", order.order_tag);
                    orderObj.put("order_total", order.order_total);
                    orderObj.put("order_status", order.order_status);
                    orderObj.put("is_payment_done", order.is_payment_done);
                    orderObj.put("created_at", order.created_at);

                    JSONArray items = new JSONArray();
                    for (OrderItem item : orderWithItem.items) {
                        JSONObject itemObj = new JSONObject();
                        itemObj.put("order_item_id", item.order_item_id);
                        itemObj.put("dish_id", item.dish_id);
                        itemObj.put("dish_name", item.dish_name_snapshot);
                        itemObj.put("price", item.price_snapshot);
                        itemObj.put("quantity", item.quantity);
                        itemObj.put("item_status", item.item_status);
                        items.put(itemObj);
                    }
                    orderObj.put("items", items);

                    result = orderObj.toString();
                } else {
                    result = "null";
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = "null";
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

    @JavascriptInterface
    public void getTodaysSales(String requestId) {
        executor.execute(() -> {
            String result = "";
            try {
                int totalSales = Handler.getInstance().getTodaysSales();
                result = String.valueOf(totalSales);
            } catch (Exception e) {
                e.printStackTrace();
                result = "0";
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

    @JavascriptInterface
    public void getMenuItems(String requestId) {
        executor.execute(() -> {
            String result = "";
            try {
                Map<String, JSONArray> menuByCategory = Handler.getInstance().getMenuItems();
                JSONObject resultObj = new JSONObject();
                
                for (Map.Entry<String, JSONArray> entry : menuByCategory.entrySet()) {
                    resultObj.put(entry.getKey(), entry.getValue());
                }
                
                result = resultObj.toString();
            } catch (Exception e) {
                e.printStackTrace();
                result = "{}";
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

    @JavascriptInterface
    public void getMenuItemById(String requestId, String dishId) {
        executor.execute(() -> {
            String result = "";
            try {
                int id = Integer.parseInt(dishId);
                Dish dish = Handler.getInstance().getDishById(id);
                
                if (dish != null) {
                    JSONObject dishObj = new JSONObject();
                    dishObj.put("dish_id", dish.dish_id);
                    dishObj.put("dish_name", dish.dish_name);
                    dishObj.put("price", dish.price);
                    dishObj.put("category", dish.category);
                    
                    JSONObject resultObj = new JSONObject();
                    resultObj.put("dish", dishObj);
                    resultObj.put("ingredients", new JSONArray()); // Empty array for Android
                    
                    result = resultObj.toString();
                } else {
                    result = "null";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "null";
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

    @JavascriptInterface
    public void getCategories(String requestId) {
        executor.execute(() -> {
            String result = "";
            try {
                List<String> categories = Handler.getInstance().getCategories();
                JSONArray categoriesArray = new JSONArray(categories);
                result = categoriesArray.toString();
            } catch (Exception e) {
                e.printStackTrace();
                result = "[]";
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

    @JavascriptInterface
    public void updateMenuItem(String requestId, String itemDataJson) {
        executor.execute(() -> {
            String result = "";
            try {
                JSONObject itemData = new JSONObject(itemDataJson);
                boolean success = Handler.getInstance().updateMenuItem(itemData);
                
                JSONObject resultObj = new JSONObject();
                resultObj.put("message", success ? "Dish updated successfully" : "Failed to update dish");
                result = resultObj.toString();
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject errorObj = new JSONObject();
                try {
                    errorObj.put("message", "Internal Server Error");
                    result = errorObj.toString();
                } catch (JSONException ex) {
                    result = "{\"message\":\"Internal Server Error\"}";
                }
            }

            String js = "window.__nativeResolve(" +
                    JSONObject.quote(requestId) + "," +
                    JSONObject.quote(result) +
                    ");";
            webView.post(() -> webView.evaluateJavascript(js, null));
        });
    }

}