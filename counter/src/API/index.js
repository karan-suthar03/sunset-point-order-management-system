import axios from 'axios';


window.__nativePromises = {};
window.__nativeResolve = function (id, response) {
  if (window.__nativePromises[id]) {
    window.__nativePromises[id](response? JSON.parse(response) : null);
    delete window.__nativePromises[id];
  }
};

// Event listeners for state changes
const bluetoothStateListeners = new Set();
const printerStateListeners = new Set();

// Current states - Initialize as unknown/disconnected until we get first event
let currentBluetoothState = {
  enabled: null, // null means we don't know yet, will be set on first callback
  lastChecked: Date.now()
};

let currentPrinterState = {
  connected: false,
  printerName: null,
  lastChecked: Date.now()
};

// Bluetooth state change callback
window.__onBluetoothStateChanged = function(enabled) {
  console.log('Bluetooth state changed:', enabled);
  currentBluetoothState = {
    enabled,
    lastChecked: Date.now()
  };
  
  bluetoothStateListeners.forEach(listener => {
    try {
      listener(currentBluetoothState);
    } catch (error) {
      console.error('Error in Bluetooth state listener:', error);
    }
  });
};

// Printer state change callback
window.__onPrinterStateChanged = function(connected, printerName) {
  console.log('Printer state changed:', connected, printerName);
  currentPrinterState = {
    connected,
    printerName,
    lastChecked: Date.now()
  };
  
  printerStateListeners.forEach(listener => {
    try {
      listener(currentPrinterState);
    } catch (error) {
      console.error('Error in printer state listener:', error);
    }
  });
};

// Subscribe to Bluetooth state changes
export function onBluetoothStateChange(callback) {
  bluetoothStateListeners.add(callback);
  // Immediately call with current state
  callback(currentBluetoothState);
  
  // Return unsubscribe function
  return () => bluetoothStateListeners.delete(callback);
}

// Subscribe to printer state changes
export function onPrinterStateChange(callback) {
  printerStateListeners.add(callback);
  // Immediately call with current state
  callback(currentPrinterState);
  
  // Return unsubscribe function
  return () => printerStateListeners.delete(callback);
}

// Get current states
export function getBluetoothState() {
  return currentBluetoothState;
}

export function getPrinterState() {
  return currentPrinterState;
}

// Back button handler
let backPressHandler = null;

export function setBackPressHandler(handler) {
  backPressHandler = handler;
}

// This function is called by Android when back button is pressed
window.__handleBackPress = function() {
  if (backPressHandler && typeof backPressHandler === 'function') {
    return backPressHandler();
  }
  return false;
};


const apiClient = axios.create({
  baseURL: 'http://localhost:3000',
  timeout: 10000,
    headers: {
    'Content-Type': 'application/json',
    },  
});

export default apiClient;