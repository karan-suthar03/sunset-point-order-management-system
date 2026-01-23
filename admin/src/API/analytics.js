import apiClient from "./index.js";

export async function getAnalytics(range) {
    try {
        let response;
        if (range instanceof Object) {
            response = await apiClient.get(`/admin/analytics?start=${range.start}&end=${range.end}`);
        }else{
            response = await apiClient.get(`/admin/analytics?range=${range}`);
        }
        return response.data;
    } catch (error) {
        console.error("Error fetching dashboard data:", error);
        throw error;
    }
}

export async function getDishPerformance(range,type,limit=5) {
    try {
        let response;
        if (range instanceof Object) {
            response = await apiClient.get(`/admin/analytics/dish-performance?start=${range.start}&end=${range.end}&type=${type}&limit=${limit}`);
        }
        else{
            response = await apiClient.get(`/admin/analytics/dish-performance?range=${range}&type=${type}&limit=${limit}`);
        }
        return response.data;
    } catch (error) {
        console.error("Error fetching dish performance data:", error);
        throw error;
    }
}

export async function getCategoryPerformance(range) {
    try {
        let response;
        if (range instanceof Object) {
            response = await apiClient.get(`/admin/analytics/category-performance?start=${range.start}&end=${range.end}`);
        }
        else{
            response = await apiClient.get(`/admin/analytics/category-performance?range=${range}`);
        }
        return response.data;
    } catch (error) {
        console.error("Error fetching category performance data:", error);
        throw error;
    }
}

export async function getTotalRevenue() {
    try {
        const response = await apiClient.get('/admin/dashboard/summary');
        return response.data.totalRevenue;
    } catch (error) {
        console.error("Error fetching total revenue:", error);
        throw error;
    }
}


export async function getTrendData(range) {
    try {
        const response = await apiClient.get(`/admin/dashboard/order-trends?range=${range}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching order trends data:", error);
        throw error;
    }
}

export async function getCategorySalesData() {
    try {
        const response = await apiClient.get('/admin/dashboard/category-sales');
        return response.data;
    } catch (error) {
        console.error("Error fetching category sales data:", error);
        throw error;
    }
}

export async function getTopSellingItems() {
    try {
        const response = await apiClient.get('/admin/dashboard/top-selling-items');
        return response.data;
    } catch (error) {
        console.error("Error fetching top selling items:", error);
        throw error;
    }   
}

export async function getHighValueItems() {
    try {
        const response = await apiClient.get('/admin/dashboard/high-value-items');
        return response.data;
    } catch (error) {
        console.error("Error fetching high value items:", error);
        throw error;
    }
}