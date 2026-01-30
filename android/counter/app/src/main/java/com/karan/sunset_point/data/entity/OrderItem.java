package com.karan.sunset_point.data.entity;

public class OrderItem {

    public int order_item_id;

    public int order_id;
    public int dish_id;

    public int quantity;

    public String dish_name_snapshot;

    public int price_snapshot;

    public ItemStatus item_status = ItemStatus.PENDING;
}
