package com.qihoo.seat.fa;

import com.alibaba.fastjson.JSON;
import com.qihoo.seat.fa.ble.BleHelper;
import com.qihoo.seat.fa.util.CommonUtil;
import com.qihoo.seat.fa.util.LogUtils;
import com.qihoo.seat.fa.util.PermissionManager;
import com.qihoo.seat.fa.util.ToastUtil;
import com.qihoo.seat.fa.ResourceTable;
import ohos.aafwk.content.IntentParams;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.aafwk.content.Operation;
import ohos.ace.ability.AceAbility;
import ohos.agp.utils.TextTool;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import ohos.utils.zson.ZSONObject;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import java.io.IOException;

import java.util.Base64;

/**
 * MainAbility
 *
 * @since 2021-08-12
 */
public class MainAbility extends AceAbility {
    private static final int WINDOW_MODAL_CARD = 3;
    private static final String MAC_PARAM = "04:C3:E6:CE:FF:37";
    private static final String PRODUCTID_PARAM = "25HN";
    //todo 这里填写设备的mac地址
    private static final String MAC_ADDR = "";
    //todo XXXX这里修改为品类id,与share/common/img/customised/XXXX保持一致
    private static final String PROID = "XXXX";
    private static final String INTENT_PARAM = "window_modal";

    private PermissionManager permissionManager;
    private String[] permissionList = {"ohos.permission.LOCATION"};

    @Override
    public void onStart(Intent intent) {
        LogUtils.i("mainability intent params = " + ZSONObject.toZSONString(intent.getParams()));
//        String deviceId = intent.getStringParam("deviceId");
//        String uuid = intent.getStringParam("uuid");
//        boolean init = intent.getBooleanParam("init", false);
//        String bleMac = intent.getStringParam("bleMac");
//        String prodId = intent.getStringParam("prodId");
        CommonOperatorAbility.getInstance().setContext(this);
//        if (TextTool.isNullOrEmpty(bleMac) || TextTool.isNullOrEmpty(prodId)) {
//            ToastUtil.showToast(this, "未获取到Mac地址或pid");
//            terminateAbility();
//            return;
//        }
//        String mac = bleMac.replaceAll(".{2}(?=.)", "$0:").toUpperCase();
        intent.setParam(INTENT_PARAM, WINDOW_MODAL_CARD);
//        intent.setParam("macAddr", mac);
//        intent.setParam("productId", prodId);
        intent.setParam("macAddr", "04:C3:E6:CF:FF:3D");
        intent.setParam("productId", "25HN");
        setPageParams(null, intent.getParams());
//        if (intent.hasParameter("productInfo")) {
//            // Started by oneHop
//            intent.setParam(INTENT_PARAM, WINDOW_MODAL_CARD);
//            resolveMac(intent);
//            setPageParams(null, intent.getParams());
//        }else{
//            intent.setParam(INTENT_PARAM, WINDOW_MODAL_CARD);
//            intent.setParam("macAddr", "04:C3:E6:CE:FF:37");
//            intent.setParam("productId", "25HN");
//            setPageParams(null, intent.getParams());
//        }
//        init = true ;
//        if (init) {
//            CommonOperatorUtil.getInstance().initData(init, deviceId, uuid);
//        } else {
//            goToControl(mac, prodId);
//            terminateAbility();
//            return;
//        }

        super.onStart(intent);
        ResourceManager resourceManager = getResourceManager();
        if (!isBleOpen()) {
            try {
                ToastUtil.showToast(this, resourceManager.getElement(ResourceTable.String_public_bt_down).getString());
            } catch (IOException | NotExistException | WrongTypeException e) {
                LogUtils.e("BT disabled string is not defined");
            }
            terminateAbility();
            return;
        }
        if (!CommonUtil.checkNetwork(this)) {
            try {
                ToastUtil.showToast(
                        this, resourceManager.getElement(ResourceTable.String_public_network_down).getString());
            } catch (IOException | NotExistException | WrongTypeException e) {
                LogUtils.e("Network disabled string is not defined");
            }
            terminateAbility();
            return;
        }
        permissionManager = new PermissionManager(this);
        permissionManager.checkPermissions(permissionList, 0);
        LogUtils.e("111111111111111111111111");
    }

    private void goToControl(String mac, String pid) {
        Intent intent = new Intent();
        ZSONObject params = getParams(mac, pid);
        LogUtils.e("params = " + params.toString());
        intent.setParam("params", params.toString());
        Operation operation = new Intent.OperationBuilder().withDeviceId("")
                .withBundleName("com.qihoo.seat.fa")
                .withAbilityName("com.qihoo.seat.fa.ControlAbility")
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }

    private ZSONObject getParams(String mac, String pid) {
        ZSONObject params = new ZSONObject();
        params.put("macAddr", mac);
        params.put("productId", pid);
        return params;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isBleOpen() {
        return BleHelper.getInstance().isBlueOpen();
    }

    private void resolveMac(Intent intent) {
        if (intent != null && intent.hasParameter("businessInfo")) {
            IntentParams params = intent.getParams();
            Object businessInfo = params.getParam("businessInfo");
            String data91 = "";
            if (businessInfo != null) {
                ZSONObject businessInfoZson = ZSONObject.classToZSON(businessInfo);
                data91 = businessInfoZson.getZSONObject("params").getString("91");
            }
            byte[] data = Base64.getDecoder().decode(data91);
            String macAddr = byteToStr(data);
            String productInfo = getProductId(intent);
            LogUtils.e("macAddr = " + macAddr);
            LogUtils.e("productInfo = " + productInfo);
            intent.setParam(MAC_PARAM, macAddr);
            intent.setParam(PRODUCTID_PARAM, productInfo);
        }
    }

    private String getProductId(Intent intent) {
        String productInfo = intent.getStringParam("productInfo");
        return productInfo.substring(0, 4);
    }

    private String byteToStr(byte[] arrBytes) {
        String str = "";
        for (int i = 0; i < arrBytes.length; i++) {
            String tmp;
            byte num = arrBytes[i];
            if (num < 0) {
                tmp = Integer.toHexString(255 + num + 1);
            } else {
                tmp = Integer.toHexString(num);
            }
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            str += tmp;
            if (i != arrBytes.length - 1) {
                str += ":";
            }
        }
        return str.toUpperCase();
    }
}