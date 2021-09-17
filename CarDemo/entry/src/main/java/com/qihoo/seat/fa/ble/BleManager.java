package com.qihoo.seat.fa.ble;

import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateCallback;
import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateChangeCallback;
import com.qihoo.seat.fa.ble.listener.ConnectStateCallback;
import com.qihoo.seat.fa.ble.listener.DeviceInfo;
import com.qihoo.seat.fa.util.GattCharacteristicConstants;
import com.qihoo.seat.fa.util.GattDescriptorConstants;
import com.qihoo.seat.fa.util.LogUtils;
import com.qihoo.seat.fa.ble.subscriber.BluetoothAdapterStateSubscriber;
import com.qihoo.seat.fa.util.UIManager;
import ohos.app.Context;
import ohos.bluetooth.BluetoothHost;
import ohos.bluetooth.ProfileBase;
import ohos.bluetooth.ble.*;
import ohos.event.commonevent.CommonEventManager;
import ohos.event.commonevent.CommonEventSubscribeInfo;
import ohos.event.commonevent.MatchingSkills;
import ohos.rpc.RemoteException;
import ohos.utils.zson.ZSONObject;
import java.nio.charset.Charset;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 蓝牙管理类
 *
 * @since 2021-08-12
 */
public class BleManager {
    // 服务标识（读取数据）
    private static final UUID READ_SERVICE_UUID = UUID.fromString("15f1e000-a277-43fc-a484-dd39ef8a9100");

    private static final UUID CHARACTERISTIC_READ_UUID = UUID.fromString("02000200-0000-1000-8000-009178563412");

    private static final UUID WRITE_SERVICE_UUID = UUID.fromString("15f1f000-a277-43fc-a484-dd39ef8a9100");

    private static final UUID CHARACTERISTIC_WRITE_UUID = UUID.fromString("03000300-0000-1000-8000-009278563412");

    private static final UUID NOTIFY_SERVICE_UUID = UUID.fromString("0000fda0-0000-1000-8000-00805f9b12ea");

    private static final UUID CHARACTERISTIC_NOTIFY_UUID = UUID.fromString("02000200-0000-1000-8000-009178563412");

    private static final int TIME_UNIT = 1000;
    private static final int STATE_CONNECTED = 1;
    private static final int STATE_DISCONNECTED = 2;
    private BluetoothHost bluetoothHost;
    private final BleCentralManager centralManager;
    private final List<UUID> uuidList;
    private boolean isScanning = false;
    private final Map<String, BlePeripheralDevice> scanBleDevicesMap;
    private String macAddr;

    private Optional<BlePeripheralDevice> peripheralDevice;
    private GattCharacteristic mWriteChar;
    private GattCharacteristic mReadChar;
    private GattCharacteristic mNotifyChar;

    private ConnectStateCallback mConnectStateCallback;
    private DeviceInfo mDeviceInfoCallback;
    private final BluetoothAdapterStateSubscriber bluetoothAdapterStateSubscriber;
    private boolean isConnect = false;

    /**
     * 构造方法
     *
     * @param context 上下文对象
     */
    public BleManager(Context context) {
        LogUtils.e("BleManager");
        uuidList = new ArrayList<>();
        scanBleDevicesMap = new HashMap<>();
        bluetoothHost = BluetoothHost.getDefaultHost(context);
        centralManager = new BleCentralManager(context, getCentralManagerCallback());
        bluetoothAdapterStateSubscriber = new BluetoothAdapterStateSubscriber(getStageChangeSubscribeInfo());
        try {
            CommonEventManager.subscribeCommonEvent(bluetoothAdapterStateSubscriber);
        } catch (RemoteException e) {
            LogUtils.e("BleCentralOperator construct exception: " + e.getCause().getLocalizedMessage());
        }
    }

    /**
     * 注册蓝牙事件变化消息
     *
     * @return CommonEventSubscribeInfo
     */
    private CommonEventSubscribeInfo getStageChangeSubscribeInfo() {
        MatchingSkills matchingSkills = new MatchingSkills();
        matchingSkills.addEvent(BluetoothHost.EVENT_HOST_STATE_UPDATE);
        matchingSkills.addEvent(BluetoothHost.EVENT_HOST_DISCOVERY_STARTED);
        matchingSkills.addEvent(BluetoothHost.EVENT_HOST_DISCOVERY_FINISHED);
        return new CommonEventSubscribeInfo(matchingSkills);
    }

