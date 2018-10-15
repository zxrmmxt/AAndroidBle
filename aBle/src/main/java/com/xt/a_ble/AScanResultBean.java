package com.xt.a_ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by xuti on 2018/5/17.
 */

public class AScanResultBean {
    private BluetoothDevice mBluetoothDevice;
    private int rssi;
    private byte[] scanRecord;
    private String state;

    public AScanResultBean() {
    }

    public AScanResultBean(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        mBluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
