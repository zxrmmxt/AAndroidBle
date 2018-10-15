package com.xt.a_ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.xt.m_common_utils.MConvertUtils;

/**
 * Created by xuti on 2018/8/13.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OneDeviceConnectControl {
    private ABleConnectControl mABleConnectControl = new ABleConnectControl();

    /**********************************connect*********************************************/

    public boolean isConnecting() {
        return mABleConnectControl != null && !mABleConnectControl.isConnectFinished();
    }

    public boolean isConnected() {
        return mABleConnectControl != null && mABleConnectControl.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connect(BluetoothDevice device, Context context) {
        mABleConnectControl.connect(context, false, device);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void addBleGattCallback(ABluetoothGattCallback aBluetoothGattCallback) {
        mABleConnectControl.addBleGattCallback(aBluetoothGattCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeBleGattCallback(ABluetoothGattCallback aBluetoothGattCallback) {
        mABleConnectControl.removeBleGattCallback(aBluetoothGattCallback);
    }
    /**********************************connect*********************************************/
    /**********************************writeAsync*********************************************/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void writeAsync(byte[] data) {
        mABleConnectControl.writeCharacteristicAsync(data);
    }

    public void writeAsync(String hexData) {
        mABleConnectControl.writeCharacteristicAsync(MConvertUtils.hexString2Bytes(hexData));
    }

    public boolean writeSync(String hexData) {
        return mABleConnectControl.writeCharacteristicSync(MConvertUtils.hexString2Bytes(hexData));
    }

    public boolean read() {
        return mABleConnectControl.readCharacteristic();
    }

    public ABleConnectControl getABleConnectControl() {
        return mABleConnectControl;
    }

    /**********************************writeAsync*********************************************/
}
