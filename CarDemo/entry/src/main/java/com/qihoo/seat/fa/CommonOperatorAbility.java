package com.qihoo.seat.fa;

import com.alibaba.fastjson.JSON;
import com.qihoo.seat.fa.util.CommonOperationConstants;
import com.qihoo.seat.fa.util.LogUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.ace.ability.AceAbility;
import ohos.ace.ability.AceInternalAbility;
import ohos.app.AbilityContext;
import ohos.bundle.IBundleManager;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.rpc.IRemoteObject;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.rpc.RemoteException;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONObject;
import org.apache.hc.core5.util.TextUtils;
import ohos.agp.utils.TextTool;

import java.util.*;

/**
 * The application context capability interface is implemented on the Java side and can be remotely
 * invoked by CommonOperator.js. This class is in singleton mode and can be registered for multiple times.
 *
 * @since 2021-08-12
 */
public class CommonOperatorAbility extends AceInternalAbility {
    private static final String TAG = "CommonOperatorAbility";

    private static final String BUNDLE_NAME = "com.qihoo.seat.fa";

    private static final String ABILITY_NAME = "com.qihoo.seat.fa.CommonOperatorAbility";

    private static final String KEY_UNIQUE_ID = "uniqueId";

    private static volatile CommonOperatorAbility sInstance;

    private final Set<IRemoteObject> permissionStateCallbackSet = new HashSet<>();

    private final Set<IRemoteObject> permissionRequestCallbackSet = new HashSet<>();

    private final Set<IRemoteObject> startAbilityCallbackSet = new HashSet<>();

    private final Set<IRemoteObject> checkPackageCallbackSet = new HashSet<>();

    private final Set<IRemoteObject> getUniqueIdCallbackSet = new HashSet<>();

    private AbilityContext context;

    private String uniqueId;

    private CommonOperatorAbility(AbilityContext abilityContext) {
        super(BUNDLE_NAME, ABILITY_NAME);
    }

    /**
     * get the instance of CommonOperatorAbility
     *
     * @return instance
     */
    public static CommonOperatorAbility getInstance() {
        return sInstance;
    }

    /**
     * register the context and init CommonOperatorAbility
     *
     * @param abilityContext ability context
     */
    public static void register(AbilityContext abilityContext) {
        if (sInstance == null) {
            sInstance = new CommonOperatorAbility(abilityContext);
        }
        sInstance.onRegister(abilityContext);
    }

