/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License,Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qihoo.seat.fa.ble.subscriber;

import com.qihoo.seat.fa.ble.listener.BluetoothAdapterStateChangeCallback;
import com.qihoo.seat.fa.util.LogUtils;

import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.bluetooth.BluetoothHost;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventSubscribeInfo;
import ohos.event.commonevent.CommonEventSubscriber;
import ohos.utils.zson.ZSONObject;

/**
 * 蓝牙事件接收器
 *
 * @since 2021-08-12
 */
public class BluetoothAdapterStateSubscriber extends CommonEventSubscriber {
    private static final String TAG = "BluetoothAdapterStateSubscriber";

    private static final int STATE_OFF = 10;

    private static final int STATE_TURNING_ON = 11;

    private static final int STATE_ON = 12;

    private static final int STATE_TURNING_OFF = 13;

    private BluetoothAdapterStateChangeCallback bluetoothAdapterStateChangeCallback;

    private boolean isDiscovering;

    /**
     * constructor with subscribeInfo
     *
     * @param subscribeInfo subscribeInfo
     */
    public BluetoothAdapterStateSubscriber(CommonEventSubscribeInfo subscribeInfo) {
        super(subscribeInfo);
    }

    @Override
    public void onReceiveEvent(CommonEventData commonEventData) {
        if (commonEventData == null) {
            return;
        }
        Intent info = commonEventData.getIntent();
        LogUtils.i("intent data is = " + ZSONObject.toZSONString(info));
        if (info == null) {
            return;
        }
        LogUtils.i("bluetoothAdapterStateChangeCallback = " + bluetoothAdapterStateChangeCallback);
        if (bluetoothAdapterStateChangeCallback == null) {
            return;
        }
        dealWithActions(info);
    }

    /**
     * set callback of bluetooth adapter state change
     *
     * @param bluetoothAdapterStateChangeCallback callback
     */
    public void setBluetoothAdapterStateChangeCallback(
            BluetoothAdapterStateChangeCallback bluetoothAdapterStateChangeCallback) {
        this.bluetoothAdapterStateChangeCallback = bluetoothAdapterStateChangeCallback;
    }

    private void dealWithActions(Intent info) {
        if (info != null && info.getParams() != null && !info.getParams().isEmpty()) {
            IntentParams myParam = info.getParams();
            int currentState = getBluetoothState((int) myParam.getParam(BluetoothHost.HOST_PARAM_CUR_STATE));

            // Enabling or disabling Bluetooth, not reported
            if (currentState != BluetoothHost.STATE_TURNING_ON && currentState != BluetoothHost.STATE_TURNING_OFF) {
                boolean isAvailable = currentState == BluetoothHost.STATE_ON;
                switch (info.getAction()) {
                    case BluetoothHost.EVENT_HOST_STATE_UPDATE:
                        LogUtils.i(
                                TAG,
                                "BluetoothAdapterStateSubscriber onReceiveEvent : "
                                        + myParam.getParam(BluetoothHost.HOST_PARAM_CUR_STATE));
                        LogUtils.i(TAG, "BluetoothAdapterStateSubscriber currentState : " + isAvailable);
                        if (bluetoothAdapterStateChangeCallback != null) {
                            bluetoothAdapterStateChangeCallback.onBluetoothAdapterStateChange(
                                    isDiscovering, isAvailable);
                        }
                        break;
                    case BluetoothHost.EVENT_HOST_DISCOVERY_STARTED:
                        LogUtils.i(TAG, "BluetoothAdapterStateSubscriber EVENT_HOST_DISCOVERY_STARTED");
                        isDiscovering = true;
                        if (bluetoothAdapterStateChangeCallback != null) {
                            bluetoothAdapterStateChangeCallback.onBluetoothAdapterStateChange(
                                    isDiscovering, isAvailable);
                        }
                        break;
                    case BluetoothHost.EVENT_HOST_DISCOVERY_FINISHED:
                        LogUtils.i(TAG, "BluetoothAdapterStateSubscriber EVENT_HOST_DISCOVERY_FINISHED");
                        isDiscovering = false;
                        if (bluetoothAdapterStateChangeCallback != null) {
                            bluetoothAdapterStateChangeCallback.onBluetoothAdapterStateChange(
                                    isDiscovering, isAvailable);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private int getBluetoothState(int state) {
        switch (state) {
            case STATE_OFF:
                return BluetoothHost.STATE_OFF;
            case STATE_TURNING_ON:
                return BluetoothHost.STATE_TURNING_ON;
            case STATE_ON:
                return BluetoothHost.STATE_ON;
            case STATE_TURNING_OFF:
                return BluetoothHost.STATE_TURNING_OFF;
            default:
                return state;
        }
    }
}
