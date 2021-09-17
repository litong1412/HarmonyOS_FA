const data = {
    scopeList: ["https://www.huawei.com/auth/account/base.profile",
        "openid",
    ],
    scanRequired:true,
    bundleName: "com.qihoo.seat.fa",
    abilityName: "com.qihoo.seat.fa.ControlAbility",
    pairTimeout:10000,
    bleScanFilterServices:["01000100-0000-1000-8000-009078563412"],
    permissions: ["ohos.permission.USE_BLUETOOTH",
        "ohos.permission.LOCATION",
        "ohos.permission.LOCATION_IN_BACKGROUND",
        "ohos.permission.DISCOVER_BLUETOOTH"],
    mandatoryPermissions: ["ohos.permission.LOCATION"],
    toast_duration: 5000,
//    NOTIFY_SERVICE_UUID:"0000fda0-0000-1000-8000-00805f9b12ea",
//    CHARACTERISTIC_NOTIFY_UUID:"0000fda5-0000-1000-8000-00805f9b12ea",
    deviceImgStyle:"square",
}

export default data