    /**
     * 开始扫描蓝牙
     *
     * @param services services
     * @param mac mac地址
     * @param interval 时长
     * @return code
     */
    public int startBluetoothDevicesDiscovery(UUID[] services, String mac, int interval) {
        LogUtils.i("startBluetoothDevicesDiscovery mac = " + mac);
        this.macAddr = mac;
        uuidList.clear();
        uuidList.addAll(Arrays.asList(services));
        LogUtils.i("interval = " + interval);
        boolean hasConnect = hasConnected(macAddr);
        LogUtils.e("hasConnected is = " + hasConnect);
        if (hasConnect) {
            connectToDevice(macAddr);
        } else {
            UIManager.getInstance().postEventDelay((Runnable) () -> {
                LogUtils.i("isScanning = " + isScanning);
                if (isScanning) {
                    stopScan();
                    mConnectStateCallback.stateChange(1, "have no discovey");
                }

            }, interval * 1000);
            isScanning = true;
            List<BleScanFilter> emptyList = new ArrayList<>();
            centralManager.startScan(emptyList);
        }
        return BleOperatorConstants.ERROR_CODE_OK;
    }

    private boolean hasConnected(String mac) {
        List<BlePeripheralDevice> devicesByStates = centralManager.getDevicesByStates(new int[]{ProfileBase.STATE_CONNECTED});
        LogUtils.error("devicesByStates = " + ZSONObject.toZSONString(devicesByStates));
        for (int i = 0; devicesByStates != null && i < devicesByStates.size(); i++) {
            BlePeripheralDevice device = devicesByStates.get(i);
            if (mac.equals(device.getDeviceAddr())) {
                return true;
            }
        }
        return false;
    }


    public void setConnectStateCallback(ConnectStateCallback callback) {
        this.mConnectStateCallback = callback;
    }

    /**
     * 设备状态变化的回调
     *
     * @param callback DeviceInfo
     */
    public void setDeviceInfoCallback(DeviceInfo callback) {
        this.mDeviceInfoCallback = callback;
    }

    /**
     * 蓝牙状态变化的回调
     *
     * @param callback BluetoothAdapterStateChangeCallback
     */
    public void setBluetoothAdapterStateChangeCallback(BluetoothAdapterStateChangeCallback callback) {
        bluetoothAdapterStateSubscriber.setBluetoothAdapterStateChangeCallback(callback);
    }

    /**
     * 获取蓝牙状态
     *
     * @param stateCallback BluetoothAdapterStateCallback
     */
    public void getBluetoothAdapterState(BluetoothAdapterStateCallback stateCallback) {
        if (stateCallback != null) {
            boolean isDiscovering = bluetoothHost.isBtDiscovering();
            boolean isOpen = bluetoothHost.getBtState() == BluetoothHost.STATE_ON;
            stateCallback.getBluetoothAdapterState(isDiscovering, isOpen, BleOperatorConstants.ERROR_CODE_OK);
        }
    }

    /**
     * 判断蓝牙是否打开
     *
     * @return true false
     */
    public boolean isBlueOpen() {
        if (bluetoothHost != null) {
            return bluetoothHost.getBtState() == BluetoothHost.STATE_ON
                    || bluetoothHost.getBtState() == BluetoothHost.STATE_BLE_ON;
        }
        return false;
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        isScanning = false;
        centralManager.stopScan();
    }

    /**
     * 断开设备
     *
     * @return true false
     */
    public int disConnectDevice() {
        if (isScanning) {
            stopScan();
        }
        int code = peripheralDevice.map(device -> device.disconnect() && device.close()
                                                ? BleOperatorConstants.ERROR_CODE_OK
                                                : BleOperatorConstants.ERROR_CODE_DISCONNECT_ERR)
                        .orElse(BleOperatorConstants.ERROR_CODE_COMMON_ERR);
        if (code == 0) {
            this.isConnect = false;
        }
        return code;
    }

