import apiClient from "./index.js";

function uuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// ========== fetchTodaysSales ==========
async function fetchTodaysSales_w() {
    try {
        const response = await apiClient.get('/admin/orders/todays-sales');
        return response.data.totalSales;
    } catch (error) {
        console.error("Error fetching today's sales:", error);
        throw error;
    }
}

async function fetchTodaysSales_a() {
    let result = await (new Promise((resolve) => {
        const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[id] = resolve;

        window.NativeApi.getTodaysSales(id);
    }));
    return Number(result);
}

// ========== getOrders ==========
async function getOrders_w(params) {
    try {   
        params = {searchQuery: params.searchQuery, startDate: params.dateRange.start, endDate: params.dateRange.end, sortKey: params.sortConfig.key, sortDirection: params.sortConfig.direction, page: params.currentPage};
        console.log("Fetching orders with params:", new URLSearchParams(params).toString());
        const response = await apiClient.get('/admin/orders?' + new URLSearchParams(params).toString());
        return response.data;
    } catch (error) {
        console.error("Error fetching orders:", error);
        throw error;
    }
}

async function getOrders_a(params) {
    let result = await (new Promise((resolve) => {
        const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[id] = resolve;

        window.NativeApi.getOrdersAdmin(
            id,
            JSON.stringify(params)
        );
    }));
    return result;
}

// ========== getOrderById ==========
async function getOrderById_w(orderId) {
    try {
        const response = await apiClient.get(`/admin/orders/${orderId}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching order by ID:", error);
        throw error;
    }
}

async function getOrderById_a(orderId) {
    let result = await (new Promise((resolve) => {
        const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[id] = resolve;

        window.NativeApi.getOrderById(id, String(orderId));
    }));
    
    return result;
}

// ========== Select Implementation ==========
let fetchTodaysSales;
let getOrders;
let getOrderById;

if (window.NativeApi) {
    fetchTodaysSales = fetchTodaysSales_a;
    getOrders = getOrders_a;
    getOrderById = getOrderById_a;
} else {
    fetchTodaysSales = fetchTodaysSales_w;
    getOrders = getOrders_w;
    getOrderById = getOrderById_w;
}

export { fetchTodaysSales, getOrders, getOrderById };