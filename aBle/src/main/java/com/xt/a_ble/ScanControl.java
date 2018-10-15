package com.xt.a_ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.xt.m_common_utils.MLogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xuti on 2018/5/8.
 */

class ScanControl {
    /*private enum ScanState {
        IDLE("未扫描"),
        SCANNING("扫描中"),
        ;
        private String desc;

        ScanState(String desc) {
            this.desc = desc;
        }
    }*/
    private BluetoothAdapter mBluetoothAdapter;
    private ABleScanRuleConfig mScanRuleConfig;
    private List<ALeScanCallback> mALeScanCallbacks = new ArrayList<>();
    private List<BluetoothDevice> mBleDeviceList = new ArrayList<>();
    private Handler mHandler;
    private Runnable mScanTimeoutTask = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void run() {
            LeScanFinish();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void LeScanFinish() {
        stopLeScan("扫描结束");
    }

    private boolean isScanning = false;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    ScanControl(BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
        mScanRuleConfig = new ABleScanRuleConfig.Builder()
//                    .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                    .setDeviceName(true, names)         // 只扫描指定广播名的设备，可选
//                    .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
//                    .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
//                .setScanTimeOut(20000)              // 扫描超时时间，可选，默认10秒
                .setScanTimeOut(-1)              // 扫描超时时间，可选，小于等于0，会实现无限扫描
                .build();
        HandlerThread handlerThread = new HandlerThread("scanThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    boolean isScanning() {
        return isScanning;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    boolean startBleScan() {
        if (isScanning) {
            onStartLeScanCallback(true, "扫描中");
            return true;
        } else {
            if (mBluetoothAdapter.startLeScan(mScanRuleConfig.getServiceUuids(), mLeScanCallback)) {
                //开始扫描
                long scanTimeOut = mScanRuleConfig.getScanTimeOut();
                if (scanTimeOut != -1) {
                    mHandler.postDelayed(mScanTimeoutTask, scanTimeOut);
                }
                isScanning = true;
                onStartLeScanCallback(true, "开始扫描");
                return true;
            } else {
                onStartLeScanCallback(false, "扫描失败");
                return false;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    void cancelLeScan() {
        mHandler.removeCallbacks(mScanTimeoutTask);
        stopLeScan("取消扫描");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    void onStartLeScanCallback(boolean isStartScanSuccess, String desc) {
        for (ALeScanCallback aLeScanCallback : mALeScanCallbacks) {
            aLeScanCallback.onStartLeScanCallback(isStartScanSuccess, desc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private synchronized void stopLeScan(String text) {
        if (mBluetoothAdapter != null) {
            isScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            for (ALeScanCallback aLeScanCallback : mALeScanCallbacks) {
                aLeScanCallback.onStopLeScanCallback();
            }
//            ToastUtils.showShort(text);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            checkDevice(device);
            for (ALeScanCallback item : mALeScanCallbacks) {
                item.onLeScan(device, rssi, scanRecord);
            }
        }
    };

    private void checkDevice(BluetoothDevice bleDevice) {
        String deviceMac = mScanRuleConfig.getDeviceMac();
        String[] deviceNames = mScanRuleConfig.getDeviceNames();
        if (TextUtils.isEmpty(deviceMac) && (deviceNames == null || deviceNames.length < 1)) {
            correctDeviceAndNextStep(bleDevice);
            return;
        }

        if (!TextUtils.isEmpty(deviceMac)) {
            if (!deviceMac.equalsIgnoreCase(bleDevice.getAddress()))
                return;
        }

        if (deviceNames != null && deviceNames.length > 0) {
            AtomicBoolean equal = new AtomicBoolean(false);
            for (String name : deviceNames) {
                String remoteName = bleDevice.getName();
                if (remoteName == null)
                    remoteName = "";
                if (mScanRuleConfig.isFuzzy() ? remoteName.contains(name) : remoteName.equals(name)) {
                    equal.set(true);
                }
            }
            if (!equal.get()) {
                return;
            }
        }

        correctDeviceAndNextStep(bleDevice);
    }


    private void correctDeviceAndNextStep(final BluetoothDevice bleDevice) {
        //hasFound,是否已经发现了（mBleDeviceList是否包含）
        AtomicBoolean hasFound = new AtomicBoolean(false);
        for (BluetoothDevice result : mBleDeviceList) {
            if (result.equals(bleDevice)) {
                hasFound.set(true);
            }
        }
        if (!hasFound.get()) {
            MLogUtil.i("device detected  ------"
                    + "  name: " + bleDevice.getName()
                    + "  mac: " + bleDevice.getAddress());

            mBleDeviceList.add(bleDevice);
        }
    }


    void addMyLeScanCallback(ALeScanCallback aLeScanCallback) {
        mALeScanCallbacks.add(aLeScanCallback);
    }

    void removeMyLeScanCallback(ALeScanCallback aLeScanCallback) {
        mALeScanCallbacks.remove(aLeScanCallback);
    }
}
