package com.qihoo.seat.fa.ble;

import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateCallback;
import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateChangeCallback;
import com.qihoo.seat.fa.ble.listener.ConnectStateCallback;
import com.qihoo.seat.fa.ble.listener.DeviceInfo;
import com.qihoo.seat.fa.util.LogUtils;
import ohos.app.Context;

import java.util.Optional;
import java.util.UUID;

/**
 * ble helper class
 *
 * @since 2021-08-12
 */
public class BleHelper {
    private Context mContext;
    private Optional<BleManager> bleCenterManager;

    /**
     * 构造方法
     */
    public BleHelper() {
    }

    /**
     * 判断蓝牙是否连接
     *
     * @return flag
     */
    public boolean isBleConnected() {
        return bleCenterManager.get().isConnect();
    }

    /**
     * 开始扫描设备
     *
     * @param services services
     * @param mac mac address
     * @param interval 时长
     * @return code
     */
    public String startBluetoothDevicesDiscovery(UUID[] services, String mac, int interval) {
        int code = bleCenterManager.get().startBluetoothDevicesDiscovery(services, mac, interval);
        return String.valueOf(code);
    }

    /**
     * 添加蓝牙状态回调
     *
     * @param stateCallback BluetoothAdapterStateCallback
     */
    public void getBluetoothAdapterState(BluetoothAdapterStateCallback stateCallback) {
        if (stateCallback != null) {
            bleCenterManager.get().getBluetoothAdapterState(stateCallback);
        }
    }

    /**
     * 添加连接状态的回调
     *
     * @param callback ConnectStateCallback
     */
    public void getStateCallback(ConnectStateCallback callback) {
        bleCenterManager.get().setConnectStateCallback(callback);
    }

    /**
     * 蓝牙状态变化的回调
     *
     * @param callback BluetoothAdapterStateChangeCallback
     */
    public void onBluetoothAdapterStateChange(BluetoothAdapterStateChangeCallback callback) {
        bleCenterManager.get().setBluetoothAdapterStateChangeCallback(callback);
    }

    /**
     * 连接设备
     *
     * @param deviceId mac address
     */
    public void createBleConnected(String deviceId) {
        bleCenterManager.get().connectToDevice(deviceId);
    }

    /**
     * 设备状态变化的回调
     *
     * @param callback DeviceInfo
     */
    public void setDeviceInfoCallback(DeviceInfo callback) {
        bleCenterManager.get().setDeviceInfoCallback(callback);
    }

    /**
     * 获取设备信息状态
     */
    public void getDeviceInfo() {
        bleCenterManager.get().getDeviceInfo();
    }

    /**
     * 断开蓝牙连接
     */
    public void disConnect() {
        bleCenterManager.get().disConnectDevice();
    }

    /**
     * 控制设备
     *
     * @param value 根据协议来
     */
    public void controlDevice(String value) {
        bleCenterManager.get().controlDevice(value);
    }

    /**
     * 断开设备连接
     *
     * @param deviceId mac address
     * @return code
     */
    public String closeBleConnection(String deviceId) {
        int code = bleCenterManager.get().disConnectDevice();
        return String.valueOf(code);
    }

    /**
     * 判断蓝牙开关是否打开
     *
     * @return true or false
     */
    public boolean isBlueOpen() {
        return bleCenterManager.get().isBlueOpen();
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public void init(Context context) {
        this.mContext = context;
        BleManager manager = new BleManager(this.mContext);
        bleCenterManager = Optional.ofNullable(manager);
    }

    /**
     * 获取单例对象
     *
     * @return BleHelper
     */
    public static BleHelper getInstance() {
        return BluetoothHelper.INSTANCE;
    }

    /**
     * BluetoothHelper
     *
     * @since 2021-08-12
     */
    private static class BluetoothHelper {
        public static final BleHelper INSTANCE = new BleHelper();
    }
}
