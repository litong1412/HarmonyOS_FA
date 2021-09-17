/**
* 在此文件中，修改增加状态以适配特定流程。
* 修改后，需要进入main/src/main/js/HwHealthOperators/component中，
* 将对应组件中的showState修改增加需要显示的对应状态
*/
const data ={
    pair:{
        PRIVACY: "PRIVACY",//隐私协议及登录页面
        PERMISSION_FORBIDDEN: "PERMISSION_FORBIDDEN", // 权限被禁止授予引导页面
        PAIR: "PAIR", // 配对页面
        PAIR_FAILED: "PAIR_FAILED", //配对失败页面
        REPAIR_REQUESTED: "REPAIR_REQUESTED", //配对失败，请求重连，同时
        PERMISSION_DENIED: "PERMISSION_DENIED",
    },
    measure:{
        measure:{
            NOT_MEASURED: "NOT_MEASURED",
            MEASURING:"MEASURING",
            MEASURE_FAILED:"MEASURE_FAILED",
            MEASURE_FINISHED:"MEASURE_FINISHED",
        },
        connection:{
            CONNECTED: "CONNECTED",
            DISCONNECTED:"DISCONNECTED",
            RECONNECT_REQUESTED:"RECONNECT_REQUESTED",
            RECONNECTING:"RECONNECTING",
        }
    }
}

export default data;