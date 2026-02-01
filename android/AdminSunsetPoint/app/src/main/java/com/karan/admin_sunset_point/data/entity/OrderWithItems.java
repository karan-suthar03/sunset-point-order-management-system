package com.karan.admin_sunset_point.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class OrderWithItems {

    @Embedded
    public Order order;

    @Relation(
            parentColumn = "order_id",
            entityColumn = "order_id"
    )
    public List<OrderItem> items;
}
