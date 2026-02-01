package com.karan.sunset_point;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;


public class PrinterManager {

    private static BluetoothConnection connection;
    private static EscPosPrinter printer;
    private static String connectedPrinterName;

    private static Boolean inititalizing = false;

    public static String getConnectedPrinterName() {
        return connectedPrinterName;
    }

    public static boolean isConnected() {
        if (printer == null || connection == null) {
            return false;
        }
        
        // Check if the Bluetooth device is still connected
        try {
            if (ActivityCompat.checkSelfPermission(App.context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter == null || !adapter.isEnabled()) {
                    return false;
                }
                // Check if device is still paired
                if (connection.getDevice() == null) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }

    public static void resetConnection() {
        try {
            if (connection != null) {
                connection.disconnect();
            }
        } catch (Exception ignored) {}
        
        printer = null;
        connection = null;
        connectedPrinterName = null;
    }

    public static synchronized void connect(OnPrinterConnected callback) throws Exception {

        // Check if Bluetooth is enabled
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            throw new Exception("Bluetooth is not supported on this device");
        }
        
        if (!bluetoothAdapter.isEnabled()) {
            throw new Exception("Bluetooth is disabled. Please enable Bluetooth to use printing functionality");
        }

        if (printer != null && connection != null) {
            if (callback != null) {
                if (ActivityCompat.checkSelfPermission(App.context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                String name = connection.getDevice().getName();
                callback.onConnected(name, connection, printer);
            }
            return;
        }

        inititalizing = true;

        if (ActivityCompat.checkSelfPermission(
                App.context,
                Manifest.permission.BLUETOOTH_SCAN
        ) != PackageManager.PERMISSION_GRANTED) {

            inititalizing = false;
            throw new Exception("Bluetooth permission not granted");
        }

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        connection = BluetoothPrintersConnections.selectFirstPaired();

        if (connection == null) {
            inititalizing = false;
            throw new Exception("No printer found");
        }

        printer = new EscPosPrinter(connection, 203, 48f, 32);

        inititalizing = false;

        if (callback != null) {
            String name = connection.getDevice().getName();
            connectedPrinterName = name;
            MainActivity.notifyPrinterState(true, name);
            callback.onConnected(name, connection, printer);
        }
    }

    public static void print(String text) {

        new Thread(() -> {

            try {

                if (inititalizing) return;

                try {

                    connect((name, conn, pr) -> {
                        try {
                            pr.printFormattedText(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    });

                } catch (Exception firstFail) {

                    try {
                        if (connection != null) connection.disconnect();
                    } catch (Exception ignored) {}

                    printer = null;
                    connection = null;
                    connectedPrinterName = null;
                    MainActivity.notifyPrinterState(false, null);

                    // Only retry if it's not a disconnection error
                    if (!(firstFail.getCause() instanceof java.io.IOException)) {
                        connect((name, conn, pr) -> {
                            try {
                                pr.printFormattedText(text);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        Log.e("PrinterManager", "Skipping retry - printer disconnected");
                    }
                }

            } catch (Exception e) {
                MainActivity.notifyPrinterState(false, null);
                e.printStackTrace();
            }

        }).start();
    }
}
