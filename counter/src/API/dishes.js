import apiClient from ".";

function uuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

let getDishes;


async function getDishes_a() {
  let result = await (new Promise((resolve) => {
    const id = crypto?.randomUUID ? crypto.randomUUID() : uuid();

    window.__nativePromises[id] = resolve;

    window.NativeApi.getDishes(
      id
    );
  }));
  return result;
}



async function getDishes_w() {
    const response = await apiClient.get('/dishes');
    return response.data;
}

if (window.NativeApi) {
    getDishes = getDishes_a;
} else {
    getDishes = getDishes_w;
}

export { getDishes };