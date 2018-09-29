package com.xt.m_ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 蓝牙适配器监听
 * Created by admin on 2017/10/27.
 */

public abstract class BluetoothAdapterStateChangeReceiver extends BroadcastReceiver {

    public BluetoothAdapterStateChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            //蓝牙适配器状态改变
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            int oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);
            onStateChange(state, oldState);
        }
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){

        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){

        }
    }

    protected abstract void onStateChange(int state, int oldState);
}
