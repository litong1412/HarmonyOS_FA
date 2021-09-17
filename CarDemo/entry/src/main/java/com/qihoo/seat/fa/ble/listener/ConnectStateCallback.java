package com.qihoo.seat.fa.ble.listener;

/**
 * ConnectStateCallback
 *
 * @since 2021-08-12
 */
public interface ConnectStateCallback {
    /**
     * 连接状态变化
     *
     * @param state state
     * @param desc desc
     */
    void stateChange(int state, String desc);
}
