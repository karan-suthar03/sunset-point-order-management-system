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

let closeOrder;

async function closeOrder_w(orderId) {
  const response = await apiClient.put(`/orders/close?id=${orderId}`);
  return response.data;
}

async function closeOrder_a(orderId) {
  await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.closeOrder(
      id,
      orderId
    );
  }));
}

let toggleServedStatus;

async function toggleServedStatus_w(orderId, itemId) {
  const response = await apiClient.put(`/orders/toggle-served`, {
    orderId,
    itemId,
  });
  return response.data;
}

async function toggleServedStatus_a(orderId, itemId) {
  const result = await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.toggleServedStatus(
      id,
      orderId,
      itemId
    );
  }));
  return result;
}

let deleteItemFromOrder;

async function deleteItemFromOrder_w(itemId) {
  const response = await apiClient.delete(`/orders/item?id=${itemId}`);
  return response.data;
}

async function deleteItemFromOrder_a(itemId) {
  await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.deleteItemFromOrder(
      id,
      itemId
    );
  }));
}

let toggleOrderPayment;

async function toggleOrderPayment_w(orderId) {
  const response = await apiClient.put(`/orders/toggle-payment?id=${orderId}`);
  return response.data;
}

async function toggleOrderPayment_a(orderId) {
  return await (new Promise((resolve)=>{
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.toggleOrderPayment(
      id,
      orderId
    );
  }))
}

let cancelOrder;

async function cancelOrder_w(orderId) {
  const response = await apiClient.put(`/orders/cancel?id=${orderId}`);
  return response.data;
}

async function cancelOrder_a(orderId) {
  await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
    window.__nativePromises[id] = resolve;
    window.NativeApi.cancelOrder(
      id,
      orderId
    );
  }));
}

if (window.NativeApi) {
    getOrders = getOrders_a;
    createOrder = createOrder_a;
    closeOrder = closeOrder_a;
    toggleServedStatus = toggleServedStatus_a;
    deleteItemFromOrder = deleteItemFromOrder_a;
    toggleOrderPayment = toggleOrderPayment_a;
    cancelOrder = cancelOrder_a;
} else {
    getOrders = getOrders_w;
    createOrder = createOrder_w;
    closeOrder = closeOrder_w;
    toggleServedStatus = toggleServedStatus_w;
    deleteItemFromOrder = deleteItemFromOrder_w;
    toggleOrderPayment = toggleOrderPayment_w;
    cancelOrder = cancelOrder_w;
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
