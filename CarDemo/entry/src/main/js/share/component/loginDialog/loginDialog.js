import app from '@system.app';
import storage from '@system.storage';
import prompt from '@system.prompt';
import HMSAccount from "@hmscore/hms-js-account";
import config from '../../common/util/config.js'
import constants from '../../common/util/constants.js'
import configuration from '@system.configuration'
//import commonOperator from '../../../HwHealthOperators/commonOperator.js'
//import dataOperator from '../../../HwHealthOperators/dataOperator.js'
import state from '../../common/util/state.js'
import Log from '../../common/util/log.js'
// index.js
import router from '@system.router';

const showState = [state.pair.PRIVACY]
const JS_TAG = "JS/Component/Login dialog: ";

export default {
    props: ["state"],
    data: {
        checked: false,
        version: ""
    },
    computed: {
        showObj() {
            return showState.indexOf(this.state) > -1;
        }
    },
    async onInit() {
        let info = app.getInfo();
        this.version = info.versionName;
        await this.checkPrivacy()
        Log.error("3333333333")
    },
    checkPrivacy: function () {
        storage.get({
            key: 'privacy',
            success: data => {
                if (data == 'true') {
                    Log.info(JS_TAG + 'Privacy has been agreed.')
                    this.$emit("onPrivacyAgreed");
                    this.checkLoginStatus();
                } else {
                    Log.info(JS_TAG + 'Privacy is not agreed.');
                }
            },
            fail: (data, code) => {
                Log.info(JS_TAG + 'Check privacy data failed, error code:' + code + ', data: ' + data);
            },
            complete: () => {
                Log.info(JS_TAG + 'Check privacy finished');
            }
        });
    },
    agreePrivacy: function () {
        Log.info(JS_TAG + 'Privacy agree is clicked, start saving privacy agree status.');
        storage.set({
            key: 'privacy',
            value: 'true',
            success: () => {
                Log.info(JS_TAG + 'Privacy agree status is saved.')
            },
            fail: (data, code) => {
                Log.error(JS_TAG + 'Save privacy agree status failed, error code:' + code + ', data: ' + data);
            },
            complete: () => {
                Log.info(JS_TAG + 'Save privacy agree status finished.');
            }
        });
    },
    login() {
        Log.info(JS_TAG + 'Login start');
        HMSAccount.signIn(constants.loginPermissions, config.scopeList)
            .then(this.processLogin)
            .catch(this.processLoginError);
    },
    checkLoginStatus() {
        Log.info(JS_TAG + 'Check login status start');
        HMSAccount.silentSignIn(constants.loginPermissions, config.scopeList)
            .then(this.processLogin)
            .catch(this.processLoginError);
    },
    processLogin(loginResult) {
        Log.info(JS_TAG + 'Sign in success, result:' + JSON.stringify(loginResult));
        HMSAccount.containScopes(loginResult.data, config.scopeList)
            .then((resultAuth) => {
            Log.info(JS_TAG + 'Authentication verification success! All scopes granted: ' + JSON
            .stringify(resultAuth.data));
            let resultObj = {}
            resultObj.allScopeGranted = resultAuth.data;
            resultObj.authCode = loginResult.data.serverAuthCode;
            this.agreePrivacy();
            this.initAuth(loginResult.data.serverAuthCode)
            this.$emit('onLoginSuccess', resultObj);
        })
            .catch((error) => {
            Log.info(JS_TAG + 'Get authentication result fail: ' + error);
            prompt.showToast({
                message: this.$t("strings.systemError"),
                duration: config.toast_duration
            });
            this.$emit('onLoginError');
        });
    },
    processLoginError(loginError) {
        Log.error('Sign in fail, result: ' + JSON.stringify(loginError));
        switch (loginError.resultCode) {
            case constants.loginErrorCode.SIGN_IN_AUTH:
                prompt.showToast({
                    message: this.$t("strings.public_logged_out"),
                    duration: config.toast_duration
                });
                break;
            case constants.loginErrorCode.SIGN_IN_NETWORK_ERROR:
            case constants.loginErrorCode.SIGN_IN_AUTH_SERVER_FAILED:
                prompt.showToast({
                    message: this.$t("strings.networkError"),
                    duration: config.toast_duration
                });
                break;
            case constants.loginErrorCode.FAILED:
            case constants.loginErrorCode.SIGN_IN_CANCELLED:
            // The user taps the back button to cancel the sign-in.
                this.changeState(state.pair.PRIVACY, state.pair.PAIR);
                break;
            default:
                prompt.showToast({
                    message: this.$t("strings.systemError"),
                    duration: config.toast_duration
                });
        }
        this.$emit('onLoginError', loginError);
    },
    initAuth(authCode) {
        Log.info(JS_TAG + 'Init auth code:' + authCode);
        //        dataOperator.initAuthCode(authCode,callbackJson=>{
        //            console.info(JS_TAG + "initAuth:" + JSON.stringify(callbackJson));
        //        })
    },
    onCheckboxChange(status) {
        this.checked = status.checked;
    },
    openUrl() {
        Log.info(JS_TAG + "openPrivacyUrl");
        //        commonOperator.openUrl(config.privacyUrl);
    },
    exit() {
        Log.info("app terminate3")
        app.terminate();
    }
}