package com.xt.a_ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * Created by xuti on 2018/5/9.
 */

public class BleConnectionStateUtils {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int getConnectState(ABleManager aBleManager, BluetoothDevice bluetoothDevice) {
        BluetoothManager bluetoothManager = aBleManager.getBluetoothManager();
        if (bluetoothManager != null && bluetoothDevice != null) {
            return bluetoothManager.getConnectionState(bluetoothDevice, BluetoothProfile.GATT);
        }
        return BluetoothProfile.STATE_DISCONNECTED;
    }

    public static boolean isConnecting(int newState) {//连接中的
        return newState == BluetoothProfile.STATE_CONNECTING;
    }

    public static boolean isConnected(int newState) {
        return newState == BluetoothProfile.STATE_CONNECTED;//已连接的
    }

    public static boolean isDisconnecting(int newState) {
        return newState == BluetoothProfile.STATE_DISCONNECTING;//断开连接中的
    }

    public static boolean isDisconnected(int newState) {
        return newState == BluetoothProfile.STATE_DISCONNECTED;//已断开连接的
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getConnectStateStr(ABleManager aBleManager, BluetoothDevice bluetoothDevice) {
        int connectState = getConnectState(aBleManager, bluetoothDevice);
        return getConnectStateStr(connectState);
    }

    @NonNull
    public static String getConnectStateStr(int connectState) {
        if (isConnecting(connectState)) {
            return "连接中";
        } else if (isConnected(connectState)) {
            return "已连接";
        } else if (isDisconnecting(connectState)) {
            return "断开中";
        } else if (isDisconnected(connectState)) {
            return "已断开";
        } else {
            return "未知状态";
        }
    }
}
