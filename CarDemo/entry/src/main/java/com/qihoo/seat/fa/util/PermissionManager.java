package com.qihoo.seat.fa.util;

import ohos.app.Context;
import ohos.bundle.IBundleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionManager
 *
 * @since 2021-08-12
 */
public class PermissionManager {
    private final Context mContext;

    /**
     * 构造方法
     *
     * @param context 上下文对象
     */
    public PermissionManager(Context context) {
        this.mContext = context;
    }

    /**
     * checkPermissions
     *
     * @param permissions permission
     * @param requestCode requestCode
     */
    public void checkPermissions(String[] permissions, int requestCode) {
        if (permissions.length > 0) {
            mContext.requestPermissionsFromUser(permissions, requestCode);
        }
    }

    /**
     * verifySelfPermission
     *
     * @param permissions permissions
     */
    public void verifySelfPermission(String[] permissions) {
        List<String> unVerifyPermission = new ArrayList<>();
        if (permissions.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (mContext.verifySelfPermission(permissions[i]) != IBundleManager.PERMISSION_GRANTED) {
                    unVerifyPermission.add(permissions[i]);
                }
            }
            if (unVerifyPermission.size() > 0) {
                checkPermissions(listToArray(unVerifyPermission), 0);
            }
        }
    }

    /**
     * listToArray
     *
     * @param permissions permissions
     * @return array
     */
    public String[] listToArray(List<String> permissions) {
        String[] arrayPermission = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            arrayPermission[i] = permissions.get(i);
        }
        return arrayPermission;
    }
}