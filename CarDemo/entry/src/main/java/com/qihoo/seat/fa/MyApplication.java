package com.qihoo.seat.fa;

import com.qihoo.seat.fa.ble.BleOptionAbility;
import com.qihoo.seat.fa.ble.BleHelper;;
import com.qihoo.seat.fa.util.LogUtils;
import com.huawei.hms.jsb.adapter.har.bridge.HmsBridge;
import ohos.aafwk.ability.AbilityPackage;

/**
 * MyApplication
 *
 * @since 2021-08-21
 */
public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        HmsBridge.getInstance().initBridge(this);
        BleOptionAbility.getInstance().register(this);
        CommonOperatorAbility.register(this);
    }

    @Override
    public void onEnd() {
        super.onEnd();
        BleOptionAbility.getInstance().unRegister();
        BleHelper.getInstance().disConnect();
    }
}
