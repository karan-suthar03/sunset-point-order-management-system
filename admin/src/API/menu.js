import apiClient from ".";

export async function getMenuItems() {
    try {
        const response = await apiClient.get('/dishes');
        return response.data;
    } catch (error) {
        console.error("Error fetching menu items:", error);
        throw error;
    }
}

export async function getMenuItemById(id) {
    try {
        const response = await apiClient.get(`/dishes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching menu item with id ${id}:`, error);
        throw error;
    }
}

export async function getCategories() {
    try {
        const response = await apiClient.get('/dishes/categories');
        return response.data;
    } catch (error) {
        console.error("Error fetching categories:", error);
        throw error;
    }
}