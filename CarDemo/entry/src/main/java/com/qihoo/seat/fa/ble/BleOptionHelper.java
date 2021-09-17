package com.qihoo.seat.fa.ble;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateCallback;
import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateChangeCallback;
import com.qihoo.seat.fa.ble.listener.ConnectStateCallback;
import com.qihoo.seat.fa.ble.listener.DeviceInfo;
import com.qihoo.seat.fa.ble.model.BluetoothDiscoveryParam;
import com.qihoo.seat.fa.util.BasicConstants;
import com.qihoo.seat.fa.util.LogUtils;
import ohos.app.Context;
import ohos.rpc.IRemoteObject;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.rpc.RemoteException;
import ohos.utils.zson.ZSONObject;
import ohos.agp.utils.TextTool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * ble option helper
 *
 * @since 2021-08-12
 */
public class BleOptionHelper {
    private static final String IS_DISCOVERING = "isDiscovering";

    private static final String IS_AVAILABLE = "isAvailable";

    private static final String CODE = "code";

    private static final String DATA = "data";

    private static final String DEVICE_ID = "deviceId";

    /**
     * 构造方法
     *
     * @param context 上下文对象
     */
    public BleOptionHelper(Context context) {
        LogUtils.e("BleOptionHelper");
        BleHelper.getInstance().init(context);
    }

    /**
     * 设备信息更新回调
     *
     * @param bleDeviceInfoCallbackSet Set
     */
    public void setDeviceUpdateLinstener(Set<IRemoteObject> bleDeviceInfoCallbackSet) {
        DeviceInfo infoCallback = deviceInfo -> {
            LogUtils.i("setConnectStateLinstener deviceInfo:" + deviceInfo);
            MessageParcel data = MessageParcel.obtain();
            MessageOption option = new MessageOption();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("deviceInfo", deviceInfo);
            data.writeString(ZSONObject.toZSONString(resultMap));
            MessageParcel reply = MessageParcel.obtain();
            for (IRemoteObject remoteObject : bleDeviceInfoCallbackSet) {
                sendRequest(remoteObject, BasicConstants.SUCCESS, data, reply, option);
            }
            reply.reclaim();
            data.reclaim();
        };
        BleHelper.getInstance().setDeviceInfoCallback(infoCallback);
    }

    /**
     * 设置连接状态的回调
     *
     * @param bleStateCallbackSet Set
     */
    public void setConnectStateLinstener(Set<IRemoteObject> bleStateCallbackSet) {
        ConnectStateCallback stateCallback = (state, desc) -> {
            LogUtils.i("setConnectStateLinstener state:" + state);
            LogUtils.i("setConnectStateLinstener desc:" + desc);
            MessageParcel data = MessageParcel.obtain();
            MessageOption option = new MessageOption();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("state", state);
            resultMap.put("desc", desc);
            data.writeString(ZSONObject.toZSONString(resultMap));
            MessageParcel reply = MessageParcel.obtain();
            for (IRemoteObject remoteObject : bleStateCallbackSet) {
                sendRequest(remoteObject, BasicConstants.SUCCESS, data, reply, option);
            }
            reply.reclaim();
            data.reclaim();
        };
        BleHelper.getInstance().getStateCallback(stateCallback);
    }

    /**
     * 获取设备状态信息
     */
    public void getDeviceInfo() {
        BleHelper.getInstance().getDeviceInfo();
    }

    /**
     * ble adapter state change callback
     *
     * @param bleAdapterStateChangeCallbackSet Set
     */
    public void onBluetoothAdapterStateChange(Set<IRemoteObject> bleAdapterStateChangeCallbackSet) {
        BluetoothAdapterStateChangeCallback callback = (isDiscovering, isAvailable) -> {
            LogUtils.i("onBluetoothAdapterStateChange isDiscovering:" + isDiscovering);
            LogUtils.i("onBluetoothAdapterStateChange isAvailable:" + isAvailable);
            MessageParcel data = MessageParcel.obtain();
            MessageParcel reply = MessageParcel.obtain();
            MessageOption option = new MessageOption();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(IS_DISCOVERING, isDiscovering);
            resultMap.put(IS_AVAILABLE, isAvailable);
            data.writeString(ZSONObject.toZSONString(resultMap));
            for (IRemoteObject remoteObject : bleAdapterStateChangeCallbackSet) {
                sendRequest(remoteObject, BleOperatorConstants.BLE_SATE_CODE, data, reply, option);
            }
            reply.reclaim();
            data.reclaim();
        };
        BleHelper.getInstance().onBluetoothAdapterStateChange(callback);
    }

    /**
     * 关闭连接
     *
     * @param data data
     * @param reply reply
     * @param option option
     * @return code
     */
    public boolean closeBleConnection(MessageParcel data, MessageParcel reply, MessageOption option) {
        String dataString = data.readString();
        ZSONObject dataObject = ZSONObject.stringToZSON(dataString);
        String deviceId = String.valueOf(dataObject.get(DEVICE_ID));
        if (TextTool.isNullOrEmpty(deviceId)) {
            LogUtils.i("deviceId can not be null");
            return true;
        }
        String errorCode = BleHelper.getInstance().closeBleConnection(deviceId);
        return replyResult(reply, option, parseErrorCode(errorCode), new HashMap<>());
    }

