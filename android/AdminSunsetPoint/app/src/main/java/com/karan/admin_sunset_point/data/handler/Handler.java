package com.karan.admin_sunset_point.data.handler;

import com.karan.admin_sunset_point.App;
import com.karan.admin_sunset_point.data.AppDatabase;
import com.karan.admin_sunset_point.data.entity.CategoryPerformance;
import com.karan.admin_sunset_point.data.entity.Dish;
import com.karan.admin_sunset_point.data.entity.DishPerformance;
import com.karan.admin_sunset_point.data.entity.OrderAnalysis;
import com.karan.admin_sunset_point.data.entity.OrderWithItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Handler {
    private static Handler handler;
    private AppDatabase db;

    private Handler(){
        db = AppDatabase.getInstance(App.context);
    }

    public static Handler getInstance(){
        if(handler == null){
            handler = new Handler();
        }
        return handler;
    }

    public OrderAnalysis getAnalyticsByDateRange(String start, String end) {
        OrderAnalysis orderAnalysis = new OrderAnalysis();
        orderAnalysis.orderSummary = db.orderDao().getOrderSummary(start, end);
        orderAnalysis.categoryPerformances = db.orderDao().getTopCategories(start, end);
        orderAnalysis.hourlyRushes = db.orderDao().getHourlyRush(start, end);
        orderAnalysis.salesTrends = db.orderDao().getSalesTrend(start, end);
        orderAnalysis.orderSizeDistribution = db.orderDao().getOrderSizeDistribution(start, end);
        return orderAnalysis;
    }

    public List<CategoryPerformance> getCategoryPerformanceByDateRange(String start, String end) {
        return db.orderDao().getTopCategories(start, end);
    }

    public List<DishPerformance> getDishPerformance(String start, String end, String type, int limit) {
        if ("revenue".equals(type)) {
            return db.orderDao().getTopDishesByRevenue(start, end, limit);
        } else {
            return db.orderDao().getTopDishesByQuantity(start, end, limit);
        }
    }

    public List<OrderWithItems> getOrdersAdmin(String searchQuery, String startDate, String endDate, String sortKey, String sortDirection, int page) {
        searchQuery = searchQuery == null ? "" : searchQuery;
        page = page <= 0 ? 1 : page;

        if ("order_total".equals(sortKey)) {
            return "asc".equals(sortDirection)
                    ? db.orderDao().getOrdersByTotalAsc(searchQuery, startDate, endDate, page)
                    : db.orderDao().getOrdersByTotalDesc(searchQuery, startDate, endDate, page);
        } else {
            return "asc".equals(sortDirection)
                    ? db.orderDao().getOrdersByCreatedAsc(searchQuery, startDate, endDate, page)
                    : db.orderDao().getOrdersByCreatedDesc(searchQuery, startDate, endDate, page);
        }
    }

    public OrderWithItems getOrderById(int orderId) {
        return db.orderDao().getOrderByIdWithItems(orderId);
    }

    public int getTodaysSales() {
        return db.orderDao().getTodaysTotalSales();
    }

    public Map<String, JSONArray> getMenuItems() throws JSONException {
        List<Dish> dishes = db.dishDao().getAllDishes();
        Map<String, JSONArray> menuByCategory = new HashMap<>();
        
        for (Dish dish : dishes) {
            if (!menuByCategory.containsKey(dish.category)) {
                menuByCategory.put(dish.category, new JSONArray());
            }
            
            JSONObject dishObj = new JSONObject();
            dishObj.put("id", dish.dish_id);
            dishObj.put("name", dish.dish_name);
            dishObj.put("price", dish.price);
            
            menuByCategory.get(dish.category).put(dishObj);
        }
        
        return menuByCategory;
    }

    public Dish getDishById(int dishId) {
        return db.dishDao().getDishById(dishId);
    }

    public List<String> getCategories() {
        List<Dish> dishes = db.dishDao().getAllDishes();
        List<String> categories = new ArrayList<>();
        
        for (Dish dish : dishes) {
            if (!categories.contains(dish.category)) {
                categories.add(dish.category);
            }
        }
        
        return categories;
    }

    public boolean updateMenuItem(JSONObject itemData) throws JSONException {
        try {
            Dish dish = new Dish();
            
            if (itemData.has("id")) {
                // Update existing dish
                int id = itemData.getInt("id");
                dish.dish_id = id;
                dish.dish_name = itemData.getString("name");
                dish.price = itemData.getInt("price");
                dish.category = itemData.getString("category");
                db.dishDao().updateDish(dish);
            } else {
                // Insert new dish
                dish.dish_name = itemData.getString("name");
                dish.price = itemData.getInt("price");
                dish.category = itemData.getString("category");
                db.dishDao().insertDish(dish);
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
