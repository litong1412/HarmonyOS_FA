package com.qihoo.seat.fa.ble.model;

/**
 * BluetoothDiscoveryParam model
 *
 * @since 2021-08-12
 */
public class BluetoothDiscoveryParam {
    private String[] services;
    private String macAddr;
    private int interval;



    /**
     * get time of interval
     *
     * @return time of interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * set interval time
     *
     * @param interval interval time
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * get array of uuid
     *
     * @return array of uuid
     */
    public String[] getServices() {
        return services.clone();
    }

    /**
     * set array of uuid
     *
     * @param services array of uuid
     */
    public void setServices(String[] services) {
        this.services = services.clone();
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
}
