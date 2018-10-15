package com.xt.a_ble;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by xuti on 2018/5/17.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class ALeScanCallback implements BluetoothAdapter.LeScanCallback {
    public abstract void onStartLeScanCallback(boolean isStartScanSuccess,String desc);
    public abstract void onStopLeScanCallback();
}
