package com.xt.a_ble;

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
    private Map<String, ABleConnectControl> mBleConnectMap = new HashMap<>();

    /**********************************connect*********************************************/

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean hasOneConnected() {
        Set<Map.Entry<String, ABleConnectControl>> entries = mBleConnectMap.entrySet();
        for (Map.Entry<String, ABleConnectControl> entry : entries) {
            ABleConnectControl value = entry.getValue();
            if (value.isConnected()) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public BluetoothDevice getOneConnectedDevice() {
        Set<Map.Entry<String, ABleConnectControl>> entries = mBleConnectMap.entrySet();
        for (Map.Entry<String, ABleConnectControl> entry : entries) {
            ABleConnectControl value = entry.getValue();
            if (value.isConnected()) {
                return value.getBluetoothDevice();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        ABleConnectControl aBleConnectControl = mBleConnectMap.get(bluetoothDevice.getAddress());
        return aBleConnectControl.isConnected();
    }

    public Map<String, ABleConnectControl> getBleConnectMap() {
        return mBleConnectMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connect(BluetoothDevice device, Context context) {
//        ConnectControl.connect(bleDevice);
        ABleConnectControl myBleConnectUtils = mBleConnectMap.get(device.getAddress());
        if (myBleConnectUtils == null) {
            myBleConnectUtils = new ABleConnectControl(device);
            mBleConnectMap.put(device.getAddress(), myBleConnectUtils);
        }
        myBleConnectUtils.connect(context, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void addBleGattCallback(BluetoothDevice device, ABluetoothGattCallback aBluetoothGattCallback) {
        ABleConnectControl myBleConnectUtils = mBleConnectMap.get(device.getAddress());
        if (myBleConnectUtils == null) {
            myBleConnectUtils = new ABleConnectControl(device);
            mBleConnectMap.put(device.getAddress(), myBleConnectUtils);
        }
        myBleConnectUtils.addBleGattCallback(aBluetoothGattCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeBleGattCallback(BluetoothDevice device, ABluetoothGattCallback aBluetoothGattCallback) {
        ABleConnectControl myBleConnectUtils = mBleConnectMap.get(device.getAddress());
        if (myBleConnectUtils != null) {
            myBleConnectUtils.removeBleGattCallback(aBluetoothGattCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeAllBleGattCallback(ABluetoothGattCallback aBluetoothGattCallback) {
        Set<Map.Entry<String, ABleConnectControl>> entries = mBleConnectMap.entrySet();
        for (Map.Entry<String, ABleConnectControl> item : entries) {
            ABleConnectControl aBleConnectControl = item.getValue();
            aBleConnectControl.removeBleGattCallback(aBluetoothGattCallback);
        }
    }
    /**********************************connect*********************************************/

    /**********************************writeAsync*********************************************/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void write(BluetoothDevice bleDevice, byte[] data) {
//        WriteControl.writeAsync(bleDevice, data);
        ABleConnectControl myBleConnectUtils = mBleConnectMap.get(bleDevice.getAddress());
        if (myBleConnectUtils != null) {
            myBleConnectUtils.writeCharacteristicAsync(data);
        } else {
        }
    }
    /**********************************writeAsync*********************************************/
}
