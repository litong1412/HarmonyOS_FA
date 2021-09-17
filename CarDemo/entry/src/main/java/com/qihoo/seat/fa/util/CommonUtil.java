package com.qihoo.seat.fa.util;

import ohos.aafwk.ability.Ability;
import ohos.app.Context;
import ohos.net.NetManager;

/**
 * CommonUtil
 *
 * @since 2021-08-12
 */
public class CommonUtil {
    /**
     * 检查网络
     *
     * @param context 上下文对象
     * @return true false
     */
    public static boolean checkNetwork(Context context) {
        return NetManager.getInstance(context).getAllNets().length > 0;
    }
}
