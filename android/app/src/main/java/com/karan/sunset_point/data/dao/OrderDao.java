package com.karan.sunset_point.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.karan.sunset_point.data.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insertOrder(Order order);

    @Query("SELECT * FROM orders ORDER BY created_at DESC")
    List<Order> getAllOrders();

    @Query("SELECT * FROM orders WHERE order_id = :id")
    Order getOrderById(int id);

    @Query("UPDATE orders SET order_total = :total WHERE order_id = :orderId")
    void updateOrderTotal(int orderId, int total);

    @Query("SELECT COALESCE(SUM(quantity * price_snapshot), 0) FROM order_items WHERE order_id = :orderId")
    int calculateOrderTotal(int orderId);

    /** replaces trigger + function */
    @Transaction
    default void recalcOrderTotal(int orderId) {
        int total = calculateOrderTotal(orderId);
        updateOrderTotal(orderId, total);
    }
}
