import Log from '../util/log.js'
// abilityType: 0-Ability; 1-Internal Ability
const ABILITY_TYPE_EXTERNAL = 0;
const ABILITY_TYPE_INTERNAL = 1;

// syncOption: 0-Sync; 1-Async
const ACTION_SYNC = 0;
const ACTION_ASYNC = 1;

// Java端入口
const BUNDLE_NAME = "com.qihoo.seat.fa";
import prompt from '@system.prompt';
const ABILITY_NAME = "BleOptionAbility";
const GET_BLUETOOTH_ADAPTER_STATE = 1003;
const ON_BLUETOOTH_ADAPTER_STATE_CHANGE = 1004;
const START_BLUETOOTH_DEVICES_DISCOVERY = 1005;
const STOP_BLUETOOTH_DEVICES_DISCOVERY = 1006;
const ON_BLUETOOTH_DEVICE_FOUND = 1007;
const CREATE_BLE_CONNECTION = 1008;
const CLOSE_BLE_CONNECTION = 1009;
const ON_BLE_CONNECTION_STATE_CHANGE = 1010;
const GET_DEVICE_ID = 1011;
const ON_BLE_SERVICES_DISCOVERED = 1012;
const READ_BLE_CHARACTERISTIC_VALUE = 1013;
const WRITE_BLE_CHARACTERISTIC_VALUE = 1014;
const ON_BLE_CHARACTERISTIC_VALUE_CHANGE = 1015;
const NOTIFY_BLE_CHARACTERISTIC_VALUE_CHANGE = 1016;
const SET_ENABLE_INDICATION = 1017;
const ON_ENABLE_NOTIFY_INDICATE = 1020;
const READ_BLE_DESCRIPTOR_VALUE = 1023;
const WRITE_BLE_DESCRIPTOR_VALUE = 1024;
const GET_BLE_CONNECTION_STATE = 1025;
const GET_DEVICE_STATE = 1026;
const GET_DEVICE_INFO = 1027;
const CONTROL_DEVICE = 1028;
var getRequestAction = function (requestCode) {
    return {
        bundleName: BUNDLE_NAME,
        abilityName: ABILITY_NAME,
        abilityType: ABILITY_TYPE_INTERNAL,
        syncOption: ACTION_SYNC,
        messageCode: requestCode,
    };
}

export default {
    // 获取蓝牙适配器状态
    getBluetoothAdapterState: async function(callback) {
        let action = getRequestAction(GET_BLUETOOTH_ADAPTER_STATE);
        Log.info("action = " + JSON.stringify(action))
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            Log.info("getBluetoothAdapterState is " + JSON.stringify(callbackData))
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        })

        Log.info('getBluetoothAdapterState result is:')
        Log.info('getBluetoothAdapterState result is:' + resultStr)
        let resultObj = JSON.parse(resultStr);

        if (resultObj.code == 2004) {
            prompt.showToast({
                message: "获取蓝牙适配器状态失败",
                duration: 1000
            })
        }

        return resultObj;
    },
    onBluetoothAdapterStateChange: async function(callback){
        let action = getRequestAction(ON_BLUETOOTH_ADAPTER_STATE_CHANGE);
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {

            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });
        Log.info('onBluetoothAdapterStateChange result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    // 启动蓝牙设备发现
    startBluetoothDevicesDiscovery: async function(services, macAddr, interval) {
        let action = getRequestAction(START_BLUETOOTH_DEVICES_DISCOVERY);
        let actionData = {};
        actionData.services = services;
        actionData.macAddr = macAddr;
        actionData.interval = interval;
        action.data = actionData;

        let resultStr = await FeatureAbility.callAbility(action);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    // 停止蓝牙设备发现
    stopBluetoothDevicesDiscovery: async function(){
        let action = getRequestAction(STOP_BLUETOOTH_DEVICES_DISCOVERY);
        let resultStr = await FeatureAbility.callAbility(action);
        Log.info('stopBluetoothDevicesDiscovery result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    onBluetoothDeviceFound: async function(callback){
        let action = getRequestAction(ON_BLUETOOTH_DEVICE_FOUND);
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });
        Log.info('onBluetoothDeviceFound result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    createBleConnection: async function(deviceId) {
        let action = getRequestAction(CREATE_BLE_CONNECTION);
        let actionData = {};
        Log.info("bleOperator createBleConnection deviceId = " + deviceId);
        actionData.deviceId = deviceId.toUpperCase();
        action.data = actionData;

        let resultStr = await FeatureAbility.callAbility(action);
        Log.info('createBleConnection result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);

        return resultObj;
    },
    closeBleConnection: async function(deviceId) {
        let action = getRequestAction(CLOSE_BLE_CONNECTION);
        let actionData = {};
        Log.info("bleOperator closeBleConnection deviceId = " + deviceId);
        actionData.deviceId = deviceId.toUpperCase();
        action.data = actionData;

        let resultStr = await FeatureAbility.callAbility(action);
        Log.info('closeBleConnection result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    onBleConnectionStateChange: async function(callback) {
        let action = getRequestAction(ON_BLE_CONNECTION_STATE_CHANGE);
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });
        Log.info('onBleConnectionStateChange result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    onBleCharacteristicValueChange: async function(callback) {
        let action = getRequestAction(ON_BLE_CHARACTERISTIC_VALUE_CHANGE);
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });
        Log.info('onBleCharacteristicValueChange result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    onBleServicesDiscovered: async function(callback){
        let action = getRequestAction(ON_BLE_SERVICES_DISCOVERED);
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });
        Log.info('onBleServicesDiscovered result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    setEnableIndication: async function(isEnableIndication) {
        let action = getRequestAction(SET_ENABLE_INDICATION);
        let actionData = {};
        actionData.isEnableIndication = isEnableIndication;
        action.data = actionData;

        let resultStr = await FeatureAbility.callAbility(action);
        Log.info('setEnableIndication result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    notifyBleCharacteristicValueChange: async function(deviceId, serviceId, characteristicId, state) {
        let action = getRequestAction(NOTIFY_BLE_CHARACTERISTIC_VALUE_CHANGE);
        let actionData = {};
        Log.info("bleOperator notifyBleCharacteristicValueChange deviceId = " + deviceId);
        actionData.deviceId = deviceId.toUpperCase();
        actionData.serviceId = serviceId;
        actionData.characteristicId = characteristicId;
        actionData.state = state;
        action.data = actionData;

        let resultStr = await FeatureAbility.callAbility(action);
        Log.info('notifyBleCharacteristicValueChange result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    setStateListener: async function(callback){
        Log.info('setStateListener result is:' + 123456);
        let action = getRequestAction(GET_BLE_CONNECTION_STATE)
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackdata => {
            let callbackJson = JSON.parse(callbackdata);
            callback(callbackJson);
        })

        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    setDeviceInfoListener: async function(callback){
        let action = getRequestAction(GET_DEVICE_STATE)
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackdata => {
            let callbackJson = JSON.parse(callbackdata);
            callback(callbackJson);
        })
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    getDeviceInfo: async function(){
        let action = getRequestAction(GET_DEVICE_INFO)
        Log.info("action = " + action)
        let resultStr = await FeatureAbility.callAbility(action)
        Log.info('getDeviceInfo result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    controlDevice: async function(data){
        let action = getRequestAction(CONTROL_DEVICE)
        Log.info("control device")
        action.data = data
        let resultStr = await FeatureAbility.callAbility(action)
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    }
}