    private BleCentralManagerCallback getCentralManagerCallback() {
        return new BleCentralManagerCallback() {
            @Override
            public void scanResultEvent(BleScanResult bleScanResult) {
                if (bleScanResult == null) {
                    return;
                }
                BlePeripheralDevice device = bleScanResult.getPeripheralDevice();
                if (device == null) {
                    return;
                }

                LogUtils.i("scan name = " + device.getDeviceName().get());
                List<UUID> uuids = bleScanResult.getServiceUuids();
                if (uuids != null) {
                    LogUtils.i("UUIDs:" + uuids.toString());
                }
                String mac = device.getDeviceAddr();
                LogUtils.i("scan MacAddress:" + mac);
                LogUtils.i("scan MacAddress1263456:" + macAddr);
                LogUtils.i("scan MacAddress:" + mac.equals(macAddr));
                if (mac.equals(macAddr)) {
                    if (isScanning) {
                        stopScan();
                    }
                    connectToDevice(mac);
                }
            }

            @Override
            public void scanFailedEvent(int i) {
            }

            @Override
            public void groupScanResultsEvent(List<BleScanResult> list) {
            }
        };
    }

    /**
     * 连接设备
     *
     * @param deviceId mac地址
     */
    public void connectToDevice(String deviceId) {
        LogUtils.i("deviceId = " + deviceId);
        BlePeripheralDevice device = BlePeripheralDevice.createInstance(deviceId);
        peripheralDevice = Optional.ofNullable(device);
        peripheralDevice.map(peripheralDevice -> peripheralDevice.connect(false, getBlePeripheralCallback(device)));
    }

    private BlePeripheralCallback getBlePeripheralCallback(BlePeripheralDevice device) {
        return new BlePeripheralCallback() {
            @Override
            public void servicesDiscoveredEvent(int status) {
                super.servicesDiscoveredEvent(status);
                onServiceDiscoveredEvent(status, device);
            }

            @Override
            public void connectionStateChangeEvent(int connectionState) {
                super.connectionStateChangeEvent(connectionState);
                onConnectionStateChangeEvent(connectionState);
            }

            @Override
            public void characteristicReadEvent(GattCharacteristic characteristic, int ret) {
                super.characteristicReadEvent(characteristic, ret);
                LogUtils.i("characteristicReadEventret = " + ret);
                if (ret == BlePeripheralDevice.OPERATION_SUCC) {
                    byte[] byteData = characteristic.getValue();
                    String data = new String(byteData,Charset.defaultCharset());
                    LogUtils.i(data + "characteristicReadEvent");
                    if (mDeviceInfoCallback != null) {
                        mDeviceInfoCallback.updateDeviceInfo(data);
                    }
                }
            }

            @Override
            public void characteristicWriteEvent(GattCharacteristic characteristic, int ret) {
                super.characteristicWriteEvent(characteristic, ret);
//                LogUtils.i("characteristicWriteEvent ret = " + ret);
//                if (ret == BlePeripheralDevice.OPERATION_SUCC) {
//                    byte[] byteData = characteristic.getValue();
//                    String data = Hex.encodeHexString(byteData);
//                    LogUtils.i(data + "characteristicWriteEvent");
//                    if (mDeviceInfoCallback != null) {
//                        mDeviceInfoCallback.updateDeviceInfo(data);
//                        getDeviceInfo();
//                    }
//                }
            }

            @Override
            public void characteristicChangedEvent(GattCharacteristic characteristic) {
                super.characteristicChangedEvent(characteristic);
                LogUtils.i("characteristicChangedEvent");
                byte[] byteData = characteristic.getValue();
                String data = Hex.encodeHexString(byteData);
                LogUtils.i(data + "characteristicChangedEvent");
                if (mDeviceInfoCallback != null) {
                    mDeviceInfoCallback.updateDeviceInfo(data);
                }
            }
        };
    }

