package com.qihoo.seat.fa.ble;

/**
 * BleOperatorConstants
 *
 * @since 2021-08-12
 */
public class BleOperatorConstants {
    /**
     * normal state
     */
    public static final int ERROR_CODE_OK = 0;

    /**
     * ble state change code
     */
    public static final int BLE_SATE_CODE = 100;

    /**
     * General error code
     */
    public static final int ERROR_CODE_COMMON_ERR = 11_001;

    /**
     * Bluetooth connection failed error code
     */
    public static final int ERROR_CODE_CONNECT_ERR = 11_002;

    /**
     * Bluetooth disconnection failure error code
     */
    public static final int ERROR_CODE_DISCONNECT_ERR = 11_003;

    /**
     * Return message of successful interface call
     */
    public static final String SUCCESS_MESSAGE = "Success";

    /**
     * Open the communication code of the Bluetooth adapter
     */
    public static final int OPEN_BLUETOOTH_ADAPTER = 1001;

    /**
     * Turn off the communication code of the Bluetooth adapter
     */
    public static final int CLOSE_BLUETOOTH_ADAPTER = 1002;

    /**
     * Get the communication code of the Bluetooth adapter status
     */
    public static final int GET_BLUETOOTH_ADAPTER_STATE = 1003;

    /**
     * The communication code to monitor the status change of the Bluetooth adapter
     */
    public static final int ON_BLUETOOTH_ADAPTER_STATE_CHANGE = 1004;

    /**
     * Start the communication code for Bluetooth device discovery
     */
    public static final int START_BLUETOOTH_DEVICES_DISCOVERY = 1005;

    /**
     * Stop the communication code discovered by the Bluetooth device
     */
    public static final int STOP_BLUETOOTH_DEVICES_DISCOVERY = 1006;

    /**
     * Monitor the communication code found by the Bluetooth device
     */
    public static final int ON_BLUETOOTH_DEVICE_FOUND = 1007;

    /**
     * The communication code to open the Bluetooth low energy connection
     */
    public static final int CREATE_BLE_CONNECTION = 1008;

    /**
     * Turn off the communication code of the Bluetooth low energy connection
     */
    public static final int CLOSE_BLE_CONNECTION = 1009;

    /**
     * Communication code for monitoring the Bluetooth low energy connection status
     */
    public static final int ON_BLE_CONNECTION_STATE_CHANGE = 1010;

    /**
     * Get the communication code of the connected Bluetooth device ID
     */
    public static final int GET_DEVICE_ID = 1011;

    /**
     * Listen to the communication code discovered by the Bluetooth low energy service
     */
    public static final int ON_BLE_SERVICES_DISCOVERED = 1012;

    /**
     * Read the communication code of the Bluetooth low energy characteristic value
     */
    public static final int READ_BLE_CHARACTERISTIC_VALUE = 1013;

    /**
     * Write the communication code of the Bluetooth low energy characteristic value
     */
    public static final int WRITE_BLE_CHARACTERISTIC_VALUE = 1014;

    /**
     * The communication code that monitors the change of the Bluetooth low energy characteristic value
     */
    public static final int ON_BLE_CHARACTERISTIC_VALUE_CHANGE = 1015;

    /**
     * Communication code notifying the change of Bluetooth low energy characteristic value
     */
    public static final int NOTIFY_BLE_CHARACTERISTIC_VALUE_CHANGE = 1016;

    /**
     * Set the communication code to enable indication or notification
     */
    public static final int SET_ENABLE_INDICATION = 1017;

    /**
     * monitor enable notify/indicate
     */
    public static final int ON_ENABLE_NOTIFY_INDICATE = 1020;

    /**
     * Read the communication code of the Bluetooth Low Energy Descriptor
     */
    public static final int READ_BLE_DESCRIPTOR_VALUE = 1023;

    /**
     * Write the communication code of the Bluetooth low energy descriptor
     */
    public static final int WRITE_BLE_DESCRIPTOR_VALUE = 1024;

    /**
     * Get the connection state by device id
     */
    public static final int GET_BLE_CONNECTION_STATE = 1025;
    /**
     * get sedevice state
     */
    public static final int GET_DEVICE_STATE = 1026;

    /**
     * get device info
     */
    public static final int GET_DEVICE_INFO = 1027;

    /**
     * control device
     */
    public static final int CONTROL_DEVICE = 1028;

    /**
     * is ble connected
     */
    public static final int IS_BLE_CONNECTED = 1029;

    /**
     * check ble switch
     */
    public static final int CHECK_BLE_SWITCH = 1030;

    private BleOperatorConstants() {
    }
}
