export const BUNDLE_NAME = "com.qihoo.seat.fa"
export function goToAbility(abilityName, dataInfo, flag) {
    var action = {};
    action.bundleName = BUNDLE_NAME;
    action.flag = flag;
    action.abilityName = abilityName
    action.data = dataInfo;
    //@ts-ignore
    FeatureAbility.startAbility(action);
}

//字节数组转十六进制字符串，对负值填坑
export function Bytes2HexString(arrBytes) {
    var str = "";
    for (var i = 0; i < arrBytes.length; i++) {
        var tmp;
        var num = arrBytes[i];
        if (num < 0) {
            //此处填坑，当byte因为符合位导致数值为负时候，需要对数据进行处理
            tmp = (255 + num + 1).toString(16);
        } else {
            tmp = num.toString(16);
        }
        if (tmp.length == 1) {
            tmp = "0" + tmp;
        }
        str += tmp;
    }
    return str;
}