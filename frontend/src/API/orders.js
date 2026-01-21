import apiClient from ".";


async function getOrders() {
    const response = await apiClient.get('/orders');
    return response.data;
}

async function createOrder(order) {
    const response = await apiClient.post('/orders', order);
    return response.data;
}

async function closeOrder(orderId) {
    const response = await apiClient.put(`/orders/close?id=${orderId}`);
    return response.data;
}

export { getOrders, createOrder, closeOrder };