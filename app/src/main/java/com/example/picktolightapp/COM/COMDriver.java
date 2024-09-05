package com.example.picktolightapp.COM;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.util.HashMap;

public class COMDriver {

    private static final String TAG = "COMDriver";

    private D2xxManager ftD2xx;
    private FT_Device ftDev;

    // Costruttore della classe
    public COMDriver(Context context) {
        try {
            ftD2xx = D2xxManager.getInstance(context);
        } catch (D2xxManager.D2xxException e) {
            Log.e(TAG, "D2xxManager initialization failed", e);
        }
    }

    // Metodo per aprire il dispositivo
    public boolean openDevice(Context context) {
        int devCount = ftD2xx.createDeviceInfoList(context);
        Log.i(TAG, "Number of devices: " + devCount);

        if (devCount > 0) {
            ftDev = ftD2xx.openByIndex(context, 0);
            if (ftDev != null && ftDev.isOpen()) {
                configureDevice();
                Log.i(TAG, "Device opened successfully");
                return true;
            }
        }
        Log.e(TAG, "Failed to open device");
        return false;
    }

    // Metodo per configurare il dispositivo
    private void configureDevice() {
        if (ftDev != null && ftDev.isOpen()) {
            ftDev.setBaudRate(9600);  // Imposta il baud rate
            ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
                    D2xxManager.FT_STOP_BITS_1,
                    D2xxManager.FT_PARITY_NONE);
            ftDev.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0, (byte) 0);
        }
    }

    // Metodo per inviare stringhe via USB
    public boolean sendString(String data) {
        if (ftDev != null && ftDev.isOpen()) {
            byte[] writeData = data.getBytes();
            int written = ftDev.write(writeData, writeData.length);
            if (written == writeData.length) {
                Log.i(TAG, "Data sent successfully");
                return true;
            } else {
                Log.e(TAG, "Failed to send data");
            }
        }
        return false;
    }

    // Metodo per ricevere stringhe dal dispositivo USB
    public String readString() {
        if (ftDev != null && ftDev.isOpen()) {
            byte[] readData = new byte[64];
            int readCount = ftDev.read(readData, readData.length);
            if (readCount > 0) {
                String receivedData = new String(readData, 0, readCount);
                Log.i(TAG, "Data received: " + receivedData);
                return receivedData;
            }
        }
        Log.e(TAG, "Failed to read data");
        return null;
    }

    // Metodo per chiudere il dispositivo
    public void closeDevice() {
        if (ftDev != null) {
            ftDev.close();
            Log.i(TAG, "Device closed");
        }
    }

    // Metodo per ottenere il permesso USB
    public void requestUsbPermission(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.example.USB_PERMISSION"), 0);
            usbManager.requestPermission(device, permissionIntent);
        }
    }
}
