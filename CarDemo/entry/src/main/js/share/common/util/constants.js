const data = {
    loginErrorCode: {
        FAILED: -1,
        SIGN_IN_AUTH: 2002,
        SIGN_IN_NETWORK_ERROR: 2005,
        SIGN_IN_AUTH_SERVER_FAILED: 2009,
        SIGN_IN_CANCELLED: 2012
    },
    loginPermissions: [
            "https://www.huawei.com/auth/account/base.profile/accesstoken",
            "idtoken",
            "https://www.huawei.com/auth/account/base.profile/serviceauthcode"
    ],
    settingBundleName: "com.android.settings",
    settingActivityName: "com.android.settings.HWSettings",
    settingAction:"android.settings.APPLICATION_DETAILS_SETTINGS",
}

export default data