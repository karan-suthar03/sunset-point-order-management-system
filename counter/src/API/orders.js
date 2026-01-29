import apiClient from ".";

function uuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

let getOrders;

async function getOrders_w() {
  const response = await apiClient.get("/orders");
  return response.data;
}

async function getOrders_a() {
  let result = await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.getOrders(
      id
    );
  }));
  return result;
}

let createOrder;

async function createOrder_a(order) {
  await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.createOrder(
      id,
      JSON.stringify(order)
    );
  }));
}

async function createOrder_w(order) {
  const response = await apiClient.post("/orders", order);
  return response.data;
}

async function closeOrder(orderId) {
  const response = await apiClient.put(`/orders/close?id=${orderId}`);
  return response.data;
}

async function toggleServedStatus(orderId, itemId) {
  const response = await apiClient.put(`/orders/toggle-served`, {
    orderId,
    itemId,
  });
  return response.data;
}

async function deleteItemFromOrder(itemId) {
  const response = await apiClient.delete(`/orders/item?id=${itemId}`);
  return response.data;
}

async function toggleOrderPayment(orderId) {
  const response = await apiClient.put(`/orders/toggle-payment?id=${orderId}`);
  return response.data;
}

async function cancelOrder(orderId) {
  const response = await apiClient.put(`/orders/cancel?id=${orderId}`);
  return response.data;
}

if (window.NativeApi) {
    getOrders = getOrders_a;
    createOrder = createOrder_a;
} else {
    getOrders = getOrders_w;
    createOrder = createOrder_w;
}

export {
  getOrders,
  createOrder,
  closeOrder,
  toggleServedStatus,
  deleteItemFromOrder,
  toggleOrderPayment,
  cancelOrder,
};
