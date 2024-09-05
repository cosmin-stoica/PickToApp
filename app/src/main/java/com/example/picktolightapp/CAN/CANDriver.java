package com.example.picktolightapp.CAN;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.app.PendingIntent;
import android.util.Log;
import android.widget.EditText;
import com.viewtool.Ginkgo.ControlCAN;
import com.viewtool.Ginkgo.ErrorType;
import com.viewtool.Ginkgo.GinkgoDriver;

import java.util.Arrays;

public class CANDriver {
    private static final String ACTION_USB_PERMISSION = "viewtool.usb_can_test.USB_PERMISSION";

    private UsbManager usbManager;
    private GinkgoDriver ginkgoDriver;
    private UsbDevice usbDevice;
    private PendingIntent pendingIntent;
    private String TAG = "CANDRIVER!!!";

    public CANDriver(Context context) {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        this.ginkgoDriver = new GinkgoDriver(usbManager, pendingIntent);
    }

    public boolean openConnection() {
        usbDevice = ginkgoDriver.ControlCAN.VCI_ScanDevice();
        if (usbDevice == null) {
            Log.w(TAG,"No device connected\n");
            return false;
        }

        int ret = ginkgoDriver.ControlCAN.VCI_OpenDevice();
        if (ret != ErrorType.ERR_SUCCESS) {
            Log.w(TAG,"Open device error!\n");
            Log.w(TAG,String.format("Error code: %d\n", ret));
            return false;
        } else {
            Log.w(TAG,"Open device success!\n");
            return true;
        }
    }

    public boolean initCAN() {
        ControlCAN.VCI_INIT_CONFIG_EX CAN_InitEx = ginkgoDriver.ControlCAN.new VCI_INIT_CONFIG_EX();

        CAN_InitEx.CAN_ABOM = 0; // Automatic bus-off management
        CAN_InitEx.CAN_Mode = 0; // Loop back
        CAN_InitEx.CAN_BRP = 3; // Baud Rate Prescaler: (3 + 1) = 4
        CAN_InitEx.CAN_BS1 = 7; // Time Segment 1: (7 + 1) = 8
        CAN_InitEx.CAN_BS2 = 2; // Time Segment 2: (2 + 1) = 3
        CAN_InitEx.CAN_SJW = 1; // Synchronization Jump Width: (1 + 1) = 2
        CAN_InitEx.CAN_NART = 1; // No automatic retransmission
        CAN_InitEx.CAN_RFLM = 0; // Receive FIFO locked mode
        CAN_InitEx.CAN_TXFP = 0; // Transmit FIFO priority
        CAN_InitEx.CAN_RELAY = 0;

        int ret = ginkgoDriver.ControlCAN.VCI_InitCANEx((byte) 0, CAN_InitEx);
        if (ret != ErrorType.ERR_SUCCESS) {
            Log.w(TAG,"Init device failed!\n");
            Log.w(TAG,String.format("Error code: %d\n", ret));
            return false;
        } else {
            Log.w(TAG,"Init device success!\n");
            return true;
        }
    }


    public boolean setFilter() {
        ControlCAN.VCI_FILTER_CONFIG CAN_FilterConfig = ginkgoDriver.ControlCAN.new VCI_FILTER_CONFIG();
        CAN_FilterConfig.FilterIndex = 0;
        CAN_FilterConfig.Enable = 1; // Enable
        CAN_FilterConfig.ExtFrame = 0;
        CAN_FilterConfig.FilterMode = 0;
        CAN_FilterConfig.ID_IDE = 0;
        CAN_FilterConfig.ID_RTR = 0;
        CAN_FilterConfig.ID_Std_Ext = 0;
        CAN_FilterConfig.MASK_IDE = 0;
        CAN_FilterConfig.MASK_RTR = 0;
        CAN_FilterConfig.MASK_Std_Ext = 0;

        int ret = ginkgoDriver.ControlCAN.VCI_SetFilter((byte) 0, CAN_FilterConfig);
        if (ret != ErrorType.ERR_SUCCESS) {
            Log.w(TAG,"Set filter failed!\n");
            Log.w(TAG,String.format("Error code: %d\n", ret));
            return false;
        } else {
            Log.w(TAG,"Set filter success!\n");
            return true;
        }
    }

