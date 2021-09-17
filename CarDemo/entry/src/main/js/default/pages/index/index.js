import state from '../../../share/common/util/state.js'
import Log from '../../../share/common/util/log.js'
import bleOperator from '../../../share/common/ble/bleOperator.js'
import config from '../../../share/common/util/config.js'
import app from '@system.app';
import commonOperator from '../../../share/common/util/commonOperator.js'

export default {
    data: {
        title: "",
        state: state.pair.PRIVACY,
        connectTimeout: 0
    },
    onInit() {
        Log.error("222222222222222222222")
        this.initListener()
        Log.info("produceId = " +this.productId)
        Log.info("macAddr = " +this.macAddr)
    },
    initListener() {
        bleOperator.setStateListener(this.bleConnectState)
        bleOperator.onBluetoothAdapterStateChange(this.onBluetoothAdapterStateChange);
    },
    bleConnectState(callback) {
        Log.info("bleConnectState:" + JSON.stringify(callback))

        if (callback.data.state == 1) {
            this.changeState(state.pair.PAIR_FAILED, state.pair.PAIR);
        } else if (callback.data.state == 2) {
            Log.info('弹出fa页面9999999999999999999999')

            commonOperator.startAbility(config.bundleName, config.abilityName, callbackJson => {
                Log.info("app terminate 1234567891012345678910")
                app.terminate();
            }, {
                macAddr: this.macAddr,
                productId: this.productId
            },{
                flags:276826112
            });
        }
    },
    onBluetoothAdapterStateChange(callbackJson) {
        Log.info('Adapter state changed:' + JSON.stringify(callbackJson));
        if (callbackJson.data.isAvailable == false) {
            if (this.state == state.pair.PAIR) {
                this.changeState(state.pair.PAIR_FAILED, state.pair.PAIR);
                clearTimeout(this.connectTimeout);
                bleOperator.stopBluetoothDevicesDiscovery();
            }
        } else {
            Log.info('Adapter state ' + this.state);
            if (this.state == state.pair.REPAIR_REQUESTED) {
                this.changeState(state.pair.PAIR, state.pair.REPAIR_REQUESTED);
                this.connectToDevice();
            }
        }
    },
// 点击重新联网时触发
    reConnect() {
        Log.info('Reconnect started, device mac address: ' + this.macAddr);
        if (config.scanRequired) {
            bleOperator.getBluetoothAdapterState(this.onCheckAdapterState);
        } else {
            this.changeState(state.pair.PAIR, state.pair.PAIR_FAILED)
            this.connectToDevice();
        }

    },
    onLoginSuccess(resultObj) {
        Log.info('Login success:' + JSON.stringify(resultObj))
        if (resultObj.detail.allScopeGranted) {
            Log.info('All scope granted.')
            this.changeState(state.pair.PAIR, state.pair.PRIVACY)
            bleOperator.getBluetoothAdapterState(this.onCheckAdapterState)
        } else {
            Log.info('Not all scope granted.');
            this.changeState(state.pair.PRIVACY);
        }
    },
    onLoginError(resultObj) {
        this.changeState(state.pair.PAIR, state.pair.PRIVACY)
    },
// 获取蓝牙适配器状态的回调函数
    onCheckAdapterState(callbackJson) {
        Log.info('Bluetooth adapter state is:' + JSON.stringify(callbackJson))
        if (callbackJson.data.isAvailable == true) {
            this.changeState(state.pair.PAIR, state.pair.PAIR_FAILED)
            this.onAdapterAvailable();
        } else {
            this.changeState(state.pair.REPAIR_REQUESTED, state.pair.PAIR_FAILED);
            bleOperator.openBluetoothAdapter();
        }
    },
// 这个是demo代码
    async onAdapterAvailable() {
        Log.info('onAdapterAvailable scanRequired=' + config.scanRequired)
        if (config.scanRequired) {
            Log.info('start......111111111')
            if (this.isBleConnect()) {
                Log.info('start......')

                this.scanDevice();
            } else {
                Log.info('start......222222222222')
                this.scanDevice();
            }
        } else {
            this.connectToDevice();
        }
    },
//    ------------下面是我的代码
//    onAdapeterAvailable() {
//        Log.info("onAdapeterAvailable scanRequired=" + config.scanRequired)
//        if (config.scanRequired) {
//            this.scanDevice();
//        } else {
//            this.connectToDevice();
//        }
//    },
    async isBleConnect(){
        let result = await bleOperator.isBleConnected()
        Log.info('code = ' + result.code)
        Log.info('isConnected = ' + result.data.isConnected)
        if (result.code == 0) {
            let isConnected = result.data.isConnected
            return isConnected
        } else {
            return false
        }
    },
    connectToDevice: function () {
        // connect to device directly
        bleOperator.createBleConnection(this.macAddr);
    },
    onConnectTimeout() {
        Log.info("app terminate1")
        app.terminate()
    },



// 扫描设备
    scanDevice() {
        // scan device for connection
        let interval = 10;
        Log.info('startBluetoothDevicesDiscovery2222222222222')
        bleOperator.startBluetoothDevicesDiscovery(config.bleScanFilterServices, this.macAddr, interval);
    },
    onPrivacyAgreed() {
        /**
         * Event from login dialog, indicating the privacy status is agreed,
         * change state to PAIR, wait for login status
         */
        this.changeState(state.pair.PAIR, state.pair.PRIVACY);
    },
    changeState(newState, ...oldStates) {
        Log.info('Trying changing state from: ' + oldStates + ' to: ' + newState)
        if (oldStates == undefined || oldStates.length == 0 || oldStates.indexOf(this.state) > -1) {
            Log.info('State changed from: ' + this.state + ' to: ' + newState)
            this.state = newState
        }
    }
}
