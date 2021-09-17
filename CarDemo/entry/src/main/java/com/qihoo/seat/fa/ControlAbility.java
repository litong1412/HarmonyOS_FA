package com.qihoo.seat.fa;

import com.qihoo.seat.fa.ble.BleHelper;
import com.qihoo.seat.fa.util.LogUtils;
import ohos.ace.ability.AceAbility;
import ohos.aafwk.content.Intent;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

/**
 * ControlAbility
 *
 * @since 2021-08-12
 */
public class ControlAbility extends AceAbility {
    @Override
    public void onStart(Intent intent) {
        intent.setParam("window_modal", 1);
        setInstanceName("control");
        setPageParams(null, intent.getParams());
        super.onStart(intent);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onInactive() {
        super.onInactive();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.e("onStop");
        BleHelper.getInstance().disConnect();
    }
}
