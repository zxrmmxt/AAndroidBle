package com.xt.m_ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.xt.m_common_utils.MConvertUtils;
import com.xt.m_common_utils.MLogUtil;
import com.xt.m_common_utils.MToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

/**
 * Created by xuti on 2018/5/9.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyBleConnectControl {
    private static final String TAG = MyBleConnectControl.class.getSimpleName();
    private String SERVICE_UUID = "0000180b-0000-1000-8000-00805f9b34fb";
    private String NOTIFY_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private String WRITE_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private boolean isConnectFinished = true;//本次连接过程是否结束
    private boolean isConnected = false;//是否已连接

    public MyBleConnectControl() {
    }

    MyBleConnectControl(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }

    private Handler mConnectHandler;
    private Handler mWriteHandler;

    {
        HandlerThread bleConnectThread = new HandlerThread("bleConnectThread");
        bleConnectThread.start();
        mConnectHandler = new Handler(bleConnectThread.getLooper());


        HandlerThread bleWriteThread = new HandlerThread("bleWriteThread");
        bleWriteThread.start();
        mWriteHandler = new Handler(bleWriteThread.getLooper());
    }

    void connect(final Context context, final boolean autoConnect, final BluetoothDevice bluetoothDevice) {
        mConnectHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    MToastUtils.showShort(context, "已连接");
                    return;
                }
                if (!isConnectFinished()) {
                    MToastUtils.showShort(context, "连接中");
                    return;
                }
                mBluetoothDevice = bluetoothDevice;
                doConnect(autoConnect, context);
            }
        });
    }

    void connect(final Context context, final boolean autoConnect) {
        mConnectHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    MToastUtils.showShort(context, "已连接");
                } else {
                    doConnect(autoConnect, context);
                }
            }
        });
    }


    private void doConnect(boolean autoConnect, Context context) {
        if (isConnectFinished()) {
            close();
            BluetoothGatt gatt;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                gatt = mBluetoothDevice.connectGatt(context,
                        autoConnect, coreGattCallback, TRANSPORT_LE);
            } else {
                gatt = mBluetoothDevice.connectGatt(context,
                        autoConnect, coreGattCallback);
            }
            if (gatt != null) {
                //开始连接
                MToastUtils.showShort(context, "ble_connect-------1开始");
                mBluetoothGatt = gatt;
                doBleCallbackAndUpdateIsFinished(false);
                mConnectHandler.postDelayed(mConnectTimeoutTask, 10000);
            } else {
                //连接失败
                doBleCallbackAndUpdateIsFinished(true);
                close();
            }
        } else {
            MToastUtils.showShort(context, "ble_connect-------7本次连接未结束");
        }
    }

    @NonNull
    private Runnable mConnectTimeoutTask = new Runnable() {
        @Override
        public void run() {
            MLogUtil.d("ble_connect-------6-连接10秒超时");
            close();
            doBleCallbackAndUpdateIsFinished(true);
        }
    };

    private void doBleCallbackAndUpdateIsFinished(boolean connectFinished) {
        setConnectFinished(connectFinished);
        for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
            item.onConnectionStateChanged();
        }
    }

    private static final String LOCK_ISCONNECTFINISHED = "lock_isconnectfinished";

    public boolean isConnectFinished() {
        synchronized (LOCK_ISCONNECTFINISHED) {
            return isConnectFinished;
        }
    }

    private void setConnectFinished(boolean connectFinished) {
        synchronized (LOCK_ISCONNECTFINISHED) {
            isConnectFinished = connectFinished;
            if (isConnectFinished) {
                MLogUtil.d("ble_connect-------连接结束");
            } else {
                MLogUtil.d("ble_connect-------连接中");
            }
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean connected) {
        isConnected = connected;
    }

    private void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public int getConnectState(MyFastBleManager myFastBleManager) {
        return BleConnectionStateUtils.getConnectState(myFastBleManager,mBluetoothDevice);
    }

    private MyBluetoothGattCallback coreGattCallback = new MyBluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            mConnectHandler.post(new Runnable() {
                @Override
                public void run() {
                    MLogUtil.d("BLE-onConnectionStateChange-----currentThread---" + Thread.currentThread().getName() + "-" + BleConnectionStateUtils.getConnectStateStr(newState));
                    mBluetoothGatt = gatt;
                    if (BleConnectionStateUtils.isConnected(status) && BleConnectionStateUtils.isDisconnected(newState)) {
                        MLogUtil.d("BLE连接断开");
                    }
                    if (BleConnectionStateUtils.isDisconnected(newState)) {
                        setConnected(false);
                        MLogUtil.d("ble_connect-------2-onConnectionStateChange-连接断开");
                        mConnectHandler.removeCallbacks(mConnectTimeoutTask);
                        close();
                        if (!isConnectFinished()) {
                            setConnectFinished(true);
                        }
                    }
                    if (BleConnectionStateUtils.isConnected(newState)) {
                        MLogUtil.d("ble_connect-------3-onConnectionStateChange-已连接");
                        gatt.discoverServices();
                    }
                    for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                        item.onConnectionStateChange(gatt, status, newState);
                    }
                }
            });
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            super.onServicesDiscovered(gatt, status);
            mConnectHandler.post(new Runnable() {
                @Override
                public void run() {
                    mConnectHandler.removeCallbacks(mConnectTimeoutTask);
                    mBluetoothGatt = gatt;
                    if (!isConnectFinished()) {
                        setConnectFinished(true);
                    }
//                    setConnectFinished(true);
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //发现服务成功
                        setConnected(true);
                        MLogUtil.d("ble_connect-------4-发现服务成功");
                        setBleNotify(true);
                    } else {
                        setConnected(false);
                        MLogUtil.d("ble_connect-------5-发现服务失败");
                        mBluetoothGatt.disconnect();
                    }
                    MLogUtil.d("BLE-onServicesDiscovered-----currentThread---" + Thread.currentThread().getName());
                    for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                        item.onServicesDiscovered(gatt, status);
                    }
                }
            });
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            MLogUtil.d(TAG, "onCharacteristicRead--------" + mBluetoothDevice + "----" + (status == BluetoothGatt.GATT_SUCCESS) + "---" + MConvertUtils.bytes2HexString(characteristic.getValue()));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                    item.onCharacteristicRead(gatt, characteristic, status);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            MLogUtil.d(TAG, "onCharacteristicWrite--------" + mBluetoothDevice + "----" + (status == BluetoothGatt.GATT_SUCCESS) + "---" + MConvertUtils.bytes2HexString(characteristic.getValue()));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //写characteristic成功
                for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                    item.onCharacteristicWrite(gatt, characteristic, status);
                }
            }
            if (status == BluetoothGatt.GATT_FAILURE) {
                //写characteristic失败
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();//收到的数据
            for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                item.onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                item.onDescriptorRead(gatt, descriptor, status);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //写descriptor成功
            }
            if (status == BluetoothGatt.GATT_FAILURE) {
                //写descriptor失败
            }
            for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                item.onDescriptorWrite(gatt, descriptor, status);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                item.onReadRemoteRssi(gatt, rssi, status);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
                item.onMtuChanged(gatt, mtu, status);
            }
        }
    };
    private List<MyBluetoothGattCallback> mMyBluetoothGattCallbacks = new ArrayList<>();

    void addBleGattCallback(MyBluetoothGattCallback bluetoothGattCallback) {
        if (!mMyBluetoothGattCallbacks.contains(bluetoothGattCallback)) {
            mMyBluetoothGattCallbacks.add(bluetoothGattCallback);
        }
    }

    void removeBleGattCallback(MyBluetoothGattCallback bluetoothGattCallback) {
        mMyBluetoothGattCallbacks.remove(bluetoothGattCallback);
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    private UUID formUUID(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }

    @Nullable
    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(BluetoothGattService gattService, String characteristicUuid) {
        BluetoothGattCharacteristic gattCharacteristic = null;
        UUID characteristicUUID = formUUID(characteristicUuid);
        if (gattService != null && characteristicUUID != null) {
            gattCharacteristic = gattService.getCharacteristic(characteristicUUID);
        }
        return gattCharacteristic;
    }

    private BluetoothGattService getBluetoothGattService() {
        BluetoothGattService gattService = null;
        UUID serviceUUID = formUUID(SERVICE_UUID);
        if (serviceUUID != null && mBluetoothGatt != null) {
            gattService = mBluetoothGatt.getService(serviceUUID);
        }
        return gattService;
    }

    /****************************************writeAsync***********************************************/
    //no response方式
    void writeCharacteristicAsync(final byte[] data) {
        mWriteHandler.post(new Runnable() {
            @Override
            public void run() {
//                XTLogUtil.d("writeCharacteristicAsync------" + ConvertUtils.bytes2HexString(data));
                if (isConnected()) {
                    BluetoothGattService gattService = getBluetoothGattService();
                    BluetoothGattCharacteristic gattCharacteristic = getBluetoothGattCharacteristic(gattService, WRITE_UUID);
                    if (data == null || data.length <= 0) {
                        //数据为空
                        onWriteCharacteristicFailed("the data to be written is empty");
//                        return false;
                        return;
                    }

                    if (gattCharacteristic == null || (gattCharacteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
                        onWriteCharacteristicFailed("this characteristic not support writeAsync!");
//                        return false;
                        return;
                    }
                    if (gattCharacteristic.setValue(data)) {
//                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        if (!mBluetoothGatt.writeCharacteristic(gattCharacteristic)) {
                            onWriteCharacteristicFailed("gatt writeCharacteristicAsync fail");
//                            return false;
                        } else {
//                            return true;
                        }
                    } else {
                        onWriteCharacteristicFailed("Updates the locally stored value of this characteristic fail");
//                        return false;
                    }
                } else {
                    onWriteCharacteristicFailed("ble未连接");
                    //未连接
//                    return false;
                }
            }
        });
    }

    //no response方式
    boolean writeCharacteristicSync(final byte[] data) {
        if (isConnected()) {
            BluetoothGattService gattService = getBluetoothGattService();
            BluetoothGattCharacteristic gattCharacteristic = getBluetoothGattCharacteristic(gattService, WRITE_UUID);
            if (data == null || data.length <= 0) {
                //数据为空
                onWriteCharacteristicFailed("the data to be written is empty");
                return false;
            }

            if (gattCharacteristic == null || (gattCharacteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
                onWriteCharacteristicFailed("this characteristic not support writeAsync!");
                return false;
            }
            if (gattCharacteristic.setValue(data)) {
//                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
                gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                if (!mBluetoothGatt.writeCharacteristic(gattCharacteristic)) {
                    onWriteCharacteristicFailed("gatt writeCharacteristicAsync fail");
                    return false;
                } else {
                    return true;
                }
            } else {
                onWriteCharacteristicFailed("Updates the locally stored value of this characteristic fail");
                return false;
            }
        } else {
            onWriteCharacteristicFailed("ble未连接");
            //未连接
            return false;
        }
    }

    private void onWriteCharacteristicFailed(String result) {
        for (MyBluetoothGattCallback item : mMyBluetoothGattCallbacks) {
            item.onWriteCharacteristicFailed(result);
        }
    }

    /****************************************writeAsync***********************************************/
    /****************************************writeDescriptor***********************************************/
    private static final String UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    private boolean writeDescriptor(boolean enable, byte[] descriptorValue) {
        BluetoothGattService gattService = getBluetoothGattService();
        BluetoothGattCharacteristic gattCharacteristic = getBluetoothGattCharacteristic(gattService, NOTIFY_UUID);
        if (gattCharacteristic != null && (gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (mBluetoothGatt == null) {
                //gatt equal null
                return false;
            }
            if (!mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, enable)) {
                //gatt setCharacteristicNotification fail
                return false;
            }
            BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(formUUID(UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR));
            if (descriptor == null) {
                //descriptor equals null
                return false;
            } else {
                descriptor.setValue(enable ? descriptorValue :
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                if (!mBluetoothGatt.writeDescriptor(descriptor)) {
                    //gatt writeDescriptor fail
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            //this characteristic not support notify!
            return false;
        }
    }
    /****************************************writeDescriptor***********************************************/

    /****************************************notify***********************************************/
    private boolean setBleNotify(boolean enable) {
        return writeDescriptor(enable, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    }

    /****************************************notify***********************************************/

    /****************************************indicate***********************************************/
    public boolean setBleIndicate(boolean enable) {
        return writeDescriptor(enable, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
    }
    /****************************************indicate***********************************************/
    /****************************************readCharacteristic***********************************************/
    public boolean readCharacteristic() {
        BluetoothGattService gattService = getBluetoothGattService();
        BluetoothGattCharacteristic gattCharacteristic = getBluetoothGattCharacteristic(gattService, WRITE_UUID);
        if (gattCharacteristic != null && (gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            if (!mBluetoothGatt.readCharacteristic(gattCharacteristic)) {
                //gatt readCharacteristic fail
                return false;
            } else {
                return true;
            }
        } else {
            //this characteristic not support read!
            return false;
        }
    }
    /****************************************readCharacteristic***********************************************/
    /*****************************************readRssi**************************************************************/
    public boolean readRssi() {
        if (!mBluetoothGatt.readRemoteRssi()) {
            //gatt readRemoteRssi fail
            return false;
        } else {
            return true;
        }
    }
    /*****************************************readRssi**************************************************************/
    /*****************************************************setMtu***********************************************************/
    private static final int DEFAULT_MTU = 23;
    private static final int DEFAULT_MAX_MTU = 512;

    public boolean setMtu(int mtu) {
        if (mtu > DEFAULT_MAX_MTU) {
            //requiredMtu should lower than 512 !
            return false;
        }

        if (mtu < DEFAULT_MTU) {
            //requiredMtu should higher than 23 !
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!mBluetoothGatt.requestMtu(mtu)) {
                //gatt requestMtu fail
                return false;
            } else {
                return true;
            }
        } else {
            //API level lower than 21
            return false;
        }
    }
    /*****************************************************setMtu***********************************************************/
}
