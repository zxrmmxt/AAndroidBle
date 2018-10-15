package com.xt.a_ble;


import java.util.UUID;

public class ABleScanRuleConfig {

    private UUID[] mServiceUuids = null;
    private String[] mDeviceNames = null;
    private String mDeviceMac = null;
    private boolean mAutoConnect = false;
    private boolean mFuzzy = false;
    private long mScanTimeOut = 10000;
    public static final int DEFAULT_SCAN_TIME = 10000;


    public UUID[] getServiceUuids() {
        return mServiceUuids;
    }

    public String[] getDeviceNames() {
        return mDeviceNames;
    }

    public String getDeviceMac() {
        return mDeviceMac;
    }

    public boolean isAutoConnect() {
        return mAutoConnect;
    }

    public boolean isFuzzy() {
        return mFuzzy;
    }

    public long getScanTimeOut() {
        return mScanTimeOut;
    }

    public static class Builder {

        private UUID[] mServiceUuids = null;
        private String[] mDeviceNames = null;
        private String mDeviceMac = null;
        private boolean mAutoConnect = false;
        private boolean mFuzzy = false;
        private long mTimeOut = DEFAULT_SCAN_TIME;

        public Builder setServiceUuids(UUID[] uuids) {
            this.mServiceUuids = uuids;
            return this;
        }

        public Builder setDeviceName(boolean fuzzy, String... name) {
            this.mFuzzy = fuzzy;
            this.mDeviceNames = name;
            return this;
        }

        public Builder setDeviceMac(String mac) {
            this.mDeviceMac = mac;
            return this;
        }

        public Builder setAutoConnect(boolean autoConnect) {
            this.mAutoConnect = autoConnect;
            return this;
        }

        public Builder setScanTimeOut(long timeOut) {
            this.mTimeOut = timeOut;
            return this;
        }

        void applyConfig(ABleScanRuleConfig config) {
            config.mServiceUuids = this.mServiceUuids;
            config.mDeviceNames = this.mDeviceNames;
            config.mDeviceMac = this.mDeviceMac;
            config.mAutoConnect = this.mAutoConnect;
            config.mFuzzy = this.mFuzzy;
            config.mScanTimeOut = this.mTimeOut;
        }

        public ABleScanRuleConfig build() {
            ABleScanRuleConfig config = new ABleScanRuleConfig();
            applyConfig(config);
            return config;
        }

    }


}
