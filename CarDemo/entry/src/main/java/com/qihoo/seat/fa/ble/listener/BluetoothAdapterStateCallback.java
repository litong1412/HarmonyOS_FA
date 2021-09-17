package com.qihoo.seat.fa.ble.listener;

/**
 * Bluetooth adapter status callback interface
 *
 * @since 2021-08-11
 */
public interface BluetoothAdapterStateCallback {
    /**
     * Get the status of the phone's Bluetooth adapter
     *
     * @param isDiscovering Is searching for devices
     * @param isAvailable Is the Bluetooth adapter available，true is available，false is unavailable
     * @param errorCode errorCode：0, 11001
     */
    void getBluetoothAdapterState(boolean isDiscovering, boolean isAvailable, int errorCode);
}