    /**
     * 操控设备
     *
     * @param data data
     * @param reply reply
     * @param option option
     * @return code
     */
    public boolean controlDevice(MessageParcel data, MessageParcel reply, MessageOption option) {
        String dataString = data.readString();
        LogUtils.i("dataString:" + dataString);
        BleHelper.getInstance().controlDevice(dataString);
        return replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    /**
     * 蓝牙是否连接
     *
     * @param data data
     * @param reply reply
     * @param option option
     * @return code
     */
    public boolean isBleConnected(MessageParcel data, MessageParcel reply, MessageOption option) {
        boolean isConnected = BleHelper.getInstance().isBleConnected();
        HashMap<String, Object> map = new HashMap<>();
        map.put("isConnected", isConnected);
        return replyResult(reply, option, BasicConstants.SUCCESS, map);
    }

    /**
     * 检查蓝牙开关
     *
     * @param data data
     * @param reply reply
     * @param option option
     * @return code
     */
    public boolean checkBleSwitch(MessageParcel data, MessageParcel reply, MessageOption option) {
        boolean isOpen = BleHelper.getInstance().isBlueOpen();
        HashMap<String, Object> map = new HashMap<>();
        map.put("isOpen", isOpen);
        return replyResult(reply, option, BasicConstants.SUCCESS, map);
    }

    /**
     * 蓝牙连接
     *
     * @param data data
     * @param reply reply
     * @param option option
     * @return code
     */
    public boolean createBleConnected(MessageParcel data, MessageParcel reply, MessageOption option) {
        String dataString = data.readString();
        LogUtils.i("dataString deviceid:" + dataString);
        BleHelper.getInstance().createBleConnected(dataString);
        return replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    /**
     * 扫描蓝牙
     *
     * @param data data
     * @param reply reply
     * @param option option
     * @return code
     */
    public boolean startBluetoothDevicesDiscovery(MessageParcel data, MessageParcel reply, MessageOption option) {
        String dataString = data.readString();
        LogUtils.e("dataString =" +dataString);
        BluetoothDiscoveryParam param = ZSONObject.stringToClass(dataString, BluetoothDiscoveryParam.class);
        String errorCode = BleHelper.getInstance().startBluetoothDevicesDiscovery(
                                getUuidArrayByStringArray(param.getServices()),
                                param.getMacAddr(),
                                param.getInterval());
        LogUtils.i("errorCode = " + errorCode);
        int code = 0;
        try {
            code = Integer.parseInt("0");
        } catch (NumberFormatException e) {
            LogUtils.e("NumberFormatException");
        }
        return replyResult(reply, option, code, new HashMap<>());
    }

    /**
     * 获取蓝牙状态
     *
     * @param bleAdapterStateCallbackSet set
     */
    public void getBluetoothAdapterState(Set<IRemoteObject> bleAdapterStateCallbackSet) {
        BluetoothAdapterStateCallback callback = (isDiscovering, isAvailable, errorCode) -> {
            LogUtils.i("getBluetoothAdapterState isDiscovering:" + isDiscovering);
            LogUtils.i("getBluetoothAdapterState isAvailable:" + isAvailable);
            LogUtils.i("getBluetoothAdapterState errorCode:" + errorCode);
            MessageParcel data = MessageParcel.obtain();
            MessageOption option = new MessageOption();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(IS_DISCOVERING, isDiscovering);
            resultMap.put(IS_AVAILABLE, isAvailable);
            data.writeString(ZSONObject.toZSONString(resultMap));
            MessageParcel reply = MessageParcel.obtain();
            for (IRemoteObject remoteObject : bleAdapterStateCallbackSet) {
                sendRequest(remoteObject, errorCode, data, reply, option);
            }
            reply.reclaim();
            data.reclaim();
        };
        BleHelper.getInstance().getBluetoothAdapterState(callback);
    }

    private void sendRequest(
            IRemoteObject remoteObject, int errorCode, MessageParcel data, MessageParcel reply, MessageOption option) {
        if (remoteObject == null) {
            return;
        }
        try {
            remoteObject.sendRequest(errorCode, data, reply, option);
        } catch (RemoteException e) {
            LogUtils.e("RemoteException:" + e);
        }
    }

    /**
     * 回复JS调用
     *
     * @param reply reply
     * @param option option
     * @param code code
     * @param resultMap resultMap
     * @return code
     */
    public boolean replyResult(MessageParcel reply, MessageOption option, int code, Map<String, Object> resultMap) {
        if (option.getFlags() == MessageOption.TF_SYNC) {
            // SYNC
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(CODE, code);
            dataMap.put(DATA, resultMap);
            reply.writeString(ZSONObject.toZSONString(dataMap));
        } else {
            // ASYNC
            MessageParcel responseData = MessageParcel.obtain();
            responseData.writeString(ZSONObject.toZSONString(resultMap));
            IRemoteObject remoteReply = reply.readRemoteObject();
            try {
                remoteReply.sendRequest(code, responseData, MessageParcel.obtain(), new MessageOption());
                responseData.reclaim();
            } catch (RemoteException exception) {
                LogUtils.e("RemoteException = " + exception);
                return false;
            }
        }
        return true;
    }

    private UUID[] getUuidArrayByStringArray(String[] strings) {
        if (strings == null || strings.length == 0) {
            return new UUID[0];
        }
        UUID[] uuids = new UUID[strings.length];
        for (int i = 0; i < strings.length; i++) {
            uuids[i] = UUID.fromString(strings[i]);
        }
        return uuids;
    }

    private int parseErrorCode(String errorCode) {
        int code = BleOperatorConstants.ERROR_CODE_COMMON_ERR;
        try {
            code = Integer.parseInt(errorCode);
        } catch (NumberFormatException e) {
            LogUtils.e("NumberFormatException errorCode:" + errorCode);
        }
        return code;
    }
}
