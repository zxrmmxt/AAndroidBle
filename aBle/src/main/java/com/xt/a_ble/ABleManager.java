package com.xt.a_ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuti on 2018/4/28.
 *
 * @// TODO: 2018/5/2 当前FastBle版本v2.3.0（2018-04-29）
 */

public class ABleManager {
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanControl mScanControl;
    private final BluetoothManager mBluetoothManager;
    private List<BleStateChangeListener> mBleStateChangeListeners = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public ABleManager(Context context) {
        mContext = context;
        BluetoothAdapterStateChangeReceiver bluetoothAdapterStateChangeReceiver = new BluetoothAdapterStateChangeReceiver() {
            @Override
            protected void onStateChange(int state, int oldState) {
                for (BleStateChangeListener bleStateChangeListener : mBleStateChangeListeners) {
                    bleStateChangeListener.onStateChange(state, oldState);
                }
            }
        };
        mContext.registerReceiver(bluetoothAdapterStateChangeReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        mBluetoothManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        mScanControl = new ScanControl(mBluetoothAdapter);
    }

    private boolean isSupportBle() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBlueEnable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public boolean enableBluetooth() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.enable();
        } else {
            return false;
        }
    }

    public boolean disableBluetooth() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.disable();
        } else {
            return false;
        }
    }

    public BluetoothManager getBluetoothManager() {
        return mBluetoothManager;
    }

    /*****************************BLE状态回调***********************************/
    public void addBleStateChangeListener(BleStateChangeListener bleStateChangeListener) {
        mBleStateChangeListeners.add(bleStateChangeListener);
    }

    public void removeBleStateChangeListener(BleStateChangeListener bleStateChangeListener) {
        mBleStateChangeListeners.remove(bleStateChangeListener);
    }
    /*****************************BLE状态回调***********************************/

    /*************************scan**********************************/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean startBleScan() {
        if (isSupportBle()) {
            if (isBlueEnable()) {
                return mScanControl.startBleScan();
            } else {
                mScanControl.onStartLeScanCallback(false, "蓝牙未打开");
                return false;
            }
        } else {
            mScanControl.onStartLeScanCallback(false, "不支持BLE");
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void cancelLeScan() {
        mScanControl.cancelLeScan();
    }

    public boolean isScanning() {
        return mScanControl.isScanning();
    }

    public void addMyLeScanCallback(ALeScanCallback aLeScanCallback) {
        mScanControl.addMyLeScanCallback(aLeScanCallback);
    }

    public void removeMyLeScanCallback(ALeScanCallback aLeScanCallback) {
        mScanControl.removeMyLeScanCallback(aLeScanCallback);
    }

    /*************************scan**********************************/
    private OneDeviceConnectControl mOneDeviceConnectControl;

    public OneDeviceConnectControl getOneDeviceConnectControl() {
        return mOneDeviceConnectControl;
    }

    public void setOneDeviceConnectControl(OneDeviceConnectControl oneDeviceConnectControl) {
        mOneDeviceConnectControl = oneDeviceConnectControl;
    }


    private MultiDeviceConnectControl mMultiDeviceConnectControl;

    public MultiDeviceConnectControl getMultiDeviceConnectControl() {
        return mMultiDeviceConnectControl;
    }

    public void setMultiDeviceConnectControl(MultiDeviceConnectControl multiDeviceConnectControl) {
        mMultiDeviceConnectControl = multiDeviceConnectControl;
    }
}
