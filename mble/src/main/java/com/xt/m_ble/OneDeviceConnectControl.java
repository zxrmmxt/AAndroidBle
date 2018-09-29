package com.xt.m_ble;

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
    private MyBleConnectControl mMyBleConnectControl = new MyBleConnectControl();

    /**********************************connect*********************************************/

    public boolean isConnecting() {
        return mMyBleConnectControl != null && !mMyBleConnectControl.isConnectFinished();
    }

    public boolean isConnected() {
        return mMyBleConnectControl != null && mMyBleConnectControl.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connect(BluetoothDevice device, Context context) {
        mMyBleConnectControl.connect(context, false, device);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void addBleGattCallback(MyBluetoothGattCallback myBluetoothGattCallback) {
        mMyBleConnectControl.addBleGattCallback(myBluetoothGattCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void removeBleGattCallback(MyBluetoothGattCallback myBluetoothGattCallback) {
        mMyBleConnectControl.removeBleGattCallback(myBluetoothGattCallback);
    }
    /**********************************connect*********************************************/
    /**********************************writeAsync*********************************************/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void writeAsync(byte[] data) {
        mMyBleConnectControl.writeCharacteristicAsync(data);
    }

    public void writeAsync(String hexData) {
        mMyBleConnectControl.writeCharacteristicAsync(MConvertUtils.hexString2Bytes(hexData));
    }

    public boolean writeSync(String hexData) {
        return mMyBleConnectControl.writeCharacteristicSync(MConvertUtils.hexString2Bytes(hexData));
    }

    public boolean read() {
        return mMyBleConnectControl.readCharacteristic();
    }

    public MyBleConnectControl getMyBleConnectControl() {
        return mMyBleConnectControl;
    }

    /**********************************writeAsync*********************************************/
}
