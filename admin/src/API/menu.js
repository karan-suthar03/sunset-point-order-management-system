import apiClient from ".";

function uuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// ========== getMenuItems ==========
async function getMenuItems_w() {
    try {
        const response = await apiClient.get('/dishes');
        return response.data;
    } catch (error) {
        console.error("Error fetching menu items:", error);
        throw error;
    }
}

async function getMenuItems_a() {
    let result = await (new Promise((resolve) => {
        const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[id] = resolve;

        window.NativeApi.getMenuItems(id);
    }));
    return result;
}

// ========== getMenuItemById ==========
async function getMenuItemById_w(id) {
    try {
        const response = await apiClient.get(`/dishes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching menu item with id ${id}:`, error);
        throw error;
    }
}

async function getMenuItemById_a(id) {
    let result = await (new Promise((resolve) => {
        const reqId = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[reqId] = resolve;

        window.NativeApi.getMenuItemById(reqId, String(id));
    }));
    return result;
}

// ========== getCategories ==========
async function getCategories_w() {
    try {
        const response = await apiClient.get('/dishes/categories');
        return response.data;
    } catch (error) {
        console.error("Error fetching categories:", error);
        throw error;
    }
}

async function getCategories_a() {
    let result = await (new Promise((resolve) => {
        const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[id] = resolve;

        window.NativeApi.getCategories(id);
    }));
    return result;
}

// ========== updateMenuItem ==========
async function updateMenuItem_w(itemData) {
    try {
        const response = await apiClient.put(`/dishes`, itemData);
        return response.data;
    } catch (error) {
        console.error(`Error updating menu item:`, error);
        throw error;
    }
}

async function updateMenuItem_a(itemData) {
    let result = await (new Promise((resolve) => {
        const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();
        window.__nativePromises[id] = resolve;

        window.NativeApi.updateMenuItem(id, JSON.stringify(itemData));
    }));
    return result;
}

// ========== Select Implementation ==========
let getMenuItems;
let getMenuItemById;
let getCategories;
let updateMenuItem;

if (window.NativeApi) {
    getMenuItems = getMenuItems_a;
    getMenuItemById = getMenuItemById_a;
    getCategories = getCategories_a;
    updateMenuItem = updateMenuItem_a;
} else {
    getMenuItems = getMenuItems_w;
    getMenuItemById = getMenuItemById_w;
    getCategories = getCategories_w;
    updateMenuItem = updateMenuItem_w;
}

export { getMenuItems, getMenuItemById, getCategories, updateMenuItem };