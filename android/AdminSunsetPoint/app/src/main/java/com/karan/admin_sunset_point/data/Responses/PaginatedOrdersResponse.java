package com.karan.admin_sunset_point.data.Responses;

import com.karan.admin_sunset_point.data.entity.OrderWithItems;
import java.util.List;

public class PaginatedOrdersResponse {
    public List<OrderWithItems> orders;
    public int totalCount;

    public PaginatedOrdersResponse(List<OrderWithItems> orders, int totalCount) {
        this.orders = orders;
        this.totalCount = totalCount;
    }
}
