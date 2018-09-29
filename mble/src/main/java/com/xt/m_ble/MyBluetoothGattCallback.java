package com.xt.m_ble;

import android.bluetooth.BluetoothGattCallback;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by xuti on 2018/5/16.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class MyBluetoothGattCallback extends BluetoothGattCallback {
    public void onConnectionStateChanged() {
    }
    public void onWriteCharacteristicFailed(String result) {
    }
}
