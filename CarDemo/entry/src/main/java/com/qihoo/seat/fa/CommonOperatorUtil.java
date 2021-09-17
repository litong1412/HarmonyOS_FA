package com.qihoo.seat.fa;

import com.qihoo.seat.fa.util.LogUtils;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.Context;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;

public class CommonOperatorUtil {

    private boolean isInit = false;
    private boolean hasDeviceId = false;
    private String deviceId;
    private String uuid;

    public void initData(boolean isInit, String deviceId, String uuid) {
        this.isInit = isInit;
        this.deviceId = deviceId;
        this.uuid = uuid;
        if (deviceId == null || deviceId == "") {
            hasDeviceId = false;
            return;
        }
        hasDeviceId = true;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isGoHilinkRegister() {
        if (isInit && !hasDeviceId) {
            return true;
        }
        return false;
    }

    public void goHilinkRegister(Context context) {
        if (deviceId == null || "".equals(deviceId)) {
            Intent intent = new Intent();
            intent.setParam("uuid", getUuid());
            //todo 这里的prodid改为设备的pid
            //todo com.qihoo.seat.fa 修改为自己的包名
            String uri = "hilink://hilinksvc.huawei.com/device?action=deviceAdd&prodId=XXXX&fromApp=com.qihoo.seat.fa";
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withFlags(Intent.FLAG_ABILITY_NEW_MISSION | Intent.FLAG_NOT_OHOS_COMPONENT)
                    .withAction("android.intent.action.VIEW")
                    .withUri(Uri.parse(uri)).build();
            intent.setOperation(operation);
            LogUtils.e("zson data = " + ZSONObject.toZSONString(intent));
            context.startAbility(intent, 0);
        }
    }

    public static CommonOperatorUtil getInstance() {
        return CommonOperatorHelper.INSTANCE;
    }

    private static class CommonOperatorHelper {
        private static CommonOperatorUtil INSTANCE = new CommonOperatorUtil();
    }
}
