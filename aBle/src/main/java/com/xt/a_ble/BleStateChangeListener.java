package com.xt.a_ble;

/**
 * Created by xuti on 2018/8/10.
 */
public interface BleStateChangeListener {
    void onStateChange(int state, int oldState);
}
