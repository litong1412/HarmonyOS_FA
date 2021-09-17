import Log from '../util/log.js'
// abilityType: 0-Ability; 1-Internal Ability
const ABILITY_TYPE_EXTERNAL = 0;
const ABILITY_TYPE_INTERNAL = 1;

// syncOption(Optional, pair sync): 0-Sync; 1-Async
const ACTION_SYNC = 0;
const ACTION_ASYNC = 1;

// Java side register info
const BUNDLE_NAME = "com.qihoo.seat.fa";
const ABILITY_NAME = "com.qihoo.seat.fa.CommonOperatorAbility";

//const BUNDLE_NAME = "com.qihoo.seat.fa";
//const ABILITY_NAME = "CommonOperatorAbility";

// Return results
const SUCCESS = 200;
const SUCCESS_MESSAGE = "Success";
const ERROR = 201;
const ERROR_MESSAGE = "Service not defined";

// Operation Code
const CHECK_PERMISSION = 2000;
const REQUEST_PERMISSION = 2010;
const START_ABILITY = 2020;
const CHECK_PACKAGE_INSTALLATION = 2030;
const OPEN_URL = 2040;
const GET_UNIQUE_ID = 2050;
const TAG = 'JS/Operator/Common: '

var getRequestAction = function (requestCode) {
    return {
        bundleName: BUNDLE_NAME,
        abilityName: ABILITY_NAME,
        abilityType: ABILITY_TYPE_INTERNAL,
        syncOption: ACTION_SYNC,
        messageCode: requestCode,
    };
};
export default {
    data: {},
    startAbility: async function(bundleName, abilityName, callback, params, options){
        let actionData = {};
        actionData.bundleName = bundleName;
        actionData.abilityName = abilityName;
        actionData.params = params;
        actionData.options = options;

        let action = getRequestAction(START_ABILITY);
        action.data = actionData;
        Log.info(JSON.stringify(action))
        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });

        Log.info(TAG + 'Start Ability result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    checkPackageInstallation: async function(bundleName, callback){
        let actionData = {};
        actionData.bundleName = bundleName;

        let action = getRequestAction(CHECK_PACKAGE_INSTALLATION);
        action.data = actionData;

        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });

        Log.info(TAG + 'Check Package Installation result is:' + resultStr);
        let resultObj = JSON.parse(resultStr);
        return resultObj;
    },
    openUrl: async function(url, callback){
        let actionData = {};
        actionData.url = url;

        if (actionData.url == null || actionData.url == "") {
            Log.info(TAG + 'actionData.url is empty or null:' + JSON.stringify(actionData.url));
            return;
        }
        let action = getRequestAction(OPEN_URL);
        action.data = actionData;

        let resultStr = await FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            let callbackJson = JSON.parse(callbackData);
            callback(callbackJson);
        });
        let resultObj = JSON.parse(resultStr);
        Log.info(TAG + 'OpenUrl result is:' + JSON.stringify(resultObj));
    },
    getUniqueId:  function(callback){
        Log.info(TAG + 'get uniqueId');
        let action = getRequestAction(GET_UNIQUE_ID);

        let resultStr = FeatureAbility.subscribeAbilityEvent(action, callbackData => {
            try {
                let callbackJson = JSON.parse(callbackData);
                callback(callbackJson);
            } catch (e) {
                Log.error(TAG + "callbackJson get unique e =" + e);
            }
        });
        try {
            let resultObj = JSON.parse(resultStr);
            Log.info(TAG + 'get uniqueId result is:' + JSON.stringify(resultObj));
        } catch (e) {
            Log.error(TAG + "resultObj get unique e =" + e);
        }
    }
}