    public boolean startCAN() {
        int ret = ginkgoDriver.ControlCAN.VCI_StartCAN((byte) 0);
        if (ret != ErrorType.ERR_SUCCESS) {
            Log.w(TAG,"Start CAN failed!\n");
            Log.w(TAG,String.format("Error code: %d\n", ret));
            return false;
        } else {
            Log.w(TAG,("Start CAN success!\n"));
            return true;
        }
    }

    public boolean sendData() {
        ControlCAN.VCI_CAN_OBJ[] CAN_SendData = new ControlCAN.VCI_CAN_OBJ[1];
        for (int i = 0; i < CAN_SendData.length; i++) {
            CAN_SendData[i] = ginkgoDriver.ControlCAN.new VCI_CAN_OBJ();
            CAN_SendData[i].DataLen = 8;
            CAN_SendData[i].Data = new byte[8];
            for (int j = 0; j < CAN_SendData[i].DataLen; j++) {
                CAN_SendData[i].Data[j] = (byte) (i + j);
            }
            CAN_SendData[i].ExternFlag = 0;
            CAN_SendData[i].RemoteFlag = 0;
            CAN_SendData[i].ID = 0x17332010;
            CAN_SendData[i].SendType = 0;

            Log.d(TAG, String.format("Preparing CAN message ID: 0x%X", CAN_SendData[i].ID));
            Log.d(TAG, "Data: " + Arrays.toString(CAN_SendData[i].Data));
        }

        int ret = ginkgoDriver.ControlCAN.VCI_Transmit((byte) 0, CAN_SendData, CAN_SendData.length);
        if (ret != ErrorType.ERR_SUCCESS) {
            Log.w(TAG, "Send CAN data failed!");
            Log.w(TAG, String.format("Error code: %d", ret));
            return false;
        } else {
            Log.w(TAG, "Send CAN data success!");
            return true;
        }
    }


    public void receiveData() {
        ControlCAN.VCI_CAN_OBJ[] CAN_ReceiveData = new ControlCAN.VCI_CAN_OBJ[2];
        for (int i = 0; i < CAN_ReceiveData.length; i++) {
            CAN_ReceiveData[i] = ginkgoDriver.ControlCAN.new VCI_CAN_OBJ();
            CAN_ReceiveData[i].Data = new byte[8];
        }

        int dataNum = ginkgoDriver.ControlCAN.VCI_GetReceiveNum((byte) 0);
        if (dataNum > 0) {
            int readDataNum = ginkgoDriver.ControlCAN.VCI_Receive((byte) 0, CAN_ReceiveData, CAN_ReceiveData.length);
            for (int i = 0; i < readDataNum; i++) {
                Log.w(TAG,"--CAN_ReceiveData.RemoteFlag = " + CAN_ReceiveData[i].RemoteFlag + "\n");
                Log.w(TAG,"--CAN_ReceiveData.ExternFlag = " + CAN_ReceiveData[i].ExternFlag + "\n");
                Log.w(TAG,"--CAN_ReceiveData.ID = 0x" + String.format("%x", CAN_ReceiveData[i].ID) + "\n");
                Log.w(TAG,"--CAN_ReceiveData.DataLen = " + CAN_ReceiveData[i].DataLen + "\n");
                Log.w(TAG,"--CAN_ReceiveData.Data: ");
                for (int j = 0; j < CAN_ReceiveData[i].DataLen; j++) {
                    Log.w(TAG,String.format("%x", CAN_ReceiveData[i].Data[j]) + " ");
                }
                Log.w(TAG,"\n--CAN_ReceiveData.TimeStamp = " + CAN_ReceiveData[i].TimeStamp + "\n");
            }
        }
    }

    public void closeConnection() {
        ginkgoDriver.ControlCAN.VCI_ResetCAN((byte) 0);
        // ginkgoDriver.ControlCAN.VCI_CloseDevice(); // Uncomment if you want to close the device
        Log.w(TAG,"CAN connection closed.\n");
    }
}