    private void onRegister(AbilityContext abilityContext) {
        context = abilityContext;
        final String filename = "common_db";
        Preferences preferences;
        DatabaseHelper databaseHelper;
        databaseHelper = new DatabaseHelper(context);
        preferences = databaseHelper.getPreferences(filename);
        if (preferences != null) {
            uniqueId = preferences.getString(KEY_UNIQUE_ID, "");
            LogUtils.i("onRegister uniqueId from sp= " + this.uniqueId);
            if (uniqueId == null || uniqueId.isEmpty()) {
                uniqueId = UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ROOT);
                preferences.putString(KEY_UNIQUE_ID, uniqueId);
                preferences.flush();
                LogUtils.i("onRegister uniqueId from uuid= " + this.uniqueId);
            }
            LogUtils.i("onRegister uniqueId = " + this.uniqueId);

            setInternalAbilityHandler(this::onRemoteRequest);
        }
    }

    private boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        switch (code) {
            case CommonOperationConstants.START_ABILITY:
                LogUtils.i("start ability");
                if (data == null) {
                    return false;
                }
                startAbilityCallbackSet.clear();
                startAbilityCallbackSet.add(data.readRemoteObject());
//                if (CommonOperatorUtil.getInstance().isGoHilinkRegister()) {
//                    CommonOperatorUtil.getInstance().goHilinkRegister(context);
//                    sendReturnRequest(ZSONObject.stringToZSON(data.readString()).getString("abilityName"));
//                } else {
                    startAbility(data);
//                }

                if (!replyResult(reply, option, CommonOperationConstants.SUCCESS, new HashMap<>())) {
                    return false;
                }
                break;
            case CommonOperationConstants.CHECK_PACKAGE_INSTALLATION:
                checkPackageInstalled(data);
                if (!replyResult(reply, option, CommonOperationConstants.SUCCESS, new HashMap<>())) {
                    return false;
                }
                break;
            case CommonOperationConstants.OPEN_URL:
                openUrl(data);
                if (!replyResult(reply, option, CommonOperationConstants.SUCCESS, new HashMap<>())) {
                    return false;
                }
                break;
            case CommonOperationConstants.GET_UNIQUE_ID:
                getUniqueId(data);
                if (!replyResult(reply, option, CommonOperationConstants.SUCCESS, new HashMap<>())) {
                    return false;
                }
                break;
            default:
                defaultHandle(reply);
                return false;
        }
        return true;
    }

    private void defaultHandle(MessageParcel reply) {
        Map<String, Object> resultMap = new HashMap<>();
        reply.writeString(ZSONObject.toZSONString(resultMap));
    }

    private boolean replyResult(MessageParcel reply, MessageOption option, int code, Map<String, Object> resultMap) {
        if (option.getFlags() == MessageOption.TF_SYNC) {
            // SYNC
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("code", code);
            dataMap.put("data", resultMap);
            reply.writeString(ZSONObject.toZSONString(dataMap));
        } else {
            // ASYNC
            MessageParcel responseData = MessageParcel.obtain();
            responseData.writeString(ZSONObject.toZSONString(resultMap));
            IRemoteObject remoteReply = reply.readRemoteObject();
            try {
                remoteReply.sendRequest(code, responseData, MessageParcel.obtain(), new MessageOption());
                responseData.reclaim();
            } catch (RemoteException exception) {
                return false;
            }
        }
        return true;
    }

    private void startAbility(MessageParcel data) {
        if (data == null) {
            return;
        }
        String dataString = data.readString();
        LogUtils.i("startAbility dataString:" + dataString);
        if (TextTool.isNullOrEmpty(dataString)) {
            return;
        }
        ZSONObject dataObject = ZSONObject.stringToZSON(dataString);
        String bundleName = dataObject.getString("bundleName");
        String abilityName = dataObject.getString("abilityName");
        ZSONObject params = dataObject.getZSONObject("params");
        ZSONObject options = dataObject.getZSONObject("options");
        if (params == null) {
            params = new ZSONObject();
        }
        if (options == null) {
            options = new ZSONObject();
        }
        Intent intent = new Intent();
        setIntentOperation(bundleName, abilityName, params, options, intent);
        int flags = 0;
        if (options.containsKey("flags")) {
            flags = options.getIntValue("flags");
//            intent.addFlags(Intent.FLAG_ABILITY_NEW_MISSION);
            LogUtils.e("flags:" +flags);
            intent.addFlags(276826112);
        }
        if (options.containsKey("action")) {
            String action = options.getString("action");
            intent.setAction(action);
        }
        if (options.containsKey("uri")) {
            String uri = options.getString("uri");
            intent.setUri(Uri.parse(uri));
        }
        if ((flags & Intent.FLAG_NOT_OHOS_COMPONENT) == Intent.FLAG_NOT_OHOS_COMPONENT) {
            if (context instanceof AceAbility) {
                ((AceAbility) context).startAbility(intent);
            } else if (context instanceof Ability) {
                ((Ability) context).startAbility(intent);
            } else {
                context.startAbility(intent, 0);
            }
        } else {
            context.startAbility(intent, 0);
        }
        sendReturnRequest(abilityName);
    }

    private void setIntentOperation(
            String bundleName, String abilityName, ZSONObject params, ZSONObject options, Intent intent) {
        intent.setParam("params", params.toString());
        Operation operation;
        if (options != null && options.containsKey("action")) {
            // Start ability by action
            operation = new Intent.OperationBuilder().withDeviceId("").withAction(options.getString("action")).build();
        } else {
            operation =
                    new Intent.OperationBuilder()
                            .withDeviceId("")
                            .withBundleName(bundleName)
                            .withAbilityName(abilityName)
                            .build();
        }
        intent.setOperation(operation);
    }

    private void sendReturnRequest(String abilityName) {
        MessageParcel returnData = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("started", abilityName);
        returnData.writeString(ZSONObject.toZSONString(resultMap));
        sendRemoteRequest(startAbilityCallbackSet, returnData, reply, option);
        reply.reclaim();
        returnData.reclaim();
    }

    private void checkPackageInstalled(MessageParcel data) {
        if (data == null) {
            return;
        }
        checkPackageCallbackSet.clear();
        checkPackageCallbackSet.add(data.readRemoteObject());
        String dataString = data.readString();
        LogUtils.i("checkPackageInstalled dataString:" + dataString);
        if (TextTool.isNullOrEmpty(dataString)) {
            return;
        }
        ZSONObject dataObject = ZSONObject.stringToZSON(dataString);
        if (dataObject == null) {
            return;
        }
        String packageName = dataObject.getString("bundleName");
        boolean isResult = false;
        IBundleManager manager = context.getBundleManager();
        try {
            boolean isApplicationEnabled = manager.isApplicationEnabled(packageName);
            if (isApplicationEnabled) {
                isResult = true;
            }
        } catch (IllegalArgumentException e) {
            LogUtils.i("Package " + packageName + " is not installed!");
        }
        LogUtils.i("Check package " + packageName + " installation result is " + isResult);
        MessageParcel returnData = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("installed", isResult);
        returnData.writeString(ZSONObject.toZSONString(resultMap));
        sendRemoteRequest(checkPackageCallbackSet, returnData, reply, option);
        reply.reclaim();
        returnData.reclaim();
    }

    private void openUrl(MessageParcel data) {
        if (data == null) {
            return;
        }
        checkPackageCallbackSet.clear();
        checkPackageCallbackSet.add(data.readRemoteObject());
        String dataString = data.readString();
        LogUtils.i("openUrl dataString:" + dataString);
        if (TextTool.isNullOrEmpty(dataString)) {
            return;
        }
        ZSONObject dataObject = ZSONObject.stringToZSON(dataString);
        String url = dataObject.getString("url");
        Intent intent = new Intent();
        Operation operation =
                new Intent.OperationBuilder()
                        .withAction("android.intent.action.VIEW")
                        .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                        .build();
        intent.setOperation(operation);
        intent.setUri(Uri.parse(url));
        context.startAbility(intent, 0);
        LogUtils.i("Start url success, Url: " + url);
        MessageParcel returnData = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("succeed", true);
        returnData.writeString(ZSONObject.toZSONString(resultMap));
        sendRemoteRequest(checkPackageCallbackSet, returnData, reply, option);
        reply.reclaim();
        returnData.reclaim();
    }

    private void getUniqueId(MessageParcel data) {
        if (data == null) {
            return;
        }
        getUniqueIdCallbackSet.clear();
        getUniqueIdCallbackSet.add(data.readRemoteObject());
        LogUtils.i("get unique id = " + uniqueId);
        MessageParcel returnData = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(KEY_UNIQUE_ID, uniqueId);
        returnData.writeString(ZSONObject.toZSONString(resultMap));
        sendRemoteRequest(getUniqueIdCallbackSet, returnData, reply, option);
        reply.reclaim();
        returnData.reclaim();
    }

    private void sendRemoteRequest(
            Set<IRemoteObject> callbackSet, MessageParcel returnData, MessageParcel reply, MessageOption option) {
        for (IRemoteObject remoteObject : callbackSet) {
            try {
                remoteObject.sendRequest(0, returnData, reply, option);
            } catch (RemoteException e) {
                LogUtils.e("RemoteException:" + e);
            }
        }
    }

    /**
     * unregister the CommonOperatorAbility
     */
    public static void unregister() {
        sInstance.onUnregister();
    }

    private void onUnregister() {
        context = null;
        setInternalAbilityHandler(null);
    }

    /**
     * set context
     *
     * @param context context
     */
    public void setContext(AbilityContext context) {
        this.context = context;
    }
}

