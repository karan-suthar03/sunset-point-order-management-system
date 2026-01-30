package com.karan.sunset_point.data.entity;

public class Order {

    public int order_id;

    public String order_tag;

    public boolean is_payment_done = false;

    public int order_total = 0;

    public OrderStatus order_status = OrderStatus.OPEN;

    public String created_at;
}
