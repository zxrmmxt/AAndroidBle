package com.xt.m_ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuti on 2018/8/13.
 */
public class MultiDeviceConnectControl {
    private Map<String, MyBleConnectControl> mBleConnectMap = new HashMap<>();

    /**********************************connect*********************************************/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean hasOneConnected() {
        Set<Map.Entry<String, MyBleConnectControl>> entries = mBleConnectMap.entrySet();
        for (Map.Entry<String, MyBleConnectControl> entry : entries) {
            MyBleConnectControl value = entry.getValue();
            if (value.isConnected()) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothDevice getOneConnectedDevice() {
        Set<Map.Entry<String, MyBleConnectControl>> entries = mBleConnectMap.entrySet();
        for (Map.Entry<String, MyBleConnectControl> entry : entries) {
            MyBleConnectControl value = entry.getValue();
            if (value.isConnected()) {
                return value.getBluetoothDevice();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        MyBleConnectControl myBleConnectControl = mBleConnectMap.get(bluetoothDevice.getAddress());
        return myBleConnectControl.isConnected();
    }

    public Map<String, MyBleConnectControl> getBleConnectMap() {
        return mBleConnectMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connect(BluetoothDevice device, Context context) {
//        ConnectControl.connect(bleDevice);
        MyBleConnectControl myBleConnectUtils = mBleConnectMap.get(device.getAddress());
        if (myBleConnectUtils == null) {
            myBleConnectUtils = new MyBleConnectControl(device);
            mBleConnectMap.put(device.getAddress(), myBleConnectUtils);
        }
        myBleConnectUtils.connect(context, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void addBleGattCallback(BluetoothDevice device, MyBluetoothGattCallback myBluetoothGattCallback) {
        MyBleConnectControl myBleConnectUtils = mBleConnectMap.get(device.getAddress());
        if (myBleConnectUtils == null) {
            myBleConnectUtils = new MyBleConnectControl(device);
            mBleConnectMap.put(device.getAddress(), myBleConnectUtils);
        }
        myBleConnectUtils.addBleGattCallback(myBluetoothGattCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeBleGattCallback(BluetoothDevice device, MyBluetoothGattCallback myBluetoothGattCallback) {
        MyBleConnectControl myBleConnectUtils = mBleConnectMap.get(device.getAddress());
        if (myBleConnectUtils != null) {
            myBleConnectUtils.removeBleGattCallback(myBluetoothGattCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeAllBleGattCallback(MyBluetoothGattCallback myBluetoothGattCallback) {
        Set<Map.Entry<String, MyBleConnectControl>> entries = mBleConnectMap.entrySet();
        for (Map.Entry<String, MyBleConnectControl> item : entries) {
            MyBleConnectControl myBleConnectControl = item.getValue();
            myBleConnectControl.removeBleGattCallback(myBluetoothGattCallback);
        }
    }
    /**********************************connect*********************************************/

    /**********************************writeAsync*********************************************/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void write(BluetoothDevice bleDevice, byte[] data) {
//        WriteControl.writeAsync(bleDevice, data);
        MyBleConnectControl myBleConnectUtils = mBleConnectMap.get(bleDevice.getAddress());
        if (myBleConnectUtils != null) {
            myBleConnectUtils.writeCharacteristicAsync(data);
        } else {
        }
    }
    /**********************************writeAsync*********************************************/
}