    private void onConnectionStateChangeEvent(int connectionState) {
        LogUtils.i("connectionState = " + connectionState);
        if (connectionState == ProfileBase.STATE_CONNECTED) {
            isConnect = true;
            discoveryServices();
        } else if (connectionState == ProfileBase.STATE_DISCONNECTED) {

            isConnect = false;
            disConnectDevice();
        }
    }

    /**
     * 判断蓝牙是否连接
     *
     * @return true or false
     */
    public boolean isConnect() {
        return isConnect;
    }

    private void onServiceDiscoveredEvent(int status, BlePeripheralDevice device) {
        LogUtils.i("servicesDiscoveredEvent status = " + status);
        if (status == BlePeripheralDevice.OPERATION_SUCC) {
            List<GattService> services = device.getServices();
//            LogUtils.i("services size = " + services.size());
//            LogUtils.i("services content = " + services.toString());
            for (GattService gattService : services) {
                checkGattCharacteristic(gattService, device);
            }
            mConnectStateCallback.stateChange(STATE_DISCONNECTED, "connected");
        }
    }

    private void discoveryServices() {
        peripheralDevice.map(device -> device.discoverServices());
    }

    private void checkGattCharacteristic(GattService service, BlePeripheralDevice device) {
        for (GattCharacteristic gattCharacteristic : service.getCharacteristics()) {
            if (gattCharacteristic.getUuid().equals(CHARACTERISTIC_NOTIFY_UUID)) {
//                LogUtils.i("dataString");
                mNotifyChar = gattCharacteristic;
                setNotify(mNotifyChar,device,true);
//                device.setNotifyCharacteristic(mNotifyChar, true);
            }

            if (gattCharacteristic.getUuid().equals(CHARACTERISTIC_WRITE_UUID)) {
                LogUtils.i("write");
                mWriteChar = gattCharacteristic;
            }
            if (gattCharacteristic.getUuid().equals(CHARACTERISTIC_READ_UUID)) {
                LogUtils.i("read");
                mReadChar = gattCharacteristic;
            }
        }
    }

    private void setNotify(GattCharacteristic gattCharacteristic, BlePeripheralDevice device, boolean isEnable) {
        byte[] descriptorValue = GattDescriptorConstants.getEnableNotificationValue();
        int propertyFlag = GattCharacteristicConstants.PROPERTY_NOTIFY;
        if ((gattCharacteristic != null) && (gattCharacteristic.getProperties() & propertyFlag )!=0 && (device != null)) {
            device.setNotifyCharacteristic(gattCharacteristic, isEnable);
            byte[] finalDescriptorValue = descriptorValue;
            gattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")).ifPresent(descriptor -> {
                Optional.ofNullable(descriptor).get().setValue(finalDescriptorValue);
                boolean flag = device.writeDescriptor(Optional.ofNullable(descriptor).get());
//                LogUtils.e("setNotify1 flag is :" + flag);
            });
        }
    }

    /**
     * 读取设备状态信息
     */
    public void getDeviceInfo() {
//        LogUtils.i("getDeviceInfo = " + mReadChar);
        if (mReadChar != null) {
            peripheralDevice.get().readCharacteristic(mReadChar);
        }
    }

    /**
     * 控制设备
     *
     * @param value value
     */
    public void controlDevice(String value) {
//        LogUtils.e("controlDevice value = " + value);
        if (mWriteChar != null) {
            mWriteChar.setValue(hexTobytes(value));
            peripheralDevice.get().writeCharacteristic(mWriteChar);
        }
    }

    private static byte[] hexTobytes(String data) {
        if (data == null) {
            return new byte[0];
        }
        String hexString = data.replace(" ", "");
        int cycleLength = hexString.length() / 2;
        byte[] resultBytes = new byte[cycleLength];
        int beginIndex, endIndex;
        for (int i = 0; i < cycleLength; i++) {
            beginIndex = i * 2;
            endIndex = (i * 2) + 2;
            resultBytes[i] = (byte) Integer.parseInt(hexString.substring(beginIndex, endIndex), 16);
//            LogUtils.e("byte ddddddata = " + resultBytes[i]);
        }
//        LogUtils.e("result byte length:" + resultBytes.length);
        return resultBytes;
    }
}
