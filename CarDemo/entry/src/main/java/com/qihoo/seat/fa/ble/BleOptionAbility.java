package com.qihoo.seat.fa.ble;

import com.qihoo.seat.fa.util.BasicConstants;
import com.qihoo.seat.fa.util.LogUtils;
import com.qihoo.seat.fa.ble.BleOptionHelper;
import ohos.ace.ability.AceInternalAbility;
import ohos.app.Context;
import ohos.rpc.IRemoteObject;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;

import java.lang.invoke.MutableCallSite;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * BleOptionAbility
 *
 * @since 2021-08-12
 */
public class BleOptionAbility extends AceInternalAbility {
    private static final String BUNDLENAME = "com.qihoo.seat.fa";
    private static final String ABILITYNAME = "BleOptionAbility";
    private Context mContext;
    private final Set<IRemoteObject> bleAdapterStateCallbackSet = new HashSet<>();
    private Optional<BleOptionHelper> bleOptionHelper;
    private Set<IRemoteObject> bleStateCallbackSet = new HashSet<>();
    private Set<IRemoteObject> bleDeviceInfoCallbackSet = new HashSet<>();
    private final Set<IRemoteObject> bleAdapterStateChangeCallbackSet = new HashSet<>();

    /**
     * 构造方法
     */
    public BleOptionAbility() {
        super(BUNDLENAME, ABILITYNAME);
    }

    private boolean onRemoteRequestCase(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        switch (code) {
            case BleOperatorConstants.GET_BLUETOOTH_ADAPTER_STATE:
                return getBluetoothAdapterStateCase(data, reply, option);
            case BleOperatorConstants.START_BLUETOOTH_DEVICES_DISCOVERY:
                return startBluetoothDevicesDiscoveryCase(data, reply, option);
            case BleOperatorConstants.GET_BLE_CONNECTION_STATE:
                return getBleConnectState(data, reply, option);
            case BleOperatorConstants.GET_DEVICE_STATE:
                return setDeviceUpdateListener(data, reply, option);
            case BleOperatorConstants.GET_DEVICE_INFO:
                return getDeviceInfo(data, reply, option);
            case BleOperatorConstants.CONTROL_DEVICE:
                return controlDevice(data, reply, option);
            case BleOperatorConstants.ON_BLUETOOTH_ADAPTER_STATE_CHANGE:
                return onBluetoothAdapterStateChangeCase(data, reply, option);
            case BleOperatorConstants.IS_BLE_CONNECTED:
                return isBleConnected(data, reply, option);
            case BleOperatorConstants.CREATE_BLE_CONNECTION:
                return createBleConnected(data, reply, option);
            case BleOperatorConstants.CHECK_BLE_SWITCH:
                return checkBleSwitch(data, reply, option);
            case BleOperatorConstants.CLOSE_BLE_CONNECTION:
                return closeBleConnectionCase(data, reply, option);
            default:
                break;
        }
        return true;
    }

    private boolean closeBleConnectionCase(MessageParcel data, MessageParcel reply, MessageOption option) {
        return bleOptionHelper.get().closeBleConnection(data, reply, option);
    }

    private boolean checkBleSwitch(MessageParcel data, MessageParcel reply, MessageOption option) {
        return bleOptionHelper.get().checkBleSwitch(data, reply, option);
    }

    private boolean createBleConnected(MessageParcel data, MessageParcel reply, MessageOption option) {
        return bleOptionHelper.get().createBleConnected(data, reply, option);
    }

    private boolean isBleConnected(MessageParcel data, MessageParcel reply, MessageOption option) {
        return bleOptionHelper.get().isBleConnected(data, reply, option);
    }

    private boolean onBluetoothAdapterStateChangeCase(MessageParcel data, MessageParcel reply, MessageOption option) {
        bleAdapterStateChangeCallbackSet.clear();
        bleAdapterStateChangeCallbackSet.add(data.readRemoteObject());
        bleOptionHelper.get().onBluetoothAdapterStateChange(bleAdapterStateChangeCallbackSet);
        return bleOptionHelper.get().replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    private boolean controlDevice(MessageParcel data, MessageParcel reply, MessageOption option) {
        return bleOptionHelper.get().controlDevice(data, reply, option);
    }

    private boolean getDeviceInfo(MessageParcel data, MessageParcel reply, MessageOption option) {
        bleOptionHelper.get().getDeviceInfo();
        return bleOptionHelper.get().replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    private boolean setDeviceUpdateListener(MessageParcel data, MessageParcel reply, MessageOption option) {
        bleDeviceInfoCallbackSet.clear();
        bleDeviceInfoCallbackSet.add(data.readRemoteObject());
        bleOptionHelper.get().setDeviceUpdateLinstener(bleDeviceInfoCallbackSet);
        return bleOptionHelper.get().replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    private boolean getBleConnectState(MessageParcel data, MessageParcel reply, MessageOption option) {
        bleStateCallbackSet.clear();
        bleStateCallbackSet.add(data.readRemoteObject());
        bleOptionHelper.get().setConnectStateLinstener(bleStateCallbackSet);
        return bleOptionHelper.get().replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    private boolean startBluetoothDevicesDiscoveryCase(MessageParcel data, MessageParcel reply, MessageOption option) {
        return bleOptionHelper.get().startBluetoothDevicesDiscovery(data, reply, option);
    }

    private boolean getBluetoothAdapterStateCase(MessageParcel data, MessageParcel reply, MessageOption option) {
        bleAdapterStateCallbackSet.clear();
        bleAdapterStateCallbackSet.add(data.readRemoteObject());
        bleOptionHelper.get().getBluetoothAdapterState(bleAdapterStateCallbackSet);
        return bleOptionHelper.get().replyResult(reply, option, BasicConstants.SUCCESS, new HashMap<>());
    }

    /**
     * 注册
     *
     * @param context 上下文对象
     */
    public void register(Context context) {
        LogUtils.e("register register register");
        this.mContext = context;
        if (bleOptionHelper == null) {
            LogUtils.e("coming in");
            BleOptionHelper helper = new BleOptionHelper(mContext);
            bleOptionHelper = Optional.ofNullable(helper);
        }
        LogUtils.e("1111111111111111");
        setInternalAbilityHandler((i, messageParcel, messageParcel1, messageOption) ->
                        onRemoteRequestCase(i, messageParcel, messageParcel1, messageOption));
        LogUtils.e("22222222");
    }

    /**
     * 反注册
     */
    public void unRegister() {
        LogUtils.e("unRegister unRegister unRegister");
        this.mContext = null;
        setInternalAbilityHandler(null);
    }

    /**
     * 获取单例对象
     *
     * @return BleOptionAbility
     */
    public static BleOptionAbility getInstance() {
        return BleOptionAbilityHelper.INSTANCE;
    }

    /**
     * BleOptionAbilityHelper
     *
     * @since 2021-08-12
     */
    private static class BleOptionAbilityHelper {
        private static final BleOptionAbility INSTANCE = new BleOptionAbility();
    }
}
